package org.carlistingapp.autolist.ui.home.profile.views

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import org.carlistingapp.autolist.R
import org.carlistingapp.autolist.data.db.entities.CarObject
import org.carlistingapp.autolist.data.network.ListingCarsAPI
import org.carlistingapp.autolist.data.repositories.UserRepository
import org.carlistingapp.autolist.databinding.FragmentSoldUserCarDialogBinding
import org.carlistingapp.autolist.ui.home.profile.viewModel.UserViewModel
import org.carlistingapp.autolist.ui.home.profile.viewModel.UserViewModelFactory
import org.carlistingapp.autolist.utils.NoInternetException
import org.carlistingapp.autolist.utils.snackBar
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class SoldUserCarDialogFragment(private val listener: UpdateCarDetails) : DialogFragment(), KodeinAware {
    override val kodein by kodein()
    val api : ListingCarsAPI by instance()
    val repository : UserRepository by instance()
    val factory: UserViewModelFactory by instance()
    private lateinit var viewModel: UserViewModel
    private lateinit var binding: FragmentSoldUserCarDialogBinding
    private lateinit var car:CarObject
    private lateinit var carId : String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_sold_user_car_dialog,
            container,
            false
        )
        car = (arguments?.getParcelable("carObject") as CarObject?)!!
        carId = car.id.toString()
        binding.textViewCarName.text = car.name
        binding.buttonCancel.setOnClickListener {
            dismiss()
        }

        binding.buttonSold.setOnClickListener {
            soldCar()
        }
        return binding.root
    }

    private fun soldCar() {
        val sold = binding.textViewCarSold.text?.trim().toString()
        binding.soldTextInputLayout.error = null
        if (sold.isEmpty() || sold != "SOLD"){
            binding.soldTextInputLayout.error = "Type SOLD in Capital Letters"
            return
        }

        viewModel = ViewModelProvider(this, factory).get(UserViewModel::class.java)
        try {
            viewModel.soldUserCar(carId, binding.root, requireContext())
            binding.progressBar.visibility = View.VISIBLE
            viewModel.soldUserCar.observe(viewLifecycleOwner, Observer { soldUserCar ->
                binding.progressBar.visibility = View.INVISIBLE
                binding.root.snackBar(soldUserCar.message)
                dismiss()
            })
        }catch (e: NoInternetException){
            binding.root.snackBar(e.message)
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        listener.onCarSold()
    }


    interface UpdateCarDetails{
        fun onCarSold()
    }

}