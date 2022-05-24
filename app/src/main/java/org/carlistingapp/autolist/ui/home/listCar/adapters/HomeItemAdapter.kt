package org.carlistingapp.autolist.ui.home.listCar.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_home.view.*
import org.carlistingapp.autolist.R
import org.carlistingapp.autolist.data.db.entities.HomeItem


class HomeItemAdapter(private val homeItems: ArrayList<HomeItem>, private val context: Context, private val listener: HomeItemClicked
) : RecyclerView.Adapter<HomeItemAdapter.HomeItemViewHolder>(){
    var index = -1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeItemViewHolder {
        return HomeItemViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_home,parent,false)
        )
    }

    override fun onBindViewHolder(holder: HomeItemViewHolder, position: Int) {
        holder.homeItemImage.setImageResource(homeItems[position].drawable!!)
        holder.homeItemName.text = homeItems[position].name

        holder.layout.setOnClickListener {
            index = position
            notifyDataSetChanged()
        }

        if (index == position){
            holder.layout.setBackgroundResource(R.drawable.home_item_selected)
            val name = homeItems[position].name
            listener.onHomeItemClicked(position, name!!)
        }

        else{
            holder.layout.setBackgroundResource(R.drawable.top_shape_navigation)
        }

    }

    override fun getItemCount(): Int {
        return homeItems.size
    }

    inner class HomeItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val homeItemImage: ImageView = itemView.item_image
        val homeItemName: TextView = itemView.item_name
        val layout: LinearLayoutCompat = itemView.linear_layout
        override fun onClick(v: View?) {
            
        }
    }

    interface HomeItemClicked{
        fun onHomeItemClicked(position: Int, name: String)
    }
}