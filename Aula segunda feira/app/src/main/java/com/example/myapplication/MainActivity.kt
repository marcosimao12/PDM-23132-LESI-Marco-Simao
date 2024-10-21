package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.MyApplicationTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    FormScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun FormScreen(modifier: Modifier = Modifier) {
    val formLines = listOf(
        Formline(name = "Nome", type = "text", hint = "escrever"),
        Formline(name = "Nome", type = "text", hint = "escrever")
    )
                LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(formLines) { formLine ->
            FormRow(formLine = formLine)
        }
    }
}
@Composable
fun FormRow(formLine: Formline) {
    var textState by remember { mutableStateOf("") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Nome do campo (lado esquerdo)
        Text(
            text = formLine.name,
            modifier = Modifier
                .weight(1f)
                .padding(end = 16.dp)
        )
        TextField(
            value = textState,
            onValueChange = { textState = it },
            placeholder = {
                if (formLine.hint != null) {
                    Text(text = formLine.hint)
                }
            },
            modifier = Modifier.weight(2f)
        )
    }
}
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DefaultPreview() {
    MyApplicationTheme {
        FormScreen()
    }
}



