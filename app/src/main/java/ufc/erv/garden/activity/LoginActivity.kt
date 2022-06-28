package ufc.erv.garden.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import ufc.erv.garden.R
import ufc.erv.garden.databinding.LoginBinding
import ufc.erv.garden.singleton.Client
import ufc.erv.garden.viewModel.LoginModel

class LoginActivity : AppCompatActivity() {
    private val vTAG = "LoginActivity" /* Logger TAG */
    private lateinit var binding: LoginBinding
    private val viewModel: LoginModel by viewModels()

    private fun syncModel() {
        viewModel.initialize(this)
    }

    private fun openSettings() {
        val intent = Intent(
            this@LoginActivity.baseContext,
            SettingsActivity::class.java
        )
        intent.apply {
            putExtra(SettingsActivity.EXTRAS.LOGGED_IN, false)
        }
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.initialize(this)

        binding = DataBindingUtil.setContentView(this, R.layout.login)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        // Listeners
        binding.loginSettingsBttn.setOnClickListener { openSettings() }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                syncModel()

                launch {
                    viewModel.success.collect {
                        Client.fakeLogin("mock-user")
                        val intent = Intent(
                            this@LoginActivity.baseContext,
                            RegisterPlantActivity::class.java
                        )
                        startActivity(intent)
                    }
                }
            }
        }
    }
}