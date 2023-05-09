package com.example.rabbitapp.ui.mainTab.show

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.example.rabbitapp.R
import com.example.rabbitapp.databinding.FragmentLitterDetailsBinding
import com.example.rabbitapp.ui.mainTab.HomeListItem
import com.example.rabbitapp.ui.mainTab.MainListViewModel
import com.example.rabbitapp.utils.RabbitDetails


class LitterDetailsFragment : Fragment() {

    private var _binding: FragmentLitterDetailsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainListViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLitterDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.litterDetailsAge.text =
            viewModel.selectedLitter?.let { RabbitDetails.getAge(it.birth) }
        binding.litterDetailsName.text = viewModel.selectedLitter?.name
        binding.litterDetailsAmount.text = viewModel.selectedLitter?.size.toString()
        binding.litterDetailsBirth.text =
            viewModel.selectedLitter?.let { RabbitDetails.getBirthDateString(it.birth) }

        val transaction: FragmentTransaction = childFragmentManager.beginTransaction()
        if (viewModel.selectedLitter?.FkMother != null) {
            binding.litterDetailsMotherFragment.visibility = View.VISIBLE
            binding.litterDetailsMotherUnknown.visibility = View.GONE
            val selectedMotherFragment =
                HomeListItem(viewModel.getRabbitFromId(viewModel.selectedLitter!!.FkMother!!)!!)
            transaction.replace(R.id.litter_details_mother_fragment, selectedMotherFragment)
        } else {
            binding.litterDetailsMotherFragment.visibility = View.GONE
            binding.litterDetailsMotherUnknown.visibility = View.VISIBLE
        }

        if (viewModel.selectedLitter?.FkFather != null) {
            binding.litterDetailsFatherFragment.visibility = View.VISIBLE
            binding.litterDetailsFatherUnknown.visibility = View.GONE
            val selectedFatherFragment =
                HomeListItem(viewModel.getRabbitFromId(viewModel.selectedLitter!!.FkFather!!)!!)
            transaction.replace(R.id.litter_details_father_fragment, selectedFatherFragment)
        } else {
            binding.litterDetailsFatherFragment.visibility = View.GONE
            binding.litterDetailsFatherUnknown.visibility = View.VISIBLE
        }
        transaction.commit()

        binding.litterDetailsDeleteButton.setOnClickListener(deleteLitter())
        binding.litterDetailsEditButton.setOnClickListener(moveToEditLitter())
    }

    private fun moveToEditLitter(): View.OnClickListener {
        return View.OnClickListener { view ->
            view.findNavController()
                .navigate(R.id.action_litterDetailsFragment_to_addLitterFragment)
        }
    }

    private fun deleteLitter(): View.OnClickListener {
        return View.OnClickListener {
            val alertDialog = requireActivity().let {
                val builder = AlertDialog.Builder(it)
                builder.apply {
                    setPositiveButton(R.string.ok) { dialog, _ ->
                        dialog.dismiss()
                        viewModel.deleteCurrentlySelectedLitter()
                        activity?.supportFragmentManager?.popBackStack()
                    }
                    setNegativeButton(R.string.cancel) { dialog, _ ->
                        dialog.dismiss()
                    }
                    setTitle(R.string.alert)
                    setMessage(R.string.confirm_delete_litter_message)
                }
                builder.create()
            }
            alertDialog.show()
        }
    }

}