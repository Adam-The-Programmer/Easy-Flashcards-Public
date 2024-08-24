package pl.lbiio.easyflashcards.api_classes

data class CardFromBackup(
    val cardID: String,
    val word: String,
    val translations: String,
    val explanations: String,
    val phrases: String,
    val isImportant: Boolean,
    val translationKnowledgeLevel: Int,
    val explanationKnowledgeLevel: Int,
    val phraseKnowledgeLevel: Int,
    val isLearningTranslationKnown: Boolean,
    val isLearningExplanationKnown: Boolean,
    val isLearningPhraseKnown: Boolean
)