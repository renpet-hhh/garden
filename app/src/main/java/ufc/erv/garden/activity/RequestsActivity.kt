package ufc.erv.garden.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.preference.PreferenceManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch
import ufc.erv.garden.R
import ufc.erv.garden.adapter.PlantListAdapter
import ufc.erv.garden.databinding.RequestsBinding
import ufc.erv.garden.fragment.RequestsFragment
import ufc.erv.garden.viewModel.SelectedPlantModel

class RequestsActivity: DrawerBaseActivity() {
    private lateinit var binding: RequestsBinding
    private val tabTitles = listOf("Enviados", "Recebidos")

    private val selectedPlantModel: SelectedPlantModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.inflate(layoutInflater, R.layout.requests, getRootForInflate(), true)
        binding.requestsPage.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount() = 2
            override fun createFragment(position: Int): Fragment {
                val fragment = RequestsFragment()
                fragment.arguments = Bundle().apply {
                    putBoolean("sent", position == 1)
                }
                return fragment
            }
        }
        binding.lifecycleOwner = this

        TabLayoutMediator(binding.requestTabLayout, binding.requestsPage)
        { tab, position ->
            tab.text = tabTitles[position]
        }.attach()

        binding.requestTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                selectedPlantModel.deselect()
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                selectedPlantModel.deselect()
            }
        })
    }
}