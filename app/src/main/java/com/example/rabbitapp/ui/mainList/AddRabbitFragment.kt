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
import java.util.*


class AddRabbitFragment : Fragment() {
    private var _binding: FragmentAddRabbitBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainListViewModel by activityViewModels()

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

        binding.addRabbitDate.text = Editable.Factory.getInstance().newEditable(Date().toString())
        binding.addRabbitSaveButton.setOnClickListener(saveRabbit())
    }

    private fun saveRabbit(): View.OnClickListener? {
        return View.OnClickListener { view ->
            viewModel.save(
                Rabbit(
                    0,
                    binding.addRabbitName.text.toString(),
                    Date(binding.addRabbitDate.text.toString()).toInstant().toEpochMilli(),
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