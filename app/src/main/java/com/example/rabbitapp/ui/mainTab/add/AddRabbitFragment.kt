package com.example.rabbitapp.ui.mainTab.add

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
import com.example.rabbitapp.ui.mainTab.HomeListItemFragment
import com.example.rabbitapp.utils.Gender
import com.example.rabbitapp.utils.RabbitDetails
import java.time.LocalDate


class AddRabbitFragment : AddFragment() {
    private var _binding: FragmentAddRabbitBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddRabbitBinding.inflate(inflater, container, false)

        setGalleryLauncher(binding.addRabbitPicture)

        if (viewModel.selectedRabbit != null) {
            setParents(viewModel.selectedRabbit)
            setFieldsToSelectedRabbit()
        }

        setPictureToSelectedOrDefault(
            binding.addRabbitPicture,
            viewModel.selectedRabbit,
            R.drawable.rabbit_back
        )

        val transaction: FragmentTransaction = childFragmentManager.beginTransaction()
        if (viewModel.selectedMother != null) {
            val selectedMotherFragment = HomeListItemFragment(viewModel.selectedMother!!)
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
            val selectedFatherFragment = HomeListItemFragment(viewModel.selectedFather!!)
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

        val formattedDate = LocalDate.now().format(formatter)
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
    }

    private fun setFieldsToSelectedRabbit() {
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

}