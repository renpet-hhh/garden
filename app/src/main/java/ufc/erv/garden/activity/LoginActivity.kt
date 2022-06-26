package ufc.erv.garden.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.preference.PreferenceManager
import kotlinx.coroutines.launch
import ufc.erv.garden.R
import ufc.erv.garden.data.AuthInfo
import ufc.erv.garden.data.writeAuthInfo
import ufc.erv.garden.databinding.LoginBinding
import ufc.erv.garden.viewModel.LoginModel

class LoginActivity : AppCompatActivity() {
    private val vTAG = "LoginActivity" /* Logger TAG */
    private lateinit var binding: LoginBinding
    private val viewModel: LoginModel by viewModels()

    private fun syncModel() {
        viewModel.initialize(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.initialize(this)

        binding = DataBindingUtil.setContentView(this, R.layout.login)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        val regActBtn = binding.regButton
        regActBtn.setOnClickListener {

            val intent = Intent(this, RegisterUserActivity::class.java)
            startActivity(intent)
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                syncModel()

                launch {
                    viewModel.cookie.collect {
                        if (it == null) return@collect

                        AuthInfo(
                            viewModel.usernameField.value, it
                        ).writeAuthInfo(this@LoginActivity)

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