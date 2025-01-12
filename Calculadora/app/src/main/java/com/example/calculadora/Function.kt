package com.example.calculadora.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.math.sqrt


class CalculatorState {
    var displayText by mutableStateOf("")
    var numeroAtual by mutableStateOf("")
    var numeroAnterior by mutableStateOf("")
    var operador by mutableStateOf<Char?>(null)
    var memoria by mutableStateOf(0.0)
    var mrcPressionado = false

    fun onPlusMinus() {
        if (numeroAtual.isNotEmpty()) {
            numeroAtual = if (numeroAtual.startsWith("-")) {
                numeroAtual.substring(1)
            } else {
                "-$numeroAtual"
            }
            displayText = numeroAtual
        }
    }

    fun onNumeroClick(numero: String) {
        if (numero == "." && numeroAtual.contains(".")) return
        numeroAtual += numero
        displayText = numeroAtual
    }

    fun onOperadorClick(op: Char) {
        if (numeroAtual.isNotEmpty()) {
            if (numeroAnterior.isNotEmpty() && operador != null) {
                calculateResult()
            } else {
                numeroAnterior = numeroAtual
            }
            numeroAtual = ""
            operador = op
        }
    }

    fun onIgualClick() {
        if (numeroAnterior.isNotEmpty() && numeroAtual.isNotEmpty()) {
            calculateResult()
            operador = null
            numeroAtual = numeroAnterior
        }
    }

    private fun calculateResult() {
        val n1 = numeroAnterior.toDoubleOrNull()
        val n2 = numeroAtual.toDoubleOrNull()

        if (n1 != null && n2 != null) {
            val result = when (operador) {
                '+' -> n1 + n2
                '-' -> n1 - n2
                '*' -> n1 * n2
                '/' -> if (n2 != 0.0) n1 / n2 else "Erro"
                '%' -> n1 * (n2 / 100)
                else -> null
            }
            numeroAnterior = result.toString()
            displayText = numeroAnterior
        }
    }

    fun onRaizClick() {
        val numero = numeroAtual.toDoubleOrNull()
        if (numero != null && numero >= 0) {
            val resultado = sqrt(numero)
            displayText = resultado.toString()
            numeroAtual = resultado.toString()
        } else {
            displayText = "Erro"
        }
    }

    fun onClear() {
        displayText = ""
        numeroAtual = ""
        numeroAnterior = ""
        operador = null
    }

    fun MemoriaMais() {
        val numero = numeroAtual.toDoubleOrNull()
        if (numero != null) {
            memoria += numero
            numeroAtual = ""
            mrcPressionado = false
        }
    }
    fun MemoriaMenos() {
        val numero = numeroAtual.toDoubleOrNull()
        if (numero != null) {
            memoria -= numero
            numeroAtual = ""
            mrcPressionado = false
        }
    }
    fun Memoriarc() {
        if (!mrcPressionado) {
            displayText = memoria.toString()
            numeroAtual = memoria.toString()
            mrcPressionado = true
        } else {
            memoria = 0.0
            displayText = "0"
            numeroAtual = ""
            mrcPressionado = false
        }
    }
}

@Composable
fun CalculatorDisplay(displayText: String) {
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
}

@Composable
fun Calculadora(modifier: Modifier = Modifier) {
    val calculatorState = remember { CalculatorState() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Exibir o display da calculadora
        CalculatorDisplay(displayText = calculatorState.displayText)

        // Linha para os botões: raiz, %, +/- e CE
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CalculatorButton(text = "MRC", onClick = { calculatorState.Memoriarc() },
                modifier = Modifier.weight(1f),
                backgroundColor = Color.Black)
            CalculatorButton(text = "M-", onClick = { calculatorState.MemoriaMenos() },
                modifier = Modifier.weight(1f),
                backgroundColor = Color.Black)
            CalculatorButton(text = "M+", onClick = { calculatorState.MemoriaMais() },
                modifier = Modifier.weight(1f),
                backgroundColor = Color.Black)
            CalculatorButton(text = "ON/CE", onClick = { calculatorState.onClear() },
                modifier = Modifier.weight(1f),
                backgroundColor = Color.Red)
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CalculatorButton(text = "raiz", onClick = { calculatorState.onRaizClick() },
                modifier = Modifier.weight(1f),
                backgroundColor = Color.Black)
            CalculatorButton(text = "%", onClick = { calculatorState.onOperadorClick('%') },
                modifier = Modifier.weight(1f),
                backgroundColor = Color.Black)
            CalculatorButton(text = "+/-", onClick = { calculatorState.onPlusMinus() },
                modifier = Modifier.weight(1f),
                backgroundColor = Color.Black)
            CalculatorButton(text = "CE", onClick = { calculatorState.onClear() },
                modifier = Modifier.weight(1f),
                backgroundColor = Color.Red)
        }

        // Botões numéricos e operadores
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CalculatorButton(text = "7", onClick = { calculatorState.onNumeroClick("7") }
                ,modifier = Modifier.weight(1f))
            CalculatorButton(text = "8", onClick = { calculatorState.onNumeroClick("8") }
                ,modifier = Modifier.weight(1f))
            CalculatorButton(text = "9", onClick = { calculatorState.onNumeroClick("9") }
                ,modifier = Modifier.weight(1f))
            CalculatorButton(text = "/", onClick = { calculatorState.onOperadorClick('/') }
                ,modifier = Modifier.weight(1f),
                backgroundColor = Color.Black)
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CalculatorButton(text = "4", onClick = { calculatorState.onNumeroClick("4") }
                ,modifier = Modifier.weight(1f))
            CalculatorButton(text = "5", onClick = { calculatorState.onNumeroClick("5") }
                ,modifier = Modifier.weight(1f))
            CalculatorButton(text = "6", onClick = { calculatorState.onNumeroClick("6") }
                ,modifier = Modifier.weight(1f))
            CalculatorButton(text = "*", onClick = { calculatorState.onOperadorClick('*') }
                ,modifier = Modifier.weight(1f),
                backgroundColor = Color.Black)
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CalculatorButton(text = "1", onClick = { calculatorState.onNumeroClick("1") }
                ,modifier = Modifier.weight(1f))
            CalculatorButton(text = "2", onClick = { calculatorState.onNumeroClick("2") }
                ,modifier = Modifier.weight(1f))
            CalculatorButton(text = "3", onClick = { calculatorState.onNumeroClick("3") }
                ,modifier = Modifier.weight(1f))
            CalculatorButton(text = "-", onClick = { calculatorState.onOperadorClick('-') }
                ,modifier = Modifier.weight(1f),
                backgroundColor = Color.Black)
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CalculatorButton(text = "0", onClick = { calculatorState.onNumeroClick("0") }
                ,modifier = Modifier.weight(1f))
            CalculatorButton(text = ".", onClick = { calculatorState.onNumeroClick(".") }
                ,modifier = Modifier.weight(1f))
            CalculatorButton(text = "=", onClick = { calculatorState.onIgualClick() }
                ,modifier = Modifier.weight(1f))
            CalculatorButton(text = "+", onClick = { calculatorState.onOperadorClick('+') }
                ,modifier = Modifier.weight(1f),
                backgroundColor = Color.Black)
        }
    }
}