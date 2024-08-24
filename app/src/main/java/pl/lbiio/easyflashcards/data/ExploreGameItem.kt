package pl.lbiio.easyflashcards.data

data class ExploreGameItem(
    val cardID: String,
    val frontText: String,
    val backText: String,
    var importance: Boolean
)