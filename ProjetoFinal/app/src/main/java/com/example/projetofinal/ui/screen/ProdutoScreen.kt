import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.projetofinal.model.Produto
import com.example.projetofinal.viewModels.CarrinhoViewModel
import com.example.projetofinal.viewModels.ProdutoViewModel

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
    val produtos by produtoViewModel.produtos.collectAsState()
    LaunchedEffect(Unit) {
        produtoViewModel.buscarProdutos()
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Produtos") }) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(produtos) { produto ->
                ProdutoItem(produto) { produtoSelecionado ->
                    carrinhoViewModel.adicionarAoCarrinho(produtoSelecionado)
                }
            }
        }
    }
}

@Composable
fun ProdutoItem(produto: Produto, onAddToCart: (Produto) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = produto.nome, style = MaterialTheme.typography.titleMedium)
            Text(text = "Preço: R\$ ${produto.preco}", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { onAddToCart(produto) }) {
                Text("Adicionar ao Carrinho")
            }
        }
    }
}
