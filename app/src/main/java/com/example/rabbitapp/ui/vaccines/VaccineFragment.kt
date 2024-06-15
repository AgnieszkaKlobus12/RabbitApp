package com.example.rabbitapp.ui.vaccines

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.example.rabbitapp.databinding.FragmentVaccineBinding
import com.example.rabbitapp.model.entities.Vaccine
import com.example.rabbitapp.ui.mainTab.MainListViewModel

class VaccineFragment : Fragment() {

    private val args: VaccineFragmentArgs by navArgs()
    private var vaccine: Vaccine? = null

    private var _binding: FragmentVaccineBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainListViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVaccineBinding.inflate(inflater, container, false)
        vaccine = viewModel.getVaccine(args.vaccineId)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.fragmentVaccineVaccineName.text = vaccine?.name
        binding.fragmentVaccineVaccineDescription.text = vaccine?.description
    }

}