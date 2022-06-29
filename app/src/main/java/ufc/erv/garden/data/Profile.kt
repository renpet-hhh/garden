package ufc.erv.garden.data

import kotlinx.serialization.Serializable

@Serializable
data class Profile(
    var name: String,
    var email: String,
    var city: String,
    var state: String,
)