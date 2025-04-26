package com.example.rabbitapp.ui.matings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.example.rabbitapp.R
import com.example.rabbitapp.databinding.FragmentMatingBinding
import com.example.rabbitapp.model.entities.relations.Mating
import com.example.rabbitapp.utils.MainViewModel

class MatingListFragment : Fragment() {

    private var _binding: FragmentMatingBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var matingListAdapter: MatingListAdapter
    private val onSelectedItem = object : OnSelectedMating {
        override fun onItemClick(item: Mating) {
            view?.findNavController()
                ?.navigate(
                    MatingListFragmentDirections.actionNavigationHomeToDetailsMatingFragment(
                        item.id
                    )
                )
        }
    }

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
        viewModel.clearSelected()
        matingListAdapter =
            MatingListAdapter(viewModel, viewModel.getAllNotArchivedMating(), onSelectedItem)
        binding.fragmentMatingListRecyclerView.adapter = matingListAdapter

        binding.addNewMattingButton.setOnClickListener {
            it.findNavController().navigate(R.id.action_navigation_home_to_addMatingFragment)
        }

        binding.filterFrame.setOnClickListener {
            if (binding.categoriesMatings.visibility == View.VISIBLE) {
                binding.categoriesMatings.visibility = View.GONE
                binding.filterButton.contentDescription = getString(R.string.filter)
                binding.filterButton.setImageResource(R.drawable.icon_filter)
            } else {
                binding.categoriesMatings.visibility = View.VISIBLE
                binding.filterButton.contentDescription = getString(R.string.close)
                binding.filterButton.setImageResource(R.drawable.icon_close)
            }
        }

        sort()
        binding.bornChip.setOnCheckedChangeListener(filter())
        binding.notBornChip.setOnCheckedChangeListener(filter())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun sort() {
        binding.matingDateChip.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                matingListAdapter.updateData(matingListAdapter.getData().sortedBy { it.matingDate })
            }
        }
        binding.bornDateChip.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                matingListAdapter.updateData(matingListAdapter.getData().sortedBy { it.birthDate })
            }
        }
    }

    private fun filter(): CompoundButton.OnCheckedChangeListener {
        return CompoundButton.OnCheckedChangeListener { _, _ ->
            var items = viewModel.getAllNotArchivedMating()
            if (binding.bornChip.isChecked) {
                items = items.filter { it.fkLitter != null }
            }
            if (binding.notBornChip.isChecked) {
                items = items.filter { it.fkLitter == null }
            }
            if (binding.matingDateChip.isChecked) {
                items = items.sortedBy { it.matingDate }
            }
            if (binding.bornDateChip.isChecked) {
                items = items.sortedBy { it.birthDate }
            }
            matingListAdapter.updateData(items)
        }
    }
}