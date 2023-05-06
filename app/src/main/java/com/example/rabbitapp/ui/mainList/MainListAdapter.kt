package com.example.rabbitapp.ui.mainList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.rabbitapp.R
import com.example.rabbitapp.model.entities.HomeListItem

class MainListAdapter(
    private val values: List<HomeListItem>,
//    private val onRouteClickedListener: OnRouteClickedListener
) :
    RecyclerView.Adapter<MainListAdapter.HomeListItemView>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeListItemView {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.home_list_item, parent, false)
        return HomeListItemView(itemView)
    }

    override fun onBindViewHolder(holder: HomeListItemView, position: Int) {
        val item = values[position]
//        holder.image =
        holder.name.text = item.name
        holder.age.text = getAge(item.birth)

//        holder.itemView.setOnClickListener { onRouteClickedListener.onItemClick(item) }
    }

    override fun getItemCount(): Int = values.size

    private fun getAge(birthdate: Long): String {
        val epochDays = System.currentTimeMillis() / 86400000 - birthdate
        val days = epochDays % 30
        val months = epochDays / 30 % 12
        val years = epochDays / 365
        return "$years y $months m $days d"
    }

    inner class HomeListItemView(iv: View) : RecyclerView.ViewHolder(iv) {
        val image: ImageView = iv.findViewById(R.id.home_list_item_picture)
        val name: TextView = iv.findViewById(R.id.home_list_item_name)
        val age: TextView = iv.findViewById(R.id.home_list_item_age)
    }

}