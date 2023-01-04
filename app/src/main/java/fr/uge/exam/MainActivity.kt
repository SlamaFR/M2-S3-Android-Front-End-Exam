package fr.uge.exam

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.uge.exam.ui.theme.ExamTheme
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ExamTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    DiceGame()
                }
            }
        }
    }
}

@Composable
fun DiceGame() {
    var currentState by rememberSaveable { mutableStateOf(GameState.START) }

    var throwId by rememberSaveable { mutableStateOf(0) }
    var target by rememberSaveable { mutableStateOf(20) }

    fun drawTarget() {
        var sum = 0
        repeat(5) {
            sum += Random.nextInt(1, 6) + 1
        }
        target = sum
    }

    fun play() {
        throwId = 0
        drawTarget()
        currentState = GameState.IN_GAME
    }


    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (currentState) {
            GameState.START -> {
                StartScreen {
                    play()
                }
            }
            GameState.IN_GAME -> {
                GameScreen(target, throwId, { throwId++ }) {
                    currentState = GameState.FINISHED
                }
            }
            GameState.FINISHED -> {
                EndScreen(throwId) {
                    play()
                }
            }
        }
    }
}

@Composable
fun StartScreen(onStart: () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "Dice Game",
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold
        )
        Button(onClick = onStart) {
            Text(
                text = "PLAY",
                fontSize = 64.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun GameScreen(target: Int, throwId: Int, throwIncrement: () -> Unit, onFinish: () -> Unit) {
    var currentScore by remember { mutableStateOf(0) }

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Target: $target",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )
        Components.MultipleSelectableDice(5, throwId) {
            currentScore = it.sum()
            if (currentScore == target) {
                onFinish()
            }
        }
        Text(
            "Diff: ${currentScore - target}",
            fontSize = 24.sp
        )
        Button(onClick = throwIncrement) {
            Text(
                text = "THROW",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun EndScreen(rounds: Int, restart: () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Congratulations!",
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "You succeeded with $rounds throws",
            fontSize = 32.sp
        )
        Button(onClick = restart) {
            Text(
                text = "PLAY AGAIN",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ExamTheme {
        DiceGame()
    }
}