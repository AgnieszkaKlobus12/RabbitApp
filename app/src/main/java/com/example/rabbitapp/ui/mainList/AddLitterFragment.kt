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
import com.example.rabbitapp.databinding.FragmentAddLitterBinding
import com.example.rabbitapp.model.entities.Litter
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class AddLitterFragment : Fragment() {
    private var _binding: FragmentAddLitterBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainListViewModel by activityViewModels()

    val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddLitterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val formattedDate = LocalDate.now().format(formatter)
        binding.addLitterDate.text = Editable.Factory.getInstance().newEditable(formattedDate)
        binding.addLitterSaveButton.setOnClickListener(saveLitter())
    }

    private fun saveLitter(): View.OnClickListener {
        return View.OnClickListener { view ->
            viewModel.save(
                Litter(
                    0,
                    binding.addLitterName.text.toString(),
                    LocalDate.parse(binding.addLitterDate.text.toString(), formatter).toEpochDay(),
                    Integer.parseInt(binding.addLitterNumber.text.toString())
                ) //todo add parents
            )
            view.findNavController().navigate(R.id.action_addLitterFragment_to_navigation_home)

        }
    }

}