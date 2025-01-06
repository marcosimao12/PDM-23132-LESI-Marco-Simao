package com.example.projetofinal.ui.screen


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(navController)
        }
        composable("register") {
            RegisterScreen(navController)
        }
        composable("main") {
            MainScreen(navController)
        }
        composable("produtos") {
            ProdutoScreen(navController)
        }
        composable("carrinho") {
            CarrinhoScreen(navController)
        }
    }
}

@Composable
fun ProdutoScreen(navController: NavController) {
    ProdutoScreenContent(navController)
}

@Composable
fun CarrinhoScreen(navController: NavController) {
    CarrinhoScreenContent(navController)
}

@Composable
fun MainScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Bem-vindo à Tela Principal!", style = MaterialTheme.typography.titleLarge)

        Button(onClick = { navController.navigate("produtos") }) {
            Text("Ir para Produtos")
        }

        Button(onClick = { navController.navigate("carrinho") }) {
            Text("Ir para Carrinho")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            FirebaseAuth.getInstance().signOut()
            navController.navigate("login") {
                popUpTo("main") { inclusive = true }
            }
        }) {
            Text("Logout")
        }
    }
}
