package com.example.rabbitapp.ui.mainTab.add

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.rabbitapp.R
import com.example.rabbitapp.databinding.FragmentAddLitterBinding
import com.example.rabbitapp.model.entities.Litter
import com.example.rabbitapp.model.entities.Rabbit
import com.example.rabbitapp.model.entities.relations.Mating
import com.example.rabbitapp.ui.mainTab.parent.ParentSelectService
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

class LitterAddFragment : FragmentWithPicture() {

    private val args: LitterAddFragmentArgs by navArgs()
    private var mating: Mating? = null

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
        if (args.matingId != 0L) {
            mating = viewModel.getMating(args.matingId)
        }

        setGalleryLauncher(binding.addLitterPicture)

        val formattedDate = LocalDate.now().format(dateFormatter)
        binding.addLitterDate.text = Editable.Factory.getInstance().newEditable(formattedDate)

        if (viewModel.selectedLitter != null) {
            parentSelectService.setParents(
                viewModel,
                viewModel.selectedLitter!!.fkMother,
                viewModel.selectedLitter!!.fkFather
            )
            setFieldsToSelectedLitter()
        } else if (mating != null) {
            parentSelectService.setParents(viewModel, mating!!.fkMother, mating!!.fkFather)
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

        if (mating == null) {
            parentSelectService.setOnClickListenersParents(
                childFragmentManager,
                view,
                binding.fragmentAddLitterIncludeParents,
                R.id.action_addLitterFragment_to_pickMotherListFragment,
                R.id.action_addLitterFragment_to_pickFatherListFragment
            )
            parentSelectService.displaySelectParentFragment(
                R.id.action_addLitterFragment_to_pickMotherListFragment,
                R.id.action_addLitterFragment_to_pickFatherListFragment,
                childFragmentManager,
                viewModel, view
            )
        } else {
            parentSelectService.displayParentOrUnknown(
                mating?.fkMother,
                mating?.fkFather,
                childFragmentManager,
                viewModel
            )
            binding.fragmentAddLitterIncludeParents.addMotherFragment.setOnClickListener {
                Toast.makeText(
                    context,
                    "Nie można zmienić! Musi pokrywać się z podanym w pokryciu",
                    Toast.LENGTH_SHORT
                ).show()
            }
            binding.fragmentAddLitterIncludeParents.addFatherFragment.setOnClickListener {
                Toast.makeText(
                    context,
                    "Nie można zmienić! Musi pokrywać się z podanym w pokryciu",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.addLitterDeathSwitch.setOnCheckedChangeListener { _, isChecked ->
            run {
                if (isChecked) {
                    binding.addLitterDeathDateRow.visibility = View.VISIBLE
                    binding.addLitterCageNumbersRow.visibility = View.GONE
                } else {
                    binding.addLitterDeathDateRow.visibility = View.GONE
                    binding.addLitterCageNumbersRow.visibility = View.VISIBLE
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
                binding.addLitterDate.text =
                    Editable.Factory.getInstance().newEditable(selectedDate.format(dateFormatter))
            }, year, month, day)

        datePickerDialog.show()
    }

    private fun setFieldsToSelectedLitter() {
        binding.addLitterDate.text = Editable.Factory.getInstance().newEditable(
            LocalDate.ofEpochDay(viewModel.selectedLitter!!.birth).format(dateFormatter)
        )
        if (viewModel.selectedLitter!!.deathDate != null) {
            binding.addLitterDeathDate.text = Editable.Factory.getInstance().newEditable(
                viewModel.selectedRabbit!!.deathDate?.let {
                    LocalDate.ofEpochDay(it).format(dateFormatter)
                }
            )
        }
        binding.addLitterDeathSwitch.isChecked = viewModel.selectedLitter!!.deathDate != null
        binding.addLitterCageNumbers.setText(viewModel.selectedLitter!!.cageNumber.toString())
        binding.addLitterName.setText(viewModel.selectedLitter!!.name)
        binding.addLitterNumber.setText(viewModel.selectedLitter!!.size.toString())
    }

    private fun saveLitter(): View.OnClickListener {
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
                    if (binding.addLitterCageNumbers.text.toString().isEmpty()) {
                        null
                    } else {
                        binding.addLitterCageNumbers.text.toString().toInt()
                    },
                    path,
                    viewModel.selectedMother?.id, viewModel.selectedFather?.id,
                    if (binding.addLitterDeathSwitch.isChecked) {
                        LocalDate.parse(binding.addLitterDeathDate.text.toString(), dateFormatter)
                            .toEpochDay()
                    } else {
                        null
                    }
                )
            )
            if (mating != null) {
                viewModel.save(
                    mating!!.copy(
                        fkLitter = id,
                        birthDate = LocalDate.parse(
                            binding.addLitterDate.text.toString(),
                            dateFormatter
                        ).toEpochDay()
                    )
                )
                view?.findNavController()
                    ?.navigate(
                        LitterAddFragmentDirections.actionAddLitterFragmentToMatingDetailsFragment(
                            mating!!.id
                        )
                    )
            } else {
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
