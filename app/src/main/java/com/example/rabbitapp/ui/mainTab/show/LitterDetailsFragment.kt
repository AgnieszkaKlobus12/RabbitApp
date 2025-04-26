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
import androidx.navigation.fragment.navArgs
import com.example.rabbitapp.R
import com.example.rabbitapp.databinding.FragmentLitterDetailsBinding
import com.example.rabbitapp.model.entities.HomeListItem
import com.example.rabbitapp.model.entities.Litter
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

    private val args: LitterDetailsFragmentArgs by navArgs()

    private var litter: Litter? = null
    private var _binding: FragmentLitterDetailsBinding? = null
    private val binding get() = _binding!!
    private val parentSelectService: ParentSelectService = ParentSelectService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.litter_details_menu, menu)
        if (litter?.deathDate != null) {
            val item = menu.findItem(R.id.navigation_kill_litter)
            item.setVisible(false)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.navigation_add_rabbit -> {
                if (viewModel.getAllRabbitFromLitter(litter!!.id).size < litter?.size!!) {
                    view?.findNavController()
                        ?.navigate(LitterDetailsFragmentDirections.actionLitterDetailsFragmentToAddRabbitFragment())
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
                            litter!!.id
                        )
                    )
                true
            }

            R.id.navigation_vaccinate_litter -> {
                view?.findNavController()
                    ?.navigate(
                        LitterDetailsFragmentDirections.actionLitterDetailsFragmentToVaccineListFragment(
                            0L,
                            litter!!.id
                        )
                    )
                true
            }

            R.id.navigation_kill_litter -> {
                view?.findNavController()
                    ?.navigate(
                        LitterDetailsFragmentDirections.actionLitterDetailsFragmentToMarkDead(
                            0L, litter!!.id
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
                            viewModel.deleteLitter(litter!!.id)
                            dialog.dismiss()
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
        val root: View = binding.root
        if (args.litterId != 0L) {
            litter = viewModel.getLitterFromId(args.litterId)
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setPictureToSelectedOrDefault(
            binding.litterDetailsImage,
            litter,
            R.drawable.rabbit_2_back
        )

        binding.litterDetailsAge.text =
            litter?.let { RabbitDetails.getAge(it.birth) }
        binding.litterDetailsName.text = litter?.name
        binding.litterDetailsAmount.text = litter?.size.toString()
        binding.litterDetailsBirth.text =
            litter?.let { RabbitDetails.getDateString(it.birth) }
        if (litter?.deathDate != null) {
            binding.litterDetailsDeathRow.visibility = View.VISIBLE
            binding.litterDetailsDeath.text =
                litter?.let { RabbitDetails.getDateString(it.deathDate!!) }
        } else {
            binding.litterDetailsDeathRow.visibility = View.GONE
        }
        if (litter?.cageNumber != null) {
            binding.litterDetailsCageNumber.text = litter?.cageNumber.toString()
        } else {
            binding.litterDetailsCageNumber.text = ""
        }
        litter?.let {
            parentSelectService.displayParentOrUnknown(
                it.fkMother, it.fkFather,
                childFragmentManager,
                viewModel
            )
            if (it.fkMother != null) {
                binding.fragmentLitterDetailsIncludeParents.addMotherFragment.setOnClickListener {
                    view.findNavController()
                        .navigate(
                            LitterDetailsFragmentDirections.actonLitterDetailsFragmentToRabbitDetailsFragment(
                                litter!!.fkMother!!
                            )
                        )
                }
            }
            if (it.fkFather != null) {
                binding.fragmentLitterDetailsIncludeParents.addFatherFragment.setOnClickListener {
                    view.findNavController()
                        .navigate(
                            LitterDetailsFragmentDirections.actonLitterDetailsFragmentToRabbitDetailsFragment(
                                litter!!.fkFather!!
                            )
                        )
                }
            }
        }
        litter?.id?.let {
            val rabbitList = viewModel.getAllRabbitFromLitter(it)
            if (rabbitList.isNotEmpty()) {
                binding.fragmentLitterDetailsIncludeLitterItems.root.visibility = View.VISIBLE
                binding.fragmentLitterDetailsIncludeLitterItems.fragmentLitterContainsRabbitList.fragmentListRecyclerView.adapter =
                    MainListAdapter(rabbitList, object : OnSelectedItem {
                        override fun onItemClick(item: HomeListItem) {
                            if (item is Rabbit) {
                                view.findNavController()
                                    .navigate(
                                        LitterDetailsFragmentDirections.actonLitterDetailsFragmentToRabbitDetailsFragment(
                                            item.id
                                        )
                                    )
                            }
                        }
                    })
            }
        }

        litter?.id?.let {
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

        litter?.id?.let {
            val vaccinations = viewModel.getAllVaccinationsForLitter(it)
            if (vaccinations.isNotEmpty()) {
                binding.fragmentLitterDetailsIncludeVaccinations.root.visibility = View.VISIBLE
                binding.fragmentLitterDetailsIncludeVaccinations.fragmentVaccinationsListRecyclerView.adapter =
                    VaccinationsListAdapter(
                        viewModel,
                        viewModel.getAllVaccinationsForLitter(litter!!.id),
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