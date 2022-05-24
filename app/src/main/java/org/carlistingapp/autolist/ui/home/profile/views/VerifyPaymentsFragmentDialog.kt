package org.carlistingapp.autolist.ui.home.profile.views

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import org.carlistingapp.autolist.R
import org.carlistingapp.autolist.data.db.entities.CarObject
import org.carlistingapp.autolist.data.db.entities.PaymentPackage
import org.carlistingapp.autolist.data.db.entities.Payment
import org.carlistingapp.autolist.data.network.ListingCarsAPI
import org.carlistingapp.autolist.data.network.NetworkConnectionInterceptor
import org.carlistingapp.autolist.data.repositories.UserRepository
import org.carlistingapp.autolist.databinding.FragmentVerifyPaymentsDialogBinding
import org.carlistingapp.autolist.ui.home.profile.viewModel.UserViewModel
import org.carlistingapp.autolist.ui.home.profile.viewModel.UserViewModelFactory
import org.carlistingapp.autolist.utils.snackBar
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import java.util.*


class VerifyPaymentsFragmentDialog : DialogFragment(), KodeinAware {
    private lateinit var binding: FragmentVerifyPaymentsDialogBinding
    private lateinit var car : CarObject
    private lateinit var clipboardManager : ClipboardManager
    override val kodein by kodein()
    private val networkConnectionInterceptor : NetworkConnectionInterceptor by instance()
    private val api : ListingCarsAPI by instance()
    private val repository : UserRepository by instance()
    private val factory: UserViewModelFactory by instance()
    private lateinit var viewModel: UserViewModel
    private lateinit var userId : String

    private lateinit var packageAmount : String
    private lateinit var packageName : String

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_verify_payments_dialog, container, false)
        viewModel = ViewModelProvider(this.requireActivity(),factory).get(UserViewModel::class.java)
        car = (arguments?.getParcelable("carObject") as CarObject?)!!
        packageAmount = arguments?.getString("Package").toString()
        packageName = arguments?.getString("Name").toString()
        userId = arguments?.getString("userId").toString()

        Toast.makeText(requireContext(), userId,Toast.LENGTH_LONG).show()

        binding.textViewCarName.text = car.name
        binding.packageName.text = "$packageName Payment"
        binding.textViewPackagePrice.text = "AMOUNT: Ksh $packageAmount"
        binding.paymentProcess.text = "1. Go to Mpesa\n2. Select Lipa na M-pesa\n3. Select Buy Goods and Services\n4. Enter Till Number 5620857\n5 Enter AMOUNT: Ksh $packageAmount \n6. Wait for Confirmation Message"

        binding.buttonCancel.setOnClickListener {
            dismiss()
        }

        binding.buttonConfirm.setOnClickListener {
            confirmPayments()
        }

        binding.buttonCopyTill.setOnClickListener {
            clipboardManager = activity?.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val text = "5620857"
            val myClip = ClipData.newPlainText("text", text)
            clipboardManager.setPrimaryClip(myClip)
            binding.root.snackBar("Copied Till: $text To CLipBoard")
        }

        binding.pasteMessage.setOnClickListener {
            clipboardManager = activity?.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            if ((clipboardManager.hasPrimaryClip())){
                val clipData: ClipData = clipboardManager.primaryClip!!
                val item = clipData.getItemAt(0)
                binding.textMpesaVerifyMessage.setText(item.text.toString())
            }
            else{
                binding.root.snackBar("No message in clipboard. Copy you M-pesa payment message")
            }
        }
        return binding.root
    }

    @SuppressLint("ResourceType")
    private fun confirmPayments() {
        val validateMessage = "Cars Enterprises"
        val confirmationMessage = binding.textMpesaVerifyMessage.text.toString().trim()

        binding.messageTextInputLayout.error = null

        if (confirmationMessage.isEmpty()){
            binding.messageTextInputLayout.error = "Payment Confirmation Message Required"
            return
        }

        if (confirmationMessage.toLowerCase(Locale.ROOT).indexOf(validateMessage.toLowerCase(Locale.ROOT)) == -1) {
            binding.messageTextInputLayout.error = "Valid M-Pesa Confirmation Message Required"
            return
        }

        val featureUserCar = PaymentPackage(packageName, packageAmount)
        val payment = Payment(car.name!!,featureUserCar, confirmationMessage)
        binding.progressBar.visibility = View.VISIBLE
        viewModel.carPayment(userId, payment, requireContext())
        viewModel.carPayment.observe(viewLifecycleOwner, Observer { carPayment ->
            binding.progressBar.visibility = View.INVISIBLE
            binding.root.snackBar(carPayment.message)
            if (carPayment.message == "Payments confirmed successfully"){
                binding.progressBar.visibility = View.VISIBLE
                viewModel.featureCar(car.id!!, featureUserCar, binding.root, requireContext())
                viewModel.featureCar.observe(viewLifecycleOwner, Observer {
                    binding.progressBar.visibility = View.INVISIBLE
                    Toast.makeText(activity,"${car.name} Ad Created Successfully",Toast.LENGTH_LONG).show()
                    requireActivity().viewModelStore.clear()
                    findNavController().popBackStack()
                    dismiss()
                })
            }
            else{
                binding.messageTextInputLayout.error = carPayment.message
                binding.root.snackBar(carPayment.message)
            }
        })
    }
}