package org.carlistingapp.autolist.ui.home.profile.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_payment.view.*
import org.carlistingapp.autolist.R
import org.carlistingapp.autolist.data.db.entities.Payment

class PaymentsAdapter(private val paymentsList : ArrayList<Payment>) : RecyclerView.Adapter<PaymentsAdapter.PaymentAdapterViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentAdapterViewHolder {
        return PaymentAdapterViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_payment,parent, false)
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: PaymentAdapterViewHolder, position: Int) {
        holder.message.text = paymentsList[position].paymentMessage?.substring(0, 10)+"..."
        holder.carName.text = paymentsList[position].carName
        holder.packageName.text = paymentsList[position].paymentPackage?.packageName
        holder.packagePrice.text = paymentsList[position].paymentPackage?.packagePrice
    }

    override fun getItemCount(): Int {
        return paymentsList.size
    }

    class PaymentAdapterViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val message: TextView = itemView.payment_message
        val carName: TextView = itemView.car_name
        val packageName: TextView = itemView.packageName
        val packagePrice: TextView = itemView.package_price
    }

}