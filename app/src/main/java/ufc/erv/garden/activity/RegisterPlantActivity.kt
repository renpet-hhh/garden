package ufc.erv.garden.activity

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import ufc.erv.garden.R
import ufc.erv.garden.databinding.RegisterPlantBinding
import ufc.erv.garden.viewModel.RegisterPlantModel

class RegisterPlantActivity : AppCompatActivity() {
    private val vTAG = "RegisterPlantActivity" /* Logger TAG */
    private lateinit var binding: RegisterPlantBinding
    private val viewModel: RegisterPlantModel by viewModels()

    private object MESSAGE {
        const val SUCCESSFUL_REGISTER = "Planta cadastrada com sucesso [MOCK]"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.register_plant)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        /* Listeners */
        val editableFields: List<TextInputEditText> = listOf(binding.popEdit, binding.sciEdit, binding.localEdit, binding.descEdit)
        editableFields.forEach {
            it.addTextChangedListener {
                viewModel.clearError()
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.plant.collect {
                        Log.d(vTAG, "plant: $it")
                        if (it == null) return@collect
                        Toast.makeText(applicationContext, MESSAGE.SUCCESSFUL_REGISTER, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}