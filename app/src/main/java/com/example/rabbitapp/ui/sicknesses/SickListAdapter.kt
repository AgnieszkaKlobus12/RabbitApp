package com.example.rabbitapp.ui.sicknesses

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.rabbitapp.R
import com.example.rabbitapp.model.entities.relations.Sick
import com.example.rabbitapp.utils.MainViewModel
import com.example.rabbitapp.utils.RabbitDetails

class SickListAdapter(
    private val viewModel: MainViewModel,
    private val values: List<Sick>,
    private val onSelectedItem: OnSelectedSick
) :
    RecyclerView.Adapter<SickListAdapter.SickListItemView>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SickListItemView {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.sickness_list_item, parent, false)
        return SickListItemView(itemView)
    }

    override fun onBindViewHolder(holder: SickListItemView, position: Int) {
        val item = values[position]
        holder.name.text = viewModel.getSickness(item.fkSickness)?.name
        holder.startDate.text = RabbitDetails.getDateString(item.startDate)
        holder.endDate.text = item.endDate?.let { RabbitDetails.getDateString(it) }
        holder.itemView.setOnClickListener { onSelectedItem.onItemClick(item) }
    }

    override fun getItemCount(): Int = values.size

    inner class SickListItemView(iv: View) : RecyclerView.ViewHolder(iv) {
        val name: TextView = iv.findViewById(R.id.sicknesses_list_item_sickness_name)
        val startDate: TextView = iv.findViewById(R.id.sicknesses_list_date_start)
        val endDate: TextView = iv.findViewById(R.id.sicknesses_list_date_end)
    }

}