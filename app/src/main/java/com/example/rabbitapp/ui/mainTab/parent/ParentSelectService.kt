package com.example.rabbitapp.ui.mainTab.parent

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Toast
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
                override fun select(gender: Gender?) {
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
                override fun select(gender: Gender?) {
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

    fun displayParentOrUnknown(
        showingItem: HomeListItem,
        childFragmentManager: FragmentManager,
        viewModel: MainListViewModel,
    ) {
        val transaction: FragmentTransaction = childFragmentManager.beginTransaction()
        if (showingItem.fkMother != null) {
            val selectedMotherFragment =
                HomeListItemFragment(viewModel.getRabbitFromId(showingItem.fkMother!!)!!)
            transaction.replace(R.id.add_mother_fragment, selectedMotherFragment)
        } else {
            val unknownParentFragment = UnknownParentFragment()
            transaction.replace(R.id.add_mother_fragment, unknownParentFragment)
        }

        if (showingItem.fkFather != null) {
            val selectedFatherFragment =
                HomeListItemFragment(viewModel.getRabbitFromId(showingItem.fkFather!!)!!)
            transaction.replace(R.id.add_father_fragment, selectedFatherFragment)
        } else {
            val unknownParentFragment = UnknownParentFragment()
            transaction.replace(R.id.add_father_fragment, unknownParentFragment)
        }
        transaction.commit()
    }

    fun setChangeIllegalMessage(
        context: Context,
        binding: PickParentsFragmentBinding,
        message: String
    ) {
        val toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
        binding.addMotherFragment.setOnClickListener {
            toast.show()
        }
        binding.addFatherFragment.setOnClickListener {
            toast.show()
        }
    }

    fun setOnClickListenersParents(
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
        parentGender: Gender?,
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
        if (viewModel.selectedMother == null) {
            viewModel.selectedMother =
                item?.fkMother?.let { viewModel.getRabbitFromId(it) }
        }
        if (viewModel.selectedFather == null) {
            viewModel.selectedFather =
                item?.fkFather?.let { viewModel.getRabbitFromId(it) }
        }
    }
}