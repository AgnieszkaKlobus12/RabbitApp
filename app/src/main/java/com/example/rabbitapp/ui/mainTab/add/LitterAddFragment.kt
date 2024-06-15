package com.example.rabbitapp.ui.mainTab.add

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.example.rabbitapp.R
import com.example.rabbitapp.databinding.FragmentAddLitterBinding
import com.example.rabbitapp.model.entities.Litter
import com.example.rabbitapp.model.entities.Rabbit
import com.example.rabbitapp.ui.mainTab.parent.ParentSelectService
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

class LitterAddFragment : FragmentWithPicture() {
    private var _binding: FragmentAddLitterBinding? = null
    private val binding get() = _binding!!

    private val dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
    private val parentSelectService: ParentSelectService = ParentSelectService()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddLitterBinding.inflate(inflater, container, false)

        setGalleryLauncher(binding.addLitterPicture)

        val formattedDate = LocalDate.now().format(dateFormatter)
        binding.addLitterDate.text = Editable.Factory.getInstance().newEditable(formattedDate)

        if (viewModel.selectedLitter != null) {
            parentSelectService.setParents(viewModel.selectedLitter, viewModel)
            setFieldsToSelectedLitter()
        }

        setPictureToSelectedOrDefault(
            binding.addLitterPicture,
            viewModel.selectedLitter,
            R.drawable.rabbit_2_back
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addLitterSaveButton.setOnClickListener(saveLitter())
        binding.addLitterDate.setOnClickListener {
            showDatePickerDialog()
        }

        viewModel.selectedLitter?.id?.let {
            if (viewModel.getAllRabbitFromLitter(it).isNotEmpty()) {
                binding.addLitterMessage.visibility = View.VISIBLE
            }
        }

        parentSelectService.displaySelectParentFragment(
            R.id.action_addLitterFragment_to_pickMotherListFragment,
            R.id.action_addLitterFragment_to_pickFatherListFragment,
            childFragmentManager,
            viewModel, view
        )
        parentSelectService.setOnClickListenersParents(
            childFragmentManager,
            view,
            binding.fragmentAddLitterIncludeParents,
            R.id.action_addLitterFragment_to_pickMotherListFragment,
            R.id.action_addLitterFragment_to_pickFatherListFragment
        )
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog =
            DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = LocalDate.of(selectedYear, selectedMonth + 1, selectedDay)
                binding.addLitterDate.text =
                    Editable.Factory.getInstance().newEditable(selectedDate.format(dateFormatter))
            }, year, month, day)

        datePickerDialog.show()
    }

    private fun setFieldsToSelectedLitter() {
        binding.addLitterDate.text = Editable.Factory.getInstance().newEditable(
            LocalDate.ofEpochDay(viewModel.selectedLitter!!.birth).format(dateFormatter)
        )
        binding.addLitterName.setText(viewModel.selectedLitter!!.name)
        binding.addLitterNumber.setText(viewModel.selectedLitter!!.size.toString())
    }

    private fun saveLitter(): View.OnClickListener {
        return View.OnClickListener { view ->
            if (!validateFields()) {
                return@OnClickListener
            }
            var rabbitList = emptyList<Rabbit>()
            val path = saveNewPicture(viewModel.selectedLitter, binding.addLitterPicture)
            viewModel.selectedLitter?.id?.let {
                rabbitList = viewModel.getAllRabbitFromLitter(it)
            }
            val id = viewModel.save(
                Litter(
                    viewModel.selectedLitter?.id ?: 0,
                    binding.addLitterName.text.toString(),
                    LocalDate.parse(binding.addLitterDate.text.toString(), dateFormatter)
                        .toEpochDay(),
                    Integer.parseInt(binding.addLitterNumber.text.toString()),
                    path,
                    viewModel.selectedMother?.id, viewModel.selectedFather?.id
                )
            )
            rabbitList.forEach { rabbit: Rabbit ->
                run {
                    rabbit.birth =
                        LocalDate.parse(
                            binding.addLitterDate.text.toString(),
                            dateFormatter
                        )
                            .toEpochDay()
                    rabbit.fkFather = viewModel.selectedFather?.id
                    rabbit.fkMother = viewModel.selectedMother?.id
                    rabbit.fkLitter = id
                    viewModel.update(rabbit)
                }
            }
            viewModel.selectedLitter = id.let { viewModel.getLitterFromId(it) }
            view.findNavController()
                .navigate(R.id.action_addLitterFragment_to_litterDetailsFragment)
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
