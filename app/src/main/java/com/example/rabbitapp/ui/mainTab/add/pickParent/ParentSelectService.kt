package com.example.rabbitapp.ui.mainTab.add.pickParent

import android.util.Log
import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.findNavController
import com.example.rabbitapp.R
import com.example.rabbitapp.databinding.PickParentsFragmentBinding
import com.example.rabbitapp.model.entities.HomeListItem
import com.example.rabbitapp.ui.mainTab.HomeListItemFragment
import com.example.rabbitapp.ui.mainTab.MainListViewModel
import com.example.rabbitapp.ui.mainTab.add.PickButtonFragment
import com.example.rabbitapp.ui.mainTab.add.StartSelect
import com.example.rabbitapp.utils.Gender

class ParentSelectService {

    fun displaySelectParentFragment(
        pickMotherListFragment: Int,
        pickFatherListFragment: Int,
        childFragmentManager: FragmentManager,
        viewModel: MainListViewModel,
        view: View?
    ) {
        Log.d(
            "ParentSelect",
            "Selected mother " + viewModel.selectedMother?.toString() + " father " + viewModel.selectedFather?.toString()
        )
        val transaction: FragmentTransaction = childFragmentManager.beginTransaction()
        if (viewModel.selectedMother != null) {
            val selectedMotherFragment = HomeListItemFragment(viewModel.selectedMother!!)
            transaction.replace(R.id.add_mother_fragment, selectedMotherFragment)
        } else {
            val pickButtonFragment = PickButtonFragment(Gender.FEMALE, object : StartSelect {
                override fun select(gender: Gender) {
                    parentSelect(
                        gender,
                        pickMotherListFragment,
                        pickFatherListFragment,
                        childFragmentManager,
                        view
                    )
                }
            })
            transaction.replace(R.id.add_mother_fragment, pickButtonFragment)
        }

        if (viewModel.selectedFather != null) {
            val selectedFatherFragment = HomeListItemFragment(viewModel.selectedFather!!)
            transaction.replace(R.id.add_father_fragment, selectedFatherFragment)
        } else {
            val pickButtonFragment = PickButtonFragment(Gender.MALE, object : StartSelect {
                override fun select(gender: Gender) {
                    parentSelect(
                        gender,
                        pickMotherListFragment,
                        pickFatherListFragment,
                        childFragmentManager,
                        view
                    )
                }
            })
            transaction.replace(R.id.add_father_fragment, pickButtonFragment)
        }
        transaction.commit()
    }

    fun setOnClickListeners(
        childFragmentManager: FragmentManager,
        view: View?,
        binding: PickParentsFragmentBinding,
        pickMotherListFragment: Int,
        pickFatherListFragment: Int
    ) {
        binding.addMotherFragment.setOnClickListener {
            parentSelect(
                Gender.FEMALE,
                pickMotherListFragment,
                pickFatherListFragment,
                childFragmentManager,
                view
            )
        }
        binding.addFatherFragment.setOnClickListener {
            parentSelect(
                Gender.MALE,
                pickMotherListFragment,
                pickFatherListFragment,
                childFragmentManager,
                view
            )
        }
    }

    fun parentSelect(
        parentGender: Gender,
        pickMotherListFragment: Int,
        pickFatherListFragment: Int,
        childFragmentManager: FragmentManager, view: View?
    ) {
        val transaction: FragmentTransaction = childFragmentManager.beginTransaction()
        if (parentGender == Gender.FEMALE) {
            view?.findNavController()
                ?.navigate(pickMotherListFragment)
        }
        if (parentGender == Gender.MALE) {
            view?.findNavController()
                ?.navigate(pickFatherListFragment)
        }
        transaction.commit()
    }

    fun setParents(item: HomeListItem?, viewModel: MainListViewModel) {
        viewModel.selectedMother =
            item?.fkMother?.let { viewModel.getRabbitFromId(it) }
        viewModel.selectedFather =
            item?.fkFather?.let { viewModel.getRabbitFromId(it) }
    }
}