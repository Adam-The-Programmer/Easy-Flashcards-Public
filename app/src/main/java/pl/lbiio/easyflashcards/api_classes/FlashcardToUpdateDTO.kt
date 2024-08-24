package pl.lbiio.easyflashcards.api_classes

data class FlashcardToUpdateDTO(
    val packageID: String,
    val cardID: String,
    val word: String,
    val translations: String,
    val explanations: String,
    val phrases: String
)