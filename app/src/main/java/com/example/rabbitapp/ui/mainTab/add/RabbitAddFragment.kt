package com.example.rabbitapp.ui.mainTab.add

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.isDigitsOnly
import androidx.navigation.findNavController
import com.example.rabbitapp.R
import com.example.rabbitapp.databinding.FragmentAddRabbitBinding
import com.example.rabbitapp.model.entities.Rabbit
import com.example.rabbitapp.ui.mainTab.parent.ParentSelectService
import com.example.rabbitapp.utils.Gender
import com.example.rabbitapp.utils.RabbitDetails
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

class RabbitAddFragment : FragmentWithPicture() {
    private var _binding: FragmentAddRabbitBinding? = null
    private val binding get() = _binding!!

    private val dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
    private val parentSelectService: ParentSelectService = ParentSelectService()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddRabbitBinding.inflate(inflater, container, false)

        setGalleryLauncher(binding.addRabbitPicture)

        val formattedDate = LocalDate.now().format(dateFormatter)
        binding.addRabbitDate.text = Editable.Factory.getInstance().newEditable(formattedDate)

        if (viewModel.selectedRabbit != null) {
            parentSelectService.setParents(
                viewModel,
                viewModel.selectedRabbit!!.fkMother,
                viewModel.selectedRabbit!!.fkFather
            )
            setFieldsToSelectedRabbit()
            (activity as AppCompatActivity).supportActionBar?.title =
                resources.getString(R.string.edit_rabbit)
        }
        if (viewModel.selectedLitter != null) {
            parentSelectService.setParents(
                viewModel,
                viewModel.selectedLitter!!.fkMother,
                viewModel.selectedLitter!!.fkFather
            )
            setLitterItem()
        }

        setPictureToSelectedOrDefault(
            binding.addRabbitPicture,
            viewModel.selectedRabbit,
            R.drawable.rabbit_back
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addRabbitSaveButton.setOnClickListener(saveRabbit())
        parentSelectService.displaySelectParentFragment(
            R.id.action_addRabbitFragment_to_pickMotherListFragment,
            R.id.action_addRabbitFragment_to_pickFatherListFragment,
            childFragmentManager,
            viewModel, view
        )
        if (viewModel.selectedLitter == null) {
            parentSelectService.setOnClickListenersParents(
                childFragmentManager,
                view,
                binding.fragmentAddRabbitIncludeParents,
                R.id.action_addRabbitFragment_to_pickMotherListFragment,
                R.id.action_addRabbitFragment_to_pickFatherListFragment
            )

            binding.addRabbitDate.setOnClickListener {
                showDatePickerDialog()
            }
        } else {
            parentSelectService.setChangeIllegalMessage(
                requireContext(),
                binding.fragmentAddRabbitIncludeParents,
                getString(R.string.illegal_parent_change)
            )
        }

        binding.addRabbitDeathSwitch.setOnCheckedChangeListener { _, isChecked ->
            run {
                if (isChecked) {
                    binding.addRabbitDeathDateRow.visibility = View.VISIBLE
                    binding.addRabbitCageNumbersRow.visibility = View.GONE
                } else {
                    binding.addRabbitDeathDateRow.visibility = View.GONE
                    binding.addRabbitCageNumbersRow.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog =
            DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = LocalDate.of(selectedYear, selectedMonth + 1, selectedDay)
                binding.addRabbitDate.text =
                    Editable.Factory.getInstance().newEditable(selectedDate.format(dateFormatter))
            }, year, month, day)

        datePickerDialog.show()
    }

    private fun setFieldsToSelectedRabbit() {
        binding.addRabbitDate.text = Editable.Factory.getInstance().newEditable(
            LocalDate.ofEpochDay(viewModel.selectedRabbit!!.birth).format(dateFormatter)
        )
        binding.addRabbitName.setText(viewModel.selectedRabbit!!.name)
        binding.addRabbitNumbers.setText(viewModel.selectedRabbit!!.earNumber)
        if (viewModel.selectedRabbit!!.deathDate != null) {
            binding.addRabbitDeathDate.text = Editable.Factory.getInstance().newEditable(
                viewModel.selectedRabbit!!.deathDate?.let {
                    LocalDate.ofEpochDay(it).format(dateFormatter)
                }
            )
        }
        binding.addRabbitDeathSwitch.isChecked = viewModel.selectedRabbit!!.deathDate != null
        binding.addRabbitCageNumbers.setText(viewModel.selectedRabbit!!.cageNumber.toString())
        if (viewModel.selectedRabbit!!.sex == Gender.FEMALE.name) {
            binding.addRabbitGenderFemale.isChecked = true
        } else {
            binding.addRabbitGenderMale.isChecked = true
        }
    }

    private fun saveRabbit(): View.OnClickListener {
        return View.OnClickListener { view ->
            if (!validateFields()) {
                return@OnClickListener
            }
            if (!viewModel.getEditable()) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.non_editable), Toast.LENGTH_SHORT
                ).show()
                return@OnClickListener
            }
            val path = saveNewPicture(viewModel.selectedRabbit, binding.addRabbitPicture)
            val rabbitId: Long = viewModel.save(
                Rabbit(
                    viewModel.selectedRabbit?.id ?: 0,
                    binding.addRabbitName.text.toString(),
                    LocalDate.parse(binding.addRabbitDate.text.toString(), dateFormatter)
                        .toEpochDay(),
                    getRabbitGender(),
                    binding.addRabbitNumbers.text.toString(),
                    if (binding.addRabbitCageNumbers.text.toString().isDigitsOnly()) {
                        binding.addRabbitCageNumbers.text.toString().toInt()
                    } else {
                        null
                    },
                    path,
                    viewModel.selectedMother?.id, viewModel.selectedFather?.id,
                    viewModel.selectedLitter?.id,
                    if (binding.addRabbitDeathSwitch.isChecked) {
                        LocalDate.parse(binding.addRabbitDeathDate.text.toString(), dateFormatter)
                            .toEpochDay()
                    } else {
                        null
                    }
                )
            )
            if (viewModel.selectedLitter != null) {
                viewModel.getAllVaccinationsForLitter(viewModel.selectedLitter!!.id).forEach {
                    viewModel.save(it.copy(id = 0, fkRabbit = rabbitId, fkLitter = null))
                }
                viewModel.getAllSicknessesForLitter(viewModel.selectedLitter!!.id).forEach {
                    viewModel.save(it.copy(id = 0, fkRabbit = rabbitId, fkLitter = null))
                }
            }
            viewModel.selectedRabbit = rabbitId.let { viewModel.getRabbitFromId(it) }
            view.findNavController()
                .navigate(R.id.action_addRabbitFragment_to_rabbitDetailsFragment)
        }
    }

    private fun validateFields(): Boolean {
        var correct = true
        if (binding.addRabbitName.text.isEmpty()) {
            binding.addRabbitName.error = getString(R.string.error_empty)
            correct = false
        }
        if (binding.addRabbitDate.text.isEmpty()) {
            binding.addRabbitDate.error = getString(R.string.error_empty)
            correct = false
        }
        if (!binding.addRabbitGenderFemale.isChecked && !binding.addRabbitGenderMale.isChecked) {
            binding.addRabbitGenderError.visibility = View.VISIBLE
            correct = false
        } else {
            binding.addRabbitGenderError.visibility = View.GONE
        }
        return correct
    }

    private fun getRabbitGender(): String {
        return if (binding.addRabbitGenderFemale.isChecked) {
            Gender.FEMALE.name
        } else {
            Gender.MALE.name
        }
    }

    private fun setLitterItem() {
        binding.fragmentAddRabbitBelongTo.root.visibility = View.VISIBLE
        binding.fragmentAddRabbitBelongTo.fragmentBelongsToLitterItem.homeListItemAge.text =
            viewModel.selectedLitter?.let { RabbitDetails.getAge(it.birth) }
        binding.fragmentAddRabbitBelongTo.fragmentBelongsToLitterItem.homeListItemName.text =
            viewModel.selectedLitter?.name
        setPictureToSelectedOrDefault(
            binding.fragmentAddRabbitBelongTo.fragmentBelongsToLitterItem.homeListItemPicture,
            viewModel.selectedLitter,
            R.drawable.rabbit_2_back
        )
        binding.addRabbitDate.text = Editable.Factory.getInstance().newEditable(
            LocalDate.ofEpochDay(viewModel.selectedLitter!!.birth).format(dateFormatter)
        )
        binding.addRabbitDate.setOnClickListener {
            Toast.makeText(
                requireContext(),
                getString(R.string.date_change_illegal), Toast.LENGTH_SHORT
            ).show()
        }
        Log.d("RabbitFragment", "Litter added ${viewModel.selectedLitter}")
    }

}
