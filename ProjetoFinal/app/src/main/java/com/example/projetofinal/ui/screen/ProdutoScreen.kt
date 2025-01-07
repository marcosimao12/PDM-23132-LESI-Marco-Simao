package com.example.projetofinal.ui.screen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import com.example.projetofinal.R
import com.example.projetofinal.model.Carrinho
import com.example.projetofinal.model.CarrinhoItem
import com.example.projetofinal.model.Produto
import com.example.projetofinal.viewModels.CarrinhoViewModel
import com.example.projetofinal.viewModels.ProdutoViewModel
import com.google.firebase.auth.FirebaseAuth

class ProdutoScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            ProdutoScreenContent(navController)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProdutoScreenContent(
    navController: NavController,
    produtoViewModel: ProdutoViewModel = viewModel(),
    carrinhoViewModel: CarrinhoViewModel = viewModel()
) {
    val produtos by produtoViewModel.produtos.collectAsState()
    var carrinhoSelecionado by remember { mutableStateOf<Carrinho?>(null) }
    val carrinhosAutorizados by carrinhoViewModel.carrinhosAutorizados.collectAsState()
    var expanded by remember { mutableStateOf(false) }
    val currentUser = FirebaseAuth.getInstance().currentUser
    val currentUserEmail = currentUser?.email

    LaunchedEffect(Unit) {
        produtoViewModel.listenProdutosEmTempoReal()
        carrinhoViewModel.carregarCarrinhosAutorizadosDoUser()
        currentUserEmail?.let {
            carrinhoViewModel.buscarOuCriarCarrinhoPorEmail(it)
        }

    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Produtos") },
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
            )
        }
    ){ padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            //DROPDOWN
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                val textoSelecionado = if (carrinhoSelecionado == null) {
                    "Selecione um carrinho"
                } else {
                    val dono = carrinhoSelecionado?.ownerEmail ?: ""
                    if (dono == currentUserEmail) "Meu carrinho"
                    else "Carrinho de $dono"
                }

                TextField(
                    readOnly = true,
                    value = textoSelecionado,
                    onValueChange = {},
                    label = { Text("Adicionar itens em:") },
                    trailingIcon = {
                        Icon(
                            imageVector = if (expanded)
                                Icons.Filled.ArrowDropDown
                            else
                                Icons.Filled.ArrowDropDown,
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
                        val ehMeu = carrinho.ownerEmail == currentUserEmail
                        val label = if (ehMeu) "Meu carrinho"
                        else "Carrinho de ${carrinho.ownerEmail}"

                        DropdownMenuItem(
                            text = { Text(label) },
                            onClick = {
                                carrinhoSelecionado = carrinho
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(produtos) { produto ->
                    ProdutoItem(
                        produto = produto,
                        onAddToCart = { produtoSelecionado ->
                            val item = CarrinhoItem(
                                produtoId = produtoSelecionado.id.toString(),
                                nome = produtoSelecionado.nome,
                                preco = produtoSelecionado.preco,
                                quantidade = 1
                            )
                            carrinhoSelecionado?.let { carrinho ->
                                carrinhoViewModel.adicionarItemSeAutorizado(
                                    cartId = carrinho.id,
                                    emailQuemAdiciona = currentUserEmail ?: "",
                                    item = item
                                )
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
            .padding(8.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            AsyncImage(
                model = produto.url,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
            )

            Text(text = produto.nome, style = MaterialTheme.typography.titleSmall)
            Text(text = produto.descricao, style = MaterialTheme.typography.bodySmall)
            Text(text = "Preço: ${produto.preco} €", style = MaterialTheme.typography.bodySmall)

            Button(
                onClick = { onAddToCart(produto) },
                modifier = Modifier.padding(top = 4.dp),
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                Text("Adicionar")
            }
        }
    }
}

