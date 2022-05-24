package org.carlistingapp.autolist.ui.home.profile.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.android.synthetic.main.item_edit_images.view.*
import org.carlistingapp.autolist.R

class ImagesAdapter (private val imagesList: ArrayList<Uri>, private val context: Context, private val listener: OnClickListener) : RecyclerView.Adapter<ImagesAdapter.ImagesAdapterViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagesAdapterViewHolder {
        return ImagesAdapterViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_edit_images, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ImagesAdapterViewHolder, position: Int) {
        val image = imagesList[position]
        Glide.with(context)
            .load(image)
            .listener(object : RequestListener<Drawable?> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable?>?,
                    isFirstResource: Boolean
                ): Boolean {
                    holder.progressBar.visibility = View.GONE
                    return  false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable?>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    holder.progressBar.visibility = View.GONE
                    return false
                }

            })
            .into(holder.imageView)
    }

    override fun getItemCount(): Int {
       return imagesList.size
    }

    inner class ImagesAdapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener{
        val imageView = itemView.imageView!!
        private val deleteImage = itemView.image_delete!!
        val progressBar = itemView.progress_bar!!

        init {
            itemView.setOnClickListener(this)
            deleteImage.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION){
                    listener.onImageDelete(position)
                }
            }
        }
        override fun onClick(v: View?) {
            val position = adapterPosition

        }
    }

    interface OnClickListener{
        fun onImageDelete(position: Int)
    }

}
