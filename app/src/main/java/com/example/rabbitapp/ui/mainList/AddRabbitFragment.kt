package com.example.rabbitapp.ui.mainList

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.example.rabbitapp.R
import com.example.rabbitapp.databinding.FragmentAddRabbitBinding
import com.example.rabbitapp.model.entities.Rabbit
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class AddRabbitFragment : Fragment() {
    private var _binding: FragmentAddRabbitBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainListViewModel by activityViewModels()

    val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddRabbitBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val formattedDate = LocalDate.now().format(formatter)
        binding.addRabbitDate.text = Editable.Factory.getInstance().newEditable(formattedDate)
        binding.addRabbitSaveButton.setOnClickListener(saveRabbit())
    }

    private fun saveRabbit(): View.OnClickListener? {
        return View.OnClickListener { view ->
            viewModel.save(
                Rabbit(
                    0,
                    binding.addRabbitName.text.toString(),
                    LocalDate.parse(binding.addRabbitDate.text.toString(), formatter).toEpochDay(),
                    getRabbitGender()
                ) //todo add parents
            )
            view.findNavController().navigate(R.id.action_addRabbitFragment_to_navigation_home)

        }
    }

    private fun getRabbitGender(): String {
        return if (binding.addRabbitGenderFemale.isChecked) {
            binding.addRabbitGenderFemale.text.toString()
        } else {
            binding.addRabbitGenderMale.text.toString()
        }
    }

}