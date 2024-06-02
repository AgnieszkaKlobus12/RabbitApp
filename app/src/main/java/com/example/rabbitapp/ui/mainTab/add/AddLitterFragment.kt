package com.example.rabbitapp.ui.mainTab.add

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.findNavController
import com.example.rabbitapp.R
import com.example.rabbitapp.databinding.FragmentAddLitterBinding
import com.example.rabbitapp.model.entities.Litter
import com.example.rabbitapp.ui.mainTab.HomeListItemFragment
import com.example.rabbitapp.utils.Gender
import com.example.rabbitapp.utils.RabbitDetails
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

class AddLitterFragment : AddFragment() {
    private var _binding: FragmentAddLitterBinding? = null
    private val binding get() = _binding!!

    private val dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddLitterBinding.inflate(inflater, container, false)

        setGalleryLauncher(binding.addLitterPicture, viewModel.selectedLitter)

        if (viewModel.selectedLitter != null) {
            setParents(viewModel.selectedLitter)
            setFieldsToSelectedLitter()
        }

        setPictureToSelectedOrDefault(
            binding.addLitterPicture,
            viewModel.selectedLitter,
            R.drawable.rabbit_2_back
        )

        val transaction: FragmentTransaction = childFragmentManager.beginTransaction()
        if (viewModel.selectedMother != null) {
            val selectedMotherFragment = HomeListItemFragment(viewModel.selectedMother!!)
            transaction.replace(R.id.add_litter_mother_fragment, selectedMotherFragment)
        } else {
            val pickButtonFragment = PickButtonFragment(Gender.FEMALE, object : StartSelect {
                override fun select(gender: Gender) {
                    parentSelect(
                        gender,
                        R.id.action_addLitterFragment_to_pickMotherListFragment,
                        R.id.action_addLitterFragment_to_pickFatherListFragment
                    )
                }
            })
            transaction.replace(R.id.add_litter_mother_fragment, pickButtonFragment)
        }

        if (viewModel.selectedFather != null) {
            val selectedFatherFragment = HomeListItemFragment(viewModel.selectedFather!!)
            transaction.replace(R.id.add_litter_father_fragment, selectedFatherFragment)
        } else {
            val pickButtonFragment = PickButtonFragment(Gender.MALE, object : StartSelect {
                override fun select(gender: Gender) {
                    parentSelect(
                        gender,
                        R.id.action_addLitterFragment_to_pickMotherListFragment,
                        R.id.action_addLitterFragment_to_pickFatherListFragment
                    )
                }
            })
            transaction.replace(R.id.add_litter_father_fragment, pickButtonFragment)
        }
        transaction.commit()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val formattedDate = LocalDate.now().format(dateFormatter)
        binding.addLitterDate.text = Editable.Factory.getInstance().newEditable(formattedDate)
        binding.addLitterSaveButton.setOnClickListener(saveLitter())

        binding.addLitterMotherFragment.setOnClickListener {
            parentSelect(
                Gender.FEMALE,
                R.id.action_addLitterFragment_to_pickMotherListFragment,
                R.id.action_addLitterFragment_to_pickFatherListFragment
            )
        }
        binding.addLitterFatherFragment.setOnClickListener {
            parentSelect(
                Gender.MALE,
                R.id.action_addLitterFragment_to_pickMotherListFragment,
                R.id.action_addLitterFragment_to_pickFatherListFragment
            )
        }

        binding.addLitterDate.setOnClickListener {
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
            binding.addLitterDate.text = Editable.Factory.getInstance().newEditable(selectedDate.format(dateFormatter))
        }, year, month, day)

        datePickerDialog.show()
    }

    private fun setFieldsToSelectedLitter() {
        binding.addLitterDate.text = Editable.Factory.getInstance().newEditable(LocalDate.ofEpochDay(viewModel.selectedRabbit!!.birth).format(dateFormatter))
        binding.addLitterName.setText(viewModel.selectedLitter!!.name)
        binding.addLitterNumber.setText(viewModel.selectedLitter!!.size.toString())
    }

    private fun saveLitter(): View.OnClickListener {
        return View.OnClickListener { view ->
            if (!validateFields()) {
                return@OnClickListener
            }
            val path = saveNewPicture(viewModel.selectedLitter, binding.addLitterPicture)
            viewModel.save(
                Litter(
                    viewModel.selectedLitter?.id ?: 0,
                    binding.addLitterName.text.toString(),
                    LocalDate.parse(binding.addLitterDate.text.toString(), dateFormatter).toEpochDay(),
                    Integer.parseInt(binding.addLitterNumber.text.toString()),
                    path,
                    viewModel.selectedMother?.id, viewModel.selectedFather?.id
                )
            )
            view.findNavController().navigate(R.id.action_addLitterFragment_to_navigation_home)
        }
    }

    private fun validateFields(): Boolean {
        var correct = true
        if (binding.addLitterName.text.isEmpty()) {
            binding.addLitterName.error = getString(R.string.error_empty)
            correct = false
        }
        if (binding.addLitterDate.text.isEmpty()) {
            binding.addLitterDate.error = getString(R.string.error_empty)
            correct = false
        }
        if (binding.addLitterNumber.text.isEmpty()) {
            binding.addLitterNumber.error = getString(R.string.error_empty)
            correct = false
        }
        return correct
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
