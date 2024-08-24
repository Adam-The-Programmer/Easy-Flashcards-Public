package pl.lbiio.easyflashcards.data

data class QuizItem(val cardID: String, val question: String, val options: List<QuizOption>)
