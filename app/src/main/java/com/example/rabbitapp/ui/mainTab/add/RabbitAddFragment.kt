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
import androidx.navigation.fragment.navArgs
import com.example.rabbitapp.R
import com.example.rabbitapp.databinding.FragmentAddRabbitBinding
import com.example.rabbitapp.model.entities.Litter
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
    private val args: RabbitAddFragmentArgs by navArgs()
    private var rabbit: Rabbit? = null
    private var litter: Litter? = null
    private val dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
    private val parentSelectService: ParentSelectService = ParentSelectService()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddRabbitBinding.inflate(inflater, container, false)
        if (args.rabbitId != 0L) {
            rabbit = viewModel.getRabbitFromId(args.rabbitId)
            litter = rabbit!!.fkLitter?.let { viewModel.getLitterFromId(it) }
        }
        if (args.litterId != 0L) {
            litter = viewModel.getLitterFromId(args.litterId)
            rabbit?.fkLitter = args.litterId
        }
        setGalleryLauncher(binding.addRabbitPicture)

        val formattedDate = LocalDate.now().format(dateFormatter)
        binding.addRabbitDate.text = Editable.Factory.getInstance().newEditable(formattedDate)

        if (rabbit != null) {
            parentSelectService.setParents(
                viewModel,
                rabbit!!.fkMother,
                rabbit!!.fkFather
            )
            setFieldsToSelectedRabbit()
            (activity as AppCompatActivity).supportActionBar?.title =
                resources.getString(R.string.edit_rabbit)
        }
        if (litter != null) {
            parentSelectService.setParents(
                viewModel,
                litter!!.fkMother,
                litter!!.fkFather
            )
            setLitterItem()
        }

        setPictureToSelectedOrDefault(
            binding.addRabbitPicture,
            rabbit,
            R.drawable.rabbit_back
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addRabbitSaveButton.setOnClickListener(saveRabbit())
        parentSelectService.displaySelectParentFragment(
            RabbitAddFragmentDirections.actionAddRabbitFragmentToPickMotherListFragment(
                rabbit?.id ?: 0L
            ),
            RabbitAddFragmentDirections.actionAddRabbitFragmentToPickFatherListFragment(
                rabbit?.id ?: 0L
            ),
            childFragmentManager,
            viewModel, view
        )
        if (rabbit?.fkLitter == null && litter == null) {
            parentSelectService.setOnClickListenersParents(
                RabbitAddFragmentDirections.actionAddRabbitFragmentToPickMotherListFragment(
                    rabbit?.id ?: 0L
                ),
                RabbitAddFragmentDirections.actionAddRabbitFragmentToPickFatherListFragment(
                    rabbit?.id ?: 0L
                ),
                childFragmentManager,
                view,
                binding.fragmentAddRabbitIncludeParents,
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
            LocalDate.ofEpochDay(rabbit!!.birth).format(dateFormatter)
        )
        binding.addRabbitName.setText(rabbit!!.name)
        binding.addRabbitNumbers.setText(rabbit!!.earNumber)
        if (rabbit!!.deathDate != null) {
            binding.addRabbitDeathDateRow.visibility = View.VISIBLE
            binding.addRabbitCageNumbersRow.visibility = View.GONE
            binding.addRabbitDeathDate.text = Editable.Factory.getInstance().newEditable(
                rabbit!!.deathDate?.let {
                    LocalDate.ofEpochDay(it).format(dateFormatter)
                }
            )
        }
        binding.addRabbitDeathSwitch.isChecked = rabbit!!.deathDate != null
        binding.addRabbitCageNumbers.setText(rabbit!!.cageNumber.toString())
        if (rabbit!!.sex == Gender.FEMALE.name) {
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
            val path = saveNewPicture(rabbit, binding.addRabbitPicture)
            val imageList = rabbit?.imagePath?.toMutableList() ?: mutableListOf()
            if (path != null) {
                imageList.add(path)
            }
            val rabbitId: Long = viewModel.save(
                Rabbit(
                    rabbit?.id ?: 0,
                    binding.addRabbitName.text.toString(),
                    LocalDate.parse(binding.addRabbitDate.text.toString(), dateFormatter)
                        .toEpochDay(),
                    getRabbitGender(),
                    binding.addRabbitNumbers.text.toString(),
                    if (binding.addRabbitCageNumbers.text.toString() != "" && binding.addRabbitCageNumbers.text.toString()
                            .isDigitsOnly()
                    ) {
                        binding.addRabbitCageNumbers.text.toString().toInt()
                    } else {
                        null
                    },
                    imageList,
                    viewModel.selectedMother?.id, viewModel.selectedFather?.id,
                    litter?.id,
                    if (binding.addRabbitDeathSwitch.isChecked) {
                        LocalDate.parse(binding.addRabbitDeathDate.text.toString(), dateFormatter)
                            .toEpochDay()
                    } else {
                        null
                    }
                )
            )
            if (litter != null) {
                viewModel.getAllVaccinationsForLitter(litter!!.id).forEach {
                    viewModel.save(it.copy(id = 0, fkRabbit = rabbitId, fkLitter = null))
                }
                viewModel.getAllSicknessesForLitter(litter!!.id).forEach {
                    viewModel.save(it.copy(id = 0, fkRabbit = rabbitId, fkLitter = null))
                }
            }
            rabbit = rabbitId.let { viewModel.getRabbitFromId(it) }
            view.findNavController()
                .navigate(
                    RabbitAddFragmentDirections.actionAddRabbitFragmentToRabbitDetailsFragment(
                        rabbit!!.id
                    )
                )
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
            litter?.let { RabbitDetails.getAge(it.birth) }
        binding.fragmentAddRabbitBelongTo.fragmentBelongsToLitterItem.homeListItemName.text =
            litter?.name
        setPictureToSelectedOrDefault(
            binding.fragmentAddRabbitBelongTo.fragmentBelongsToLitterItem.homeListItemPicture,
            litter,
            R.drawable.rabbit_2_back
        )
        binding.addRabbitDate.text = Editable.Factory.getInstance().newEditable(
            LocalDate.ofEpochDay(litter!!.birth).format(dateFormatter)
        )
        binding.addRabbitDate.setOnClickListener {
            Toast.makeText(
                requireContext(),
                getString(R.string.date_change_illegal), Toast.LENGTH_SHORT
            ).show()
        }
        Log.d("RabbitFragment", "Litter added $litter")
    }

}
