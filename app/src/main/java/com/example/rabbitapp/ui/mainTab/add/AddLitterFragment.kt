package com.example.rabbitapp.ui.mainTab.add

import android.graphics.BitmapFactory
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
import com.example.rabbitapp.ui.mainTab.HomeListItem
import com.example.rabbitapp.utils.Gender
import com.example.rabbitapp.utils.RabbitDetails
import java.time.LocalDate

class AddLitterFragment : AddFragment() {
    private var _binding: FragmentAddLitterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddLitterBinding.inflate(inflater, container, false)

        val galleryLauncher = getGalleryLauncher(binding.addLitterPicture)
        binding.addLitterPicture.setOnClickListener {
            galleryLauncher.launch("image/*")
        }

        if (viewModel.selectedLitter != null) {
            viewModel.selectedMother =
                viewModel.selectedLitter?.fkMother?.let { viewModel.getRabbitFromId(it) }
            viewModel.selectedFather =
                viewModel.selectedLitter?.fkFather?.let { viewModel.getRabbitFromId(it) }
            setFieldsToSelectedLitter()
        }

        setPictureToSelectedOrDefault()

        val transaction: FragmentTransaction = childFragmentManager.beginTransaction()
        if (viewModel.selectedMother != null) {
            val selectedMotherFragment = HomeListItem(viewModel.selectedMother!!)
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
            val selectedFatherFragment = HomeListItem(viewModel.selectedFather!!)
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

        val formattedDate = LocalDate.now().format(formatter)
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
    }

    private fun setFieldsToSelectedLitter() {
        binding.addLitterDate.setText(RabbitDetails.getBirthDateString(viewModel.selectedLitter!!.birth))
        binding.addLitterName.setText(viewModel.selectedLitter!!.name)
        binding.addLitterNumber.setText(viewModel.selectedLitter!!.size.toString())
    }

    private fun setPictureToSelectedOrDefault() {
        if (viewModel.selectedRabbit != null && viewModel.selectedRabbit!!.imagePath != null && viewModel.selectedRabbit!!.imagePath!!.isNotEmpty()) {
            binding.addLitterPicture.setImageBitmap(BitmapFactory.decodeFile(viewModel.selectedRabbit!!.imagePath!!));
        } else {
            binding.addLitterPicture.setImageResource(R.drawable.rabbit_2_back)
        }
    }

    private fun saveLitter(): View.OnClickListener {
        return View.OnClickListener { view ->
            viewModel.save(
                Litter(
                    viewModel.selectedLitter?.id ?: 0,
                    binding.addLitterName.text.toString(),
                    LocalDate.parse(binding.addLitterDate.text.toString(), formatter).toEpochDay(),
                    Integer.parseInt(binding.addLitterNumber.text.toString()),
                    null,
                    viewModel.selectedMother?.id, viewModel.selectedFather?.id
                )
            )
            view.findNavController().navigate(R.id.action_addLitterFragment_to_navigation_home)
        }
    }

}