package com.example.rabbitapp.ui.vaccines

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.rabbitapp.R
import com.example.rabbitapp.databinding.FragmentVaccineListBinding
import com.example.rabbitapp.model.entities.Vaccine
import com.example.rabbitapp.utils.MainListViewModel

class VaccineListFragment : Fragment() {

    private val args: VaccineListFragmentArgs by navArgs()

    private var _binding: FragmentVaccineListBinding? = null
    private val viewModel: MainListViewModel by activityViewModels()
    private val binding get() = _binding!!
    private lateinit var vaccineListAdapter: VaccineListAdapter
    private val onSelectedItem = object : OnSelectedVaccine {
        override fun onItemClick(item: Vaccine) {
            if (args.rabbitId == 0L && args.litterId == 0L) {
                view?.findNavController()
                    ?.navigate(
                        VaccineListFragmentDirections.actionNavigationVaccineListToVaccineDetailsFragment(
                            item.id
                        )
                    )
            } else {
                if (!viewModel.getEditable()) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.non_editable), Toast.LENGTH_SHORT
                    ).show()
                    return
                }
                view?.findNavController()
                    ?.navigate(
                        VaccineListFragmentDirections.actionNavigationVaccineListToVaccinateFragment(
                            args.rabbitId,
                            args.litterId,
                            item.id
                        )
                    )
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVaccineListBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vaccineListAdapter = VaccineListAdapter(viewModel.getAllVaccines(), onSelectedItem)
        binding.fragmentListRecyclerView.adapter = vaccineListAdapter

        binding.addNewVaccineButton.setOnClickListener {
            if (!viewModel.getEditable()) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.non_editable), Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            view.findNavController()
                .navigate(R.id.action_navigation_vaccine_list_to_vaccineEditFragment)
        }

        binding.filterFrame.setOnClickListener {
            if (binding.categoriesVaccine.visibility == View.VISIBLE) {
                binding.categoriesVaccine.visibility = View.GONE
                binding.filterButton.contentDescription = getString(R.string.filter)
                binding.filterButton.setImageResource(R.drawable.icon_filter)
            } else {
                binding.categoriesVaccine.visibility = View.VISIBLE
                binding.filterButton.contentDescription = getString(R.string.close)
                binding.filterButton.setImageResource(R.drawable.icon_close)
            }
        }

        sort()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun sort() {
        binding.nameChip.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                vaccineListAdapter.updateData(
                    vaccineListAdapter.getData().sortedBy { it.name })
            }
        }
    }
}