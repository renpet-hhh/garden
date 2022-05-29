package ufc.erv.garden.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import ufc.erv.garden.R
import ufc.erv.garden.databinding.PlantInfoFragBinding
import ufc.erv.garden.viewModel.SelectedPlantModel

class PlantInfoFragment : Fragment(R.layout.plant_info_frag) {
    private val selectedPlantModel: SelectedPlantModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: PlantInfoFragBinding = DataBindingUtil.inflate(inflater, R.layout.plant_info_frag, container, false)
        binding.viewModel = selectedPlantModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }



}