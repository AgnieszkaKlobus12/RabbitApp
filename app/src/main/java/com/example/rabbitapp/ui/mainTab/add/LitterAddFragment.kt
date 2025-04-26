package com.example.rabbitapp.ui.mainTab.add

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.isDigitsOnly
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
    private var litter: Litter? = null
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
        if (args.litterId != 0L) {
            litter = viewModel.getLitterFromId(args.litterId)
        }

        setGalleryLauncher(binding.addLitterPicture)

        val formattedDate = LocalDate.now().format(dateFormatter)
        binding.addLitterDate.text = Editable.Factory.getInstance().newEditable(formattedDate)
        binding.addLitterDeathDate.text = Editable.Factory.getInstance().newEditable(formattedDate)

        if (litter != null) {
            parentSelectService.setParents(
                viewModel,
                litter!!.fkMother,
                litter!!.fkFather
            )
            (activity as AppCompatActivity).supportActionBar?.title =
                resources.getString(R.string.edit_litter)
            setFieldsToSelectedLitter()
        } else if (mating != null) {
            parentSelectService.setParents(viewModel, mating!!.fkMother, mating!!.fkFather)
        }

        setPictureToSelectedOrDefault(
            binding.addLitterPicture,
            litter,
            R.drawable.rabbit_2_back
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addLitterSaveButton.setOnClickListener(saveLitter())
        binding.addLitterDate.setOnClickListener {
            showDatePickerDialog(binding.addLitterDate)
        }
        binding.addLitterDeathDate.setOnClickListener {
            showDatePickerDialog(binding.addLitterDeathDate)
        }

        litter?.id?.let {
            if (viewModel.getAllRabbitFromLitter(it).isNotEmpty()) {
                binding.addLitterMessage.visibility = View.VISIBLE
            }
        }

        if (mating == null) {
            parentSelectService.setOnClickListenersParents(
                LitterAddFragmentDirections.actionAddLitterFragmentToPickMotherListFragment(
                    0L,
                    litter?.id ?: 0L
                ),
                LitterAddFragmentDirections.actionAddLitterFragmentToPickFatherListFragment(
                    0L,
                    litter?.id ?: 0L
                ),
                childFragmentManager,
                view,
                binding.fragmentAddLitterIncludeParents,
            )
            parentSelectService.displaySelectParentFragment(
                LitterAddFragmentDirections.actionAddLitterFragmentToPickMotherListFragment(
                    0L,
                    litter?.id ?: 0L
                ),
                LitterAddFragmentDirections.actionAddLitterFragmentToPickFatherListFragment(
                    0L,
                    litter?.id ?: 0L
                ),
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

    private fun showDatePickerDialog(view: TextView) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog =
            DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = LocalDate.of(selectedYear, selectedMonth + 1, selectedDay)
                view.text =
                    Editable.Factory.getInstance().newEditable(selectedDate.format(dateFormatter))
            }, year, month, day)

        datePickerDialog.show()
    }

    private fun setFieldsToSelectedLitter() {
        binding.addLitterDate.text = Editable.Factory.getInstance().newEditable(
            LocalDate.ofEpochDay(litter!!.birth).format(dateFormatter)
        )
        if (litter!!.deathDate != null) {
            binding.addLitterDeathDate.text = Editable.Factory.getInstance().newEditable(
                litter!!.deathDate?.let {
                    LocalDate.ofEpochDay(it).format(dateFormatter)
                }
            )
            binding.addLitterDeathDateRow.visibility = View.VISIBLE
            binding.addLitterCageNumbersRow.visibility = View.GONE
        }
        binding.addLitterDeathSwitch.isChecked = litter!!.deathDate != null
        binding.addLitterCageNumbers.setText(litter?.cageNumber?.toString() ?: "")
        binding.addLitterName.setText(litter!!.name)
        binding.addLitterNumber.setText(litter!!.size.toString())
    }

    private fun saveLitter(): View.OnClickListener {
        return View.OnClickListener { view ->
            if (!validateFields()) {
                return@OnClickListener
            }
            var rabbitList = emptyList<Rabbit>()
            val path = saveNewPicture(litter, binding.addLitterPicture)
            val imageList = litter?.imagePath?.toMutableList() ?: mutableListOf()
            if (path != null) {
                imageList.add(path)
            }
            litter?.id?.let {
                rabbitList = viewModel.getAllRabbitFromLitter(it)
            }
            val id = viewModel.save(
                Litter(
                    litter?.id ?: 0,
                    binding.addLitterName.text.toString(),
                    LocalDate.parse(binding.addLitterDate.text.toString(), dateFormatter)
                        .toEpochDay(),
                    Integer.parseInt(binding.addLitterNumber.text.toString()),
                    if (binding.addLitterCageNumbers.text.toString() != "" && binding.addLitterCageNumbers.text.toString()
                            .isDigitsOnly()
                    ) {
                        binding.addLitterCageNumbers.text.toString().toInt()
                    } else {
                        null
                    },
                    imageList.toList(),
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
                litter = id.let { viewModel.getLitterFromId(it) }
                view.findNavController()
                    .navigate(
                        LitterAddFragmentDirections.actionAddLitterFragmentToLitterDetailsFragment(
                            litter!!.id
                        )
                    )
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
        if (binding.addLitterDeathSwitch.isChecked && LocalDate.parse(
                binding.addLitterDeathDate.text.toString(),
                dateFormatter
            ).isBefore(LocalDate.parse(binding.addLitterDate.text.toString(), dateFormatter))
        ) {
            correct = false
            binding.addLitterDeathError.visibility = View.VISIBLE
        } else {
            binding.addLitterDeathError.visibility = View.GONE
        }
        return correct
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
