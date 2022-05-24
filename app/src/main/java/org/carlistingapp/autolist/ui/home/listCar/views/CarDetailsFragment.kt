package org.carlistingapp.autolist.ui.home.listCar.views
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.bumptech.glide.signature.ObjectKey
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
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.carlistingapp.autolist.BuildConfig
import org.carlistingapp.autolist.R
import org.carlistingapp.autolist.data.db.entities.CarObject
import org.carlistingapp.autolist.data.network.ListingCarsAPI
import org.carlistingapp.autolist.data.repositories.UserRepository
import org.carlistingapp.autolist.databinding.FragmentCarDetailsBinding
import org.carlistingapp.autolist.ui.auth.views.LogInUserFragment
import org.carlistingapp.autolist.ui.home.listCar.adapters.ListCarFeaturesAdapter
import org.carlistingapp.autolist.ui.home.listCar.adapters.SimilarCarsAdapter
import org.carlistingapp.autolist.ui.home.listCar.viewModels.ListCarViewModel
import org.carlistingapp.autolist.ui.home.listCar.viewModels.ListCarViewModelFactory
import org.carlistingapp.autolist.utils.Session
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import java.net.URLEncoder
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class CarDetailsFragment() : Fragment(), KodeinAware, SimilarCarsAdapter.OnItemClickListener {
    override val kodein by kodein()
    val api : ListingCarsAPI by instance()
    val repository : UserRepository by instance()
    val factory : ListCarViewModelFactory by instance()
    val session : Session by instance()
    private lateinit var viewModel : ListCarViewModel
    private lateinit var binding: FragmentCarDetailsBinding
    private lateinit var car : CarObject
    private var carsList = ArrayList<CarObject>()
    private var similarCarsList = ArrayList<CarObject>()
    private lateinit var newCarsList : ArrayList<CarObject>
    private val featuresList = ArrayList<String>()
    private val imageList = ArrayList<SlideModel>()
    private val imageListView = ArrayList<String>()
    private  var email : String? = null
    private  var carName : String? = null
    private  var phoneNumber : Int? = null
    private lateinit var adapter : SimilarCarsAdapter
    private lateinit var contactSellerDialog: ContactSellerDialog
    private var positionInitial : Int? = null


    override fun onStart() {
        super.onStart()
        carsList.removeAt(positionInitial!!)
        adapter.notifyItemRemoved(positionInitial!!)

        for(carSimilar in carsList){
            if (carSimilar.name == car.name){
                similarCarsList.add(carSimilar)
            }
        }
        if (similarCarsList.isEmpty()){
            for(carSimilar in carsList){
                if (carSimilar.model == car.model){
                    similarCarsList.add(carSimilar)
                }
            }
        }

        if (similarCarsList.isEmpty()){
            for(carSimilar in carsList){
                if (carSimilar.body == car.body){
                    similarCarsList.add(carSimilar)
                }
            }
        }

        if (similarCarsList.isEmpty()){
            for(carSimilar in carsList){
                if (carSimilar.make == car.make){
                    similarCarsList.add(carSimilar)
                }
            }
        }
    }



    override fun onStop() {
        super.onStop()
        if(!carsList.contains(car)){
            carsList.add(positionInitial!!, car)
            adapter
            adapter.notifyItemRangeRemoved(0, carsList.size);
            adapter.notifyItemInserted(positionInitial!!)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        carsList = arguments?.getParcelableArrayList<CarObject>("carsList") as ArrayList<CarObject>
        positionInitial = arguments?.getInt("Position")
        car = carsList[positionInitial!!]
    }

    @SuppressLint("SetTextI18n")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        imageList.clear()
        featuresList.clear()
        for (image in car.images!!){
            imageList.add(SlideModel(image, car.name))
        }



        if (session.getUserUserFavoriteCars().contains(car.id)){
            binding.buttonFavourite.setImageResource(R.drawable.ic_favourite_24)
        }
        session.saveRecentViewedCars(car)

        //Toast.makeText(requireContext(),"${session.getRecentViewedCars().size}",Toast.LENGTH_LONG).show()
        for (feature in car.features!!){
            featuresList.add(feature)
        }



       // Toast.makeText(requireContext(),session.getUserRecentSearches().size.toString(),Toast.LENGTH_LONG).show()
        //session.getRecentSearches()
        adapter = SimilarCarsAdapter(similarCarsList, requireActivity(), this)

        viewModel = ViewModelProvider(this, factory).get(ListCarViewModel::class.java)
        binding.progressBarSmall.visibility = View.VISIBLE
        viewModel.carViewed(car.id!!, requireContext())
        viewModel.carViewed.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            requireActivity().viewModelStore.clear()
            //Toast.makeText(requireContext(),"$it", Toast.LENGTH_LONG).show()
        })

        binding.buttonBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.buttonShare.setOnClickListener {
            binding.progressBarSmall.visibility = View.VISIBLE
            Glide.with(requireContext())
                .asBitmap()
                .load(car.images?.get(0))
                .apply(RequestOptions().signature(ObjectKey("signature string")))
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        val path = MediaStore.Images.Media.insertImage(
                            requireActivity().contentResolver,
                            resource,
                            "IMG_" + Calendar.getInstance().time,
                            null
                        )
                        val imageUri = Uri.parse(path)
                        val shareIntent = Intent()
                        shareIntent.action = Intent.ACTION_SEND
                        shareIntent.putExtra(
                            Intent.EXTRA_TEXT,
                            "Check Out " + car.name + " Listed On Autolist. View Car Details: https://www.autolist.co.ke/vehicle/"+ car.id +" Get Autolist App from play store: https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID
                        )
                        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri)
                        shareIntent.type = "*/*"
                        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        binding.progressBarSmall.visibility = View.INVISIBLE
                        ContextCompat.startActivity(requireContext(), shareIntent, null)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                    }
                })
        }

        binding.buttonFavourite.setOnClickListener {
            if (session.getSession() == "userLoggedOut") {
                val logInUserFragment = LogInUserFragment()
                logInUserFragment.setStyle(
                    BottomSheetDialogFragment.STYLE_NO_FRAME,
                    0
                )
                logInUserFragment.show(requireActivity().supportFragmentManager, "LogInUserFragment")
            }
            else{
                binding.favouriteProgressBar.visibility = View.VISIBLE
                viewModel.favouriteCar(
                    session.getSession()!!,
                    car.id!!,
                    requireContext()
                )
                viewModel.addCarToFavouriteList.observe(
                    viewLifecycleOwner,
                    androidx.lifecycle.Observer {
                        if (it.message == "Car added to favourite list successfuly") {
                            session.clearUserFavouriteCars()
                            session.saveUserFavoriteCars(it.userObject?.favouriteCars!!)
                            Toast.makeText(requireContext(), "Car added to favourite list successfully", Toast.LENGTH_SHORT).show()
                            binding.favouriteProgressBar.visibility = View.GONE
                            binding.buttonFavourite.setImageResource(R.drawable.ic_favourite_24)
                            requireActivity().viewModelStore.clear()
                        }
                        if (it.message == "Car removed from favourite list successfuly") {
                            session.clearUserFavouriteCars()
                            session.saveUserFavoriteCars(it.userObject?.favouriteCars!!)
                            Toast.makeText(requireContext(), "Car removed from favourite list successfully", Toast.LENGTH_SHORT).show()
                            binding.favouriteProgressBar.visibility = View.GONE
                            binding.buttonFavourite.setImageResource(R.drawable.ic_favourite_outline_24)
                            requireActivity().viewModelStore.clear()
                        }
                    })

            }

        }

        binding.progressBarSmall.visibility = View.INVISIBLE
        binding.imageSlider.setImageList(imageList, ScaleTypes.CENTER_CROP)
        binding.price.text = NumberFormat.getNumberInstance(Locale.US).format(car.price).toString()
        binding.location.text = car.location
        binding.condition.text = car.condition
        binding.transmission.text = car.transmission
        binding.mileage.text = NumberFormat.getNumberInstance(Locale.US).format(car.mileage).toString()+" Km"
        binding.fuel.text = car.fuel
        binding.engine.text = NumberFormat.getNumberInstance(Locale.US).format(car.engineSize).toString()+" CC"
        binding.make.text = car.make
        binding.model.text = car.model
        binding.year.text = car.year.toString()
        binding.body.text= car.body
        binding.duty.text = car.duty
        binding.color.text = car.color
        binding.interior.text = car.color
        binding.interior.text = car.interior
        binding.description.text = car.description
        phoneNumber = car.seller?.sellerNumber
        email = car.seller?.sellerEmail
        carName = car.name

        binding.imageSlider.setItemClickListener(object : ItemClickListener {
            override fun onItemSelected(position: Int) {
                val bundle = bundleOf("carObject" to car)
                Navigation.findNavController(requireActivity(), R.id.fragment).navigate(
                    R.id.viewImageFragment,
                    bundle
                )
            }
        })

        if (car.seller!!.sellerPhoto.isNotEmpty()){
            binding.imageProgressBar.visibility = View.VISIBLE
            Glide.with(requireContext())
                .load(car.seller!!.sellerPhoto)
                .listener(object : RequestListener<Drawable?> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable?>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding.imageProgressBar.visibility = View.GONE
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable?>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding.imageProgressBar.visibility = View.GONE
                        return false
                    }

                })
                .into(binding.userImage)
        }

        binding.userImage.setOnClickListener {
            val bundle = bundleOf("car" to car)
            findNavController().navigate(R.id.viewUserFragment, bundle)
        }

        binding.userName.text = "Seller : " + car.seller!!.sellerName

        val s = car.seller!!.sellerAvailableSince
        val arr = s.split(" ".toRegex()).toTypedArray()
        binding.userJoinedDate.text = "Joined : "+ arr[1] +" "+ arr[3]

        binding.carViews.text = car.views.toString() + " Views"

        binding.buttonCall.setOnClickListener {
            val intent = Intent(
                Intent.ACTION_DIAL,
                Uri.parse("tel:" + Uri.encode("0" + phoneNumber.toString()))
            )
            ContextCompat.startActivity(requireActivity(), intent, null)
        }
        binding.buttonWhatsApp.setOnClickListener {
            val sendIntent = Intent(Intent.ACTION_VIEW)
            val url =
                "https://api.whatsapp.com/send?phone=" + "254"+"${car.seller?.sellerNumber}" + "&text=" + URLEncoder.encode(
                    "Hello, I am interested in your ${car.name} listed in Motii!. Top Cars",
                    "UTF-8"
                )
            sendIntent.data = Uri.parse(url)
            requireActivity().startActivity(sendIntent)
        }
        binding.buttonMessage.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("smsto:" + Uri.encode("0" + phoneNumber.toString()))
            intent.putExtra(
                "sms_body",
                "Hallo, I am interested in your vehicle $carName Listed On Motii. Top Cars"
            )
            ContextCompat.startActivity(requireActivity(), intent, null)
        }

        binding.featuresRecyclerView.also {
            it.layoutManager = GridLayoutManager(
                requireContext(),
                3,
                GridLayoutManager.VERTICAL,
                false
            )
            it.setHasFixedSize(false)
            it.adapter = ListCarFeaturesAdapter(featuresList, this.requireActivity())
        }

        binding.recyclerView.also {
            it.layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )
            it.setHasFixedSize(true)
            it.adapter = adapter
        }

//        val adLoader = AdLoader.Builder(requireActivity(), getString(R.string.ad_unit_id))
//            .forUnifiedNativeAd { ad: UnifiedNativeAd ->
//                // Show the ad.
//                val styles = NativeTemplateStyle.Builder().withMainBackgroundColor(
//                    ColorDrawable(
//                        Color.parseColor(
//                            "#ffffff"
//                        )
//                    )
//                ).build()
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


    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        binding=  DataBindingUtil.inflate(inflater, R.layout.fragment_car_details, container, false)
        return binding.root
    }

    override fun onItemClick(position: Int) {
        val bundle = bundleOf("carsList" to similarCarsList)
        bundle.putInt("Position", position)
        Navigation.findNavController(requireActivity(), R.id.fragment).navigate(
            R.id.carDetailsFragment,
            bundle
        )
    }

    override fun contactSellerOnClick(position: Int) {
        var email :String = ""
        contactSellerDialog = ContactSellerDialog(requireActivity())
        val carObject = carsList[position] as CarObject
        if (carObject.seller?.sellerEmail != null){
            email = carObject.seller.sellerEmail
        }
        contactSellerDialog.startLoadingContactDialog(carObject)
    }

    override fun shareButtonOnClick(position: Int) {
        val carObject = carsList[position] as CarObject
        binding.progressBarSmall.visibility = View.VISIBLE
        Glide.with(requireContext())
            .asBitmap()
            .load(carObject.images?.get(0))
            .apply(RequestOptions().signature(ObjectKey("signature string")))
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    val path = MediaStore.Images.Media.insertImage(
                        requireActivity().contentResolver,
                        resource,
                        "IMG_" + Calendar.getInstance().time,
                        null
                    )
                    val imageUri = Uri.parse(path)
                    val shareIntent = Intent()
                    shareIntent.action = Intent.ACTION_SEND
                    shareIntent.putExtra(
                        Intent.EXTRA_TEXT,
                        "Check Out " + carObject.name + " Listed On Moti Car Listing App https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID
                    )
                    shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri)
                    shareIntent.type = "*/*"
                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    binding.progressBarSmall.visibility = View.INVISIBLE
                    ContextCompat.startActivity(requireContext(), shareIntent, null)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                }
            })
    }


}


