package org.carlistingapp.autolist.ui.home.profile.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import kotlinx.android.synthetic.main.item_car.view.car_textView_location
import kotlinx.android.synthetic.main.item_car.view.car_textView_name
import kotlinx.android.synthetic.main.item_car.view.car_textView_price
import kotlinx.android.synthetic.main.item_car.view.car_textView_price_negotiable
import kotlinx.android.synthetic.main.item_user_car.view.*
import kotlinx.android.synthetic.main.item_user_car.view.image_slider
import kotlinx.android.synthetic.main.item_user_car.view.imagesCount
import org.carlistingapp.autolist.R
import org.carlistingapp.autolist.data.db.entities.CarObject
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class UserCarsAdapter(private val userCars : ArrayList<CarObject>, private val context: Context, private val listener : OnItemClickListener
) : RecyclerView.Adapter<UserCarsAdapter.UserCarsViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserCarsViewHolder {
        return UserCarsViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_user_car,parent,false)
        )
    }

    @SuppressLint("SetTextI18n", "ResourceType")
    override fun onBindViewHolder(holder: UserCarsViewHolder, position: Int) {
        if (userCars[position].name!!.length > 20){
            holder.carName.text = userCars[position].name!!.replace(userCars[position].make!!,"")
        }else{
            holder.carName.text = userCars[position].name
        }

        holder.carPrice.text = "Ksh: "+ NumberFormat.getNumberInstance(Locale.US).format(userCars[position].price).toString()
        holder.carLocation.text = userCars[position].location
        if (userCars[position].priceNegotiable == true){
            holder.carPriceNegotiable.text = "Negotiable"
        }
        holder.carStatusText.text = userCars[position].status
        holder.imagesCount.text = userCars[position].images?.size.toString()

        val imageList = ArrayList<SlideModel>()
        for (image in userCars[position].images!!){
            imageList.add(SlideModel(image))
        }
        holder.carImage.setImageList(imageList, ScaleTypes.CENTER_CROP)
        holder.carImage.setItemClickListener(object : ItemClickListener {
            override fun onItemSelected(positionImage: Int) {
                if (position != RecyclerView.NO_POSITION){
                    listener.onItemClick(position)
                }
            }
        })

        if (userCars[position].status == "sold"){
            holder.soldUserCarButton.visibility = View.GONE
            //holder.editUserCarButton.visibility = View.GONE
            holder.featureUserCarButton.visibility = View.GONE
            holder.carStatusIcon.setBackgroundResource(R.drawable.ic_sold)
        }

       // 'underreview', 'active', 'declined', 'sold'
        if (userCars[position].status == "underreview"){
           // holder.soldUserCarButton.visibility = View.GONE
            holder.carStatusIcon.setBackgroundResource(R.drawable.ic_underreview)
        }

        if (userCars[position].status == "active"){
            holder.carStatusIcon.setBackgroundResource(R.drawable.ic_approved)
        }

        if (userCars[position].status == "declined"){
            //holder.soldUserCarButton.visibility = View.GONE
            //holder.featureUserCarButton.visibility = View.GONE
            holder.carStatusIcon.setBackgroundResource(R.drawable.ic_declined)
        }

        holder.views.text = userCars[position].views.toString()

    }

    override fun getItemCount(): Int {
        return userCars.size
    }

    inner class UserCarsViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var carName  = itemView.car_textView_name!!
        var  carPrice = itemView.car_textView_price!!
        var carImage = itemView.image_slider!!
        var carLocation = itemView.car_textView_location!!
        var carPriceNegotiable = itemView.car_textView_price_negotiable!!
        var imagesCount = itemView.imagesCount!!
        var carStatusText = itemView.status_text!!
        var carStatusIcon = itemView.status_icon!!
        var editUserCarButton: LinearLayout = itemView.button_edit
        private var deleteUserCarButton: LinearLayout = itemView.button_delete
        var soldUserCarButton : LinearLayout = itemView.button_sold
        var featureUserCarButton : LinearLayout = itemView.button_feature
        var views = itemView.views_text

        init {
            itemView.setOnClickListener(this)
            editUserCarButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION){
                    listener.onClickEditUserCar(position)
                }
            }

            featureUserCarButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION){
                    listener.onFeatureUserCar(position)
                }
            }

            deleteUserCarButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION){
                    listener.onClickDeleteUserCar(position)
                }
            }

            soldUserCarButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION){
                    listener.onClickSoldUserCar(position)
                }
            }
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION){
                listener.onItemClick(position)
            }
        }
    }

    interface OnItemClickListener{
        fun onItemClick(position: Int)
        fun onClickEditUserCar(position: Int)
        fun onClickDeleteUserCar(position: Int)
        fun onClickSoldUserCar(position: Int)
        fun onFeatureUserCar(position: Int)
    }


}