package ufc.erv.garden.fragment

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.preference.PreferenceManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import kotlinx.coroutines.launch
import ufc.erv.garden.R
import ufc.erv.garden.adapter.PlantListAdapter
import ufc.erv.garden.adapter.PlantRequestListAdapter
import ufc.erv.garden.data.PlantRequest
import ufc.erv.garden.databinding.RequestsFragmentBinding
import ufc.erv.garden.viewModel.RequestsFragModel
import ufc.erv.garden.viewModel.SelectedPlantModel

class RequestsFragment : Fragment() {
    // When requested, this adapter returns a ReceivedRequestsFragment,
    // representing an object in the collection.
    private lateinit var binding: RequestsFragmentBinding
    private val viewModel: RequestsFragModel by viewModels()
    private val selectedPlantModel: SelectedPlantModel by activityViewModels()

    private fun onRequestClick(request: PlantRequest) {
        selectedPlantModel.plant.value = request.plant
    }

    private fun syncModel() {
        this.context?.let { viewModel.initialize(it) }
    }
    private fun refresh() {
        viewModel.refresh()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        syncModel()

        val sent = arguments?.getBoolean("sent") ?: false
        viewModel.setType(sent)
        viewModel.refresh()

        binding = DataBindingUtil.inflate(inflater, R.layout.requests_fragment, container, false)
        binding.viewModel = viewModel
        binding.selectedPlant = selectedPlantModel
        binding.requestsList.adapter = PlantRequestListAdapter { _, request ->
            onRequestClick(request)
        }
        binding.lifecycleOwner = viewLifecycleOwner

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                syncModel()
                refresh()
            }
        }
        return binding.root
    }
}
