package org.carlistingapp.autolist.ui.home.profile.views

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import com.google.android.ads.nativetemplates.NativeTemplateStyle
import com.google.android.ads.nativetemplates.TemplateView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.formats.NativeAdOptions
import com.google.android.gms.ads.formats.UnifiedNativeAd
import kotlinx.android.synthetic.main.activity_home.*
import org.carlistingapp.autolist.R
import org.carlistingapp.autolist.data.db.entities.CarObject
import org.carlistingapp.autolist.data.network.ListingCarsAPI
import org.carlistingapp.autolist.data.repositories.UserRepository
import org.carlistingapp.autolist.databinding.FragmentUserCarDetailsBinding
import org.carlistingapp.autolist.ui.home.listCar.adapters.ListCarFeaturesAdapter
import org.carlistingapp.autolist.ui.home.profile.viewModel.UserViewModel
import org.carlistingapp.autolist.ui.home.profile.viewModel.UserViewModelFactory
import org.carlistingapp.autolist.utils.Session
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class UserCarDetailsFragment : Fragment(), KodeinAware, SoldUserCarDialogFragment.UpdateCarDetails {
    override val kodein by kodein()
    private lateinit var binding : FragmentUserCarDetailsBinding
    val api : ListingCarsAPI by instance()
    val repository : UserRepository by instance()
    val factory : UserViewModelFactory by instance()
    private lateinit var carId: String
    private lateinit var viewModel : UserViewModel
    private var car = CarObject()
    private val featuresList = ArrayList<String>()
    private val imageList = ArrayList<SlideModel>()
    val session : Session by instance()
    private lateinit var userId : String

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        requireActivity().toolBar.title = ""

        userId = session.getSession().toString()
        carId = (arguments?.getParcelable("carObject") as CarObject?)!!.id.toString()
        carDetails()

//        val adLoader = AdLoader.Builder(requireActivity(), getString(R.string.ad_unit_id))
//            .forUnifiedNativeAd { ad: UnifiedNativeAd ->
//                val styles = NativeTemplateStyle.Builder().withMainBackgroundColor(ColorDrawable( Color.parseColor("#ffffff"))).build()
//                val template: TemplateView = binding.myTemplate
//                template.setStyles(styles)
//                template.setNativeAd(ad)
//            }
//            .withAdListener(object : AdListener() {
//                override fun onAdFailedToLoad(adError: LoadAdError) {
//                }
//            })
//            .withNativeAdOptions(NativeAdOptions.Builder().build()).build()
//        adLoader.loadAd(AdRequest.Builder().build())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_user_car_details, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    private fun carDetails(){
        viewModel = ViewModelProvider(this, factory).get(UserViewModel::class.java)
        viewModel.getUserCar(carId, requireActivity())
        binding.progressBar.visibility = View.VISIBLE
        viewModel.userCarResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer { carObjectResponse ->
            binding.progressBar.visibility = View.INVISIBLE
            //binding.root.snackBar(carObjectResponse.message)
            car = carObjectResponse.carObject!!

            featuresList.clear()
            imageList.clear()
            for (image in carObjectResponse.carObject.images!!){
                imageList.add(SlideModel(image,car.name))
            }
            for (feature in carObjectResponse.carObject.features!!){
                featuresList.add(feature)
            }

            binding.imageSlider.setImageList(imageList, ScaleTypes.CENTER_CROP)
            binding.price.text = NumberFormat.getNumberInstance(Locale.US).format(car.price).toString()
            binding.location.text = car.location
            binding.condition.text = car.condition
            binding.transmission.text = car.transmission
            binding.mileage.text = car.mileage.toString()+" Km"
            binding.fuel.text = car.fuel
            binding.engine.text = car.engineSize.toString()+" CC"
            binding.make.text = car.make
            binding.model.text = car.model
            binding.year.text = car.year.toString()
            binding.body.text= car.body
            binding.duty.text = car.duty
            binding.color.text = car.color
            binding.interior.text = car.color
            binding.interior.text = car.interior
            binding.description.text = car.description
            binding.statusText.text = car.status

            binding.featuresRecyclerView.also {
                it.layoutManager = GridLayoutManager(requireContext(),3,
                    GridLayoutManager.VERTICAL,false)
                it.setHasFixedSize(false)
                it.adapter = ListCarFeaturesAdapter(featuresList, this.requireActivity())
            }


            // 'underreview', 'active', 'declined', 'sold'
            if (car.status == "sold"){
                binding.buttonFeature.visibility = View.GONE
                //binding.buttonEdit.visibility = View.GONE
                binding.buttonSold.visibility = View.GONE
                binding.statusIcon.setBackgroundResource(R.drawable.ic_sold)
            }

            if (car.status == "active"){
                binding.statusIcon.setBackgroundResource(R.drawable.ic_approved)
            }

            if (car.status == "underreview"){
                binding.buttonSold.visibility = View.GONE
                binding.statusIcon.setBackgroundResource(R.drawable.ic_underreview)
            }

            if (car.status == "declined"){
                binding.buttonSold.visibility = View.GONE
                binding.buttonFeature.visibility = View.GONE
                binding.statusIcon.setBackgroundResource(R.drawable.ic_declined)
            }

            binding.viewsText.text = car.views.toString()

            binding.imageSlider.setItemClickListener(object : ItemClickListener {
                override fun onItemSelected(position: Int) {
                    val bundle = bundleOf("carObject" to car)
                    Navigation.findNavController(requireActivity(), R.id.fragment).navigate(
                        R.id.viewImageFragment,
                        bundle
                    )
                }
            })
        })


        binding.buttonEdit.setOnClickListener {
            val bundle = bundleOf("carObject" to car)
            findNavController().navigate(R.id.editCarFragment,bundle)
        }

        binding.buttonDelete.setOnClickListener {
            val bundle = bundleOf("carObject" to car)
            val deleteCarDialogFragment = DeleteUserCarDialogFragment()
            deleteCarDialogFragment.setStyle(DialogFragment.STYLE_NO_FRAME, 0)
            deleteCarDialogFragment.arguments = bundle
            deleteCarDialogFragment.show(requireActivity().supportFragmentManager,"DeleteCarDialogFragment")
        }

        binding.buttonSold.setOnClickListener {
            val bundle = bundleOf("carObject" to car)
            val soldCarDialogFragment = SoldUserCarDialogFragment(this)
            soldCarDialogFragment.setStyle(DialogFragment.STYLE_NO_FRAME, 0)
            soldCarDialogFragment.arguments = bundle
            soldCarDialogFragment.show(
                requireActivity().supportFragmentManager,
                "SoldCarDialogFragment"
            )
        }

        binding.buttonFeature.setOnClickListener {
            val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss'Z'")
            val endDate = format.parse(car.featured!!.endDay)
            val startDate = format.parse(car.featured!!.startDay)
            val today = Calendar.getInstance().time
            val diff: Long = endDate!!.time - today.time
            val seconds = diff / 1000
            val minutes = seconds / 60
            val hours = minutes / 60
            val days = hours / 24

            if (days <= 0){
                val bundle = bundleOf("carObject" to car)
                bundle.putString("userId", userId)
                findNavController().navigate(R.id.featureCarFragment,bundle)
            }
            else{
                val bundle = bundleOf("carObject" to car)
                bundle.putString("Package", car.featured!!.featuredCarPackage.packagePrice)
                bundle.putString("Days", days.toString())
                val carIsFeaturedFragmentDialog = CarIsFeaturedFragmentDialog()
                carIsFeaturedFragmentDialog.setStyle(DialogFragment.STYLE_NO_FRAME, 0)
                carIsFeaturedFragmentDialog.arguments = bundle
                carIsFeaturedFragmentDialog.show(requireActivity().supportFragmentManager, "CarIsFeaturedFragmentDialog")
            }
        }
    }


    override fun onCarSold() {
        carDetails()
    }

}