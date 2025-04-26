package com.example.rabbitapp.ui.sicknesses

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.rabbitapp.R
import com.example.rabbitapp.databinding.FragmentSicknessEditBinding
import com.example.rabbitapp.model.entities.Sickness
import com.example.rabbitapp.utils.MainViewModel

class SicknessEditFragment : Fragment() {

    private val args: SicknessEditFragmentArgs by navArgs()
    private var sickness: Sickness? = null

    private var _binding: FragmentSicknessEditBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSicknessEditBinding.inflate(inflater, container, false)
        sickness = viewModel.getSickness(args.sicknessId)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).supportActionBar?.title =
            resources.getString(R.string.add_sickness)

        sickness?.let {
            binding.fragmentSicknessEditSicknessName.setText(it.name)
            binding.fragmentSicknessEditSicknessSymptoms.setText(it.symptoms)
            binding.fragmentSicknessEditSicknessTreatment.setText(it.treatment)
            (activity as AppCompatActivity).supportActionBar?.title =
                resources.getString(R.string.edit_sickness)
        }

        binding.addSicknessSaveButton.setOnClickListener {
            if (!validateFields()) {
                return@setOnClickListener
            }
            val savedId = viewModel.save(
                Sickness(
                    sickness?.id ?: 0,
                    binding.fragmentSicknessEditSicknessName.text.toString(),
                    binding.fragmentSicknessEditSicknessSymptoms.text.toString(),
                    binding.fragmentSicknessEditSicknessTreatment.text.toString()
                )
            )
            view.findNavController()
                .navigate(
                    SicknessEditFragmentDirections.navigationEditSicknessToSicknessDetailsFragment(
                        savedId
                    )
                )
        }
    }


    private fun validateFields(): Boolean {
        var correct = true
        if (binding.fragmentSicknessEditSicknessName.text.isEmpty()) {
            binding.fragmentSicknessEditSicknessName.error = getString(R.string.error_empty)
            correct = false
        }
        if (binding.fragmentSicknessEditSicknessSymptoms.text.isEmpty()) {
            binding.fragmentSicknessEditSicknessSymptoms.error = getString(R.string.error_empty)
            correct = false
        }
        if (binding.fragmentSicknessEditSicknessTreatment.text.isEmpty()) {
            binding.fragmentSicknessEditSicknessTreatment.error = getString(R.string.error_empty)
            correct = false
        }
        return correct
    }


}