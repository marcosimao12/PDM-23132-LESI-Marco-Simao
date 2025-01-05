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
    // Se esse usuário estiver logado, obtemos o e-mail. Pode ser nulo se não estiver logado ou se não tiver e-mail.
    val currentUserEmail = currentUser?.email

    // Observa o carrinho atual (para exibir na tela)
    val carrinhoAtual by carrinhoViewModel.carrinhoAtual.collectAsState()

    // Dropdown states
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf("Meu carrinho") }

    // Vamos renomear para "outroUserEmail" para deixar claro que é e-mail
    var outroUserEmail by remember { mutableStateOf("") }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Carrinho") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            // ------------------ Dropdown "Meu carrinho" ou "Outro usuário" ------------------
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

            // Se a opção for "Outro usuário", aparece o campo para digitar o e-mail
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
                            // Carrega o(s) carrinho(s) desse outro user usando o e-mail
                            carrinhoViewModel.carregarCarrinhosPorEmail(outroUserEmail)
                        }
                    }
                ) {
                    Text("Ver carrinho do outro usuário")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Exibe o carrinhoAtual
            if (carrinhoAtual != null) {
                Text(text = "Carrinho ID: ${carrinhoAtual?.id}")
                Spacer(modifier = Modifier.height(8.dp))

                // Lista de itens
                carrinhoAtual?.itens?.forEach { item ->
                    CarrinhoItemView(item)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- Aqui chamamos o Composable para autorizar outro usuário a usar este carrinho ---
                AutorizarOutroUsuario(carrinhoViewModel)
            } else {
                Text("Nenhum carrinho carregado ou não encontrado.")
            }
        }
    }
}

/**
 * Exibe os dados de um item do carrinho (produto, quantidade, preço, etc.).
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
 * Composable que permite autorizar outro usuário (via e-mail, por exemplo)
 * a adicionar itens neste carrinho.
 */
@Composable
fun AutorizarOutroUsuario(
    carrinhoViewModel: CarrinhoViewModel
) {
    val carrinhoAtual by carrinhoViewModel.carrinhoAtual.collectAsState()
    var emailAutorizado by remember { mutableStateOf("") }

    Column {
        Text(text = "Autorizar outro usuário a usar este carrinho:")
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = emailAutorizado,
            onValueChange = { emailAutorizado = it },
            label = { Text("E-mail do usuário") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                val cartId = carrinhoAtual?.id
                if (!emailAutorizado.isBlank() && cartId != null) {
                    // Chama a função do ViewModel que autoriza esse e-mail
                    carrinhoViewModel.autorizarEmail(cartId, emailAutorizado)
                    // Limpa o campo
                    emailAutorizado = ""
                } else {
                    // Tratar erro ou mostrar uma mensagem
                }
            }
        ) {
            Text("Autorizar Acesso")
        }
    }
}
