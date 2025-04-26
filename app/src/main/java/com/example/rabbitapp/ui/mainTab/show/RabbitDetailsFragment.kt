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
import android.widget.CompoundButton
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.rabbitapp.R
import com.example.rabbitapp.databinding.FragmentRabbitDetailsBinding
import com.example.rabbitapp.model.entities.Litter
import com.example.rabbitapp.model.entities.Rabbit
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

    private val args: RabbitDetailsFragmentArgs by navArgs()

    private var rabbit: Rabbit? = null
    private var _binding: FragmentRabbitDetailsBinding? = null
    private val binding get() = _binding!!
    private val parentSelectService: ParentSelectService = ParentSelectService()
    private lateinit var matingListAdapter: MatingListAdapter
    private val onSelectedItem = object : OnSelectedMating {
        override fun onItemClick(item: Mating) {
            view?.findNavController()
                ?.navigate(
                    RabbitDetailsFragmentDirections.actionRabbitDetailsFragmentToMatingFragment(
                        item.id
                    )
                )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.rabbit_details_menu, menu)
        if (rabbit?.deathDate != null) {
            val item = menu.findItem(R.id.navigation_kill_rabbit)
            item.setVisible(false)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.navigation_vaccinate_rabbit -> {
                view?.findNavController()
                    ?.navigate(
                        RabbitDetailsFragmentDirections.actionRabbitDetailsFragmentToVaccineListFragment(
                            rabbit!!.id
                        )
                    )
                true
            }

            R.id.navigation_rabbit_sickness -> {
                view?.findNavController()
                    ?.navigate(
                        RabbitDetailsFragmentDirections.actionRabbitDetailsFragmentToSicknessesListFragment(
                            rabbit!!.id,
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
                            if (rabbit?.sex == "FEMALE") {
                                rabbit!!.id
                            } else {
                                0L
                            },
                            if (rabbit?.sex == "MALE") {
                                rabbit!!.id
                            } else {
                                0L
                            },
                            if (rabbit!!.fkLitter != null) {
                                rabbit!!.fkLitter!!
                            } else {
                                0L
                            }
                        )
                    )
                true
            }

            R.id.navigation_kill_rabbit -> {
                view?.findNavController()
                    ?.navigate(
                        RabbitDetailsFragmentDirections.actionRabbitDetailsFragmentToMarkDead(
                            rabbit!!.id, 0L
                        )
                    )
                true
            }

            R.id.navigation_edit_rabbit -> {
                Log.d("RabbitDetailsFragment", "Edit rabbit")
                view?.findNavController()
                    ?.navigate(
                        RabbitDetailsFragmentDirections.actionRabbitDetailsFragmentToAddRabbitFragment(
                            rabbit!!.id
                        )
                    )
                true
            }

            R.id.navigation_delete_rabbit -> {
                Log.d("RabbitDetailsFragment", "Delete rabbit")
                val alertDialog = requireActivity().let {
                    val builder = AlertDialog.Builder(it)
                    builder.apply {
                        setPositiveButton(R.string.ok) { dialog, _ ->
                            viewModel.deleteRabbit(rabbit!!.id)
                            dialog.dismiss()
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
        val root: View = binding.root

        if (args.rabbitId != 0L) {
            rabbit = viewModel.getRabbitFromId(args.rabbitId)
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rabbit?.id?.let {
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
                                        RabbitDetailsFragmentDirections.actionRabbitDetailsFragmentToVaccinateDetailsFragment(
                                            item.id
                                        )
                                    )
                            }
                        })
            }
        }

        rabbit?.id?.let {
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

        rabbit?.id?.let {
            val matings = viewModel.getAllMatingsForRabbit(it)
            if (matings.isNotEmpty()) {
                binding.fragmentRabbitDetailsIncludeMatings.root.visibility = View.VISIBLE
                matingListAdapter = MatingListAdapter(viewModel, matings, onSelectedItem)
                binding.fragmentRabbitDetailsIncludeMatings.fragmentMatingListRecyclerView.adapter =
                    matingListAdapter

                binding.fragmentRabbitDetailsIncludeMatings.filterFrame.setOnClickListener {
                    if (binding.fragmentRabbitDetailsIncludeMatings.categoriesMatings.visibility == View.VISIBLE) {
                        binding.fragmentRabbitDetailsIncludeMatings.categoriesMatings.visibility =
                            View.GONE
                        binding.fragmentRabbitDetailsIncludeMatings.filterButton.contentDescription =
                            getString(R.string.filter)
                        binding.fragmentRabbitDetailsIncludeMatings.filterButton.setImageResource(R.drawable.icon_filter)
                    } else {
                        binding.fragmentRabbitDetailsIncludeMatings.categoriesMatings.visibility =
                            View.VISIBLE
                        binding.fragmentRabbitDetailsIncludeMatings.filterButton.contentDescription =
                            getString(R.string.close)
                        binding.fragmentRabbitDetailsIncludeMatings.filterButton.setImageResource(R.drawable.icon_close)
                    }
                }

                binding.fragmentRabbitDetailsIncludeMatings.bornChip.setOnCheckedChangeListener(
                    filter()
                )
                binding.fragmentRabbitDetailsIncludeMatings.notBornChip.setOnCheckedChangeListener(
                    filter()
                )
                binding.fragmentRabbitDetailsIncludeMatings.archivedChip.setOnCheckedChangeListener(
                    filter()
                )
                binding.fragmentRabbitDetailsIncludeMatings.notArchivedChip.setOnCheckedChangeListener(
                    filter()
                )
            }
        }

        setPictureToSelectedOrDefault(
            binding.rabbitDetailsImage,
            rabbit,
            R.drawable.rabbit_back
        );
        binding.rabbitDetailsAge.text =
            rabbit?.let { RabbitDetails.getAge(it.birth) }
        binding.rabbitDetailsName.text = rabbit?.name
        binding.rabbitDetailsNumber.text = rabbit?.earNumber
        binding.rabbitDetailsSex.text =
            getGenderTranslated(rabbit?.sex)
        binding.rabbitDetailsBirth.text =
            rabbit?.let { RabbitDetails.getDateString(it.birth) }
        if (rabbit?.deathDate != null) {
            binding.rabbitDetailsDeathRow.visibility = View.VISIBLE
            binding.rabbitDetailsDeath.text =
                rabbit?.let { RabbitDetails.getDateString(it.deathDate!!) }
        } else {
            binding.rabbitDetailsDeathRow.visibility = View.GONE
        }
        if (rabbit?.cageNumber != null) {
            binding.rabbitDetailsCageNumber.text = rabbit?.cageNumber.toString()
        } else {
            binding.rabbitDetailsCageNumber.text = ""
        }
        rabbit?.let {
            parentSelectService.displayParentOrUnknown(
                it.fkMother, it.fkFather,
                childFragmentManager,
                viewModel
            )
            if (it.fkMother != null) {
                binding.fragmentRabbitDetailsIncludeParents.addMotherFragment.setOnClickListener {
                    view.findNavController()
                        .navigate(
                            RabbitDetailsFragmentDirections.actionRabbitDetailsFragmentToRabbitDetailsFragment(
                                rabbit!!.fkMother!!
                            )
                        )
                }
            }
            if (it.fkFather != null) {
                binding.fragmentRabbitDetailsIncludeParents.addFatherFragment.setOnClickListener {
                    view.findNavController()
                        .navigate(
                            RabbitDetailsFragmentDirections.actionRabbitDetailsFragmentToRabbitDetailsFragment(
                                rabbit!!.fkFather!!
                            )
                        )
                }
            }
        }

        if (rabbit?.fkLitter != null) {
            val litter: Litter? =
                viewModel.getLitterFromId(rabbit!!.fkLitter!!)
            binding.fragmentAddRabbitBelongTo.root.visibility = View.VISIBLE
            binding.fragmentAddRabbitBelongTo.fragmentBelongsToLitterItem.homeListItemAge.text =
                litter?.let { RabbitDetails.getAge(it.birth) }
            binding.fragmentAddRabbitBelongTo.fragmentBelongsToLitterItem.homeListItemName.text =
                litter?.name
            setPictureToSelectedOrDefault(
                binding.fragmentAddRabbitBelongTo.fragmentBelongsToLitterItem.homeListItemPicture,
                litter,
                R.drawable.rabbit_2_back
            )
            Log.d("RabbitDetailsFragment", "Litter added $litter")
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

    private fun filter(): CompoundButton.OnCheckedChangeListener {
        return CompoundButton.OnCheckedChangeListener { _, _ ->
            var items = viewModel.getAllMatingsForRabbit(rabbit!!.id)
            if (binding.fragmentRabbitDetailsIncludeMatings.bornChip.isChecked) {
                items = items.filter { it.fkLitter != null }
            }
            if (binding.fragmentRabbitDetailsIncludeMatings.notBornChip.isChecked) {
                items = items.filter { it.fkLitter == null }
            }
            if (binding.fragmentRabbitDetailsIncludeMatings.archivedChip.isChecked) {
                items = items.filter { it.archived }
            }
            if (binding.fragmentRabbitDetailsIncludeMatings.notArchivedChip.isChecked) {
                items = items.filter { !it.archived }
            }
            matingListAdapter.updateData(items)
        }
    }

}