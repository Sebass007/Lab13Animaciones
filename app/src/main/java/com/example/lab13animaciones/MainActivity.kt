package com.example.lab13animaciones

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lab13animaciones.ui.theme.Lab13AnimacionesTheme

// Definición de los estados de la aplicación
sealed class AppState {
    object Loading : AppState()
    object Content : AppState()
    object Error : AppState()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Lab13AnimacionesTheme {
                AnimatedContentScreen()
            }
        }
    }
}

@Composable
fun AnimatedContentScreen() {
    var currentState by remember { mutableStateOf<AppState>(AppState.Loading) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Fila de botones para cambiar estados
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = { currentState = AppState.Loading }) {
                Text("Cargando")
            }
            Button(onClick = { currentState = AppState.Content }) {
                Text("Contenido")
            }
            Button(onClick = { currentState = AppState.Error }) {
                Text("Error")
            }
        }

        // Transición de contenido animada basada en el estado actual
        AnimatedContent(
            targetState = currentState,
            transitionSpec = {
                fadeIn(animationSpec = tween(500)) togetherWith fadeOut(animationSpec = tween(500))
            },
            label = "StateTransition"
        ) { state ->
            when (state) {
                is AppState.Loading -> StateCard(
                    emoji = "⏳",
                    message = "Cargando datos...",
                    backgroundColor = Color(0xFFFFF176) // Amarillo
                )
                is AppState.Content -> StateCard(
                    emoji = "✅",
                    message = "¡Contenido cargado con éxito!",
                    backgroundColor = Color(0xFFAED581) // Verde
                )
                is AppState.Error -> StateCard(
                    emoji = "⚠️",
                    message = "Ocurrió un error inesperado",
                    backgroundColor = Color(0xFFE57373) // Rojo
                )
            }
        }
    }
}

@Composable
fun StateCard(emoji: String, message: String, backgroundColor: Color) {
    Card(
        modifier = Modifier.padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = emoji, fontSize = 48.sp)
            Text(
                text = message,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}
