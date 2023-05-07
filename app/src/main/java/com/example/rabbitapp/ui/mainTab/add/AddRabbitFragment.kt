package com.example.rabbitapp.ui.mainTab.add

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.example.rabbitapp.R
import com.example.rabbitapp.databinding.FragmentAddRabbitBinding
import com.example.rabbitapp.model.entities.Rabbit
import com.example.rabbitapp.ui.mainTab.HomeListItem
import com.example.rabbitapp.ui.mainTab.MainListViewModel
import com.example.rabbitapp.utils.Gender
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class AddRabbitFragment : Fragment() {
    private var _binding: FragmentAddRabbitBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainListViewModel by activityViewModels()

    private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddRabbitBinding.inflate(inflater, container, false)

        val transaction: FragmentTransaction = childFragmentManager.beginTransaction()
        if (viewModel.selectedMother != null) {
            val selectedMotherFragment = HomeListItem(viewModel.selectedMother!!)
            transaction.replace(R.id.add_rabbit_mother_fragment, selectedMotherFragment)
        } else {
            val pickButtonFragment = PickButtonFragment(Gender.FEMALE, object : StartSelect {
                override fun select(gender: Gender) {
                    startSelect(gender)
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
                    startSelect(gender)
                }
            })
            transaction.replace(R.id.add_rabbit_father_fragment, pickButtonFragment)
        }
        transaction.commit()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val formattedDate = LocalDate.now().format(formatter)
        binding.addRabbitDate.text = Editable.Factory.getInstance().newEditable(formattedDate)
        binding.addRabbitSaveButton.setOnClickListener(saveRabbit())

        binding.addRabbitMotherFragment.setOnClickListener { startSelect(Gender.FEMALE) }
        binding.addRabbitFatherFragment.setOnClickListener { startSelect(Gender.MALE) }
    }

    private fun saveRabbit(): View.OnClickListener? {
        return View.OnClickListener { view ->
            viewModel.save(
                Rabbit(
                    0,
                    binding.addRabbitName.text.toString(),
                    LocalDate.parse(binding.addRabbitDate.text.toString(), formatter).toEpochDay(),
                    getRabbitGender(),
                    binding.addRabbitNumbers.text.toString(),
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

    private fun startSelect(gender: Gender) {
        val transaction: FragmentTransaction = childFragmentManager.beginTransaction()
        if (gender == Gender.FEMALE) {
            view?.findNavController()
                ?.navigate(R.id.action_addRabbitFragment_to_pickMotherListFragment)
        }
        if (gender == Gender.MALE) {
            view?.findNavController()
                ?.navigate(R.id.action_addRabbitFragment_to_pickFatherListFragment)
        }
        transaction.commit()
    }

}