package com.example.rabbitapp.ui.sicknesses

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.rabbitapp.R
import com.example.rabbitapp.databinding.FragmentSicknessBinding
import com.example.rabbitapp.model.entities.Sickness
import com.example.rabbitapp.ui.mainTab.MainListViewModel

class SicknessFragment : Fragment() {

    private val args: SicknessFragmentArgs by navArgs()
    private var sickness: Sickness? = null

    private var _binding: FragmentSicknessBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainListViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSicknessBinding.inflate(inflater, container, false)
        sickness = viewModel.getSickness(args.sicknessId)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fragmentSicknessSicknessName.text = sickness?.name
        binding.fragmentSicknessEditSicknessTreatment.text = sickness?.treatment
        binding.fragmentSicknessEditSicknessSymptoms.text = sickness?.symptoms

        binding.sicknessDetailsEditButton.setOnClickListener {
            view.findNavController()
                .navigate(
                    SicknessFragmentDirections.actionNavigationDetailsSicknessToEditSicknessFragment(
                        sickness?.id ?: 0
                    )
                )
        }

        binding.sicknessDetailsDeleteButton.setOnClickListener {
            val alertDialog = requireActivity().let {
                val builder = AlertDialog.Builder(it)
                builder.apply {
                    setPositiveButton(R.string.ok) { dialog, _ ->
                        dialog.dismiss()
                        viewModel.deleteSickness(sickness!!.id)
                        view.findNavController()
                            .navigate(R.id.action_navigation_details_sickness_to_navigation_sicknesses)
                    }
                    setNegativeButton(R.string.cancel) { dialog, _ ->
                        dialog.dismiss()
                    }
                    setTitle(R.string.alert)
                    setMessage(R.string.confirm_delete_sickness_message)
                }
                builder.create()
            }
            alertDialog.show()
        }
    }

}