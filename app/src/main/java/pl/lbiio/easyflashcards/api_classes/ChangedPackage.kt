package pl.lbiio.easyflashcards.api_classes

data class ChangedPackage(
    val packageIDForeign: String,
    val name: String,
    val path: String,
    val backLanguage: String,
    val frontLanguage: String,
    val lastPackageChange: Long
)
