package com.example.rabbitapp.ui.sicknesses

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.example.rabbitapp.R
import com.example.rabbitapp.databinding.FragmentSicknessesListBinding
import com.example.rabbitapp.model.entities.Sickness
import com.example.rabbitapp.ui.mainTab.MainListViewModel


class SicknessesListFragment : Fragment() {

    private var _binding: FragmentSicknessesListBinding? = null
    private val viewModel: MainListViewModel by activityViewModels()
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSicknessesListBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fragmentSicknessesListRecyclerView.adapter =
            SicknessListAdapter(viewModel.getAllSicknesses(), object : OnSelectedSickness {
                override fun onItemClick(item: Sickness) {
                    view.findNavController()
                        .navigate(
                            SicknessesListFragmentDirections.actionNavigationSicknessesToSicknessDetailsFragment(
                                item.id
                            )
                        )
                }
            })

        binding.addNewSicknessButton.setOnClickListener {
            view.findNavController()
                .navigate(R.id.action_navigation_sicknesses_to_editSicknessFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}