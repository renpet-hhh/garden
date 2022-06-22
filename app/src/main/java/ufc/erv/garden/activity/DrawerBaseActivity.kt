package ufc.erv.garden.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import ufc.erv.garden.R
import ufc.erv.garden.databinding.DrawerBaseBinding
import ufc.erv.garden.viewModel.DrawerBaseModel

open class DrawerBaseActivity : AppCompatActivity() {
    private val vTAG = "DrawerBaseActivity"
    private lateinit var binding: DrawerBaseBinding
    private val viewModel: DrawerBaseModel by viewModels()

    private fun openDrawer() {
        binding.mainDrawerLayout.openDrawer(GravityCompat.START)
    }
    private fun closeDrawer() {
        binding.mainDrawerLayout.closeDrawer(GravityCompat.START)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.drawer_base)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.mainNavDrawer.setNavigationItemSelectedListener {
            viewModel.hideMenu()
            when (it.itemId) {
                R.id.menu_item_register_plant -> {
                    val intent = Intent(this@DrawerBaseActivity.baseContext, RegisterPlantActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                    }
                    startActivity(intent)
                }
                R.id.menu_item_my_plants -> {
                    val intent = Intent(this@DrawerBaseActivity.baseContext, MyPlantsActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                    }
                    startActivity(intent)
                }
            }
            return@setNavigationItemSelectedListener true
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.visible.collect {
                    Log.d(vTAG, "visible: $it")
                    if (it) openDrawer() else closeDrawer()
                }
            }
        }
    }

    fun getRootForInflate(): FrameLayout {
        return binding.contentFrame
    }


}