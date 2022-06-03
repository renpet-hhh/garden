package ufc.erv.garden

import android.net.Uri
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.android.material.textfield.TextInputLayout
import ufc.erv.garden.adapter.PlantListAdapter
import ufc.erv.garden.data.Plant

@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<Plant>?) {
    val adapter = recyclerView.adapter as PlantListAdapter
    adapter.submitList(data)
}

@BindingAdapter("imageUrl")
fun bindImageUrl(imgView: ImageView, imgUrl: String?) {
    imgUrl?.let {
        if (it == "mock") {
            imgView.load(R.drawable.plant)
            return@let
        }
        val imgUri = imgUrl.toUri().buildUpon().scheme("https").build()
        imgView.load(imgUri)
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
