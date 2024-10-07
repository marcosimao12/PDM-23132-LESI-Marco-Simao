package com.example.calculadora

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun Calculadora() {
        Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ){
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button1(onClick = { },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray )) {
                Text(text = "7") }
            Button1(onClick = { },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray )) {
                 Text(text = "8") }
            Button1(onClick = { },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray )) {
                Text(text = "9") }
            Button1(onClick = { }) { Text(text = "/") }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button1(onClick = { },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray )) {
                Text(text = "4") }
            Button1(onClick = { },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray )) {
                Text(text = "5") }
            Button1(onClick = { },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray )) {
                Text(text = "6") }
            Button1(onClick = { }) { Text(text = "*") }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button1(onClick = { },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray )) {
                Text(text = "1") }
            Button1(onClick = { },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray )) {
                Text(text = "2") }
            Button1(onClick = { },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray )) {
                Text(text = "3") }
            Button1(onClick = { }) { Text(text = "-") }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button1(onClick = { },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray )) {
                Text(text = "0") }
            Button1(onClick = { },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray )) {
                Text(text = ".") }
            Button1(onClick = { },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray )) {
                Text(text = "=") }
            Button1(onClick = { }) { Text(text = "+") }
        }
    }
}





