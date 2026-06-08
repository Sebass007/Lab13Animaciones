package com.example.lab13animaciones

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lab13animaciones.ui.theme.Lab13AnimacionesTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Lab13AnimacionesTheme {
                VideoGameScreen()
            }
        }
    }
}

@Composable
fun VideoGameScreen() {
    // ESTADOS DEL JUEGO
    var score by remember { mutableIntStateOf(0) }
    var combo by remember { mutableIntStateOf(0) }
    var isAttacking by remember { mutableStateOf(false) }
    var enemyHealth by remember { mutableFloatStateOf(1f) }
    var energy by remember { mutableFloatStateOf(0f) }

    // LÓGICA DE TIEMPOS
    LaunchedEffect(isAttacking) {
        if (isAttacking) {
            delay(200)
            isAttacking = false
        }
    }

    // Resetear combo si no se ataca en 2 segundos
    LaunchedEffect(combo) {
        if (combo > 0) {
            delay(2000)
            combo = 0
        }
    }

    // ANIMACIONES AVANZADAS
    
    // 1. Rotación del jugador al atacar
    val rotation by animateFloatAsState(
        targetValue = if (isAttacking) 360f else 0f,
        animationSpec = tween(durationMillis = 300, easing = LinearEasing),
        label = "Rotation"
    )

    // 2. Escala del enemigo al ser golpeado
    val enemyScale by animateFloatAsState(
        targetValue = if (isAttacking) 0.8f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioHighBouncy),
        label = "EnemyScale"
    )

    // 3. Color de la barra de vida (Verde a Rojo)
    val healthColor by animateColorAsState(
        targetValue = if (enemyHealth > 0.5f) Color.Green else if (enemyHealth > 0.2f) Color.Yellow else Color.Red,
        label = "HealthColor"
    )

    // 4. Offset de sacudida (Shake) cuando hay ataque
    val shakeOffset by animateDpAsState(
        targetValue = if (isAttacking) 10.dp else 0.dp,
        animationSpec = repeatMotionSpec(iterations = 2, animation = tween(50)),
        label = "Shake"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF0F0C29), Color(0xFF302B63), Color(0xFF24243E))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 48.dp, bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // CABECERA: Puntuación y Combo
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "SCORE: $score",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.Monospace
                )
                AnimatedVisibility(
                    visible = combo > 1,
                    enter = fadeIn() + scaleIn(),
                    exit = fadeOut() + scaleOut()
                ) {
                    Text(
                        text = "COMBO x$combo",
                        color = Color(0xFFFFD700),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // ÁREA DE COMBATE: Enemigo y Jugador
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.offset(x = shakeOffset) // Sacudida de pantalla
            ) {
                // ENEMIGO
                Text("BOSS", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                // Barra de Vida del Enemigo
                Box(
                    modifier = Modifier
                        .width(200.dp)
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(Color.DarkGray)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(enemyHealth)
                            .fillMaxHeight()
                            .background(healthColor)
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Representación visual del Enemigo (Un cubo que reacciona)
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .rotate(if (isAttacking) 15f else 0f)
                        .background(Color(0xFFE94560), RoundedCornerShape(20.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("👾", fontSize = 50.sp)
                }

                Spacer(modifier = Modifier.height(60.dp))

                // JUGADOR
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .rotate(rotation) // Animación de rotación
                        .background(Color(0xFF6C63FF), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("⚔️", fontSize = 40.sp)
                }
            }

            // CONTROLES Y RECURSOS
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Barra de Energía para Especial
                Text("ENERGÍA", color = Color.Cyan, fontSize = 10.sp)
                LinearProgressIndicator(
                    progress = { energy },
                    modifier = Modifier.width(150.dp).padding(vertical = 8.dp),
                    color = Color.Cyan,
                    trackColor = Color.DarkGray
                )

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(
                        onClick = {
                            isAttacking = true
                            combo++
                            score += 10 * combo
                            energy = (energy + 0.1f).coerceAtMost(1f)
                            if (enemyHealth > 0) enemyHealth -= 0.05f
                            if (enemyHealth < 0) enemyHealth = 1f // Reset boss para seguir jugando
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE94560))
                    ) {
                        Text("ATACAR")
                    }

                    Button(
                        onClick = {
                            if (energy >= 1f) {
                                isAttacking = true
                                score += 100
                                energy = 0f
                                enemyHealth = (enemyHealth - 0.3f).coerceAtMost(1f)
                            }
                        },
                        enabled = energy >= 1f,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Cyan,
                            disabledContainerColor = Color.Gray
                        )
                    ) {
                        Text("ESPECIAL")
                    }
                }
            }
        }
    }
}

@Composable
fun <T> repeatMotionSpec(iterations: Int, animation: DurationBasedAnimationSpec<T>): FiniteAnimationSpec<T> {
    return repeatable(
        iterations = iterations,
        animation = animation,
        repeatMode = RepeatMode.Reverse
    )
}
