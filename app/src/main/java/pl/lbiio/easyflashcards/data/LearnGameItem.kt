package pl.lbiio.easyflashcards.data

data class LearnGameItem (
    val cardID: String,
    val frontText: String,
    val backText: String,
    var importance: Boolean,
    var level: Int
    )
