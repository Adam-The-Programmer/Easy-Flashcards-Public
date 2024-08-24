package pl.lbiio.easyflashcards.api_classes

data class UserDTO(
    val uid: String,
    var email: String,
    var phone: String,
    var country: String
)