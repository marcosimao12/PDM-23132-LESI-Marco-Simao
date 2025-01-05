package com.example.projetofinal.ui.screen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.projetofinal.model.CarrinhoItem
import com.example.projetofinal.model.Produto
import com.example.projetofinal.viewModels.CarrinhoViewModel
import com.example.projetofinal.viewModels.ProdutoViewModel
import com.google.firebase.auth.FirebaseAuth

class ProdutoScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProdutoScreenContent()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProdutoScreenContent(
    produtoViewModel: ProdutoViewModel = viewModel(),
    carrinhoViewModel: CarrinhoViewModel = viewModel()
) {
    // Observa a lista de produtos
    val produtos by produtoViewModel.produtos.collectAsState()

    // Obter o usuário atual
    val currentUser = FirebaseAuth.getInstance().currentUser
    // Tentar pegar o e-mail do usuário (pode ser null se não estiver logado ou se o usuário não tiver e-mail)
    val currentUserEmail = currentUser?.email

    // Dropdown para escolher em qual carrinho adicionar (meu ou outro usuário)
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf("Meu carrinho") }
    var outroUserEmail by remember { mutableStateOf("") }

    // Carrega os produtos e (opcionalmente) cria/busca o carrinho do user logado
    LaunchedEffect(Unit) {
        produtoViewModel.buscarProdutos()

        // Se quisermos criar/buscar o carrinho do próprio user logado na inicialização:
        currentUserEmail?.let { email ->
            carrinhoViewModel.buscarOuCriarCarrinhoPorEmail(email)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Produtos") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // -------------- DROPDOWN: Meu Carrinho ou Outro Usuário --------------
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                TextField(
                    readOnly = true,
                    value = selectedOption,
                    onValueChange = {},
                    label = { Text("Adicionar itens em:") },
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

            // Se for "Outro usuário", mostra o campo para digitar o E-MAIL do outro usuário
            if (selectedOption == "Outro usuário") {
                OutlinedTextField(
                    value = outroUserEmail,
                    onValueChange = { outroUserEmail = it },
                    label = { Text("E-mail do outro usuário") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // -------------- LISTA DE PRODUTOS --------------
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(produtos) { produto ->
                    ProdutoItem(
                        produto = produto,
                        onAddToCart = { produtoSelecionado ->
                            // Transforma Produto em CarrinhoItem
                            val item = CarrinhoItem(
                                produtoId = produtoSelecionado.id.toString(),
                                nome = produtoSelecionado.nome,
                                preco = produtoSelecionado.preco,
                                quantidade = 1
                            )

                            // Verifica a opção escolhida no dropdown
                            if (selectedOption == "Meu carrinho") {
                                // Se não houver user ou userEmail, não conseguimos adicionar
                                if (currentUserEmail != null) {
                                    // Adiciona item ao carrinho do user (via email)
                                    carrinhoViewModel.adicionarItemAoCarrinhoPorEmail(
                                        ownerEmail = currentUserEmail,
                                        item = item
                                    )
                                } else {
                                    // Exibir mensagem: "Faça login" ou algo do tipo
                                }
                            } else {
                                // Outro usuário
                                if (outroUserEmail.isNotBlank()) {
                                    carrinhoViewModel.adicionarItemAoCarrinhoPorEmail(
                                        ownerEmail = outroUserEmail,
                                        item = item
                                    )
                                } else {
                                    // Mensagem de erro: "Preencha o e-mail do outro user"
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ProdutoItem(
    produto: Produto,
    onAddToCart: (Produto) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = produto.nome, style = MaterialTheme.typography.titleMedium)
            Text(text = produto.descricao, style = MaterialTheme.typography.bodyMedium)
            Text(text = "Preço: R\$ ${produto.preco}", style = MaterialTheme.typography.bodyLarge)

            Button(
                onClick = { onAddToCart(produto) },
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text("Adicionar ao Carrinho")
            }
        }
    }
}
