package ufc.erv.garden.data

import kotlinx.serialization.Serializable

@Serializable
data class Plant(
    val id: Int,
    val popularName: String,
    val scientificName: String? = null,
    val description: String? = null,
)
