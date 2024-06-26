package com.example.rabbitapp.ui.matings

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.example.rabbitapp.R
import com.example.rabbitapp.databinding.FragmentAddMatingBinding
import com.example.rabbitapp.model.entities.relations.Mating
import com.example.rabbitapp.ui.mainTab.HomeListItemFragment
import com.example.rabbitapp.ui.mainTab.MainListViewModel
import com.example.rabbitapp.ui.mainTab.add.PickButtonFragment
import com.example.rabbitapp.ui.mainTab.add.StartSelect
import com.example.rabbitapp.ui.mainTab.parent.ParentSelectService
import com.example.rabbitapp.utils.Gender
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

class AddMatingFragment : Fragment() {

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

        val formattedMatingDate = LocalDate.now().format(dateFormatter)
        binding.addMatingDate.text = Editable.Factory.getInstance().newEditable(formattedMatingDate)
        val formattedDate = LocalDate.now().plusDays(30).format(dateFormatter)
        binding.matingDateBirth.text = Editable.Factory.getInstance().newEditable(formattedDate)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addMatingSaveButton.setOnClickListener {
            viewModel.save(
                Mating(
                    0,
                    LocalDate.parse(binding.addMatingDate.text.toString(), dateFormatter)
                        .toEpochDay(),
                    LocalDate.parse(binding.matingDateBirth.text.toString(), dateFormatter)
                        .toEpochDay(),
                    viewModel.selectedMother?.id,
                    viewModel.selectedFather?.id,
                    viewModel.selectedLitter?.id
                )
            )
            view.findNavController()
                .navigate(R.id.action_addMatingFragment_to_matingDetailsFragment)
        }

        parentSelectService.displaySelectParentFragment(
            R.id.action_addMatingFragment_to_pickMotherListFragment,
            R.id.action_addMatingFragment_to_pickFatherListFragment,
            childFragmentManager,
            viewModel, view
        )
        parentSelectService.setOnClickListenersParents(
            childFragmentManager,
            view,
            binding.fragmentAddMatingIncludeParents,
            R.id.action_addMatingFragment_to_pickMotherListFragment,
            R.id.action_addMatingFragment_to_pickFatherListFragment
        )

        binding.matingDateBirth.setOnClickListener {
            showDatePickerDialog(binding.matingDateBirth)
        }
        binding.addMatingDate.setOnClickListener {
            showDatePickerDialog(binding.addMatingDate)
        }

        displayLitterIfSelected()
    }

    private fun displayLitterIfSelected() {
        val transaction: FragmentTransaction = childFragmentManager.beginTransaction()
        if (viewModel.selectedLitter != null) {
            val selectedLitterFragment = HomeListItemFragment(viewModel.selectedLitter!!)
            transaction.replace(R.id.add_litter_fragment, selectedLitterFragment)
        } else {
            val pickButtonFragment = PickButtonFragment(null, object : StartSelect {
                override fun select(gender: Gender?) {
                    val transaction: FragmentTransaction = childFragmentManager.beginTransaction()
                    view?.findNavController()
                        ?.navigate(R.id.action_addMatingFragment_to_pickLitterListFragment)
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