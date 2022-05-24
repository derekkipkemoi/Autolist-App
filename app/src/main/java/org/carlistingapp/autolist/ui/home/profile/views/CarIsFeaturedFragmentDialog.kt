package org.carlistingapp.autolist.ui.home.profile.views

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import org.carlistingapp.autolist.R
import org.carlistingapp.autolist.data.db.entities.CarObject
import org.carlistingapp.autolist.databinding.FragmentCarIsFeaturedBinding


class CarIsFeaturedFragmentDialog : DialogFragment() {
    private lateinit var binding: FragmentCarIsFeaturedBinding
    private lateinit var packageAmount : String
    private lateinit var packageName : String
    private lateinit var car:CarObject

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_car_is_featured, container, false)
        car = (arguments?.getParcelable("carObject") as CarObject?)!!
        val days = arguments?.getString("Days").toString()


        binding.textViewCarName.text = car.name
        binding.packageName.text = "Package: ${car.featured?.featuredCarPackage?.packageName}"
        binding.prize.text = "Amount: Ksh ${car.featured?.featuredCarPackage?.packagePrice}"
        binding.activeDays.text = "Days Remaining: $days"
        binding.buttonOk.setOnClickListener {
            dismiss()
        }
        return  binding.root
    }

}