package ufc.erv.garden.data

data class Plant(
    val id: String,
    var popularName: String,
    var scientificName: String = "",
    var description: String = "",
    var localization: String = "",
)
