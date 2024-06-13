package com.example.rabbitapp.ui.mainTab.show

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.example.rabbitapp.R
import com.example.rabbitapp.databinding.FragmentRabbitDetailsBinding
import com.example.rabbitapp.ui.mainTab.add.FragmentWithPicture
import com.example.rabbitapp.ui.mainTab.parent.ParentSelectService
import com.example.rabbitapp.utils.Gender
import com.example.rabbitapp.utils.RabbitDetails


class RabbitDetailsFragment : FragmentWithPicture() {

    private var _binding: FragmentRabbitDetailsBinding? = null
    private val binding get() = _binding!!
    private val parentSelectService: ParentSelectService = ParentSelectService()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRabbitDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setPictureToSelectedOrDefault(
            binding.rabbitDetailsImage,
            viewModel.selectedRabbit,
            R.drawable.rabbit_back
        );
        binding.rabbitDetailsAge.text =
            viewModel.selectedRabbit?.let { RabbitDetails.getAge(it.birth) }
        binding.rabbitDetailsName.text = viewModel.selectedRabbit?.name
        binding.rabbitDetailsNumber.text = viewModel.selectedRabbit?.earNumber
        binding.rabbitDetailsSex.text =
            getGenderTranslated(viewModel.selectedRabbit?.sex)
        binding.rabbitDetailsBirth.text =
            viewModel.selectedRabbit?.let { RabbitDetails.getBirthDateString(it.birth) }

        viewModel.selectedRabbit?.let {
            parentSelectService.displayParentOrUnknown(
                it,
                childFragmentManager,
                viewModel
            )
        }

        if (viewModel.selectedRabbit?.fkLitter != null) {
            viewModel.selectedLitter =
                viewModel.getLitterFromId(viewModel.selectedRabbit!!.fkLitter!!)
            binding.fragmentAddRabbitBelongTo.root.visibility = View.VISIBLE
            binding.fragmentAddRabbitBelongTo.fragmentBelongsToLitterItem.homeListItemAge.text =
                viewModel.selectedLitter?.let { RabbitDetails.getAge(it.birth) }
            binding.fragmentAddRabbitBelongTo.fragmentBelongsToLitterItem.homeListItemName.text =
                viewModel.selectedLitter?.name
            setPictureToSelectedOrDefault(
                binding.fragmentAddRabbitBelongTo.fragmentBelongsToLitterItem.homeListItemPicture,
                viewModel.selectedLitter,
                R.drawable.rabbit_2_back
            )
            Log.d("RabbitDetailsFragment", "Litter added ${viewModel.selectedLitter}")
        }

        binding.rabbitDetailsDeleteButton.setOnClickListener(deleteRabbit())
        binding.rabbitDetailsEditButton.setOnClickListener(moveToEditRabbit())
    }

    private fun moveToEditRabbit(): View.OnClickListener {
        return View.OnClickListener { view ->
            view.findNavController()
                .navigate(R.id.action_rabbitDetailsFragment_to_addRabbitFragment)
        }
    }

    private fun deleteRabbit(): View.OnClickListener {
        return View.OnClickListener {
            val alertDialog = requireActivity().let {
                val builder = AlertDialog.Builder(it)
                builder.apply {
                    setPositiveButton(R.string.ok) { dialog, _ ->
                        dialog.dismiss()
                        viewModel.deleteCurrentlySelectedRabbit()
                        activity?.supportFragmentManager?.popBackStack()
                    }
                    setNegativeButton(R.string.cancel) { dialog, _ ->
                        dialog.dismiss()
                    }
                    setTitle(R.string.alert)
                    setMessage(R.string.confirm_delete_rabbit_message)
                }
                builder.create()
            }
            alertDialog.show()
        }
    }

    private fun getGenderTranslated(gender: String?): String {
        if (gender == Gender.MALE.name) {
            return getString(R.string.male)
        } else if (gender == Gender.FEMALE.name) {
            return getString(R.string.female)
        }
        return getString(R.string.unknown)
    }

}