package ufc.erv.garden

import android.net.Uri
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.android.material.textfield.TextInputLayout
import okhttp3.internal.cacheGet
import okhttp3.internal.wait
import ufc.erv.garden.adapter.PlantAdListAdapter
import ufc.erv.garden.adapter.PlantListAdapter
import ufc.erv.garden.adapter.PlantRequestListAdapter
import ufc.erv.garden.data.Plant
import ufc.erv.garden.data.PlantAd
import ufc.erv.garden.data.PlantRequest

@BindingAdapter("listPlant")
fun bindListPlant(recyclerView: RecyclerView, data: List<Plant>?) {
    val adapter = recyclerView.adapter as PlantListAdapter
    adapter.submitList(data)
}

@BindingAdapter("listPlantRequest")
fun bindListPlantRequest(recyclerView: RecyclerView, data: List<PlantRequest>?) {
    val adapter = recyclerView.adapter as PlantRequestListAdapter
    adapter.submitList(data)
}

@BindingAdapter("listPlantAd")
fun bindListAd(recyclerView: RecyclerView, data: List<PlantAd>?) {
    val adapter = recyclerView.adapter as PlantAdListAdapter
    adapter.submitList(data)
}

@BindingAdapter("imageUrl")
fun bindImageUrl(imgView: ImageView, imgUrl: String?) {
    imgUrl?.let {
        if (imgView.drawable != null) return@let
        if (it == "mock") {
            imgView.load(R.drawable.plant)
            return@let
        }
        val imgUri = imgUrl.toUri().buildUpon().scheme("https").build()
        imgView.load(imgUri) {
            crossfade(true)
            error(R.drawable.plant)
            placeholder(R.drawable.plant)
        }
    }
}

@BindingAdapter("imageUri")
fun bindImageUri(imgView: ImageView, imgUri: Uri?) {
    imgUri?.let {
        imgView.load(imgUri)
    }
}

@BindingAdapter("errorText")
fun bindError(textInputLayout: TextInputLayout, error: String) {
    textInputLayout.error = error.ifEmpty { null }
}
