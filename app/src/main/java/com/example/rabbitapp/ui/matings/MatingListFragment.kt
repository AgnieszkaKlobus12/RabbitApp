package com.example.rabbitapp.ui.matings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.example.rabbitapp.R
import com.example.rabbitapp.databinding.FragmentMatingBinding
import com.example.rabbitapp.model.entities.relations.Mating
import com.example.rabbitapp.utils.MainListViewModel

class MatingListFragment : Fragment() {

    private var _binding: FragmentMatingBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainListViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMatingBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fragmentMatingListRecyclerView.adapter =
            MatingListAdapter(
                viewModel,
                viewModel.getAllNotArchivedMating(),
                object : OnSelectedMating {
                override fun onItemClick(item: Mating) {
                    view.findNavController()
                        .navigate(
                            MatingListFragmentDirections.actionNavigationHomeToDetailsMatingFragment(
                                item.id
                            )
                        )
                }
            })

        binding.addNewMattingButton.setOnClickListener {
            if (!viewModel.getEditable()) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.non_editable), Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            it.findNavController().navigate(R.id.action_navigation_home_to_addMatingFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}