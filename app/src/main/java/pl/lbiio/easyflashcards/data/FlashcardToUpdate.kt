package pl.lbiio.easyflashcards.data

data class FlashcardToUpdate (
    var cardID: String,
    var word: String,
    var translations: String,
    var explanations: String,
    var phrases: String
        )