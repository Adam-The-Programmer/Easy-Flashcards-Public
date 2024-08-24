package pl.lbiio.easyflashcards.api_classes

data class PackageToBuyDTO(
    val packageID: String,
    val description: String,
    val price: Int,
    val currency: String
)