package com.example.rabbitapp.ui.sicknesses

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.rabbitapp.R
import com.example.rabbitapp.databinding.FragmentAddSicknessBinding
import com.example.rabbitapp.model.entities.HomeListItem
import com.example.rabbitapp.model.entities.Litter
import com.example.rabbitapp.model.entities.Rabbit
import com.example.rabbitapp.model.entities.Sickness
import com.example.rabbitapp.model.entities.relations.Sick
import com.example.rabbitapp.ui.mainTab.add.FragmentWithPicture
import com.example.rabbitapp.utils.RabbitDetails
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

class AddSicknessFragment : FragmentWithPicture() {

    private val args: AddSicknessFragmentArgs by navArgs()

    private var sick: Sick? = null
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
            Log.d("AddSicknessFragment", "rabbit: $item")
        } else {
            item = viewModel.getLitterFromId(args.litterId)
            Log.d("AddSicknessFragment", "litter: $item")
        }
        sickness = viewModel.getSickness(args.sicknessId)
        if (args.sickId != 0L) {
            sick = viewModel.getSick(args.sickId)
            Log.d("AddSicknessFragment", "sick: $sick")
            sickness = sick?.let { viewModel.getSickness(it.fkSickness) }
            item = if (sick!!.fkLitter != null) {
                sick!!.fkLitter?.let { viewModel.getLitterFromId(it) }
            } else {
                sick!!.fkRabbit?.let { viewModel.getRabbitFromId(it) }

            }
            Log.d("AddSicknessFragment", "item: $item")

        }
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

        binding.fragmentAddSicknessRabbit.root.setOnClickListener {
            Toast.makeText(
                context,
                getString(R.string.cannot_change_remove_and_add_again), Toast.LENGTH_SHORT
            ).show()
        }
        binding.addSicknessSicknessDetails.setOnClickListener {
            Toast.makeText(
                context,
                getString(R.string.cannot_change_remove_and_add_again), Toast.LENGTH_SHORT
            ).show()
        }
        binding.fragmentAddSicknessSicknessName.text = sickness!!.name
        binding.fragmentAddSicknessSicknessTreatment.text = sickness!!.treatment
        binding.fragmentAddSicknessSicknessSymptoms.text = sickness!!.symptoms

        binding.addSicknessSaveButton.setOnClickListener {
            if (!viewModel.getEditable()) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.non_editable), Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            viewModel.save(
                Sick(
                    sick?.id ?: 0L,
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
                    if (item is Rabbit) {
                        item!!.id
                    } else {
                        null
                    },
                    if (item is Litter) {
                        item!!.id
                    } else {
                        null
                    },
                    sickness!!.id
                )
            )
            if (item is Rabbit) {
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