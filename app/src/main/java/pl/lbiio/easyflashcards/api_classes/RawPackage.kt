package pl.lbiio.easyflashcards.api_classes

data class RawPackage(
    val packageID: String,
    val name: String,
    val path: String,
    val backLanguage: String,
    val frontLanguage: String,
    val maxKnowledgeLevel: Int
)

