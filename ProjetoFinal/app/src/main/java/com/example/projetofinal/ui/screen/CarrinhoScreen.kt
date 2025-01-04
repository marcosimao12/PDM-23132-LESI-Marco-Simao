package com.example.projetofinal.ui.screen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarrinhoScreenContent(
    carrinhoViewModel: CarrinhoViewModel = viewModel()
) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val carrinhoAtual by carrinhoViewModel.carrinhoAtual.collectAsState()

    // Dropdown states
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf("Meu carrinho") }
    var outroUserId by remember { mutableStateOf("") }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Carrinho") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
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
                            imageVector = if (expanded)
                                androidx.compose.material.icons.Icons.Filled.ArrowDropDown
                            else
                                androidx.compose.material.icons.Icons.Filled.ArrowDropDown,
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
                            outroUserId = ""
                            expanded = false

                            // Se tiver user logado, carrega o carrinho desse user
                            currentUser?.uid?.let { userId ->
                                carrinhoViewModel.carregarCarrinhosPorUser(userId)
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

            // Se a opção for "Outro usuário", aparece o campo para digitar
            if (selectedOption == "Outro usuário") {
                OutlinedTextField(
                    value = outroUserId,
                    onValueChange = { outroUserId = it },
                    label = { Text("ID do outro usuário") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                )
                Button(
                    onClick = {
                        if (outroUserId.isNotBlank()) {
                            // Carrega o(s) carrinho(s) desse outro user
                            carrinhoViewModel.carregarCarrinhosPorUser(outroUserId)
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
                carrinhoAtual?.itens?.forEach { item ->
                    CarrinhoItemView(item)
                }
            } else {
                Text("Nenhum carrinho carregado ou não encontrado.")
            }
        }
    }
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
            Text(text = "Preço: R\$ ${item.preco}")
        }
    }
}
