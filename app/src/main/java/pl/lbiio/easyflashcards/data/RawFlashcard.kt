package pl.lbiio.easyflashcards.data

data class RawFlashcard (
    var FlashcardIDForeign: String,
    var word: String,
    var translations: String,
    var explanations: String,
    var phrases: String,
        )