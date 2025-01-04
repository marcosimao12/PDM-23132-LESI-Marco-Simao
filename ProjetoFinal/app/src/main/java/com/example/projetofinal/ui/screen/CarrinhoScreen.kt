package com.example.projetofinal.ui.screen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.projetofinal.model.CarrinhoItem
import com.example.projetofinal.viewModels.CarrinhoViewModel

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
fun CarrinhoScreenContent(carrinhoViewModel: CarrinhoViewModel = viewModel()) {
    val carrinho by carrinhoViewModel.carrinho.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Meu Carrinho") }) }
    ) { padding ->
        if (carrinho.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Text("Seu carrinho está vazio.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(carrinho) { item ->
                    CarrinhoItemView(item)
                }
            }
        }
    }
}

@Composable
fun CarrinhoItemView(item: CarrinhoItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = item.nome, style = MaterialTheme.typography.titleMedium)
            Text(text = "Quantidade: ${item.quantidade}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
