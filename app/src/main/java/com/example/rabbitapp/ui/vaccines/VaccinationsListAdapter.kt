package com.example.rabbitapp.ui.vaccines

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.rabbitapp.R
import com.example.rabbitapp.model.entities.relations.Vaccinated
import com.example.rabbitapp.ui.mainTab.MainListViewModel
import com.example.rabbitapp.utils.RabbitDetails

class VaccinationsListAdapter(
    private val viewModel: MainListViewModel,
    private val values: List<Vaccinated>,
    private val onSelectedItem: OnSelectedVaccination
) :
    RecyclerView.Adapter<VaccinationsListAdapter.VaccinationsListItemView>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VaccinationsListItemView {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.vaccination_list_item, parent, false)
        return VaccinationsListItemView(itemView)
    }

    override fun onBindViewHolder(holder: VaccinationsListItemView, position: Int) {
        val item = values[position]
        holder.name.text = viewModel.getVaccine(item.fkVaccine)?.name
        holder.date.text = RabbitDetails.getDateString(item.date)
        holder.dose.text = item.dose
        holder.itemView.setOnClickListener { onSelectedItem.onItemClick(item) }
    }

    override fun getItemCount(): Int = values.size

    inner class VaccinationsListItemView(iv: View) : RecyclerView.ViewHolder(iv) {
        val name: TextView = iv.findViewById(R.id.vaccination_list_item_vaccine_name)
        val date: TextView = iv.findViewById(R.id.vaccination_list_date)
        val dose: TextView = iv.findViewById(R.id.vaccination_list_dose)
    }

}