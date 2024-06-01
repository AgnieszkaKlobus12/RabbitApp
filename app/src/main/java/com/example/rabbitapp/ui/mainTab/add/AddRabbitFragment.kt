package com.example.rabbitapp.ui.mainTab.add

import android.app.DatePickerDialog
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.findNavController
import com.example.rabbitapp.R
import com.example.rabbitapp.databinding.FragmentAddRabbitBinding
import com.example.rabbitapp.model.entities.Rabbit
import com.example.rabbitapp.ui.mainTab.HomeListItem
import com.example.rabbitapp.utils.Gender
import com.example.rabbitapp.utils.RabbitDetails
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

class AddRabbitFragment : AddFragment() {
    private var _binding: FragmentAddRabbitBinding? = null
    private val binding get() = _binding!!

    private val dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddRabbitBinding.inflate(inflater, container, false)

        val galleryLauncher = getGalleryLauncher(binding.addRabbitPicture)

        binding.addRabbitPicture.setOnClickListener {
            galleryLauncher.launch("image/*")
        }

        if (viewModel.selectedRabbit != null) {
            viewModel.selectedMother =
                viewModel.selectedRabbit?.fkMother?.let { viewModel.getRabbitFromId(it) }
            viewModel.selectedFather =
                viewModel.selectedRabbit?.fkFather?.let { viewModel.getRabbitFromId(it) }

            setFieldsToSelectedRabbit()
        }

        setPictureToSelectedOrDefault()

        val transaction: FragmentTransaction = childFragmentManager.beginTransaction()
        if (viewModel.selectedMother != null) {
            val selectedMotherFragment = HomeListItem(viewModel.selectedMother!!)
            transaction.replace(R.id.add_rabbit_mother_fragment, selectedMotherFragment)
        } else {
            val pickButtonFragment = PickButtonFragment(Gender.FEMALE, object : StartSelect {
                override fun select(gender: Gender) {
                    parentSelect(
                        gender,
                        R.id.action_addRabbitFragment_to_pickMotherListFragment,
                        R.id.action_addRabbitFragment_to_pickFatherListFragment
                    )
                }
            })
            transaction.replace(R.id.add_rabbit_mother_fragment, pickButtonFragment)
        }

        if (viewModel.selectedFather != null) {
            val selectedFatherFragment = HomeListItem(viewModel.selectedFather!!)
            transaction.replace(R.id.add_rabbit_father_fragment, selectedFatherFragment)
        } else {
            val pickButtonFragment = PickButtonFragment(Gender.MALE, object : StartSelect {
                override fun select(gender: Gender) {
                    parentSelect(
                        gender,
                        R.id.action_addRabbitFragment_to_pickMotherListFragment,
                        R.id.action_addRabbitFragment_to_pickFatherListFragment
                    )
                }
            })
            transaction.replace(R.id.add_rabbit_father_fragment, pickButtonFragment)
        }
        transaction.commit()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val formattedDate = LocalDate.now().format(dateFormatter)
        binding.addRabbitDate.text = Editable.Factory.getInstance().newEditable(formattedDate)
        binding.addRabbitSaveButton.setOnClickListener(saveRabbit())

        binding.addRabbitMotherFragment.setOnClickListener {
            parentSelect(
                Gender.FEMALE,
                R.id.action_addRabbitFragment_to_pickMotherListFragment,
                R.id.action_addRabbitFragment_to_pickFatherListFragment
            )
        }
        binding.addRabbitFatherFragment.setOnClickListener {
            parentSelect(
                Gender.MALE,
                R.id.action_addRabbitFragment_to_pickMotherListFragment,
                R.id.action_addRabbitFragment_to_pickFatherListFragment
            )
        }

        binding.addRabbitDate.setOnClickListener {
            showDatePickerDialog()
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = LocalDate.of(selectedYear, selectedMonth + 1, selectedDay)
            binding.addRabbitDate.text = Editable.Factory.getInstance().newEditable(selectedDate.format(dateFormatter))
        }, year, month, day)

        datePickerDialog.show()
    }

    private fun setPictureToSelectedOrDefault() {
        if (viewModel.selectedRabbit != null && viewModel.selectedRabbit!!.imagePath != null && viewModel.selectedRabbit!!.imagePath!!.isNotEmpty()) {
            binding.addRabbitPicture.setImageBitmap(BitmapFactory.decodeFile(viewModel.selectedRabbit!!.imagePath!!))
        } else {
            binding.addRabbitPicture.setImageResource(R.drawable.rabbit_back)
        }
    }

    private fun setFieldsToSelectedRabbit() {
        binding.addRabbitDate.text = Editable.Factory.getInstance().newEditable(LocalDate.ofEpochDay(viewModel.selectedRabbit!!.birth).format(dateFormatter))
        binding.addRabbitName.setText(viewModel.selectedRabbit!!.name)
        binding.addRabbitNumbers.setText(viewModel.selectedRabbit!!.earNumber)
        if (viewModel.selectedRabbit!!.sex == Gender.FEMALE.name) {
            binding.addRabbitGenderFemale.isChecked = true
        } else {
            binding.addRabbitGenderMale.isChecked = true
        }
    }

    private fun saveRabbit(): View.OnClickListener {
        return View.OnClickListener { view ->
            viewModel.save(
                Rabbit(
                    viewModel.selectedRabbit?.id ?: 0,
                    binding.addRabbitName.text.toString(),
                    LocalDate.parse(binding.addRabbitDate.text.toString(), dateFormatter).toEpochDay(),
                    getRabbitGender(),
                    binding.addRabbitNumbers.text.toString(),
                    null,
                    viewModel.selectedMother?.id, viewModel.selectedFather?.id
                )
            )
            view.findNavController().navigate(R.id.action_addRabbitFragment_to_navigation_home)
        }
    }

    private fun getRabbitGender(): String {
        return if (binding.addRabbitGenderFemale.isChecked) {
            Gender.FEMALE.name
        } else {
            Gender.MALE.name
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
