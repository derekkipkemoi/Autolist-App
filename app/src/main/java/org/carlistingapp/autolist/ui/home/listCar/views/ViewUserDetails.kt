package org.carlistingapp.autolist.ui.home.listCar.views

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.ads.nativetemplates.NativeTemplateStyle
import com.google.android.ads.nativetemplates.TemplateView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.formats.NativeAdOptions
import com.google.android.gms.ads.formats.UnifiedNativeAd
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_payments.view.*
import org.carlistingapp.autolist.R
import org.carlistingapp.autolist.data.db.entities.CarObject
import org.carlistingapp.autolist.data.network.ListingCarsAPI
import org.carlistingapp.autolist.databinding.FragmentViewUserDetailsBinding
import org.carlistingapp.autolist.ui.home.listCar.adapters.SimilarCarsAdapter
import org.carlistingapp.autolist.ui.home.profile.viewModel.UserViewModel
import org.carlistingapp.autolist.ui.home.profile.viewModel.UserViewModelFactory
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class ViewUserDetails : Fragment(), KodeinAware,SimilarCarsAdapter.OnItemClickListener {
    override val kodein by kodein()
    private lateinit var binding : FragmentViewUserDetailsBinding
    private lateinit var api : ListingCarsAPI
    private val factory: UserViewModelFactory by instance()
    private lateinit var viewModel : UserViewModel
    private lateinit var carsList : ArrayList<CarObject>
    private lateinit var contactSellerDialog: ContactSellerDialog

    @SuppressLint("SetTextI18n")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val car : CarObject = arguments?.getParcelable("car")!!

        requireActivity().toolBar.title = car.seller?.sellerName
        binding.userName.text = "Hi, I am " +car.seller?.sellerName


        val s = car.seller!!.sellerAvailableSince
        val arr = s.split(" ".toRegex()).toTypedArray()
        binding.userJoinedDate.text = "Joined : "+ arr[1] +" "+ arr[3]


        if (car.seller.sellerPhoto.isNotEmpty()){
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

        binding.userConfirmedDetails.text = car.seller.sellerName + " Confirmed"
        binding.userPhone.text = "Phone Number"
        if (car.seller.sellerEmail.length > 2){
            binding.userEmail.text = "Email Address"
        }else{
            binding.userEmail.visibility = View.GONE
        }

        binding.userListings.text = car.seller.sellerName + " Listings"

       viewModel = ViewModelProvider(this, factory).get(UserViewModel::class.java)
        binding.progressBar.visibility = View.VISIBLE
        viewModel.viewUserCars(car.seller.sellerID, requireContext())
        viewModel.userCars.observe(viewLifecycleOwner, Observer { carObjectResponse ->
            binding.progressBar.visibility = View.INVISIBLE
            carsList = carObjectResponse
            binding.recyclerView.also {
                it.layoutManager = LinearLayoutManager(
                    requireContext(),
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
                it.setHasFixedSize(true)
                binding.userListingsSize.text = carsList.size.toString()
                it.adapter = SimilarCarsAdapter(carsList, requireActivity(), this)
            }
        })

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_view_user_details, container, false)
        return binding.root
    }

    override fun onItemClick(position: Int) {
        val bundle = bundleOf("carsList" to carsList)
        bundle.putInt("Position", position)
        Navigation.findNavController(requireActivity(), R.id.fragment).navigate(
            R.id.carDetailsFragment,
            bundle
        )
    }

    override fun contactSellerOnClick(position: Int) {
        contactSellerDialog = ContactSellerDialog(requireActivity())
        val carObject = carsList[position]
        contactSellerDialog.startLoadingContactDialog(carObject)
    }

    override fun shareButtonOnClick(position: Int) {
        TODO("Not yet implemented")
    }
}