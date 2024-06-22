package com.example.rabbitapp.ui.vaccines

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.rabbitapp.R
import com.example.rabbitapp.databinding.FragmentVaccineListBinding
import com.example.rabbitapp.model.entities.Vaccine
import com.example.rabbitapp.ui.mainTab.MainListViewModel

class VaccineListFragment : Fragment() {

    private val args: VaccineListFragmentArgs by navArgs()

    private var _binding: FragmentVaccineListBinding? = null
    private val viewModel: MainListViewModel by activityViewModels()
    private val binding get() = _binding!!

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

        binding.fragmentListRecyclerView.adapter =
            VaccineListAdapter(viewModel.getAllVaccines(), object : OnSelectedVaccine {
                override fun onItemClick(item: Vaccine) {
                    if (args.rabbitId == 0L) {
                        view.findNavController()
                            .navigate(
                                VaccineListFragmentDirections.actionNavigationVaccineListToVaccineDetailsFragment(
                                    item.id
                                )
                            )
                    } else {
                        view.findNavController()
                            .navigate(
                                VaccineListFragmentDirections.actionNavigationVaccineListToVaccinateFragment(
                                    item.id,
                                    args.rabbitId
                                )
                            )
                    }
                }
            })

        binding.addNewVaccineButton.setOnClickListener {
            view.findNavController()
                .navigate(R.id.action_navigation_vaccine_list_to_vaccineEditFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}