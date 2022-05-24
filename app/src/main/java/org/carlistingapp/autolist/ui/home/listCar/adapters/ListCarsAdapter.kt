package org.carlistingapp.autolist.ui.home.listCar.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.google.android.gms.ads.formats.UnifiedNativeAdView
import kotlinx.android.synthetic.main.item_car.view.*
import org.carlistingapp.autolist.R
import org.carlistingapp.autolist.data.db.entities.CarObject
import org.carlistingapp.autolist.ui.home.listCar.views.UnifiedNativeAdViewHolder
import java.net.URLEncoder
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

private const val CAR_VIEW_TYPE = 0
private const val UNIFIED_NATIVE_AD_VIEW_TYPE = 1

class ListCarsAdapter(
    private val mRecyclerViewItems: ArrayList<Any>,
    private val context: Context,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    val fullList = ArrayList(mRecyclerViewItems)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            UNIFIED_NATIVE_AD_VIEW_TYPE -> {
                val unifiedNativeLayoutView = LayoutInflater.from(viewGroup.context).inflate(R.layout.ad_unified, viewGroup, false)
                UnifiedNativeAdViewHolder(unifiedNativeLayoutView)
            }
            else -> {
                val carViewHolder: View = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_car,viewGroup, false)
                CarViewHolder(carViewHolder)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val recyclerViewItem: Any = mRecyclerViewItems[position]
        return if (recyclerViewItem is UnifiedNativeAd) {
            UNIFIED_NATIVE_AD_VIEW_TYPE
        } else CAR_VIEW_TYPE
    }

    override fun getItemCount(): Int {
       return mRecyclerViewItems.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(getItemViewType(position)){
            UNIFIED_NATIVE_AD_VIEW_TYPE -> {
                populateNativeAdView(
                    mRecyclerViewItems[position] as UnifiedNativeAd, (holder as UnifiedNativeAdViewHolder).adView
                )
            }

            CAR_VIEW_TYPE -> {
                val carViewHolder = holder as CarViewHolder
                val carObject = mRecyclerViewItems[position] as CarObject

                carViewHolder.carName.text = carObject.name
                carViewHolder.carPrice.text = "Ksh: "+NumberFormat.getNumberInstance(Locale.US).format(carObject.price).toString()
                carViewHolder.carLocation.text = carObject.location
                if (carObject.priceNegotiable == true){
                    carViewHolder.carPriceNegotiable.text = "Negotiable"
                }
                carViewHolder.carStatus.text = carObject.condition
                carViewHolder.imagesCount.text = carObject.images?.size.toString()
                val imageList = ArrayList<SlideModel>()
                for (image in carObject.images!!){
                    imageList.add(SlideModel(image))
                }
                carViewHolder.imageSlider.setImageList(imageList, ScaleTypes.CENTER_CROP)

                holder.imageSlider.setItemClickListener(object : ItemClickListener {
                    override fun onItemSelected(positionImage: Int) {
                        if (position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position)
                        }
                    }
                })

                holder.callButton.setOnClickListener {
                    val dialIntent = Intent(Intent.ACTION_DIAL)
                    dialIntent.data = Uri.parse("tel:" + Uri.encode("0" + carObject.seller?.sellerNumber.toString()))
                    ContextCompat.startActivity(context, dialIntent, null)
                }

                holder.messageButton.setOnClickListener {
                    val messageIntent = Intent(Intent.ACTION_SENDTO)
                    messageIntent.data = Uri.parse("smsto:" + Uri.encode("0" + carObject.seller?.sellerNumber.toString()))
                    messageIntent.putExtra(
                        "sms_body",
                        "Hallo, I am interested in your vehicle ${carObject.name} Listed On Motii App. Top cars"
                    )
                    ContextCompat.startActivity(context, messageIntent, null)
                }

                holder.chatButton.setOnClickListener {
                    val sendIntent = Intent(Intent.ACTION_VIEW)
                    val url =
                        "https://api.whatsapp.com/send?phone=" + "254"+"${carObject.seller?.sellerNumber}" + "&text=" + URLEncoder.encode(
                            "Hello, I am interested in your ${carObject.name} listed in Motii App. Top Cars",
                            "UTF-8"
                        )
                    sendIntent.data = Uri.parse(url)
                    context.startActivity(sendIntent)
                }

                holder.contactSeller.setOnClickListener {
                    holder.contactSeller.visibility = View.INVISIBLE
                    holder.contactSellerLayout.visibility = View.VISIBLE
                }

            }
        }
    }



    inner class CarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var carName  = itemView.car_textView_name!!
        var  carPrice = itemView.car_textView_price!!
        var imageSlider = itemView.image_slider!!
        var carLocation = itemView.car_textView_location!!
        var carPriceNegotiable = itemView.car_textView_price_negotiable!!
        var imagesCount = itemView.imagesCount!!
        val callButton = itemView.call_seller!!
        val chatButton = itemView.button_chat!!
        val messageButton = itemView.button_message!!
        val contactSeller = itemView.contact_seller!!
        val carStatus = itemView.car_status!!
        val contactSellerLayout = itemView.contact_seller_layout!!



        init {
            itemView.setOnClickListener(this)
            callButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION){
                    listener.contactSellerOnClick(position)
                }
            }

            itemView.setOnClickListener {
                val adapterPosition = adapterPosition
                if (adapterPosition != RecyclerView.NO_POSITION){
                    listener.onItemClick(adapterPosition)
                }
            }


            imageSlider.setItemClickListener(object : ItemClickListener {
                override fun onItemSelected(position: Int) {
                    val adapterPosition = adapterPosition
                    if (adapterPosition != RecyclerView.NO_POSITION){
                        listener.onItemClick(adapterPosition)
                    }
                }
            })



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
    }


    private fun populateNativeAdView(nativeAd: UnifiedNativeAd, adView: UnifiedNativeAdView) {
        // Some assets are guaranteed to be in every UnifiedNativeAd.
        (adView.headlineView as TextView).text = nativeAd.headline
        (adView.bodyView as TextView).text = nativeAd.body
        (adView.callToActionView as Button).text = nativeAd.callToAction

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        val icon = nativeAd.icon
        if (icon == null) {
            adView.iconView.visibility = View.INVISIBLE
        } else {
            (adView.iconView as ImageView).setImageDrawable(icon.drawable)
            adView.iconView.visibility = View.VISIBLE
        }
        if (nativeAd.price == null) {
            adView.priceView.visibility = View.INVISIBLE
        } else {
            adView.priceView.visibility = View.VISIBLE
            (adView.priceView as TextView).text = nativeAd.price
        }
        if (nativeAd.store == null) {
            adView.storeView.visibility = View.INVISIBLE
        } else {
            adView.storeView.visibility = View.VISIBLE
            (adView.storeView as TextView).text = nativeAd.store
        }
        if (nativeAd.starRating == null) {
            adView.starRatingView.visibility = View.INVISIBLE
        } else {
            (adView.starRatingView as RatingBar).rating = nativeAd.starRating.toFloat()
            adView.starRatingView.visibility = View.VISIBLE
        }
        if (nativeAd.advertiser == null) {
            adView.advertiserView.visibility = View.INVISIBLE
        } else {
            (adView.advertiserView as TextView).text = nativeAd.advertiser
            adView.advertiserView.visibility = View.VISIBLE
        }

        adView.setNativeAd(nativeAd)
    }


}

