package com.example.rabbitapp.ui.sicknesses

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.rabbitapp.R
import com.example.rabbitapp.databinding.FragmentSicknessesListBinding
import com.example.rabbitapp.model.entities.Sickness
import com.example.rabbitapp.utils.MainListViewModel


class SicknessesListFragment : Fragment() {

    private val args: SicknessesListFragmentArgs by navArgs()

    private var _binding: FragmentSicknessesListBinding? = null
    private val viewModel: MainListViewModel by activityViewModels()
    private val binding get() = _binding!!
    private lateinit var sicknessListAdapter: SicknessListAdapter
    private val onSelectedItem = object : OnSelectedSickness {
        override fun onItemClick(item: Sickness) {
            if (args.rabbitId == 0L && args.litterId == 0L) {
                view?.findNavController()
                    ?.navigate(
                        SicknessesListFragmentDirections.actionNavigationSicknessesToSicknessDetailsFragment(
                            item.id
                        )
                    )
            } else {
                if (!viewModel.getEditable()) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.non_editable), Toast.LENGTH_SHORT
                    ).show()
                    return
                }
                view?.findNavController()
                    ?.navigate(
                        SicknessesListFragmentDirections.actionNavigationSicknessesToAddSicknessFragment(
                            args.rabbitId,
                            args.litterId,
                            item.id
                        )
                    )
            }
        }
    }

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

        sicknessListAdapter = SicknessListAdapter(viewModel.getAllSicknesses(), onSelectedItem)
        binding.fragmentSicknessesListRecyclerView.adapter = sicknessListAdapter

        binding.addNewSicknessButton.setOnClickListener {
            if (!viewModel.getEditable()) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.non_editable), Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            view.findNavController()
                .navigate(R.id.action_navigation_sicknesses_to_editSicknessFragment)
        }

        binding.filterFrame.setOnClickListener {
            if (binding.categoriesSickness.visibility == View.VISIBLE) {
                binding.categoriesSickness.visibility = View.GONE
                binding.filterButton.contentDescription = getString(R.string.filter)
                binding.filterButton.setImageResource(R.drawable.icon_filter)
            } else {
                binding.categoriesSickness.visibility = View.VISIBLE
                binding.filterButton.contentDescription = getString(R.string.close)
                binding.filterButton.setImageResource(R.drawable.icon_close)
            }
        }

        sort()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun sort() {
        binding.nameChip.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                sicknessListAdapter.updateData(sicknessListAdapter.getData().sortedBy { it.name })
            }
        }
    }

}