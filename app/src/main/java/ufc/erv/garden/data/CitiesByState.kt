package ufc.erv.garden.data

import android.content.res.Resources
import ufc.erv.garden.R

object CitiesByState {
    private val cities = mapOf(
        "Acre" to R.array.cities_ac,
        "Alagoas" to R.array.cities_al,
        "Amapá" to R.array.cities_ap,
        "Amazonas" to R.array.cities_am,
        "Bahia" to R.array.cities_ba,
        "Ceará" to R.array.cities_ce,
        "Distrito Federal" to R.array.cities_df,
        "Espírito Santo" to R.array.cities_es,
        "Goiás" to R.array.cities_go,
        "Maranhão" to R.array.cities_ma,
        "Mato Grosso" to R.array.cities_mt,
        "Mato Grosso do Sul" to R.array.cities_ms,
        "Minas Gerais" to R.array.cities_mg,
        "Pará" to R.array.cities_pa,
        "Paraíba" to R.array.cities_pb,
        "Paraná" to R.array.cities_pr,
        "Pernambuco" to R.array.cities_pe,
        "Piauí" to R.array.cities_pi,
        "Rio de Janeiro" to R.array.cities_rj,
        "Rio Grande do Norte" to R.array.cities_rn,
        "Rio Grande do Sul" to R.array.cities_rs,
        "Rondônia" to R.array.cities_ro,
        "Roraima" to R.array.cities_rr,
        "Santa Catarina" to R.array.cities_sc,
        "São Paulo" to R.array.cities_sp,
        "Sergipe" to R.array.cities_se,
        "Tocantins" to R.array.cities_to,
    )

    fun getResourceArrayId(state: String) = cities[state]
    fun isValidState(state: String) = state in cities
    fun isValidCity(city: String, state: String): Boolean {
        val id = cities[state] ?: return false
        return getResourceArray(id)?.contains(city) ?: false
    }
    fun isValidCity(city: String): Boolean {
        return cities.values.any {
            getResourceArray(it)?.contains(city) ?: false
        }
    }

    private fun getResourceArray(citiesByStateId: Int): Array<String>? {
        return runCatching {
            Resources.getSystem().getStringArray(citiesByStateId)
        }.getOrNull()
    }

}