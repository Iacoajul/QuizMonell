package com.example.quizmonell


data class Question(
    val text: String,
    val options: List<String>,
    val correctAnswerIndex: Int
)

// Beispiel-Fragen (könnten später aus einer Datenbank oder Datei geladen werden)
val sampleQuestions = listOf(
    Question("Was ist die Hauptstadt von Deutschland?", listOf("Berlin", "München", "Hamburg"), 0),
    Question("Welcher Planet ist der größte in unserem Sonnensystem?", listOf("Erde", "Mars", "Jupiter"), 2),
    Question("Wie viele Kontinente gibt es?", listOf("5", "6", "7"), 2),
    Question("Wer hat die Relativitätstheorie entwickelt?", listOf("Isaac Newton", "Albert Einstein", "Galileo Galilei"), 1),
    Question("Was ist H2O?", listOf("Salz", "Zucker", "Wasser"), 2)

)