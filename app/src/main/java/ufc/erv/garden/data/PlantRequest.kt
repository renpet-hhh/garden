package ufc.erv.garden.data

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class PlantRequest(
    var id: String,
    val plant: Plant,
    val requester: String,
    val owner: String,
    @Contextual
    val date: LocalDateTime,
)
