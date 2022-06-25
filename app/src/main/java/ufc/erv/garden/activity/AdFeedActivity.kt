package ufc.erv.garden.activity

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.launch
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.preference.PreferenceManager
import kotlinx.coroutines.launch
import ufc.erv.garden.R
import ufc.erv.garden.adapter.PlantAdListAdapter
import ufc.erv.garden.contract.SelectPlantContract
import ufc.erv.garden.data.Plant
import ufc.erv.garden.data.PlantAd
import ufc.erv.garden.databinding.AdFeedBinding
import ufc.erv.garden.databinding.PlantInfoFragBinding
import ufc.erv.garden.viewModel.AdFeedModel
import ufc.erv.garden.viewModel.SelectedPlantModel

class AdFeedActivity : DrawerBaseActivity() {
    private val vTAG = "AdFeedActivity"

    private val viewModel: AdFeedModel by viewModels()
    private val selectedPlantModel: SelectedPlantModel by viewModels()
    private lateinit var binding: AdFeedBinding

    /* Launchers */
    private val getPlantForTrade = registerForActivityResult(SelectPlantContract()) { plant ->
        viewModel.selectTradePlant(plant)
        Log.d(vTAG, "tradePlant: $plant")
    }

    private fun onPlantImageClick(@Suppress("UNUSED_PARAMETER") view: View, ad: PlantAd) {
        val infoBinding: PlantInfoFragBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.plant_info_frag, null, false)
        selectedPlantModel.plant.value = ad.plant
        infoBinding.viewModel = selectedPlantModel

        AlertDialog.Builder(this)
            .setTitle("Informações")
            .setView(infoBinding.root)
            .setNegativeButton("Dispensar") { dialog, _ ->
                dialog.cancel()
            }.show()
    }

    private fun onTradeClick(@Suppress("UNUSED_PARAMETER") view: View, ad: PlantAd) {
        viewModel.deselect()
        viewModel.selectAd(ad)
        getPlantForTrade.launch()
    }

    private fun onBuyClick(@Suppress("UNUSED_PARAMETER") view: View, ad: PlantAd) {
        viewModel.deselect()
        viewModel.selectAd(ad)
        confirmBuyRequest(ad)
    }

    private fun refresh() {
        viewModel.refresh()
    }

    private fun confirmTradeRequest(ad: PlantAd, tradePlant: Plant) {
        AlertDialog.Builder(this@AdFeedActivity)
            .setTitle("Confirmação de proposta")
            .setMessage("Deseja realmente realizar uma proposta de troca?\n" +
                    "Plantas trocadas: ${ad.plant.popularName} & ${tradePlant.popularName}")
            .setPositiveButton("Confirmar") { _, _ ->
                handleTradeRequest(ad, tradePlant)
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.cancel()
            }
            .show()
    }

    /** Lida com uma solicitação de troca */
    private fun handleTradeRequest(
        @Suppress("UNUSED_PARAMETER") ad: PlantAd,
        @Suppress("UNUSED_PARAMETER") tradePlant: Plant
    ) {
        Toast.makeText(this@AdFeedActivity, "Proposta de troca realizada!", Toast.LENGTH_SHORT)
            .show()
    }

    private fun confirmBuyRequest(ad: PlantAd) {
        AlertDialog.Builder(this)
            .setTitle("Confirmação de proposta")
            .setMessage("Deseja realmente realizar uma proposta de compra?")
            .setPositiveButton("Confirmar") { _, _ ->
                handleBuyRequest(ad)
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.cancel()
            }
            .show()
    }

    /** Realiza a proposta de compra */
    private fun handleBuyRequest(@Suppress("UNUSED_PARAMETER") ad: PlantAd) {
        Toast.makeText(this@AdFeedActivity, "Proposta de compra realizada!", Toast.LENGTH_SHORT)
            .show()
    }

    private val prefListener = SharedPreferences.OnSharedPreferenceChangeListener { pref, key ->
        /* Sincroniza a configuração */
        if (key == "server") {
            viewModel.server = pref.getString("server", "mock") ?: "mock"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val server = PreferenceManager.getDefaultSharedPreferences(this).getString("server", "mock")
            ?: "mock"
        val auth = applicationContext.getSharedPreferences(
            resources.getString(R.string.auth_shared_preferences),
            MODE_PRIVATE
        )
        val username = auth.getString("username", "mock-user") ?: "mock-user"
        val cookie = auth.getString("cookie", "mock-cookie") ?: "mock-cookie"
        viewModel.initialize(server, username, cookie)

        binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.ad_feed,
            super.getRootForInflate(),
            true
        )
        binding.viewModel = viewModel
        binding.adFeedList.adapter =
            PlantAdListAdapter(this::onPlantImageClick, this::onTradeClick, this::onBuyClick)
        binding.lifecycleOwner = this

        /* Listeners */
        PreferenceManager.getDefaultSharedPreferences(this)
            .registerOnSharedPreferenceChangeListener(prefListener)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                refresh()

                launch {
                    viewModel.tradePlant.collect { plant ->
                        Log.d(vTAG, "ad for trade: ${viewModel.selectedAd.value}")
                        if (plant == null) return@collect
                        /* planta de troca selecionada */
                        viewModel.selectedAd.value?.let { ad ->
                            viewModel.deselect()
                            /* há anúncio associado */
                            confirmTradeRequest(ad, plant)
                        }
                    }
                }
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(vTAG, "onDestroy")
        PreferenceManager.getDefaultSharedPreferences(this)
            .unregisterOnSharedPreferenceChangeListener(prefListener)
    }
}