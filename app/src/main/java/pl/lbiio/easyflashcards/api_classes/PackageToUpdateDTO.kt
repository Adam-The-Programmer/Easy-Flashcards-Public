package pl.lbiio.easyflashcards.api_classes

data class PackageToUpdateDTO(
    val packageID: String,
    val name: String,
    val frontLanguage: String,
    val backLanguage: String,
    var path: String
)