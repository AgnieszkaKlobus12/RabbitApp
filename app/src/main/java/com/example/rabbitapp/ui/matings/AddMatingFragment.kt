package com.example.rabbitapp.ui.matings

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.rabbitapp.R
import com.example.rabbitapp.databinding.FragmentAddMatingBinding
import com.example.rabbitapp.model.entities.Litter
import com.example.rabbitapp.model.entities.relations.Mating
import com.example.rabbitapp.ui.mainTab.HomeListItemFragment
import com.example.rabbitapp.ui.mainTab.add.PickButtonFragment
import com.example.rabbitapp.ui.mainTab.add.StartSelect
import com.example.rabbitapp.ui.mainTab.parent.ParentSelectService
import com.example.rabbitapp.utils.Gender
import com.example.rabbitapp.utils.MainListViewModel
import com.example.rabbitapp.utils.RabbitDetails
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

class AddMatingFragment : Fragment() {

    private val args: AddMatingFragmentArgs by navArgs()

    private var litter: Litter? = null
    private var _binding: FragmentAddMatingBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainListViewModel by activityViewModels()

    private val dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
    private val parentSelectService: ParentSelectService = ParentSelectService()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddMatingBinding.inflate(inflater, container, false)
        if (args.fatherId != 0L) {
            if (viewModel.selectedFather == null) {
                viewModel.selectedFather = viewModel.getRabbitFromId(args.fatherId)
            }
        }
        if (args.motherId != 0L) {
            if (viewModel.selectedMother == null) {
                viewModel.selectedMother = viewModel.getRabbitFromId(args.motherId)
            }
        }
        if (args.litterId != 0L) {
            litter = viewModel.getLitterFromId(args.litterId)

        }
        (activity as AppCompatActivity).supportActionBar?.title =
            resources.getString(R.string.add_mating)
        if (args.matingId != 0L) {
            val mating = viewModel.getMating(args.matingId)
            (activity as AppCompatActivity).supportActionBar?.title =
                resources.getString(R.string.edit_mating)
            binding.addMatingDate.text = Editable.Factory.getInstance()
                .newEditable(RabbitDetails.getDateString(mating!!.matingDate))
            binding.matingDateBirth.text = Editable.Factory.getInstance()
                .newEditable(RabbitDetails.getDateString(mating.birthDate))
            if (litter == null) {
                litter = mating.fkLitter?.let { viewModel.getLitterFromId(it) }
            }
            if (viewModel.selectedFather == null) {
                viewModel.selectedFather = mating.fkFather?.let { viewModel.getRabbitFromId(it) }
            }
            if (viewModel.selectedMother == null) {
                viewModel.selectedMother = mating.fkMother?.let { viewModel.getRabbitFromId(it) }
            }
            if (mating.archived) {
                binding.archiveSwitch.visibility = View.VISIBLE
                binding.archiveSwitch.isChecked = true
            }
        } else {
            val formattedMatingDate = LocalDate.now().format(dateFormatter)
            binding.addMatingDate.text =
                Editable.Factory.getInstance().newEditable(formattedMatingDate)
            val formattedDate = LocalDate.now().plusDays(30).format(dateFormatter)
            binding.matingDateBirth.text = Editable.Factory.getInstance().newEditable(formattedDate)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addMatingSaveButton.setOnClickListener {
            if (!viewModel.getEditable()) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.non_editable), Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            if (litter != null) {
                viewModel.save(
                    litter!!.copy(
                        birth = LocalDate.parse(
                            binding.matingDateBirth.text.toString(),
                            dateFormatter
                        ).toEpochDay(),
                        fkFather = viewModel.selectedFather?.id,
                        fkMother = viewModel.selectedMother?.id,
                    )
                )
            }
            val id = viewModel.save(
                Mating(
                    args.matingId.takeUnless { it == 0L } ?: 0,
                    LocalDate.parse(binding.addMatingDate.text.toString(), dateFormatter)
                        .toEpochDay(),
                    LocalDate.parse(binding.matingDateBirth.text.toString(), dateFormatter)
                        .toEpochDay(),
                    binding.archiveSwitch.isChecked,
                    viewModel.selectedMother?.id,
                    viewModel.selectedFather?.id,
                    litter?.id
                )
            )
            view.findNavController().navigate(
                AddMatingFragmentDirections.actionAddMatingFragmentToMatingDetailsFragment(id)
            )
        }

        parentSelectService.displaySelectParentFragment(
            AddMatingFragmentDirections.actionAddMatingFragmentToPickMotherListFragment(
                0L,
                litter?.id ?: 0L
            ),
            AddMatingFragmentDirections.actionAddMatingFragmentToPickFatherListFragment(
                0L,
                litter?.id ?: 0L
            ),
            childFragmentManager,
            viewModel, view
        )

        if (litter != null) {
            binding.fragmentAddMatingIncludeParents.addMotherFragment.setOnClickListener {
                if (!viewModel.getEditable()) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.non_editable), Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }
                Toast.makeText(
                    context,
                    "Uwaga! Zmiana rodzice spowoduje zmianę również w powiązanym miocie!",
                    Toast.LENGTH_SHORT
                ).show()
                parentSelectService.parentSelect(
                    AddMatingFragmentDirections.actionAddMatingFragmentToPickMotherListFragment(
                        0L,
                        litter?.id ?: 0L
                    ),
                    AddMatingFragmentDirections.actionAddMatingFragmentToPickFatherListFragment(
                        0L,
                        litter?.id ?: 0L
                    ),
                    Gender.FEMALE,
                    childFragmentManager,
                    view
                )
            }
            binding.fragmentAddMatingIncludeParents.addFatherFragment.setOnClickListener {
                if (!viewModel.getEditable()) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.non_editable), Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }
                Toast.makeText(
                    context,
                    "Uwaga! Zmiana rodzice spowoduje zmianę również w powiązanym miocie!",
                    Toast.LENGTH_SHORT
                ).show()
                parentSelectService.parentSelect(
                    AddMatingFragmentDirections.actionAddMatingFragmentToPickMotherListFragment(
                        0L,
                        litter?.id ?: 0L
                    ),
                    AddMatingFragmentDirections.actionAddMatingFragmentToPickFatherListFragment(
                        0L,
                        litter?.id ?: 0L
                    ),
                    Gender.MALE,
                    childFragmentManager,
                    view
                )
            }

            binding.matingDateBirth.setOnClickListener {
                if (!viewModel.getEditable()) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.non_editable), Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }
                showDatePickerDialog(binding.matingDateBirth)
                Toast.makeText(
                    context,
                    "Uwaga! Zmiana daty urodzenia spowoduje zmianę w miocie!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            parentSelectService.setOnClickListenersParents(
                AddMatingFragmentDirections.actionAddMatingFragmentToPickMotherListFragment(
                    0L,
                    litter?.id ?: 0L
                ),
                AddMatingFragmentDirections.actionAddMatingFragmentToPickFatherListFragment(
                    0L,
                    litter?.id ?: 0L
                ),
                childFragmentManager,
                view,
                binding.fragmentAddMatingIncludeParents,
            )

            binding.matingDateBirth.setOnClickListener {
                showDatePickerDialog(binding.matingDateBirth)
            }
        }

        binding.addMatingDate.setOnClickListener {
            showDatePickerDialog(binding.addMatingDate)
        }

        displayLitterIfSelected()
    }

    private fun displayLitterIfSelected() {
        val transaction: FragmentTransaction = childFragmentManager.beginTransaction()
        if (litter != null) {
            val selectedLitterFragment = HomeListItemFragment(litter!!)
            transaction.replace(R.id.add_litter_fragment, selectedLitterFragment)
        } else {
            val pickButtonFragment = PickButtonFragment(null, object : StartSelect {
                override fun select(gender: Gender?) {
                    val transaction: FragmentTransaction = childFragmentManager.beginTransaction()
                    view?.findNavController()
                        ?.navigate(
                            AddMatingFragmentDirections.actionAddMatingFragmentToPickLitterListFragment(
                                args.motherId,
                                args.fatherId,
                                args.matingId
                            )
                        )
                    transaction.commit()
                }
            })
            transaction.replace(R.id.add_litter_fragment, pickButtonFragment)
        }
        transaction.commit()
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