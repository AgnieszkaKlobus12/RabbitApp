package com.example.rabbitapp.ui.mainTab.mainList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.example.rabbitapp.R
import com.example.rabbitapp.databinding.FragmentMainListBinding
import com.example.rabbitapp.model.entities.HomeListItem
import com.example.rabbitapp.model.entities.Litter
import com.example.rabbitapp.model.entities.Rabbit
import com.example.rabbitapp.utils.MainListViewModel

class MainListFragment : Fragment() {

    private var _binding: FragmentMainListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainListViewModel by activityViewModels()
    private lateinit var mainListAdapter: MainListAdapter
    private val onSelectedItem = object : OnSelectedItem {
        override fun onItemClick(item: HomeListItem) {
            if (item is Rabbit) {
                viewModel.selectedRabbit = item
                view?.findNavController()
                    ?.navigate(R.id.action_navigation_home_to_rabbitDetailsFragment)
            } else {
                viewModel.selectedLitter = item as Litter
                view?.findNavController()
                    ?.navigate(R.id.action_navigation_home_to_litterDetailsFragment)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.clearSelected()

        mainListAdapter = MainListAdapter(viewModel.getAll(), onSelectedItem)
        binding.fragmentHomeListInclude.fragmentListRecyclerView.adapter = mainListAdapter

        binding.addNewMainButton.setOnClickListener(showFloatingAddButtons())
        binding.addRabbitButton.setOnClickListener(moveToAddRabbit())
        binding.addLitterButton.setOnClickListener(moveToAddLitter())

        binding.filterFrame.setOnClickListener {
            if (binding.categoriesRabbits.visibility == View.VISIBLE) {
                binding.categoriesRabbits.visibility = View.GONE
                binding.filterButton.contentDescription = getString(R.string.filter)
                binding.filterButton.setImageResource(R.drawable.icon_filter)
            } else {
                binding.categoriesRabbits.visibility = View.VISIBLE
                binding.filterButton.contentDescription = getString(R.string.close)
                binding.filterButton.setImageResource(R.drawable.icon_close)
            }
        }

        sort()
        binding.deadChip.setOnCheckedChangeListener(filter())
        binding.aliveChip.setOnCheckedChangeListener(filter())
        binding.rabbitChip.setOnCheckedChangeListener(filter())
        binding.litterChip.setOnCheckedChangeListener(filter())
    }

    override fun onResume() {
        super.onResume()
        mainListAdapter.updateData(viewModel.getAll())
        filter().onCheckedChanged(null, true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showFloatingAddButtons(): View.OnClickListener {
        return View.OnClickListener {
            if (binding.addLitterButton.visibility == View.GONE) {
                binding.addLitterButton.visibility = View.VISIBLE
            } else {
                binding.addLitterButton.visibility = View.GONE
            }
            if (binding.addRabbitButton.visibility == View.GONE) {
                binding.addRabbitButton.visibility = View.VISIBLE
            } else {
                binding.addRabbitButton.visibility = View.GONE
            }
        }
    }

    private fun moveToAddRabbit(): View.OnClickListener {
        return if (viewModel.getEditable()) {
            View.OnClickListener { view ->
                view.findNavController().navigate(R.id.action_navigation_home_to_addRabbitFragment)
            }
        } else {
            View.OnClickListener {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.non_editable),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun moveToAddLitter(): View.OnClickListener {
        return if (viewModel.getEditable()) {
            View.OnClickListener { view ->
                view.findNavController().navigate(R.id.action_navigation_home_to_addLitterFragment)
            }
        } else {
            View.OnClickListener {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.non_editable),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun sort() {
        binding.numbersChip.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                mainListAdapter.updateData(mainListAdapter.getData().sortedBy { it.earNumber })
            }
        }
        binding.ageChip.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                mainListAdapter.updateData(mainListAdapter.getData().sortedBy { it.birth })
            }
        }
        binding.cageChip.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                mainListAdapter.updateData(mainListAdapter.getData().sortedBy { it.cageNumber })
            }
        }
        binding.nameChip.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                mainListAdapter.updateData(mainListAdapter.getData().sortedBy { it.name })
            }
        }
    }

    private fun filter(): CompoundButton.OnCheckedChangeListener {
        return CompoundButton.OnCheckedChangeListener { _, _ ->
            var items = viewModel.getAll()
            if (binding.deadChip.isChecked) {
                items = items.filter { it.deathDate != null }
            }
            if (binding.aliveChip.isChecked) {
                items = items.filter { it.deathDate == null }
            }
            if (binding.rabbitChip.isChecked) {
                items = items.filter { it.type == "rabbit" }
            }
            if (binding.litterChip.isChecked) {
                items = items.filter { it.type == "litter" }
            }

            if (binding.numbersChip.isChecked) {
                items = items.sortedBy { it.earNumber }
            }
            if (binding.ageChip.isChecked) {
                items = items.sortedBy { it.birth }
            }
            if (binding.cageChip.isChecked) {
                items = items.sortedBy { it.cageNumber }
            }
            if (binding.nameChip.isChecked) {
                items = items.sortedBy { it.name }
            }
            mainListAdapter.updateData(items)
        }
    }

}