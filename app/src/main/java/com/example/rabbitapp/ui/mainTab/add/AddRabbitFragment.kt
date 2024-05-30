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
import com.example.rabbitapp.utils.RabbitDetails
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class AddRabbitFragment : Fragment() {
    private var _binding: FragmentAddRabbitBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainListViewModel by activityViewModels()

    private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.selectedMother =
            viewModel.selectedRabbit!!.fkMother?.let { viewModel.getRabbitFromId(it) }
        viewModel.selectedFather =
            viewModel.selectedRabbit!!.fkFather?.let { viewModel.getRabbitFromId(it) }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddRabbitBinding.inflate(inflater, container, false)

        if (viewModel.selectedRabbit != null) {
            setFieldsToSelectedRabbit()
        }

        val transaction: FragmentTransaction = childFragmentManager.beginTransaction()
        if (viewModel.selectedMother != null) {
            val selectedMotherFragment = HomeListItem(viewModel.selectedMother!!)
            transaction.replace(R.id.add_rabbit_mother_fragment, selectedMotherFragment)
        } else {
            val pickButtonFragment = PickButtonFragment(Gender.FEMALE, object : StartSelect {
                override fun select(gender: Gender) {
                    parentSelect(gender)
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
                    parentSelect(gender)
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

        binding.addRabbitMotherFragment.setOnClickListener { parentSelect(Gender.FEMALE) }
        binding.addRabbitFatherFragment.setOnClickListener { parentSelect(Gender.MALE) }
    }

    private fun setFieldsToSelectedRabbit(){
            binding.addRabbitDate.setText(RabbitDetails.getBirthDateString(viewModel.selectedRabbit!!.birth))
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
                    LocalDate.parse(binding.addRabbitDate.text.toString(), formatter).toEpochDay(),
                    getRabbitGender(),
                    binding.addRabbitNumbers.text.toString(),
                    ByteArray(0),
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

    private fun parentSelect(parentGender: Gender) {
        val transaction: FragmentTransaction = childFragmentManager.beginTransaction()
        if (parentGender == Gender.FEMALE) {
            view?.findNavController()
                ?.navigate(R.id.action_addRabbitFragment_to_pickMotherListFragment)
        }
        if (parentGender == Gender.MALE) {
            view?.findNavController()
                ?.navigate(R.id.action_addRabbitFragment_to_pickFatherListFragment)
        }
        transaction.commit()
    }

}