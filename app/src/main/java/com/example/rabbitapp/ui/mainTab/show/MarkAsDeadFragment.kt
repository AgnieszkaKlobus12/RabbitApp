package com.example.rabbitapp.ui.mainTab.show

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.rabbitapp.R
import com.example.rabbitapp.databinding.FragmentMarkAsDeadBinding
import com.example.rabbitapp.model.entities.HomeListItem
import com.example.rabbitapp.ui.mainTab.add.FragmentWithPicture
import com.example.rabbitapp.utils.RabbitDetails
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar


class MarkAsDeadFragment : FragmentWithPicture() {

    private val args: MarkAsDeadFragmentArgs by navArgs()
    private var _binding: FragmentMarkAsDeadBinding? = null
    private val binding get() = _binding!!
    private var item: HomeListItem? = null
    private val dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMarkAsDeadBinding.inflate(inflater, container, false)
        item = if (args.rabbitId != 0L) {
            viewModel.getRabbitFromId(args.rabbitId)
        } else {
            viewModel.getLitterFromId(args.litterId)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.markDeadDate.text = Editable.Factory.getInstance().newEditable(
            LocalDate.now().format(dateFormatter)
        )
        binding.markDeadDate.setOnClickListener {
            showDatePickerDialog()
        }
        binding.markDeadRabbit.homeListItemAge.text = item?.let { RabbitDetails.getAge(it.birth) }
        binding.markDeadRabbit.homeListItemName.text = item?.name
        setPictureToSelectedOrDefault(
            binding.markDeadRabbit.homeListItemPicture,
            item,
            R.drawable.rabbit_back
        )

        binding.markDeadSaveButton.setOnClickListener {
            if (args.rabbitId != 0L) {
                viewModel.markRabbitAsDead(
                    args.rabbitId,
                    LocalDate.parse(binding.markDeadDate.text.toString(), dateFormatter)
                        .toEpochDay()
                )
                viewModel.selectedRabbit = viewModel.getRabbitFromId(args.rabbitId)
                view.findNavController()
                    .navigate(
                        MarkAsDeadFragmentDirections.navigationMarkDeadToRabbitDetailsFragment()
                    )
            } else {
                viewModel.markLitterAsDead(
                    args.litterId,
                    LocalDate.parse(binding.markDeadDate.text.toString(), dateFormatter)
                        .toEpochDay()
                )
                viewModel.selectedLitter = viewModel.getLitterFromId(args.litterId)
                view.findNavController()
                    .navigate(
                        MarkAsDeadFragmentDirections.navigationMarkDeadToLitterDetailsFragment()
                    )
            }
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog =
            DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = LocalDate.of(selectedYear, selectedMonth + 1, selectedDay)
                binding.markDeadDate.text =
                    Editable.Factory.getInstance()
                        .newEditable(selectedDate.format(dateFormatter))
            }, year, month, day)

        datePickerDialog.show()
    }

}