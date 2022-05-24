package org.carlistingapp.autolist.ui.home.listCar.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import kotlinx.android.synthetic.main.item_car.view.*
import org.carlistingapp.autolist.R
import org.carlistingapp.autolist.data.db.entities.CarObject
import java.net.URLEncoder
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class MainCarsAdapter(private val cars: ArrayList<CarObject>, private val context: Context, private val listener: OnItemClickListener)
    : RecyclerView.Adapter<MainCarsAdapter.CarsViewHolder>() {
    private val imageList = ArrayList<SlideModel>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarsViewHolder {
       return CarsViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_car_similar, parent, false))
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CarsViewHolder, @SuppressLint("RecyclerView") position: Int) {
        if (cars[position].name!!.length > 19){
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
        val imageList = ArrayList<SlideModel>()
        for (image in cars[position].images!!){
            imageList.add(SlideModel(image))
        }
        holder.imageSlider.setImageList(imageList, ScaleTypes.CENTER_CROP)
        holder.imageSlider.setItemClickListener(object : ItemClickListener {
            override fun onItemSelected(positionImage: Int) {
                if (position != RecyclerView.NO_POSITION){
                    listener.onItemClick(position)
                }
            }
        })
    }

    override fun getItemCount(): Int {
        return cars.size
    }

    inner class CarsViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView), View.OnClickListener{
        var carName  = itemView.car_textView_name!!
        var  carPrice = itemView.car_textView_price!!
        var imageSlider = itemView.image_slider!!
        var carLocation = itemView.car_textView_location!!
        var carPriceNegotiable = itemView.car_textView_price_negotiable!!
        var imagesCount = itemView.imagesCount!!
        private val callButton = itemView.call_seller!!
        private val chatButton = itemView.button_chat
        private val messageButton = itemView.button_message


        init {
            itemView.setOnClickListener(this)
            callButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION){
                    listener.contactSellerOnClick(position)
                }
            }


            callButton.setOnClickListener {
                val dialIntent = Intent(Intent.ACTION_DIAL)
                dialIntent.data = Uri.parse("tel:" + Uri.encode("0" + cars[adapterPosition].seller?.sellerNumber.toString()))
                ContextCompat.startActivity(context, dialIntent, null)
            }

            messageButton.setOnClickListener {
                val messageIntent = Intent(Intent.ACTION_SENDTO)
                messageIntent.data = Uri.parse("smsto:" + Uri.encode("0" + cars[adapterPosition].seller?.sellerNumber.toString()))
                messageIntent.putExtra(
                    "sms_body",
                    "Hallo, I am interested in your vehicle ${cars[adapterPosition].name} Listed On Motii App. Top cars"
                )
                ContextCompat.startActivity(context, messageIntent, null)
            }

            chatButton.setOnClickListener {
                val sendIntent = Intent(Intent.ACTION_VIEW)
                val url =
                    "https://api.whatsapp.com/send?phone=" + "254"+"${cars[adapterPosition].seller?.sellerNumber}" + "&text=" + URLEncoder.encode(
                        "Hello, I am interested in your ${cars[adapterPosition].name} listed in Motii App. Top Cars",
                        "UTF-8"
                    )
                sendIntent.data = Uri.parse(url)
                context.startActivity(sendIntent)
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