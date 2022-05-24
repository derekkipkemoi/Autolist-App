package org.carlistingapp.autolist.ui.home.listCar.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.android.synthetic.main.item_car_slider.view.*
import org.carlistingapp.autolist.R
import org.carlistingapp.autolist.data.db.entities.CarObject
import java.text.NumberFormat
import java.util.*

class SliderAdapter(
    private val carSliderList : ArrayList<CarObject>,
    private val viewPager2: ViewPager2, private val context: Context,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<SliderAdapter.SliderViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_car_slider, parent, false)
//        view.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        return SliderViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: SliderViewHolder, position: Int) {
        if (position == carSliderList.size - 2){
            viewPager2.post(runAble)
        }

        if (carSliderList[position].name!!.length > 20){
            holder.carName.text = carSliderList[position].name!!.replace(carSliderList[position].make!!,"")
        }else{
            holder.carName.text = carSliderList[position].name
        }

        holder.carLocation.text = carSliderList[position].location
        holder.carPrice.text = "Ksh: "+ NumberFormat.getNumberInstance(Locale.US).format(carSliderList[position].price).toString()
        if (carSliderList[position].priceNegotiable == true){
            holder.carPriceNegotiable.text = "-Negotiable"
        }

        if (!carSliderList[position].images.isNullOrEmpty()) {
            val carImage = carSliderList[position].images?.get(0)
            Glide.with(context)
                .load(carSliderList[position].images?.get(0))
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
                .into(holder.sliderImage)
        }
    }

    override fun getItemCount(): Int {
        return carSliderList.size
    }

    inner class SliderViewHolder(sliderItemView : View) : RecyclerView.ViewHolder(sliderItemView), View.OnClickListener {
        var sliderImage = sliderItemView.imageView_slider!!
        var carName = sliderItemView.car_textView_name_slider!!
        var carLocation = sliderItemView.car_textView_location_slider!!
        var carPrice = sliderItemView.car_textView_price_slider!!
        var carPriceNegotiable = sliderItemView.car_textView_price_negotiable_slider!!
        var progressBar = sliderItemView.progress_bar!!

        init {
            itemView.setOnClickListener(this)
            sliderImage.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION){
                    listener.onSliderItemClick(position)
                }
            }
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION){
                listener.onSliderItemClick(position)
            }
        }
    }

    private val runAble = Runnable {
        carSliderList.addAll(carSliderList)
        notifyDataSetChanged()
    }

    interface OnItemClickListener{
        fun onSliderItemClick(position: Int)
    }
}