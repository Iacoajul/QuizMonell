package com.example.quizmonell 

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quizmonell.ui.theme.QuizMonellTheme

class MainActivity : ComponentActivity() {
    private val quizViewModel: QuizViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QuizMonellTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    QuizApp(
                        modifier = Modifier.padding(innerPadding),
                        viewModel = quizViewModel
                    )
                }
            }
        }
    }
}

@Composable
fun QuizApp(modifier: Modifier = Modifier, viewModel: QuizViewModel) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        when (viewModel.currentScreen) {
            QuizScreenState.START -> StartScreen(
                onStartClick = { viewModel.startGame() }
            )
            QuizScreenState.QUESTION -> QuizScreen(
                question = viewModel.currentQuestion,
                score = viewModel.score,
                totalQuestions = viewModel.getTotalQuestions(),
                currentQuestionNumber = viewModel.currentQuestionIndex + 1,
                answerFeedback = viewModel.answerFeedback,
                onAnswerSelected = { selectedIndex -> viewModel.answerQuestion(selectedIndex) },
                onNextClick = { viewModel.nextQuestion() }
            )
            QuizScreenState.RESULT -> ResultScreen(
                score = viewModel.score,
                totalQuestions = viewModel.getTotalQuestions(),
                onRestartClick = { viewModel.restartQuiz() }
            )
        }
    }
}

@Composable
fun StartScreen(onStartClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Willkommen zum Quiz!",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        Button(
            onClick = onStartClick,
            modifier = Modifier.fillMaxWidth(0.7f)
        ) {
            Text("Quiz starten", fontSize = 18.sp)
        }
    }
}

@Composable
fun QuizScreen(
    question: Question?,
    score: Int,
    totalQuestions: Int,
    currentQuestionNumber: Int,
    answerFeedback: Pair<Int, Boolean>?, // Pair<AusgewählterIndex, IstRichtig>
    onAnswerSelected: (Int) -> Unit,
    onNextClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (question == null) {
        // Sollte nicht passieren, wenn die Logik korrekt ist
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Lade Frage...")
        }
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Punktestand und Fragenfortschritt
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Punkte: $score", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text("Frage: $currentQuestionNumber / $totalQuestions", fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Frage
        Text(
            text = question.text,
            fontSize = 22.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        )

        // Antwortmöglichkeiten
        question.options.forEachIndexed { index, option ->
            val isSelected = answerFeedback?.first == index
            val isCorrectOption = index == question.correctAnswerIndex

            val buttonBackgroundColor = when {
                answerFeedback == null -> MaterialTheme.colorScheme.primaryContainer // Kein Feedback
                isSelected && answerFeedback.second -> Color.Green.copy(alpha = 0.7f) // Ausgewählt und richtig
                isSelected && !answerFeedback.second -> Color.Red.copy(alpha = 0.7f)   // Ausgewählt und falsch
                answerFeedback.second && isCorrectOption -> Color.Green.copy(alpha = 0.7f) // Nicht ausgewählt, aber die richtige Antwort (nachdem Feedback da ist)
                else -> MaterialTheme.colorScheme.primaryContainer // Standard oder andere nicht ausgewählte
            }
            val buttonTextColor = when {
                answerFeedback == null -> MaterialTheme.colorScheme.onPrimaryContainer
                isSelected && answerFeedback.second -> Color.White
                isSelected && !answerFeedback.second -> Color.White
                answerFeedback.second && isCorrectOption -> Color.White
                else -> MaterialTheme.colorScheme.onPrimaryContainer
            }


            Button(
                onClick = { if (answerFeedback == null) onAnswerSelected(index) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                enabled = answerFeedback == null, // Deaktivieren nach Antwort
                colors = ButtonDefaults.buttonColors(
                    containerColor = buttonBackgroundColor,
                    contentColor = buttonTextColor
                ),
                border = if (answerFeedback != null && isCorrectOption) BorderStroke(2.dp, Color.Green)
                    else if (answerFeedback != null && !isCorrectOption && isSelected) BorderStroke(2.dp, Color.Red)
                    else null

            ) {
                Text(option, fontSize = 18.sp)
            }
        }

        Spacer(modifier = Modifier.weight(1f)) // Drückt den "Weiter"-Button nach unten

        // "Weiter"-Button (nur anzeigen, wenn eine Antwort gegeben wurde)
        if (answerFeedback != null) {
            Button(
                onClick = onNextClick,
                modifier = Modifier.fillMaxWidth(0.7f)
            ) {
                Text(
                    if (currentQuestionNumber < totalQuestions) "Nächste Frage" else "Ergebnisse anzeigen",
                    fontSize = 18.sp
                )
            }
        }
    }
}

@Composable
fun ResultScreen(
    score: Int,
    totalQuestions: Int,
    onRestartClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Quiz beendet!",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            text = "Dein Ergebnis:",
            fontSize = 22.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "$score von $totalQuestions richtig beantwortet",
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        Button(
            onClick = onRestartClick,
            modifier = Modifier.fillMaxWidth(0.7f)
        ) {
            Text("Nochmal spielen", fontSize = 18.sp)
        }
    }
}

// Previews (optional, aber hilfreich für die Entwicklung)
@Preview(showBackground = true, name = "Start Screen Preview")
@Composable
fun StartScreenPreview() {
    QuizMonellTheme {
        StartScreen(onStartClick = {})
    }
}

@Preview(showBackground = true, name = "Quiz Screen Preview - No Feedback")
@Composable
fun QuizScreenPreviewNoFeedback() {
    QuizMonellTheme {
        QuizScreen(
            question = sampleQuestions.first(),
            score = 0,
            totalQuestions = sampleQuestions.size,
            currentQuestionNumber = 1,
            answerFeedback = null,
            onAnswerSelected = {},
            onNextClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Quiz Screen Preview - Correct Feedback")
@Composable
fun QuizScreenPreviewCorrectFeedback() {
    QuizMonellTheme {
        val question = sampleQuestions.first()
        QuizScreen(
            question = question,
            score = 1,
            totalQuestions = sampleQuestions.size,
            currentQuestionNumber = 1,
            answerFeedback = Pair(question.correctAnswerIndex, true), // Erste Option als richtig ausgewählt
            onAnswerSelected = {},
            onNextClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Quiz Screen Preview - Incorrect Feedback")
@Composable
fun QuizScreenPreviewIncorrectFeedback() {
    QuizMonellTheme {
        val question = sampleQuestions.first()
        val incorrectIndex = (question.correctAnswerIndex + 1) % question.options.size
        QuizScreen(
            question = question,
            score = 0,
            totalQuestions = sampleQuestions.size,
            currentQuestionNumber = 1,
            answerFeedback = Pair(incorrectIndex, false), // Falsche Option ausgewählt
            onAnswerSelected = {},
            onNextClick = {}
        )
    }
}


@Preview(showBackground = true, name = "Result Screen Preview")
@Composable
fun ResultScreenPreview() {
    QuizMonellTheme {
        ResultScreen(score = 7, totalQuestions = 10, onRestartClick = {})
    }
}

