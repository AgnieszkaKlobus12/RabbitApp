package com.example.rabbitapp.ui.mainTab.show

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.example.rabbitapp.R
import com.example.rabbitapp.databinding.FragmentRabbitDetailsBinding
import com.example.rabbitapp.model.entities.relations.Mating
import com.example.rabbitapp.model.entities.relations.Sick
import com.example.rabbitapp.model.entities.relations.Vaccinated
import com.example.rabbitapp.ui.mainTab.add.FragmentWithPicture
import com.example.rabbitapp.ui.mainTab.parent.ParentSelectService
import com.example.rabbitapp.ui.matings.MatingListAdapter
import com.example.rabbitapp.ui.matings.OnSelectedMating
import com.example.rabbitapp.ui.sicknesses.OnSelectedSick
import com.example.rabbitapp.ui.sicknesses.SickListAdapter
import com.example.rabbitapp.ui.vaccines.OnSelectedVaccination
import com.example.rabbitapp.ui.vaccines.VaccinationsListAdapter
import com.example.rabbitapp.utils.Gender
import com.example.rabbitapp.utils.RabbitDetails


class RabbitDetailsFragment : FragmentWithPicture() {

    private var _binding: FragmentRabbitDetailsBinding? = null
    private val binding get() = _binding!!
    private val parentSelectService: ParentSelectService = ParentSelectService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.rabbit_details_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.navigation_vaccinate_rabbit -> {
                view?.findNavController()
                    ?.navigate(
                        RabbitDetailsFragmentDirections.actionRabbitDetailsFragmentToVaccineListFragment(
                            viewModel.selectedRabbit!!.id
                        )
                    )
                true
            }

            R.id.navigation_rabbit_sickness -> {
                view?.findNavController()
                    ?.navigate(
                        RabbitDetailsFragmentDirections.actionRabbitDetailsFragmentToSicknessesListFragment(
                            viewModel.selectedRabbit!!.id,
                            0L
                        )
                    )
                true
            }

            R.id.navigation_rabbit_add_mating -> {
                view?.findNavController()
                    ?.navigate(
                        RabbitDetailsFragmentDirections.actionRabbitDetailsFragmentToAddMatingFragment(
                            0L,
                            if (viewModel.selectedRabbit?.sex == "FEMALE") {
                                viewModel.selectedRabbit!!.id
                            } else {
                                0L
                            },
                            if (viewModel.selectedRabbit?.sex == "MALE") {
                                viewModel.selectedRabbit!!.id
                            } else {
                                0L
                            }
                        )
                    )
                true
            }

            R.id.navigation_edit_rabbit -> {
                moveToEditRabbit()
                true
            }

            R.id.navigation_delete_rabbit -> {
                deleteRabbit()
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
        _binding = FragmentRabbitDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.selectedRabbit?.id?.let {
            val vaccinations = viewModel.getAllVaccinationsForRabbit(it)
            if (vaccinations.isNotEmpty()) {
                binding.fragmentRabbitDetailsIncludeVaccinations.root.visibility = View.VISIBLE
                binding.fragmentRabbitDetailsIncludeVaccinations.fragmentVaccinationsListRecyclerView.adapter =
                    VaccinationsListAdapter(
                        viewModel,
                        vaccinations,
                        object : OnSelectedVaccination {
                            override fun onItemClick(item: Vaccinated) {
                                view.findNavController()
                                    .navigate(
                                        RabbitDetailsFragmentDirections.actionRabbitDetailsFragmentToVaccineFragment(
                                            item.fkVaccine
                                        )
                                    )
                            }
                        })
            }
        }

        viewModel.selectedRabbit?.id?.let {
            val sicknesses = viewModel.getAllSicknessesForRabbit(it)
            if (sicknesses.isNotEmpty()) {
                binding.fragmentRabbitDetailsIncludeSicknesses.root.visibility = View.VISIBLE
                binding.fragmentRabbitDetailsIncludeSicknesses.fragmentSicknessesListRecyclerView.adapter =
                    SickListAdapter(
                        viewModel,
                        sicknesses,
                        object : OnSelectedSick {
                            override fun onItemClick(item: Sick) {
                                view.findNavController()
                                    .navigate(
                                        RabbitDetailsFragmentDirections.actionRabbitDetailsFragmentToSickDetailsFragment(
                                            item.id
                                        )
                                    )
                            }
                        })
            }
        }

        viewModel.selectedRabbit?.id?.let {
            val matings = viewModel.getAllMatingsForRabbit(it)
            if (matings.isNotEmpty()) {
                binding.fragmentRabbitDetailsIncludeMatings.root.visibility = View.VISIBLE
                binding.fragmentRabbitDetailsIncludeMatings.fragmentMatingListRecyclerView.adapter =
                    MatingListAdapter(
                        viewModel,
                        matings,
                        object : OnSelectedMating {
                            override fun onItemClick(item: Mating) {
                                view.findNavController()
                                    .navigate(
                                        RabbitDetailsFragmentDirections.actionRabbitDetailsFragmentToMatingFragment(
                                            item.id
                                        )
                                    )
                            }
                        })
            }
        }

        setPictureToSelectedOrDefault(
            binding.rabbitDetailsImage,
            viewModel.selectedRabbit,
            R.drawable.rabbit_back
        );
        binding.rabbitDetailsAge.text =
            viewModel.selectedRabbit?.let { RabbitDetails.getAge(it.birth) }
        binding.rabbitDetailsName.text = viewModel.selectedRabbit?.name
        binding.rabbitDetailsNumber.text = viewModel.selectedRabbit?.earNumber
        binding.rabbitDetailsSex.text =
            getGenderTranslated(viewModel.selectedRabbit?.sex)
        binding.rabbitDetailsBirth.text =
            viewModel.selectedRabbit?.let { RabbitDetails.getDateString(it.birth) }

        viewModel.selectedRabbit?.let {
            parentSelectService.displayParentOrUnknown(
                it.fkMother, it.fkFather,
                childFragmentManager,
                viewModel
            )
        }

        if (viewModel.selectedRabbit?.fkLitter != null) {
            viewModel.selectedLitter =
                viewModel.getLitterFromId(viewModel.selectedRabbit!!.fkLitter!!)
            binding.fragmentAddRabbitBelongTo.root.visibility = View.VISIBLE
            binding.fragmentAddRabbitBelongTo.fragmentBelongsToLitterItem.homeListItemAge.text =
                viewModel.selectedLitter?.let { RabbitDetails.getAge(it.birth) }
            binding.fragmentAddRabbitBelongTo.fragmentBelongsToLitterItem.homeListItemName.text =
                viewModel.selectedLitter?.name
            setPictureToSelectedOrDefault(
                binding.fragmentAddRabbitBelongTo.fragmentBelongsToLitterItem.homeListItemPicture,
                viewModel.selectedLitter,
                R.drawable.rabbit_2_back
            )
            Log.d("RabbitDetailsFragment", "Litter added ${viewModel.selectedLitter}")
        }
    }

    private fun moveToEditRabbit(): View.OnClickListener {
        return View.OnClickListener { view ->
            view.findNavController()
                .navigate(R.id.action_rabbitDetailsFragment_to_addRabbitFragment)
        }
    }

    private fun deleteRabbit(): View.OnClickListener {
        return View.OnClickListener {
            val alertDialog = requireActivity().let {
                val builder = AlertDialog.Builder(it)
                builder.apply {
                    setPositiveButton(R.string.ok) { dialog, _ ->
                        dialog.dismiss()
                        viewModel.deleteCurrentlySelectedRabbit()
                        view?.findNavController()
                            ?.navigate(R.id.action_rabbitDetailsFragment_to_navigation_home)
                    }
                    setNegativeButton(R.string.cancel) { dialog, _ ->
                        dialog.dismiss()
                    }
                    setTitle(R.string.alert)
                    setMessage(R.string.confirm_delete_rabbit_message)
                }
                builder.create()
            }
            alertDialog.show()
        }
    }

    private fun getGenderTranslated(gender: String?): String {
        if (gender == Gender.MALE.name) {
            return getString(R.string.male)
        } else if (gender == Gender.FEMALE.name) {
            return getString(R.string.female)
        }
        return getString(R.string.unknown)
    }

}