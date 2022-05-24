package org.carlistingapp.autolist.ui.home.listCar.views

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import org.carlistingapp.autolist.R
import org.carlistingapp.autolist.data.db.entities.CarObject
import java.net.URLEncoder

class ContactSellerDialog(val activity: Activity) {
    private lateinit var alertDialog: AlertDialog
    @SuppressLint("InflateParams", "SetTextI18n", "IntentReset")
    fun startLoadingContactDialog(car: CarObject){
        val builder = AlertDialog.Builder(activity)
        val inflater = activity.layoutInflater
        val view = inflater.inflate(R.layout.seller_contactdetails_layout, null)
        builder.setView(view)
        val textViewCarName: TextView = view.findViewById(R.id.car_name)
        val textViewPhone: TextView = view.findViewById(R.id.seller_phone)
        val textViewEmail: TextView = view.findViewById(R.id.seller_email)
        val buttonCall  = view.findViewById<ImageButton>(R.id.button_call)
        val buttonWhatsApp  = view.findViewById<ImageButton>(R.id.button_whats_app)
        val buttonMessage  = view.findViewById<ImageButton>(R.id.button_message)
        alertDialog = builder.create()
        textViewCarName.text = car.name
        textViewPhone.text = "0" +car.seller?.sellerNumber
        textViewEmail.text = car.seller?.sellerEmail
        buttonCall.setOnClickListener {
            val dialIntent = Intent(Intent.ACTION_DIAL)
            dialIntent.data = Uri.parse("tel:" + Uri.encode("0" + car.seller?.sellerNumber.toString()))
            startActivity(this.activity, dialIntent, null)
        }

        buttonMessage.setOnClickListener {
            val messageIntent = Intent(Intent.ACTION_SENDTO)
            messageIntent.data = Uri.parse("smsto:" + Uri.encode("0" + car.seller?.sellerNumber.toString()))
            messageIntent.putExtra(
                "sms_body",
                "Hallo, I am interested in your vehicle ${car.name} Listed On Motii App"
            )
            startActivity(this.activity, messageIntent, null)
        }

        buttonWhatsApp.setOnClickListener {
            val sendIntent = Intent(Intent.ACTION_VIEW)
            val url =
                "https://api.whatsapp.com/send?phone=" + "254"+"${car.seller?.sellerNumber}" + "&text=" + URLEncoder.encode(
                    "Hello, I am interested in your ${car.name} listed in Motii App",
                    "UTF-8"
                )
            sendIntent.data = Uri.parse(url)
            activity.startActivity(sendIntent)
        }
        alertDialog.show()
    }

    fun stopContactDialog(){
        alertDialog.dismiss()
    }
}