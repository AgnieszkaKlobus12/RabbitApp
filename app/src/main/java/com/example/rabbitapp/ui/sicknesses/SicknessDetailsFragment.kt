package com.example.rabbitapp.ui.sicknesses

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.example.rabbitapp.R
import com.example.rabbitapp.databinding.FragmentSickDetailsBinding
import com.example.rabbitapp.model.entities.HomeListItem
import com.example.rabbitapp.model.entities.Sickness
import com.example.rabbitapp.model.entities.relations.Sick
import com.example.rabbitapp.ui.mainTab.add.FragmentWithPicture
import com.example.rabbitapp.utils.RabbitDetails
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class SicknessDetailsFragment : FragmentWithPicture() {

    private val args: SicknessDetailsFragmentArgs by navArgs()

    private var item: HomeListItem? = null
    private var sick: Sick? = null
    private var sickness: Sickness? = null
    private val dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
    private var _binding: FragmentSickDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSickDetailsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        sick = viewModel.getSick(args.sickId)
        item = sick?.fkRabbit?.let { viewModel.getRabbitFromId(it) }
        item = sick?.fkLitter?.let { viewModel.getLitterFromId(it) }
        sickness = sick?.fkSickness?.let { viewModel.getSickness(it) }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fragmentDetailsSicknessStartDate.text = Editable.Factory.getInstance().newEditable(
            sick?.let { LocalDate.ofEpochDay(it.startDate).format(dateFormatter) }
        )
        if (sick?.endDate != null) {
            binding.fragmentDetailsSicknessEndDate.text =
                Editable.Factory.getInstance().newEditable(
                    sick?.endDate?.let { LocalDate.ofEpochDay(it).format(dateFormatter) }
                )
        }

        binding.fragmentDetailsSicknessRabbit.homeListItemAge.text =
            RabbitDetails.getAge(item!!.birth)
        binding.fragmentDetailsSicknessRabbit.homeListItemName.text = item!!.name
        setPictureToSelectedOrDefault(
            binding.fragmentDetailsSicknessRabbit.homeListItemPicture,
            item,
            R.drawable.rabbit_back
        )

        binding.fragmentDetailsSicknessSicknessName.text = sickness!!.name
        binding.fragmentDetailsSicknessSicknessTreatment.text = sickness!!.treatment
        binding.fragmentDetailsSicknessSicknessSymptoms.text = sickness!!.symptoms
    }

}