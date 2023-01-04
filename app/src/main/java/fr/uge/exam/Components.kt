package fr.uge.exam

import android.os.SystemClock
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.material.Button
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.random.Random

@Preview(showBackground = true)
@Composable
fun TestSimpleDice() {
    Components.SimpleDice(value = 6, backgroundColor = Color.Red)
}

@Preview(showBackground = true)
@Composable
fun TestMatrixDice() {
    Components.MatrixDice(value = 5, backgroundColor = Color.Red)
}

@Preview(showBackground = true)
@Composable
fun SimpleDiceTester() {
    var diceValue by remember { mutableStateOf(0f) }

    Column(Modifier.fillMaxSize()) {
        Components.SimpleDice(value = diceValue.toInt(), backgroundColor = Color.Blue)
        Slider(
            value = diceValue,
            onValueChange = { diceValue = it },
            steps = 5,
            valueRange = 0f..6f
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DiceAccumulator() {
    var throwId by remember { mutableStateOf(0) }
    var accumulator by remember { mutableStateOf(0) }

    Column(Modifier.fillMaxSize()) {
        Components.MultipleSelectableDice(5, throwId = throwId) { accumulator = it.sum() }
        Button(onClick = { throwId++ }) {
            Text("ROLL THE DICE")
        }
        Text(text = "Total value: $accumulator")
    }
}

object Components {

    @Composable
    fun SimpleDice(value: Int, backgroundColor: Color) {
        Box(
            Modifier
                .aspectRatio(1f)
                .background(backgroundColor)
                .border(5.dp, Color.Black),
            contentAlignment = Alignment.Center
        ) {
            if (value > 0) {
                Text(value.toString(), fontSize = 50.sp)
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun MatrixDice(value: Int, backgroundColor: Color) {
        BoxWithConstraints(
            Modifier
                .aspectRatio(1f)
                .background(backgroundColor),
            contentAlignment = Alignment.Center
        ) {
            LazyVerticalGrid(
                cells = GridCells.Fixed(3),
                contentPadding = PaddingValues(3.dp),
            ) {
                items(9) { index ->
                    val row = index / 3
                    val col = index % 3
                    Box(
                        Modifier
                            .fillMaxSize()
                            .aspectRatio(1f)
                            .padding(2.dp)
                            .background(
                                if (MatrixDiceHelper.getMark(value, row, col)) {
                                    Color.Black
                                } else {
                                    Color.Transparent
                                }
                            )
                    )
                }
            }
        }
    }

    @Composable
    fun ThrowableDice(throwId: Int, backgroundColor: Color, onThrow: (Int) -> Unit = {}) {
        var diceValue by remember { mutableStateOf(0) }

        LaunchedEffect(throwId) {
            if (throwId > 0) {
                diceValue = Random.nextInt(1, 6)
                onThrow(diceValue)
            }
        }

        MatrixDice(diceValue, backgroundColor)
    }

    @Composable
    fun SuspenseDice(
        throwId: Int,
        backgroundColor: Color,
        throwDuration: Long = 2000L,
        onThrow: (Int) -> Unit = {}
    ) {
        var diceValue by remember { mutableStateOf(0) }

        LaunchedEffect(throwId) {
            if (throwId > 0) {
                val currentTime = SystemClock.elapsedRealtime()
                while (SystemClock.elapsedRealtime() - currentTime < throwDuration) {
                    diceValue = Random.nextInt(0, 6) + 1
                    delay(100)
                }
                onThrow(diceValue)
            }
        }

        MatrixDice(diceValue, backgroundColor)
    }

    @Composable
    fun MultipleDice(
        diceNumber: Int,
        throwId: Int,
        throwDuration: Long = 2000L,
        throwDelay: Long = 500L,
        onThrow: (List<Int>) -> Unit = {}
    ) {
        val throwIds: MutableList<Int> = remember { mutableStateListOf() }
        val diceValues: MutableList<Int> = remember { mutableStateListOf() }

        LaunchedEffect(throwId) {
            if (throwId > 0) {
                diceValues.clear()
                repeat(diceNumber) {
                    throwIds[it]++
                    delay(throwDelay)
                }
                delay(throwDuration)
                onThrow(diceValues)
            }
        }

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(25.dp)
        ) {
            repeat(diceNumber) { index ->
                throwIds.add(0)
                Box(Modifier.weight(1f / diceNumber)) {
                    SuspenseDice(throwId = throwIds[index], Color.Red, throwDuration) {
                        diceValues.add(it)
                    }
                }
            }
        }
    }

    @Composable
    fun MultipleSelectableDice(
        diceNumber: Int,
        throwId: Int,
        throwDuration: Long = 2000L,
        throwDelay: Long = 500L,
        onThrow: (List<Int>) -> Unit = {}
    ) {
        val throwIds = remember { mutableStateListOf<Int>() }
        val selectedDices = remember { mutableStateListOf<Boolean>() }
        val diceValues = remember { mutableStateListOf<Int>() }

        LaunchedEffect(throwId) {
            if (throwId > 0) {
                repeat(diceNumber) {
                    if (selectedDices[it]) {
                        throwIds[it]++
                        delay(throwDelay)
                    }
                }
                delay(throwDuration)
                onThrow(diceValues)
            }
        }

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(25.dp)
        ) {
            repeat(diceNumber) { index ->
                throwIds.add(0)
                selectedDices.add(true)
                diceValues.add(0)
                Box(
                    Modifier
                        .weight(1f / diceNumber)
                        .clickable {
                            selectedDices[index] = !selectedDices[index]
                        }
                ) {
                    SuspenseDice(
                        throwId = throwIds[index],
                        if (selectedDices[index]) Color.Red else Color.Gray,
                        throwDuration
                    ) {
                        diceValues[index] = it
                    }
                }
            }
        }
    }

}