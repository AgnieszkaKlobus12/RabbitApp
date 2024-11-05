package com.example.rabbitapp.ui.vaccines

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
import com.example.rabbitapp.databinding.FragmentVaccinateBinding
import com.example.rabbitapp.model.entities.HomeListItem
import com.example.rabbitapp.model.entities.Litter
import com.example.rabbitapp.model.entities.Rabbit
import com.example.rabbitapp.model.entities.Vaccine
import com.example.rabbitapp.model.entities.relations.Vaccinated
import com.example.rabbitapp.ui.mainTab.add.FragmentWithPicture
import com.example.rabbitapp.utils.RabbitDetails
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

class VaccinateFragment : FragmentWithPicture() {

    private val args: VaccinateFragmentArgs by navArgs()

    private var item: HomeListItem? = null
    private var vaccine: Vaccine? = null
    private var vaccinated: Vaccinated? = null
    private val dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
    private var _binding: FragmentVaccinateBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVaccinateBinding.inflate(inflater, container, false)
        val root: View = binding.root

        if (args.vaccinatedId != 0L) {
            vaccinated = viewModel.getVaccinatedFromId(args.vaccinatedId)
            if (vaccinated!!.fkRabbit != null) {
                item = viewModel.getRabbitFromId(vaccinated!!.fkRabbit!!)
                Log.d("VaccinateFragment", "rabbit: $item")
            } else {
                item = viewModel.getLitterFromId(vaccinated!!.fkLitter!!)
                Log.d("VaccinateFragment", "litter: $item")
            }
            vaccine = viewModel.getVaccine(vaccinated!!.fkVaccine)
        } else {
            if (args.rabbitId != 0L) {
                item = viewModel.getRabbitFromId(args.rabbitId)
                Log.d("VaccinateFragment", "rabbit: $item")
            } else {
                item = viewModel.getLitterFromId(args.litterId)
                Log.d("VaccinateFragment", "litter: $item")
            }
            vaccine = viewModel.getVaccine(args.vaccineId)
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fragmentVaccinateDate.text = Editable.Factory.getInstance().newEditable(
            LocalDate.now().format(dateFormatter)
        )

        if (vaccinated != null) {
            if (vaccinated!!.nextDoseDate != null) {
                binding.fragmentVaccinateDateNextDose.text =
                    Editable.Factory.getInstance().newEditable(
                        vaccinated!!.nextDoseDate?.let {
                            LocalDate.ofEpochDay(it).format(dateFormatter)
                        }
                    )
            }
            binding.fragmentVaccinateDate.text = Editable.Factory.getInstance().newEditable(
                LocalDate.ofEpochDay(vaccinated!!.date).format(dateFormatter)
            )
            binding.vaccinatedDoseDescription.text =
                Editable.Factory.getInstance().newEditable(vaccinated!!.dose)
            binding.fragmentVaccinateDoseNumber.text =
                Editable.Factory.getInstance().newEditable(vaccinated!!.doseNumber.toString())
        }

        binding.fragmentVaccinateDate.setOnClickListener {
            showDatePickerDialog(binding.fragmentVaccinateDate)
        }
        binding.fragmentVaccinateDateNextDose.setOnClickListener {
            showDatePickerDialog(binding.fragmentVaccinateDateNextDose)
        }
        binding.fragmentVaccinateRabbit.homeListItemAge.text = RabbitDetails.getAge(item!!.birth)
        binding.fragmentVaccinateRabbit.homeListItemName.text = item!!.name
        setPictureToSelectedOrDefault(
            binding.fragmentVaccinateRabbit.homeListItemPicture,
            item,
            R.drawable.rabbit_back
        )

        binding.fragmentVaccinateVaccineName.text = vaccine!!.name
        binding.fragmentVaccinateVaccineDescription.text = vaccine!!.description

        binding.vaccinateSaveButton.setOnClickListener {
            if (!viewModel.getEditable()) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.non_editable), Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            viewModel.save(
                Vaccinated(
                    vaccinated.takeIf { it != null }?.id ?: 0L,
                    LocalDate.parse(binding.fragmentVaccinateDate.text.toString(), dateFormatter)
                        .toEpochDay(),
                    if (binding.fragmentVaccinateDateNextDose.text.toString() != getString(R.string.unknown)) {
                        LocalDate.parse(
                            binding.fragmentVaccinateDateNextDose.text.toString(),
                            dateFormatter
                        ).toEpochDay()
                    } else {
                        null
                    },
                    binding.vaccinatedDoseDescription.text.toString(),
                    binding.fragmentVaccinateDoseNumber.text.toString().toInt(),
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
                    vaccine!!.id
                )
            )
            if (item is Rabbit) {
                view.findNavController()
                    .navigate(R.id.action_navigation_vaccinate_to_rabbitDetailsFragment)
            } else {
                view.findNavController()
                    .navigate(R.id.action_navigation_vaccinate_to_litterDetailsFragment)
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