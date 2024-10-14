package com.example.calculadora

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.calculadora.ui.theme.CalculadoraTheme
import androidx.compose.material3.Button as Button1

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CalculadoraTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Calculadora(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Calculadora(modifier: Modifier = Modifier) {
    var displayText by remember { mutableStateOf("") }
    var numeroAtual by remember { mutableStateOf("") }
    var numeroAnterior by remember { mutableStateOf("") }
    var operador by remember { mutableStateOf<Char?>(null) }


    fun onPlusMinus() {
        if (numeroAtual.isNotEmpty()) {
            if (numeroAtual.startsWith("-")) {
                numeroAtual = numeroAtual.substring(1)
            } else {
                numeroAtual = "-$numeroAtual"
            }
            displayText = numeroAtual
        }
    }
    //funcao chamada para quando alguem clica num numero
    //logica com o caso de alguem nao clicar num operador, logo pode meter mais que um numero
    fun onNumeroClick(numero: String) {
        if (numero == "." && numeroAtual.contains(".")){
            return
        }
        if (operador == null) {
            numeroAtual += numero
        } else {
            numeroAtual += numero
        }
        displayText = numeroAtual
    }

    // Função chamada quando o botão "raiz" é clicado na calculadora
    fun onRaizClick() {
        val numero = numeroAtual.toDoubleOrNull()

        if (numero != null && numero >= 0) { // Verifica se o número não é  negativo
            val resultado = kotlin.math.sqrt(numero)
            displayText = resultado.toString()
            numeroAtual = resultado.toString()
        } else {
            displayText = "Erro" // Exibe erro se o número for inválido ou negativo
        }
    }

    //Função chamada quando um operador (como '+', '-', '*', '/') é clicado na calculadora.
    // Se `numeroAtual` não estiver vazio, ou seja, se um número foi digitado, a função
    //  verifica se há um número anterior e um operador já definidos.
    fun onOperadorClick(op: Char) {
        if (numeroAtual.isNotEmpty()) {
            // Se já existe um número anterior e um operador, realiza o cálculo antes de continuar
            if (numeroAnterior.isNotEmpty() && operador != null) {

                val n1 = numeroAnterior.toDoubleOrNull()
                val n2 = numeroAtual.toDoubleOrNull()
                // Se os números forem válidos, realiza a operação com base no operador anterior
                if (n1 != null && n2 != null) {
                    val resultado = when (operador) {
                        '+' -> n1 + n2
                        '-' -> n1 - n2
                        '*' -> n1 * n2
                        '/' -> if (n2 != 0.0) n1 / n2 else "Erro"
                        '%' -> n1 * (n2 / 100)
                        else -> null
                    }
                    numeroAnterior = resultado.toString()
                    displayText = numeroAnterior
                }
            } else {

                numeroAnterior = numeroAtual
            }
            numeroAtual = ""
            operador = op
        }
    }
    //Função chamada quando o botão "igual" ('=') é clicado na calculadora.
    fun onIgualClick() {
        val n1 = numeroAnterior.toDoubleOrNull()
        val n2 = numeroAtual.toDoubleOrNull()

        if (n1 != null && n2 != null && operador != null) {
            val resultado = when (operador) {
                '+' -> n1 + n2
                '-' -> n1 - n2
                '*' -> n1 * n2
                '/' -> if (n2 != 0.0) n1 / n2 else "Erro"
                '%' -> n1 * (n2 / 100)
                else -> null
            }
            displayText = resultado.toString()
            numeroAtual = resultado.toString()
            numeroAnterior = ""
            operador = null
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(22.dp)
            .border(2.dp, Color.Black)
            .padding(8.dp)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .background(Color.LightGray)
                    .padding(10.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Text(
                    text = displayText,
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.Black
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button1(
                    onClick = { },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) {
                    Text(text = "MRC")
                }
                Button1(
                    onClick = { },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) {
                    Text(text = "M-")
                }
                Button1(
                    onClick = { },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) {
                    Text(text = "M+")
                }
                Button1(
                    onClick = { },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) { Text(text = "ON/C") }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button1(
                    onClick = { onRaizClick() },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) {
                    Text(text = "raiz")
                }
                Button1(
                    onClick = { onOperadorClick('%') },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) {
                    Text(text = "%")
                }
                Button1(
                    onClick = { onPlusMinus() },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) {
                    Text(text = "+/-")
                }
                Button1(
                    onClick = {
                        displayText = ""
                        numeroAtual = ""
                        numeroAnterior = ""
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) { Text(text = "CE") }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button1(
                    onClick = { onNumeroClick("7") },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                ) {
                    Text(text = "7")
                }
                Button1(
                    onClick = { onNumeroClick("8") },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                ) {
                    Text(text = "8")
                }
                Button1(
                    onClick = { onNumeroClick("9") },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                ) {
                    Text(text = "9")
                }
                Button1(
                    onClick = { onOperadorClick('/') },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) { Text(text = "/") }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button1(
                    onClick = { onNumeroClick("4") },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                ) {
                    Text(text = "4")
                }
                Button1(
                    onClick = { onNumeroClick("5") },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                ) {
                    Text(text = "5")
                }
                Button1(
                    onClick = { onNumeroClick("6") },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                ) {
                    Text(text = "6")
                }
                Button1(
                    onClick = { onOperadorClick('*') },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) { Text(text = "*") }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button1(
                    onClick = { onNumeroClick("1") },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                ) {
                    Text(text = "1")
                }
                Button1(
                    onClick = { onNumeroClick("2") },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                ) {
                    Text(text = "2")
                }
                Button1(
                    onClick = {onNumeroClick("3") },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                ) {
                    Text(text = "3")
                }
                Button1(
                    onClick = { onOperadorClick('-') },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) { Text(text = "-") }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button1(
                    onClick = { onNumeroClick("0") },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                ) {
                    Text(text = "0")
                }
                Button1(
                    onClick = { onNumeroClick(".") },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                ) {
                    Text(text = ".")
                }
                Button1(
                    onClick = { onIgualClick() },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                ) {
                    Text(text = "=")
                }
                Button1(
                    onClick = { onOperadorClick('+') },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) { Text(text = "+") }
            }
        }
    }
}









