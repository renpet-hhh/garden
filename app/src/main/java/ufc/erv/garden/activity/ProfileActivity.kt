package ufc.erv.garden.activity

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import ufc.erv.garden.R
import ufc.erv.garden.data.CitiesByState
import ufc.erv.garden.databinding.ProfileBinding
import ufc.erv.garden.viewModel.ProfileModel

class ProfileActivity : DrawerBaseActivity() {
    private lateinit var binding: ProfileBinding
    private val viewModel: ProfileModel by viewModels()

    private fun bindAdapter(view: AutoCompleteTextView, optionsSourceId: Int) {
        val options = resources.getStringArray(optionsSourceId)
        val adapter = ArrayAdapter(this, R.layout.dropdown_item, options)
        view.setAdapter(adapter)
    }

    private fun onSaveSuccess() {
        Toast.makeText(this, "Perfil atualizado com sucesso", Toast.LENGTH_SHORT)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.initialize(this)
        viewModel.fetchData()

        binding = DataBindingUtil.inflate(layoutInflater, R.layout.profile, super.getRootForInflate(), true)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        bindAdapter(binding.stateDisplay, R.array.estados)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.saveSuccess.collect {
                        onSaveSuccess()
                    }
                }
                launch {
                    viewModel.state.collect {
                        val citiesArrayId = CitiesByState.getResourceArrayId(it) ?: return@collect
                        bindAdapter(binding.cityDisplay, citiesArrayId)
                    }
                }
            }
        }
    }
}