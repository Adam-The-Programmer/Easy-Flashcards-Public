package pl.lbiio.easyflashcards.data

data class PackageToUpdate (
    var packageID: String,
    var name: String,
    var nativeLanguage: String,
    var foreignLanguage: String,
    var artwork: String
        )