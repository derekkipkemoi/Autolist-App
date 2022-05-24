package org.carlistingapp.autolist.ui.home.listCar.adapters

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.android.synthetic.main.item_image.view.*
import org.carlistingapp.autolist.R


class ViewImageAdapter(private val imageList: List<String>, private val context: Context) :
    RecyclerView.Adapter<ViewImageAdapter.ViewImageViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewImageViewHolder {
        //val view : View = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
        return ViewImageViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewImageViewHolder, position: Int) {
        val imagePosition = position + 1
        holder.imageCount.text = imagePosition.toString()
        holder.imageCountTotal.text = imageList.size.toString()
        val image = imageList[position]

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
                    return false
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
        return imageList.size
    }

    class ViewImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView = itemView.item_image!!
        val progressBar = itemView.progress_bar!!
        val imageCount = itemView.imagesCount!!
        val imageCountTotal = itemView.imagesCountTotal!!
    }

}