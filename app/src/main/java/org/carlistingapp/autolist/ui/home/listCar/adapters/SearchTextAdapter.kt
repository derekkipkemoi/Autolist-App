package org.carlistingapp.autolist.ui.home.listCar.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_search_text.view.*
import org.carlistingapp.autolist.R

class SearchTextAdapter(private val searchTextList : ArrayList<String>, private val listener: OnTextItemClicked) :
    RecyclerView.Adapter<SearchTextAdapter.SearchTextViewHolder>() {
    inner class SearchTextViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val searchedText = itemView.text_searched!!
        private val reSearchImage = itemView.image_research!!
        override fun onClick(v: View?) {
            itemView.setOnClickListener(this)
        }

        init {
            reSearchImage.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION){
                    listener.onSearchedTextClicked(position)
                }
            }

            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION){
                    listener.onSearchedTextClicked(position)
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchTextViewHolder {
        return SearchTextViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_search_text,parent, false)
        )
    }

    override fun onBindViewHolder(holder: SearchTextViewHolder, position: Int) {
        holder.searchedText.text = searchTextList[position]
    }

    override fun getItemCount(): Int {
        return searchTextList.size
    }

    interface OnTextItemClicked{
        fun onSearchedTextClicked(position: Int)
    }
}