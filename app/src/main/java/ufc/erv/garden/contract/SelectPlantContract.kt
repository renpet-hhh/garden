package ufc.erv.garden.contract

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import ufc.erv.garden.activity.MyPlantsActivity
import ufc.erv.garden.data.Plant

class SelectPlantContract : ActivityResultContract<Unit, Plant?>() {
    override fun createIntent(context: Context, input: Unit): Intent {
        return Intent(context, MyPlantsActivity::class.java).apply {
            putExtra(MyPlantsActivity.EXTRAS.RETURN_PLANT, true)
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Plant? {
        if (resultCode != Activity.RESULT_OK) return null
        val raw = intent?.getSerializableExtra(MyPlantsActivity.EXTRAS.RETURNED_PLANT) ?: return null
        return Json.decodeFromString<Plant>(raw.toString())
    }
}