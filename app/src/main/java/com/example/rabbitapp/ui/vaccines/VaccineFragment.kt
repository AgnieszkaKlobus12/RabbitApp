package com.example.rabbitapp.ui.vaccines

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fragmentVaccineVaccineName.text = vaccine?.name
        binding.fragmentVaccineVaccineDescription.text = vaccine?.description

        binding.vaccineDetailsEditButton.setOnClickListener {
            view.findNavController()
                .navigate(
                    VaccineFragmentDirections.actionNavigationVaccineDetailsToVaccineEditFragment(
                        vaccine?.id ?: 0
                    )
                )
        }

        binding.vaccineDetailsDeleteButton.setOnClickListener {
            val alertDialog = requireActivity().let {
                val builder = AlertDialog.Builder(it)
                builder.apply {
                    setPositiveButton(R.string.ok) { dialog, _ ->
                        dialog.dismiss()
                        viewModel.deleteVaccine(vaccine?.id)
                        view.findNavController()
                            .navigate(R.id.action_navigation_vaccine_details_to_vaccineListFragment)
                    }
                    setNegativeButton(R.string.cancel) { dialog, _ ->
                        dialog.dismiss()
                    }
                    setTitle(R.string.alert)
                    setMessage(R.string.confirm_delete_vaccine_message)
                }
                builder.create()
            }
            alertDialog.show()
        }
    }

}