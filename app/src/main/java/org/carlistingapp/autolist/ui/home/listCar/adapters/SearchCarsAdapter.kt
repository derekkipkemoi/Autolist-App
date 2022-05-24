package org.carlistingapp.autolist.ui.home.listCar.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
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

class SearchCarsAdapter(private val carsList : ArrayList<CarObject>, private val context: Context,
private val listener: OnItemClickListener):
RecyclerView.Adapter<SearchCarsAdapter.ViewCarViewHolder>(), Filterable{
    private var fullList   = ArrayList<CarObject>(carsList)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewCarViewHolder {
        return ViewCarViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_car,parent, false)
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewCarViewHolder, position: Int) {

        holder.carName.text = carsList[position].name
        holder.carPrice.text = "Ksh: "+ NumberFormat.getNumberInstance(Locale.US).format(carsList[position].price).toString()
        holder.carLocation.text = carsList[position].location
        if (carsList[position].priceNegotiable == true){
            holder.carPriceNegotiable.text = "Negotiable"
        }
        holder.carStatus.text = carsList[position].condition
        holder.imagesCount.text = carsList[position].images?.size.toString()
        val imageList = ArrayList<SlideModel>()
        for (image in carsList[position].images!!){
            imageList.add(SlideModel(image))
        }
        holder.imageSlider.setImageList(imageList, ScaleTypes.CENTER_CROP)

        holder.imageSlider.setItemClickListener(object : ItemClickListener {
            override fun onItemSelected(positionSlider: Int) {
                if (position != RecyclerView.NO_POSITION){
                    listener.onItemClick(position)
                }
            }
        })

    }

    override fun getItemCount(): Int {
        return carsList.size
    }

    inner class ViewCarViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var carName  = itemView.car_textView_name!!
        var  carPrice = itemView.car_textView_price!!
        val imageSlider = itemView.image_slider!!
        var carLocation = itemView.car_textView_location!!
        var carPriceNegotiable = itemView.car_textView_price_negotiable!!
        var imagesCount = itemView.imagesCount!!
        private val callButton = itemView.call_seller!!
        private val chatButton = itemView.button_chat!!
        private val messageButton = itemView.button_message!!
        private val contactSeller = itemView.contact_seller!!
        val carStatus = itemView.car_status!!
        private val contactSellerLayout = itemView.contact_seller_layout!!

        init {
            itemView.setOnClickListener(this)
            itemView.setOnClickListener {
                val adapterPosition = adapterPosition
                if (adapterPosition != RecyclerView.NO_POSITION){
                    listener.onItemClick(adapterPosition)
                }
            }


            callButton.setOnClickListener {
                val dialIntent = Intent(Intent.ACTION_DIAL)
                dialIntent.data = Uri.parse("tel:" + Uri.encode("0" + carsList[position].seller?.sellerNumber.toString()))
                ContextCompat.startActivity(context, dialIntent, null)
            }

            messageButton.setOnClickListener {
                val messageIntent = Intent(Intent.ACTION_SENDTO)
                messageIntent.data = Uri.parse("smsto:" + Uri.encode("0" + carsList[position].seller?.sellerNumber.toString()))
                messageIntent.putExtra(
                    "sms_body",
                    "Hallo, I am interested in your vehicle ${carsList[position].name} Listed On Motii App. Top cars"
                )
                ContextCompat.startActivity(context, messageIntent, null)
            }

            chatButton.setOnClickListener {
                val sendIntent = Intent(Intent.ACTION_VIEW)
                val url =
                    "https://api.whatsapp.com/send?phone=" + "254"+"${carsList[position].seller?.sellerNumber}" + "&text=" + URLEncoder.encode(
                        "Hello, I am interested in your ${carsList[position].name} listed in Motii App. Top Cars",
                        "UTF-8"
                    )
                sendIntent.data = Uri.parse(url)
                context.startActivity(sendIntent)
            }

            contactSeller.setOnClickListener {
                contactSeller.visibility = View.INVISIBLE
                contactSellerLayout.visibility = View.VISIBLE
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
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredList = ArrayList<CarObject>()
                if (constraint == null || constraint.isEmpty()){
                    filteredList.addAll(fullList)
                }else{
                    val filterPattern = constraint.toString().toLowerCase(Locale.ROOT).trim()
                    for (car in fullList){
                        if (car.name?.toLowerCase(Locale.ROOT)?.contains(filterPattern) == true
                            || car.location?.toLowerCase(Locale.ROOT)?.contains(filterPattern) == true
                            || car.condition?.toLowerCase(Locale.ROOT)?.contains(filterPattern) == true
                            || car.body?.toLowerCase(Locale.ROOT)?.contains(filterPattern) == true
                            || car.color?.toLowerCase(Locale.ROOT)?.contains(filterPattern) == true
                            || car.description?.toLowerCase(Locale.ROOT)?.contains(filterPattern) == true
                            || car.interior?.toLowerCase(Locale.ROOT)?.contains(filterPattern) == true
                            || car.duty?.toLowerCase(Locale.ROOT)?.contains(filterPattern) == true
                            || car.description?.toLowerCase(Locale.ROOT)?.contains(filterPattern) == true){
                            filteredList.add(car)
                        }
                    }
                }
                val results = FilterResults()
                results.values = filteredList
                return results
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                carsList.clear()
                carsList.addAll(results?.values as ArrayList<CarObject>)
                notifyDataSetChanged()
            }

        }
    }
}