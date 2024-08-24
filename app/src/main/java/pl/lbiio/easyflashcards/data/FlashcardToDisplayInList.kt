package pl.lbiio.easyflashcards.data

data class FlashcardToDisplayInList (
    var cardID: String,
    var word: String,
    var status: Int,
    var translations: String,
    var cardMaxKnowledgeLevel: Int,
    var isTranslationKnown: Boolean,
    var isExplanationKnown: Boolean,
    var isPhraseKnown: Boolean,
    var isImportant: Boolean
        )