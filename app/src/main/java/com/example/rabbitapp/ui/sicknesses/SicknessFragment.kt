package com.example.rabbitapp.ui.sicknesses

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.rabbitapp.R
import com.example.rabbitapp.databinding.FragmentSicknessBinding
import com.example.rabbitapp.model.entities.HomeListItem
import com.example.rabbitapp.model.entities.Sickness
import com.example.rabbitapp.ui.mainTab.mainList.MainListAdapter
import com.example.rabbitapp.ui.mainTab.mainList.OnSelectedItem
import com.example.rabbitapp.utils.MainViewModel

class SicknessFragment : Fragment() {

    private val args: SicknessFragmentArgs by navArgs()
    private var sickness: Sickness? = null

    private var _binding: FragmentSicknessBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSicknessBinding.inflate(inflater, container, false)
        sickness = viewModel.getSickness(args.sicknessId)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.sick_details_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.navigation_edit_sick -> {
                view?.findNavController()
                    ?.navigate(
                        SicknessFragmentDirections.actionNavigationDetailsSicknessToEditSicknessFragment(
                            sickness?.id ?: 0
                        )
                    )
                true
            }

            R.id.navigation_delete_sick -> {
                val alertDialog = requireActivity().let {
                    val builder = AlertDialog.Builder(it)
                    builder.apply {
                        setPositiveButton(R.string.ok) { dialog, _ ->
                            dialog.dismiss()
                            viewModel.deleteSickness(sickness!!.id)
                            view?.findNavController()
                                ?.navigate(R.id.action_navigation_details_sickness_to_navigation_sicknesses)
                        }
                        setNegativeButton(R.string.cancel) { dialog, _ ->
                            dialog.dismiss()
                        }
                        setTitle(R.string.alert)
                        setMessage(R.string.confirm_delete_sickness_message)
                    }
                    builder.create()
                }
                alertDialog.show()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fragmentSicknessSicknessName.text = sickness?.name
        binding.fragmentSicknessEditSicknessTreatment.text = sickness?.treatment
        binding.fragmentSicknessEditSicknessSymptoms.text = sickness?.symptoms

        val sick = viewModel.getAllWithSickness(sickness!!.id)
        if (sick.isNotEmpty()) {
            binding.fragmentSicknessSick.root.visibility = View.VISIBLE
            binding.fragmentSicknessDivider.visibility = View.VISIBLE
        }

        binding.fragmentSicknessSick.fragmentSickListRecyclerView.adapter =
            MainListAdapter(sick, object : OnSelectedItem {
                override fun onItemClick(item: HomeListItem) {
                    //nothing
                }
            })
    }

}