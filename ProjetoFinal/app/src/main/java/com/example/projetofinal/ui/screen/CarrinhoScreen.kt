package com.example.projetofinal.ui.screen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.projetofinal.viewModels.CarrinhoViewModel
import com.example.projetofinal.model.CarrinhoItem
import com.google.firebase.auth.FirebaseAuth

class CarrinhoScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CarrinhoScreenContent()
        }
    }
}

/**
 * Tela principal do Carrinho.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarrinhoScreenContent(
    carrinhoViewModel: CarrinhoViewModel = viewModel()
) {
    // Obtemos o usuário atual do FirebaseAuth
    val currentUser = FirebaseAuth.getInstance().currentUser
    // E-mail do usuário, se estiver logado
    val currentUserEmail = currentUser?.email

    // Observa o carrinho atual (para exibir na tela)
    val carrinhoAtual by carrinhoViewModel.carrinhoAtual.collectAsState()

    // Controle do dropdown para "Meu carrinho" ou "Outro usuário"
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf("Meu carrinho") }
    var outroUserEmail by remember { mutableStateOf("") }

    // Controle para exibir o Dialog de autorização
    var showAutorizarDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Carrinho") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            // ---------------- Dropdown "Meu carrinho" ou "Outro usuário" ----------------
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                TextField(
                    readOnly = true,
                    value = selectedOption,
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
                    DropdownMenuItem(
                        text = { Text("Meu carrinho") },
                        onClick = {
                            selectedOption = "Meu carrinho"
                            outroUserEmail = ""
                            expanded = false

                            // Se tiver userEmail, carrega o carrinho desse e-mail
                            currentUserEmail?.let { email ->
                                carrinhoViewModel.carregarCarrinhosPorEmail(email)
                            }
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Outro usuário") },
                        onClick = {
                            selectedOption = "Outro usuário"
                            expanded = false
                        }
                    )
                }
            }

            // Se a opção for "Outro usuário", mostra um campo para digitar o e-mail
            if (selectedOption == "Outro usuário") {
                OutlinedTextField(
                    value = outroUserEmail,
                    onValueChange = { outroUserEmail = it },
                    label = { Text("E-mail do outro usuário") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
                Button(
                    onClick = {
                        if (outroUserEmail.isNotBlank()) {
                            carrinhoViewModel.carregarCarrinhosPorEmail(outroUserEmail)
                        }
                    }
                ) {
                    Text("Ver carrinho do outro usuário")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ---------------- Exibe carrinhoAtual ----------------
            if (carrinhoAtual != null) {
                Text(text = "Carrinho ID: ${carrinhoAtual?.id}")
                Spacer(modifier = Modifier.height(8.dp))

                // Lista de itens do carrinho
                carrinhoAtual?.itens?.forEach { item ->
                    CarrinhoItemView(item)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Botão para abrir o Dialog de autorização
                Button(
                    onClick = { showAutorizarDialog = true }
                ) {
                    Text("Autorizar outras pessoas ao carrinho")
                }
            } else {
                Text("Nenhum carrinho carregado ou não encontrado.")
            }
        }
    }

    // Se showAutorizarDialog == true, exibimos nosso AlertDialog para autorizar e-mail
    if (showAutorizarDialog) {
        AutorizarOutroUsuarioDialog(
            carrinhoViewModel = carrinhoViewModel,
            onDismiss = { showAutorizarDialog = false }
        )
    }
}

/**
 * Mostra o item do carrinho (produto, quantidade e preço).
 */
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
            Text(text = "Preço: R\$ ${item.preco}")
        }
    }
}

/**
 * AlertDialog (popup) para digitar e autorizar o e-mail de outro usuário.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AutorizarOutroUsuarioDialog(
    carrinhoViewModel: CarrinhoViewModel,
    onDismiss: () -> Unit
) {
    val carrinhoAtual by carrinhoViewModel.carrinhoAtual.collectAsState()
    var emailAutorizado by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { onDismiss() },
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
                    if (!emailAutorizado.isBlank() && cartId != null) {
                        // Autoriza o e-mail no carrinho
                        carrinhoViewModel.autorizarEmail(cartId, emailAutorizado)
                    }
                    onDismiss() // Fecha o diálogo
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
