package com.example.rabbitapp.ui.mainTab.show

import android.app.AlertDialog
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.example.rabbitapp.R
import com.example.rabbitapp.databinding.FragmentRabbitDetailsBinding
import com.example.rabbitapp.ui.mainTab.HomeListItemFragment
import com.example.rabbitapp.ui.mainTab.MainListViewModel
import com.example.rabbitapp.utils.Gender
import com.example.rabbitapp.utils.RabbitDetails


class RabbitDetailsFragment : Fragment() {

    private var _binding: FragmentRabbitDetailsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainListViewModel by activityViewModels()

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
        setPictureToSelectedOrDefault();
        binding.rabbitDetailsAge.text =
            viewModel.selectedRabbit?.let { RabbitDetails.getAge(it.birth) }
        binding.rabbitDetailsName.text = viewModel.selectedRabbit?.name
        binding.rabbitDetailsNumber.text = viewModel.selectedRabbit?.earNumber
        binding.rabbitDetailsSex.text =
            getGenderTranslated(viewModel.selectedRabbit?.sex)
        binding.rabbitDetailsBirth.text =
            viewModel.selectedRabbit?.let { RabbitDetails.getBirthDateString(it.birth) }

        val transaction: FragmentTransaction = childFragmentManager.beginTransaction()
        if (viewModel.selectedRabbit?.fkMother != null) {
            binding.rabbitDetailsMotherFragment.visibility = View.VISIBLE
            binding.rabbitDetailsMotherUnknown.visibility = View.GONE
            val selectedMotherFragment =
                HomeListItemFragment(viewModel.getRabbitFromId(viewModel.selectedRabbit!!.fkMother!!)!!)
            transaction.replace(R.id.rabbit_details_mother_fragment, selectedMotherFragment)
        } else {
            binding.rabbitDetailsMotherFragment.visibility = View.GONE
            binding.rabbitDetailsMotherUnknown.visibility = View.VISIBLE
        }

        if (viewModel.selectedRabbit?.fkFather != null) {
            binding.rabbitDetailsFatherFragment.visibility = View.VISIBLE
            binding.rabbitDetailsFatherUnknown.visibility = View.GONE
            val selectedFatherFragment =
                HomeListItemFragment(viewModel.getRabbitFromId(viewModel.selectedRabbit!!.fkFather!!)!!)
            transaction.replace(R.id.rabbit_details_father_fragment, selectedFatherFragment)
        } else {
            binding.rabbitDetailsFatherFragment.visibility = View.GONE
            binding.rabbitDetailsFatherUnknown.visibility = View.VISIBLE
        }
        transaction.commit()

        binding.rabbitDetailsDeleteButton.setOnClickListener(deleteRabbit())
        binding.rabbitDetailsEditButton.setOnClickListener(moveToEditRabbit())
    }

    private fun moveToEditRabbit(): View.OnClickListener {
        return View.OnClickListener { view ->
            view.findNavController().navigate(R.id.action_rabbitDetailsFragment_to_addRabbitFragment)
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

    private fun setPictureToSelectedOrDefault() {
        if (viewModel.selectedRabbit?.imagePath != null && viewModel.selectedRabbit?.imagePath!!.isNotEmpty()) {
            binding.rabbitDetailsImage.setImageBitmap(BitmapFactory.decodeFile(viewModel.selectedRabbit?.imagePath!!))
        } else {
            binding.rabbitDetailsImage.setImageResource(R.drawable.rabbit_back)
        }
    }
}