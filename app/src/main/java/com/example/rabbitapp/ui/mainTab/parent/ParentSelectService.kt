package com.example.rabbitapp.ui.mainTab.parent

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import com.example.rabbitapp.R
import com.example.rabbitapp.databinding.PickParentsFragmentBinding
import com.example.rabbitapp.ui.mainTab.HomeListItemFragment
import com.example.rabbitapp.ui.mainTab.add.PickButtonFragment
import com.example.rabbitapp.ui.mainTab.add.StartSelect
import com.example.rabbitapp.utils.Gender
import com.example.rabbitapp.utils.MainListViewModel

class ParentSelectService {

    fun displaySelectParentFragment(
        actionMother: NavDirections,
        actionFather: NavDirections,
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
                        actionMother,
                        actionFather,
                        gender,
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
                        actionMother,
                        actionFather,
                        gender,
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
        fkMother: Long?, fkFather: Long?,
        childFragmentManager: FragmentManager,
        viewModel: MainListViewModel,
    ) {
        val transaction: FragmentTransaction = childFragmentManager.beginTransaction()
        if (fkMother != null) {
            val selectedMotherFragment =
                HomeListItemFragment(viewModel.getRabbitFromId(fkMother)!!)
            transaction.replace(R.id.add_mother_fragment, selectedMotherFragment)
        } else {
            val unknownParentFragment = UnknownParentFragment()
            transaction.replace(R.id.add_mother_fragment, unknownParentFragment)
        }

        if (fkFather != null) {
            val selectedFatherFragment =
                HomeListItemFragment(viewModel.getRabbitFromId(fkFather)!!)
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
        actionMother: NavDirections,
        actionFather: NavDirections,
        childFragmentManager: FragmentManager,
        view: View?,
        binding: PickParentsFragmentBinding,
    ) {
        binding.addMotherFragment.setOnClickListener {
            parentSelect(
                actionMother,
                actionFather,
                Gender.FEMALE,
                childFragmentManager,
                view
            )
        }
        binding.addFatherFragment.setOnClickListener {
            parentSelect(
                actionMother,
                actionFather,
                Gender.MALE,
                childFragmentManager,
                view
            )
        }
    }

    fun parentSelect(
        actionMother: NavDirections,
        actionFather: NavDirections,
        parentGender: Gender?,
        childFragmentManager: FragmentManager, view: View?
    ) {
        val transaction: FragmentTransaction = childFragmentManager.beginTransaction()
        if (parentGender == Gender.FEMALE) {
            view?.findNavController()
                ?.navigate(actionMother)
        }
        if (parentGender == Gender.MALE) {
            view?.findNavController()
                ?.navigate(actionFather)
        }
        transaction.commit()
    }

    fun setParents(viewModel: MainListViewModel, motherId: Long?, fatherId: Long?) {
        if (viewModel.selectedMother == null) {
            viewModel.selectedMother =
                motherId?.let { viewModel.getRabbitFromId(it) }
        }
        if (viewModel.selectedFather == null) {
            viewModel.selectedFather =
                fatherId?.let { viewModel.getRabbitFromId(it) }
        }
    }
}