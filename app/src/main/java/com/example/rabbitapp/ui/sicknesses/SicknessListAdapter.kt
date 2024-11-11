package com.example.rabbitapp.ui.sicknesses

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.rabbitapp.R
import com.example.rabbitapp.model.entities.Sickness

class SicknessListAdapter(
    private var values: List<Sickness>,
    private val onSelectedVaccine: OnSelectedSickness
) :
    RecyclerView.Adapter<SicknessListAdapter.SicknessListItemView>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SicknessListItemView {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.simple_list_item, parent, false)
        return SicknessListItemView(itemView)
    }

    fun getData(): List<Sickness> {
        return values
    }

    fun updateData(newItems: List<Sickness>) {
        values = newItems
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: SicknessListItemView, position: Int) {
        val item = values[position]
        holder.name.text = item.name
        holder.itemView.setOnClickListener { onSelectedVaccine.onItemClick(item) }
    }

    override fun getItemCount(): Int = values.size

    inner class SicknessListItemView(iv: View) : RecyclerView.ViewHolder(iv) {
        val name: TextView = iv.findViewById(R.id.simple_list_item_name)
    }

}