package ufc.erv.garden.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.preference.PreferenceManager
import kotlinx.coroutines.launch
import ufc.erv.garden.R
import ufc.erv.garden.databinding.LoginBinding
import ufc.erv.garden.viewModel.LoginModel

class LoginActivity : AppCompatActivity() {
    private val vTAG = "LoginActivity" /* Logger TAG */
    private lateinit var binding: LoginBinding
    private val viewModel: LoginModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val server = PreferenceManager.getDefaultSharedPreferences(this).getString("server", "mock") ?: "mock"
        viewModel.server = server

        binding = DataBindingUtil.setContentView(this, R.layout.login)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.cookie.collect {
                        if (it == null) return@collect

                        val auth = applicationContext.getSharedPreferences(resources.getString(R.string.auth_shared_preferences), MODE_PRIVATE).edit()
                        auth.putString("username", viewModel.username.value)
                        auth.putString("cookie", it)
                        auth.apply()

                        val intent = Intent(this@LoginActivity.baseContext, RegisterPlantActivity::class.java)
                        startActivity(intent)
                    }
                }
            }
        }
    }
}