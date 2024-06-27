package com.example.rabbitapp.ui.vaccines

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.rabbitapp.databinding.FragmentVaccineEditBinding
import com.example.rabbitapp.model.entities.Vaccine
import com.example.rabbitapp.ui.mainTab.MainListViewModel

class VaccineEditFragment : Fragment() {

    private val args: VaccineEditFragmentArgs by navArgs()
    private var vaccine: Vaccine? = null

    private var _binding: FragmentVaccineEditBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainListViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVaccineEditBinding.inflate(inflater, container, false)
        vaccine = viewModel.getVaccine(args.vaccineId)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vaccine?.let {
            binding.fragmentVaccineEditVaccineName.setText(it.name)
            binding.fragmentVaccineEditVaccineDescription.setText(it.description)
        }

        binding.addVaccineSaveButton.setOnClickListener {
            val savedId = viewModel.save(
                Vaccine(
                    vaccine?.id ?: 0,
                    binding.fragmentVaccineEditVaccineName.text.toString(),
                    binding.fragmentVaccineEditVaccineDescription.text.toString()
                )
            )
            view.findNavController()
                .navigate(
                    VaccineEditFragmentDirections.actionNavigationVaccineEditToVaccineDetailsFragment(
                        savedId
                    )
                )
        }
    }

}