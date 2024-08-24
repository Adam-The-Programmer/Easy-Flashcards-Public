package pl.lbiio.easyflashcards.api_classes

data class ProductDetails(
    val name: String,
    val path: String,
    val amount: Int,
    val description: String,
    val downloads: Int,
    val price: Int,
    val currency: String,
    val maxPoints: Int,
    val acquiredPoints: Int
)
