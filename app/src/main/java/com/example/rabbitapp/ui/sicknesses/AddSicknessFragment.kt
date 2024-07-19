package com.example.rabbitapp.ui.sicknesses

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.rabbitapp.R
import com.example.rabbitapp.databinding.FragmentAddSicknessBinding
import com.example.rabbitapp.model.entities.HomeListItem
import com.example.rabbitapp.model.entities.Sickness
import com.example.rabbitapp.model.entities.relations.Sick
import com.example.rabbitapp.ui.mainTab.add.FragmentWithPicture
import com.example.rabbitapp.utils.RabbitDetails
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

class AddSicknessFragment : FragmentWithPicture() {

    private val args: AddSicknessFragmentArgs by navArgs()

    private var item: HomeListItem? = null
    private var sickness: Sickness? = null
    private val dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
    private var _binding: FragmentAddSicknessBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddSicknessBinding.inflate(inflater, container, false)
        val root: View = binding.root

        if (args.rabbitId != 0L) {
            item = viewModel.getRabbitFromId(args.rabbitId)
            Log.d("VaccinateFragment", "rabbit: $item")
        } else {
            item = viewModel.getLitterFromId(args.litterId)
            Log.d("VaccinateFragment", "litter: $item")
        }
        sickness = viewModel.getSickness(args.sicknessId)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fragmentAddSicknessStartDate.text = Editable.Factory.getInstance().newEditable(
            LocalDate.now().format(dateFormatter)
        )
        binding.fragmentAddSicknessStartDate.setOnClickListener {
            showDatePickerDialog(binding.fragmentAddSicknessStartDate)
        }
        binding.fragmentAddSicknessEndDate.setOnClickListener {
            showDatePickerDialog(binding.fragmentAddSicknessEndDate)
        }

        binding.fragmentAddSicknessRabbit.homeListItemAge.text = RabbitDetails.getAge(item!!.birth)
        binding.fragmentAddSicknessRabbit.homeListItemName.text = item!!.name
        setPictureToSelectedOrDefault(
            binding.fragmentAddSicknessRabbit.homeListItemPicture,
            item,
            R.drawable.rabbit_back
        )

        binding.fragmentAddSicknessSicknessName.text = sickness!!.name
        binding.fragmentAddSicknessSicknessTreatment.text = sickness!!.treatment
        binding.fragmentAddSicknessSicknessSymptoms.text = sickness!!.symptoms

        binding.addSicknessSaveButton.setOnClickListener {
            viewModel.save(
                Sick(
                    0L,
                    LocalDate.parse(
                        binding.fragmentAddSicknessStartDate.text.toString(),
                        dateFormatter
                    )
                        .toEpochDay(),
                    if (binding.fragmentAddSicknessEndDate.text.toString() != getString(R.string.unknown)) {
                        LocalDate.parse(
                            binding.fragmentAddSicknessEndDate.text.toString(),
                            dateFormatter
                        ).toEpochDay()
                    } else {
                        null
                    },
                    binding.fragmentAddSicknessSicknessDescription.text.toString(),
                    args.rabbitId.takeIf { it != 0L },
                    args.litterId.takeIf { it != 0L },
                    sickness!!.id
                )
            )
            if (args.rabbitId != 0L) {
                view.findNavController()
                    .navigate(R.id.action_navigation_add_sickness_to_rabbitDetailsFragment)
            } else {
                view.findNavController()
                    .navigate(R.id.action_navigation_add_sickness_to_litterDetailsFragment)
            }
        }
    }

    private fun showDatePickerDialog(textView: TextView) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog =
            DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = LocalDate.of(selectedYear, selectedMonth + 1, selectedDay)
                textView.text =
                    Editable.Factory.getInstance().newEditable(selectedDate.format(dateFormatter))
            }, year, month, day)

        datePickerDialog.show()
    }

}