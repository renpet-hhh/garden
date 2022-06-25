package ufc.erv.garden.data

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class PlantAd(
    var id: String,
    var plant: Plant,
    var owner: String,
    @Contextual
    var date: LocalDateTime,
)
