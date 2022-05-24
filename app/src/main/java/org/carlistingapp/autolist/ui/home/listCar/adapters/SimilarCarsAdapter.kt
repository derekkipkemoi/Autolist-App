package org.carlistingapp.autolist.ui.home.listCar.adapters

import android.annotation.SuppressLint
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
import kotlinx.android.synthetic.main.item_car.view.car_textView_location
import kotlinx.android.synthetic.main.item_car.view.car_textView_name
import kotlinx.android.synthetic.main.item_car.view.car_textView_price
import kotlinx.android.synthetic.main.item_car.view.car_textView_price_negotiable
import kotlinx.android.synthetic.main.item_car.view.imagesCount
import kotlinx.android.synthetic.main.item_car_similar.view.*
import org.carlistingapp.autolist.R
import org.carlistingapp.autolist.data.db.entities.CarObject
import java.text.NumberFormat
import java.util.*

class SimilarCarsAdapter(private val cars : ArrayList<CarObject>, private val context: Context, private val listener: OnItemClickListener): RecyclerView.Adapter<SimilarCarsAdapter.SimilarCarsViewholder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimilarCarsViewholder {
        return SimilarCarsViewholder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_car_similar, parent, false)
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: SimilarCarsViewholder, position: Int) {
        if (cars[position].name!!.length > 20){
            holder.carName.text = cars[position].name!!.replace(cars[position].make!!,"")
        }else{
            holder.carName.text = cars[position].name
        }


        holder.carPrice.text = "Ksh: "+ NumberFormat.getNumberInstance(Locale.US).format(cars[position].price).toString()
        holder.carLocation.text = cars[position].location
        if (cars[position].priceNegotiable == true){
            holder.carPriceNegotiable.text = "Negotiable"
        }
        holder.imagesCount.text = cars[position].images?.size.toString()
        holder.carCondition.text = cars[position].condition
        if (cars[position].images?.isNullOrEmpty() == false){
            val carImage = cars[position].images?.get(0)
            Glide.with(context)
                .load(carImage)
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
                .into(holder.carImage)
        }
    }

    override fun getItemCount(): Int {
        return cars.size
    }

   inner class SimilarCarsViewholder(itemView : View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val carName  = itemView.car_textView_name!!
        val carPrice = itemView.car_textView_price!!
        val carImage = itemView.imageView!!
        val carLocation = itemView.car_textView_location!!
        val carPriceNegotiable = itemView.car_textView_price_negotiable!!
        val imagesCount = itemView.imagesCount!!
       val carCondition = itemView.car_condition!!
        val progressBar = itemView.progress_bar!!
        private val contactButton = itemView.contact_seller



        init {
            itemView.setOnClickListener(this)
            contactButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION){
                    listener.contactSellerOnClick(position)
                }
            }

            carImage.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION){
                    listener.onItemClick(adapterPosition)
                }
            }
        }


        override fun onClick(view: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION){
                listener.onItemClick(position)
            }
        }
    }

    interface OnItemClickListener{
        fun onItemClick(position: Int)
        fun contactSellerOnClick(position: Int)
        fun shareButtonOnClick(position: Int)
    }
}