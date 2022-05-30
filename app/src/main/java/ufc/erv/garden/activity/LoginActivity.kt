package ufc.erv.garden.activity

import android.content.Intent
import android.os.Bundle
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
import ufc.erv.garden.databinding.LoginBinding
import ufc.erv.garden.viewModel.LoginModel

class LoginActivity : AppCompatActivity() {
    private val vTAG = "LoginActivity" /* Logger TAG */
    private lateinit var binding: LoginBinding
    private val viewModel: LoginModel by viewModels()

    private object EXTRA {
        const val USERNAME = "ufc.erv.garden.USERNAME"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.login)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        /* Listeners */
        val editableFields: List<TextInputEditText> = listOf(binding.usernameEdit, binding.passwordEdit)
        editableFields.forEach {
            it.addTextChangedListener {
                viewModel.clearError()
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.authenticated.collect {
                        if (!it) return@collect
                        val intent = Intent(this@LoginActivity.baseContext, MyPlantsActivity::class.java).apply {
                            putExtra(EXTRA.USERNAME, viewModel.username.value)
                        }
                        startActivity(intent)
                    }
                }
            }
        }
    }
}