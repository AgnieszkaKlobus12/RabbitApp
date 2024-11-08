package com.example.rabbitapp.ui.mainTab.show

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import com.example.rabbitapp.R
import com.example.rabbitapp.databinding.FragmentLitterDetailsBinding
import com.example.rabbitapp.model.entities.HomeListItem
import com.example.rabbitapp.model.entities.Rabbit
import com.example.rabbitapp.model.entities.relations.Sick
import com.example.rabbitapp.model.entities.relations.Vaccinated
import com.example.rabbitapp.ui.mainTab.add.FragmentWithPicture
import com.example.rabbitapp.ui.mainTab.mainList.MainListAdapter
import com.example.rabbitapp.ui.mainTab.mainList.OnSelectedItem
import com.example.rabbitapp.ui.mainTab.parent.ParentSelectService
import com.example.rabbitapp.ui.sicknesses.OnSelectedSick
import com.example.rabbitapp.ui.sicknesses.SickListAdapter
import com.example.rabbitapp.ui.vaccines.OnSelectedVaccination
import com.example.rabbitapp.ui.vaccines.VaccinationsListAdapter
import com.example.rabbitapp.utils.RabbitDetails


class LitterDetailsFragment : FragmentWithPicture() {

    private var _binding: FragmentLitterDetailsBinding? = null
    private val binding get() = _binding!!
    private val parentSelectService: ParentSelectService = ParentSelectService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (viewModel.getEditable()) {
            inflater.inflate(R.menu.litter_details_menu, menu)
        }
        if (viewModel.selectedLitter?.deathDate != null) {
            val item = menu.findItem(R.id.navigation_kill_litter)
            item.setVisible(false)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.navigation_add_rabbit -> {
                if (viewModel.getAllRabbitFromLitter(viewModel.selectedLitter!!.id).size < viewModel.selectedLitter?.size!!) {
                    view?.findNavController()
                        ?.navigate(R.id.action_litterDetailsFragment_to_addRabbitFragment)
                } else {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.cannot_add_more_rabbits_to_litter),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                true
            }

            R.id.navigation_litter_sickness -> {
                view?.findNavController()
                    ?.navigate(
                        LitterDetailsFragmentDirections.actionLitterDetailsFragmentToSicknessListFragment(
                            0L,
                            viewModel.selectedLitter!!.id
                        )
                    )
                true
            }

            R.id.navigation_vaccinate_litter -> {
                view?.findNavController()
                    ?.navigate(
                        LitterDetailsFragmentDirections.actionLitterDetailsFragmentToVaccineListFragment(
                            0L,
                            viewModel.selectedLitter!!.id
                        )
                    )
                true
            }

            R.id.navigation_kill_litter -> {
                view?.findNavController()
                    ?.navigate(
                        LitterDetailsFragmentDirections.actionLitterDetailsFragmentToMarkDead(
                            0L, viewModel.selectedLitter!!.id
                        )
                    )
                true
            }

            R.id.navigation_edit_litter -> {
                view?.findNavController()
                    ?.navigate(R.id.action_litterDetailsFragment_to_addLitterFragment)
                true
            }

            R.id.navigation_delete_litter -> {
                val alertDialog = requireActivity().let {
                    val builder = AlertDialog.Builder(it)
                    builder.apply {
                        setPositiveButton(R.string.ok) { dialog, _ ->
                            dialog.dismiss()
                            viewModel.deleteCurrentlySelectedLitter()
                            view?.findNavController()
                                ?.navigate(R.id.action_litterDetailsFragment_to_navigation_home)
                        }
                        setNegativeButton(R.string.cancel) { dialog, _ ->
                            dialog.dismiss()
                        }
                        setTitle(R.string.alert)
                        setMessage(R.string.confirm_delete_litter_message)
                    }
                    builder.create()
                }
                alertDialog.show()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLitterDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setPictureToSelectedOrDefault(
            binding.litterDetailsImage,
            viewModel.selectedLitter,
            R.drawable.rabbit_2_back
        )

        binding.litterDetailsAge.text =
            viewModel.selectedLitter?.let { RabbitDetails.getAge(it.birth) }
        binding.litterDetailsName.text = viewModel.selectedLitter?.name
        binding.litterDetailsAmount.text = viewModel.selectedLitter?.size.toString()
        binding.litterDetailsBirth.text =
            viewModel.selectedLitter?.let { RabbitDetails.getDateString(it.birth) }
        if (viewModel.selectedLitter?.deathDate != null) {
            binding.litterDetailsDeathRow.visibility = View.VISIBLE
            binding.litterDetailsDeath.text =
                viewModel.selectedLitter?.let { RabbitDetails.getDateString(it.deathDate!!) }
        } else {
            binding.litterDetailsDeathRow.visibility = View.GONE
        }
        if (viewModel.selectedLitter?.cageNumber != null) {
            binding.litterDetailsCageNumber.text = viewModel.selectedLitter?.cageNumber.toString()
        } else {
            binding.litterDetailsCageNumber.text = ""
        }
        viewModel.selectedLitter?.let {
            parentSelectService.displayParentOrUnknown(
                it.fkMother, it.fkFather,
                childFragmentManager,
                viewModel
            )
        }
        viewModel.selectedLitter?.id?.let {
            val rabbitList = viewModel.getAllRabbitFromLitter(it)
            if (rabbitList.isNotEmpty()) {
                binding.fragmentLitterDetailsIncludeLitterItems.root.visibility = View.VISIBLE
                binding.fragmentLitterDetailsIncludeLitterItems.fragmentLitterContainsRabbitList.fragmentListRecyclerView.adapter =
                    MainListAdapter(rabbitList, object : OnSelectedItem {
                        override fun onItemClick(item: HomeListItem) {
                            if (item is Rabbit) {
                                viewModel.selectedRabbit = item
                                view.findNavController()
                                    .navigate(R.id.acton_litterDetailsFragment_to_rabbitDetailsFragment)
                            }
                        }
                    })
            }
        }

        viewModel.selectedLitter?.id?.let {
            val sicknesses = viewModel.getAllSicknessesForLitter(it)
            if (sicknesses.isNotEmpty()) {
                binding.fragmentLitterDetailsIncludeSicknesses.root.visibility = View.VISIBLE
                binding.fragmentLitterDetailsIncludeSicknesses.fragmentSicknessesListRecyclerView.adapter =
                    SickListAdapter(
                        viewModel,
                        sicknesses,
                        object : OnSelectedSick {
                            override fun onItemClick(item: Sick) {
                                view.findNavController()
                                    .navigate(
                                        LitterDetailsFragmentDirections.actionLitteretailsFragmentToSickDetailsFragment(
                                            item.id
                                        )
                                    )
                            }
                        })
            }
        }

        viewModel.selectedLitter?.id?.let {
            val vaccinations = viewModel.getAllVaccinationsForLitter(it)
            if (vaccinations.isNotEmpty()) {
                binding.fragmentLitterDetailsIncludeVaccinations.root.visibility = View.VISIBLE
                binding.fragmentLitterDetailsIncludeVaccinations.fragmentVaccinationsListRecyclerView.adapter =
                    VaccinationsListAdapter(
                        viewModel,
                        viewModel.getAllVaccinationsForLitter(viewModel.selectedLitter!!.id),
                        object : OnSelectedVaccination {
                            override fun onItemClick(item: Vaccinated) {
                                view.findNavController()
                                    .navigate(
                                        LitterDetailsFragmentDirections.actionLitterDetailsFragmentToVaccinateDetailsFragment(
                                            item.id
                                        )
                                    )
                            }
                        })
            }
        }
    }

}