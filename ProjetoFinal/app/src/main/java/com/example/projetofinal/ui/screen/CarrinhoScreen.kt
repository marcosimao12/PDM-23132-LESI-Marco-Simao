package com.example.projetofinal.ui.screen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import com.example.projetofinal.model.Carrinho
import com.example.projetofinal.model.CarrinhoItem
import com.example.projetofinal.viewModels.CarrinhoViewModel
import com.google.firebase.auth.FirebaseAuth

class CarrinhoScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            CarrinhoScreenContent(navController)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarrinhoScreenContent(
    navController: NavController,
    carrinhoViewModel: CarrinhoViewModel = viewModel()
) {
    LaunchedEffect(Unit) {
        carrinhoViewModel.carregarCarrinhosAutorizadosDoUsuario()
    }

    val carrinhosAutorizados by carrinhoViewModel.carrinhosAutorizados.collectAsState()
    val carrinhoAtual by carrinhoViewModel.carrinhoAtual.collectAsState()
    var selectedCarrinho by remember { mutableStateOf<Carrinho?>(null) }
    var expanded by remember { mutableStateOf(false) }
    val totalCompra = carrinhoAtual?.itens?.sumOf { it.preco } ?: 0.0


    LaunchedEffect(selectedCarrinho?.id) {
        val cartId = selectedCarrinho?.id
        if (!cartId.isNullOrBlank()) {
            carrinhoViewModel.listenCarrinhoEmTempoReal(cartId)
        }
    }


    var showAutorizarDialog by remember { mutableStateOf(false) }
    val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email
    var showPagamentoDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Carrinho") },
            navigationIcon = {
                IconButton(onClick = {
                    navController.navigate("main")
                }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Voltar"
                    )
                }
            }
            ) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                val labelSelecionado = if (selectedCarrinho == null) {
                    "Selecione um carrinho"
                } else {
                    val dono = selectedCarrinho?.ownerEmail ?: ""
                    if (dono == currentUserEmail) "Meu carrinho"
                    else "Carrinho de $dono"
                }

                TextField(
                    readOnly = true,
                    value = labelSelecionado,
                    onValueChange = {},
                    label = { Text("Ver carrinho de:") },
                    trailingIcon = {
                        Icon(
                            imageVector = if (expanded) Icons.Filled.ArrowDropDown else Icons.Filled.ArrowDropDown,
                            contentDescription = null
                        )
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    carrinhosAutorizados.forEach { carrinho ->
                        val dono = carrinho.ownerEmail
                        val label = if (dono == currentUserEmail)
                            "Meu carrinho"
                        else
                            "Carrinho de $dono"

                        DropdownMenuItem(
                            text = { Text(label) },
                            onClick = {
                                selectedCarrinho = carrinho
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (carrinhoAtual != null) {
                Text(text = "Carrinho ID: ${carrinhoAtual?.id}")
                Spacer(modifier = Modifier.height(8.dp))

                carrinhoAtual?.itens?.forEach { item ->
                    CarrinhoItemView(item)
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Total:${totalCompra}€", style = MaterialTheme.typography.titleMedium)

                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { showPagamentoDialog = true }) {
                    Text("Finalizar Compra")
                }

                Button(
                    onClick = { showAutorizarDialog = true }
                ) {
                    Text("Autorizar outras pessoas ao carrinho")
                }
            } else {
                Text("Nenhum carrinho selecionado ou não encontrado.")
            }
        }
    }

    if (showPagamentoDialog) {
        EscolherPagamentoDialog(
            onDismiss = { showPagamentoDialog = false },
            onConfirm = { tipoPagamento ->
                // Chamamos a função de "finalizar compra"
                carrinhoAtual?.id?.let { cartId ->
                    carrinhoViewModel.finalizarCompra(cartId)
                }
                showPagamentoDialog = false
                // Poderia mandar o user para a tela "Obrigado pela compra"
                navController.navigate("main")
            }
        )
    }

    if (showAutorizarDialog) {
        AutorizarOutroUsuarioDialog(
            carrinhoViewModel = carrinhoViewModel,
            onDismiss = { showAutorizarDialog = false }
        )
    }
}

@Composable
fun EscolherPagamentoDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var selectedOption by remember { mutableStateOf("MbWay") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Escolha a forma de pagamento") },
        text = {
            Column {
                // Botões de Radio ou algo similar
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = (selectedOption == "MbWay"),
                        onClick = { selectedOption = "MbWay" }
                    )
                    Text("MbWay")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = (selectedOption == "Crypto"),
                        onClick = { selectedOption = "Crypto" }
                    )
                    Text("Crypto")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(selectedOption) }) {
                Text("Confirmar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}




@Composable
fun CarrinhoItemView(item: CarrinhoItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(text = item.nome, style = MaterialTheme.typography.titleMedium)
            Text(text = "Qtd: ${item.quantidade}")
            Text(text = "Preço:${item.preco} €")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AutorizarOutroUsuarioDialog(
    carrinhoViewModel: CarrinhoViewModel,
    onDismiss: () -> Unit
) {
    val carrinhoAtual by carrinhoViewModel.carrinhoAtual.collectAsState()
    var emailAutorizado by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Autorizar outra pessoa") },
        text = {
            Column {
                Text("Digite o e-mail que poderá adicionar itens neste carrinho:")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = emailAutorizado,
                    onValueChange = { emailAutorizado = it },
                    label = { Text("E-mail do usuário") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val cartId = carrinhoAtual?.id
                    if (!emailAutorizado.isBlank() && !cartId.isNullOrBlank()) {
                        carrinhoViewModel.autorizarEmail(cartId, emailAutorizado)
                    }
                    onDismiss()
                }
            ) {
                Text("Autorizar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
