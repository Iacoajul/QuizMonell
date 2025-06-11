package com.example.quizmonell

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

//numerationsklasse für die Screens
enum class QuizScreenState {
    START,
    QUESTION,
    RESULT
}

class QuizViewModel : ViewModel() {
    // Liste der Fragen
    private val questions: List<Question> = sampleQuestions.shuffled() // Fragen mischen

    // Aktueller Zustand des Quiz
    var currentScreen by mutableStateOf(QuizScreenState.START)
        private set

    // Index der aktuellen Frage
    var currentQuestionIndex by mutableIntStateOf(0)
        private set

    // Aktueller Punktestand
    var score by mutableIntStateOf(0)
        private set

    // Aktuelle Frage
    val currentQuestion: Question?
        get() = questions.getOrNull(currentQuestionIndex)

    // Feedback zur letzten Antwort (null, wenn keine Antwort gegeben wurde oder für neue Frage)
    // Pair<AntwortIndex, IstRichtig>
    var answerFeedback by mutableStateOf<Pair<Int, Boolean>?>(null)
        private set

    //Spielstart durch screenwechsel
    fun startGame() {
        currentScreen = QuizScreenState.QUESTION
        currentQuestionIndex = 0
        score = 0
        answerFeedback = null
    }

    //Frage evaluieren; wird auf AW Buttons gemapt
    fun answerQuestion(selectedOptionIndex: Int) {
        val question = currentQuestion ?: return
        val isCorrect = selectedOptionIndex == question.correctAnswerIndex
        answerFeedback = Pair(selectedOptionIndex, isCorrect)

        if (isCorrect) {
            score++
        }
    }

    //Der Name ist Programm
    fun nextQuestion() {
        answerFeedback = null // Feedback zurücksetzen für die nächste Frage
        if (currentQuestionIndex < questions.size - 1) { //check ob noch fragen da sind
            currentQuestionIndex++ //Frage aktualisieren
        } else {
            // Quiz beendet
            currentScreen = QuizScreenState.RESULT
        }
    }

    fun restartQuiz() {
        startGame() // Einfach das Spiel neu starten
    }

    fun getTotalQuestions(): Int = questions.size
}