package org.carlistingapp.autolist.ui.home.listCar.views

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.bumptech.glide.signature.ObjectKey
import kotlinx.android.synthetic.main.activity_home.*
import org.carlistingapp.autolist.BuildConfig
import org.carlistingapp.autolist.R
import org.carlistingapp.autolist.data.db.entities.CarObject
import org.carlistingapp.autolist.data.db.entities.HomeItem
import org.carlistingapp.autolist.data.network.ListingCarsAPI
import org.carlistingapp.autolist.data.repositories.UserRepository
import org.carlistingapp.autolist.databinding.FragmentHomeDashboardBinding
import org.carlistingapp.autolist.ui.home.listCar.adapters.*
import org.carlistingapp.autolist.ui.home.listCar.viewModels.ListCarViewModel
import org.carlistingapp.autolist.ui.home.listCar.viewModels.ListCarViewModelFactory
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs
import kotlin.properties.Delegates


class HomeDashboardFragment : Fragment(), KodeinAware,
    SliderAdapter.OnItemClickListener, MainCarsAdapter.OnItemClickListener, SimilarCarsAdapter.OnItemClickListener, HomeItemAdapter.HomeItemClicked {
    override val kodein by kodein()
    val api : ListingCarsAPI by instance()
    val repository : UserRepository by instance()
    val factory : ListCarViewModelFactory by instance()
    private lateinit var viewModel: ListCarViewModel
    private lateinit var binding : FragmentHomeDashboardBinding
    private val sliderHandler = Handler()
    private lateinit var viewPager2 : ViewPager2
    private lateinit var contactSellerDialog: ContactSellerDialog
    private var carInSliderPosition by Delegates.notNull<Int>()
    private var carsList = ArrayList<CarObject>()
    private var trendingCarsList = ArrayList<CarObject>()
    private var brandList = ArrayList<HomeItem>()
//    private val sliderList = ArrayList<CarObject>()
    private val newSliderList = ArrayList<CarObject>()
    private var bodyList = ArrayList<HomeItem>()
    private var locationList = ArrayList<HomeItem>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this, factory).get(ListCarViewModel::class.java)

        brandList.add(HomeItem(R.drawable.ic_toyota, "Toyota"))
        brandList.add(HomeItem(R.drawable.ic_nissan, "Nissan"))
        brandList.add(HomeItem(R.drawable.ic_subaru, "Subaru"))
        brandList.add(HomeItem(R.drawable.ic_honda, "Honda"))
        brandList.add(HomeItem(R.drawable.ic_mitsubishi, "Mitsubishi"))
        brandList.add(HomeItem(R.drawable.ic_mercedes_benz, "Mercedes"))
        brandList.add(HomeItem(R.drawable.ic_mazda, "Mazda"))
        brandList.add(HomeItem(R.drawable.ic_volkswagen, "Volkswagen"))
        brandList.add(HomeItem(R.drawable.ic_bmw, "BMW"))
        brandList.add(HomeItem(R.drawable.ic_land_rover, "Land Rover"))
        brandList.add(HomeItem(R.drawable.ic_isuzu_2, "Isuzu"))
        brandList.add(HomeItem(R.drawable.ic_audi, "Audi"))

        bodyList.add(HomeItem(R.drawable.ic_car,"Saloons"))
        bodyList.add(HomeItem(R.drawable.ic_car_hatchback,"Hatchbacks"))
        bodyList.add(HomeItem(R.drawable.ic_wagon,"Wagons"))
        bodyList.add(HomeItem(R.drawable.ic_suv,"SUV"))
        bodyList.add(HomeItem(R.drawable.ic_microbus,"Van and Buses"))
        bodyList.add(HomeItem(R.drawable.ic_truck,"Trucks and Trailers"))
        bodyList.add(HomeItem(R.drawable.ic_tractor,"Heavy Equipments"))
        bodyList.add(HomeItem(R.drawable.ic_motorcycle,"Motorbikes"))

        locationList.add(HomeItem(R.drawable.ic_location_home_24,"Nairobi"))
        locationList.add(HomeItem(R.drawable.ic_location_home_24,"Mombasa"))
        locationList.add(HomeItem(R.drawable.ic_location_home_24,"Nakuru"))
        locationList.add(HomeItem(R.drawable.ic_location_home_24,"Kisumu"))
        locationList.add(HomeItem(R.drawable.ic_location_home_24,"Other Locations"))

    }

    @SuppressLint("SimpleDateFormat")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_home_dashboard, container, false)
        binding.root.setOnScrollChangeListener(null)
        requireActivity().toolBar.title = ""


        binding.progressBar.visibility = View.VISIBLE
        viewModel.getCars(requireContext())
        viewModel.cars.observe(viewLifecycleOwner, Observer { cars ->
            binding.progressBar.visibility = View.INVISIBLE
            carsList = cars
            trendingCarsList = cars
//            sliderList.clear()
            newSliderList.clear()



//            for (car in cars){
//                val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss'Z'")
//                val endDate = format.parse(car.featured!!.endDay)
//                format.parse(car.featured.startDay)
//                val today = Calendar.getInstance().time
//                val diff: Long = endDate!!.time - today.time
//                val seconds = diff / 1000
//                val minutes = seconds / 60
//                val hours = minutes / 60
//                val days = hours / 24
//
//                if (days > 0){
//                    sliderList.add(car)
//                   // newSliderList.add(car)
//                }
//            }


            newSliderList.addAll(cars)

            newSliderList.sortByDescending {
                it.createdAt
            }
            newSliderList.sortBy {
                it.views
            }



            viewPager2.adapter = SliderAdapter(newSliderList,viewPager2,requireContext(),this)
            viewPager2.registerOnPageChangeCallback( object : ViewPager2.OnPageChangeCallback(){
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    sliderHandler.removeCallbacks(sliderRunnable)
                    sliderHandler.postDelayed(sliderRunnable, 3400)
                }
            })



            trendingCarsList.sortByDescending {
                it.views
            }

//            trendingCarsList.take(2)

            binding.recyclerViewMostViewed.also {
                it.layoutManager = LinearLayoutManager(requireContext(),
                    LinearLayoutManager.HORIZONTAL,
                    false)
                it.setHasFixedSize(true)
                it.adapter = SimilarCarsAdapter(trendingCarsList, requireActivity(), this)
            }

            binding.searchView.setOnClickListener {
                val bundle = bundleOf("carsList" to carsList)
                findNavController().navigate(R.id.searchCarFragment, bundle)
            }

            binding.filterIcon.setOnClickListener {
                val bundle = bundleOf("carsList" to carsList)
                bundle.putString("Filter", "Filter icon clicked")
                Navigation.findNavController(requireActivity(), R.id.fragment).navigate(
                    R.id.listCarFragment,
                    bundle
                )

            }





        })

        viewPager2 = binding.viewPagerImageSlider
        viewPager2.clipToPadding = false
        viewPager2.clipChildren = false
        viewPager2.offscreenPageLimit = 3
        viewPager2.getChildAt(0).overScrollMode = View.OVER_SCROLL_NEVER

        val transformer = CompositePageTransformer()
        transformer.addTransformer(MarginPageTransformer(20))
        transformer.addTransformer(ViewPager2.PageTransformer { page, position ->
            val v = 1 - abs(position)
            page.scaleY = 0.8f + v * 0.2f
        })
        viewPager2.setPageTransformer(transformer)

        binding.foreignUsedButton.setOnClickListener {
            val foreignUsedCars = carsList.filter { it.condition == "Foreign Used" }
            val bundle = bundleOf("carsList" to foreignUsedCars)
            bundle.putString("FragmentName", "Foreign Used")
            Navigation.findNavController(requireActivity(), R.id.fragment).navigate(
                R.id.listCarFragment,
                bundle
            )
        }

        binding.locallyUsedButton.setOnClickListener {
            val locallyUsedCars = carsList.filter {
                it.condition == "Locally Used"
            }
            val bundle = bundleOf("carsList" to locallyUsedCars)
            bundle.putString("FragmentName", "Locally Used")
            Navigation.findNavController(requireActivity(), R.id.fragment).navigate(
                R.id.listCarFragment,
                bundle
            )
        }
        binding.brandNewButton.setOnClickListener {
            val newUsedCars = carsList.filter { it.condition == "Brand New" }
            val bundle = bundleOf("carsList" to newUsedCars)
            bundle.putString("FragmentName", "Brand New")
            Navigation.findNavController(requireActivity(), R.id.fragment).navigate(
                R.id.listCarFragment,
                bundle
            )
        }

        binding.allVehicles.setOnClickListener {
            val bundle = bundleOf("carsList" to carsList)
            bundle.putString("FragmentName", "All")
            Navigation.findNavController(requireActivity(), R.id.fragment).navigate(
                R.id.listCarFragment,
                bundle
            )
        }

        binding.trending.setOnClickListener {
            val bundle = bundleOf("carsList" to trendingCarsList)
            bundle.putString("FragmentName", "Foreign Used")
            Navigation.findNavController(requireActivity(), R.id.fragment).navigate(
                R.id.listCarFragment,
                bundle
            )
        }

        binding.allFeatured.setOnClickListener {
            val listForFeatured = carsList
            listForFeatured.sortByDescending {
                it.createdAt
            }
            listForFeatured.sortBy {
                it.views
            }


            val bundle = bundleOf("carsList" to listForFeatured)
            bundle.putString("FragmentName", "Foreign Used")
            Navigation.findNavController(requireActivity(), R.id.fragment).navigate(
                R.id.listCarFragment,
                bundle
            )
        }


        binding.recyclerViewMakes.also {
            it.layoutManager = LinearLayoutManager(requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false)
            it.setHasFixedSize(true)
            it.adapter = HomeItemAdapter(brandList, requireActivity(),this)
        }

        binding.recyclerViewBody.also {
            it.layoutManager = LinearLayoutManager(requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false)
            it.setHasFixedSize(true)
            it.adapter = HomeItemAdapter(bodyList, requireActivity(),this)
        }

        binding.recyclerViewLocation.also {
            it.layoutManager = LinearLayoutManager(requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false)
            it.setHasFixedSize(true)
            it.adapter = HomeItemAdapter(locationList, requireActivity(),this)
        }
        return binding.root
    }

    val sliderRunnable = Runnable {
        viewPager2.currentItem = viewPager2.currentItem +1
    }

    override fun onSliderItemClick(position: Int) {
        val carInSlider = newSliderList[position]
        val carID = carInSlider.id
        for (car in carsList){
            if (car.id == carID){
                carInSliderPosition = carsList.indexOf(car)
            }
        }
        val bundle = bundleOf("carsList" to carsList)
        bundle.putInt("Position",carInSliderPosition)
        Navigation.findNavController(requireActivity(), R.id.fragment).navigate(
            R.id.carDetailsFragment,
            bundle
        )
    }

    override fun onItemClick(position: Int) {
        val bundle = bundleOf("carsList" to carsList)
        bundle.putInt("Position",position)
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
        binding.progressBarSmall.visibility = View.VISIBLE
        Glide.with(requireActivity())
            .asBitmap()
            .load(carsList[position].images?.get(0))
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
                        "Check Out " + carsList[position].name + " Listed On Motii Listing App https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID
                    )
                    shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri)
                    shareIntent.type = "image/*"
                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    binding.progressBarSmall.visibility = View.INVISIBLE
                    startActivity(Intent.createChooser(shareIntent, "send"))
                }
                override fun onLoadCleared(placeholder: Drawable?) {
                }
            })
    }


    override fun onHomeItemClicked(position: Int, name: String) {
        when(name){
            "Toyota" -> {
                val toyota = carsList.filter { it.make == "Toyota" }
                val bundle = bundleOf("carsList" to toyota)
                bundle.putString("FragmentName", "Toyota")
                Navigation.findNavController(requireActivity(), R.id.fragment).navigate(
                    R.id.listCarFragment,
                    bundle
                )
            }
            "Nissan" -> {
                val nissan = carsList.filter { it.make == "Nissan" }
                val bundle = bundleOf("carsList" to nissan)
                bundle.putString("FragmentName", "Nissan")
                Navigation.findNavController(requireActivity(), R.id.fragment).navigate(
                    R.id.listCarFragment,
                    bundle
                )
            }
            "Subaru" -> {
                val subaru = carsList.filter { it.make == "Subaru" }
                val bundle = bundleOf("carsList" to subaru)
                bundle.putString("FragmentName", "Subaru")
                Navigation.findNavController(requireActivity(), R.id.fragment).navigate(
                    R.id.listCarFragment,
                    bundle
                )
            }
            "Mitsubishi" -> {
                val mitsubishi = carsList.filter { it.make == "Mitsubishi" }
                val bundle = bundleOf("carsList" to mitsubishi)
                bundle.putString("FragmentName", "Mitsubishi")
                Navigation.findNavController(requireActivity(), R.id.fragment).navigate(
                    R.id.listCarFragment,
                    bundle
                )
            }
            "Honda" -> {
                val honda = carsList.filter { it.make == "Honda" }
                val bundle = bundleOf("carsList" to honda)
                bundle.putString("FragmentName", "Honda")
                Navigation.findNavController(requireActivity(), R.id.fragment).navigate(
                    R.id.listCarFragment,
                    bundle
                )
            }
            "Mercedes" -> {
                val mercedes = carsList.filter { it.make == "Mercedes" }
                val bundle = bundleOf("carsList" to mercedes)
                bundle.putString("FragmentName", "Mercedes")
                Navigation.findNavController(requireActivity(), R.id.fragment).navigate(
                    R.id.listCarFragment,
                    bundle
                )
            }
            "Mazda" -> {
                val mazda = carsList.filter { it.make == "Mazda" }
                val bundle = bundleOf("carsList" to mazda)
                bundle.putString("FragmentName", "Mazda")
                Navigation.findNavController(requireActivity(), R.id.fragment).navigate(
                    R.id.listCarFragment,
                    bundle
                )
            }
            "Volkswagen" -> {
                val volkswagen = carsList.filter { it.make == "Volkswagen" }
                val bundle = bundleOf("carsList" to volkswagen)
                bundle.putString("FragmentName", "Volkswagen")
                Navigation.findNavController(requireActivity(), R.id.fragment).navigate(
                    R.id.listCarFragment,
                    bundle
                )
            }
            "BMW" -> {
                val bmw = carsList.filter { it.make == "BMW" }
                val bundle = bundleOf("carsList" to bmw)
                bundle.putString("FragmentName", "BMW")
                Navigation.findNavController(requireActivity(), R.id.fragment).navigate(
                    R.id.listCarFragment,
                    bundle
                )
            }
            "Land Rover" -> {
                val landRovers = carsList.filter { it.make == "LandRover" }
                val bundle = bundleOf("carsList" to landRovers)
                bundle.putString("FragmentName", "LandRovers")
                Navigation.findNavController(requireActivity(), R.id.fragment).navigate(
                    R.id.listCarFragment,
                    bundle
                )
            }
            "Isuzu" -> {
                val isuzu = carsList.filter { it.make == "Isuzu" }
                val bundle = bundleOf("carsList" to isuzu)
                bundle.putString("FragmentName", "Isuzu")
                Navigation.findNavController(requireActivity(), R.id.fragment).navigate(
                    R.id.listCarFragment,
                    bundle
                )
            }
            "Audi" -> {
                val audi = carsList.filter { it.make == "Audi" }
                val bundle = bundleOf("carsList" to audi)
                bundle.putString("FragmentName", "Audi")
                Navigation.findNavController(requireActivity(), R.id.fragment).navigate(
                    R.id.listCarFragment,
                    bundle
                )
            }
            "Saloons" -> {
                val saloons = carsList.filter { it.body == "Saloons" }
                val bundle = bundleOf("carsList" to saloons)
                bundle.putString("FragmentName", "Saloons")
                Navigation.findNavController(requireActivity(), R.id.fragment).navigate(
                    R.id.listCarFragment,
                    bundle
                )
            }
            "Hatchbacks" -> {
                val hatchBacks = carsList.filter { it.body == "Hatchbacks" }
                val bundle = bundleOf("carsList" to hatchBacks)
                bundle.putString("FragmentName", "Hatchbacks")
                Navigation.findNavController(requireActivity(), R.id.fragment).navigate(
                    R.id.listCarFragment,
                    bundle
                )
            }
            "Wagons" -> {
                val stationWagons = carsList.filter { it.body == "Station Wagons" }
                val bundle = bundleOf("carsList" to stationWagons)
                bundle.putString("FragmentName", "Station Wagons")
                Navigation.findNavController(requireActivity(), R.id.fragment).navigate(
                    R.id.listCarFragment,
                    bundle
                )
            }
            "SUV" -> {
                val suv = carsList.filter { it.body == "SUV" }
                val bundle = bundleOf("carsList" to suv)
                bundle.putString("FragmentName", "Sport Utility Vehicles")
                Navigation.findNavController(requireActivity(), R.id.fragment).navigate(
                    R.id.listCarFragment,
                    bundle
                )
            }
            "Van and Buses" -> {
                val vanBuses = carsList.filter { it.body == "Vans and Buses" }
                val bundle = bundleOf("carsList" to vanBuses)
                bundle.putString("FragmentName", "Van and Buses")
                Navigation.findNavController(requireActivity(), R.id.fragment).navigate(
                    R.id.listCarFragment,
                    bundle
                )
            }
            "Trucks and Trailers" -> {
                val tracksTrailers = carsList.filter { it.body == "Trucks and Trailers" }
                val bundle = bundleOf("carsList" to tracksTrailers)
                bundle.putString("FragmentName", "Trucks and Trailers")
                Navigation.findNavController(requireActivity(), R.id.fragment).navigate(
                    R.id.listCarFragment,
                    bundle
                )
            }
            "Heavy Equipments" -> {
                val tractorsMachinery = carsList.filter { it.body == "Heavy Equipments" }
                val bundle = bundleOf("carsList" to tractorsMachinery)
                bundle.putString("FragmentName", "Heavy Equipments")
                Navigation.findNavController(requireActivity(), R.id.fragment).navigate(
                    R.id.listCarFragment,
                    bundle
                )
            }
            "Motorbikes" -> {
                val motorBikes = carsList.filter { it.body == "Motorbikes" }
                val bundle = bundleOf("carsList" to motorBikes)
                bundle.putString("FragmentName", "Motorbikes")
                Navigation.findNavController(requireActivity(), R.id.fragment).navigate(
                    R.id.listCarFragment,
                    bundle
                )
            }
            "Nairobi" -> {
                val nairobi = carsList.filter { it.location == "Nairobi" }
                val bundle = bundleOf("carsList" to nairobi)
                bundle.putString("FragmentName", "Nairobi")
                Navigation.findNavController(requireActivity(), R.id.fragment).navigate(
                    R.id.listCarFragment,
                    bundle
                )
            }
            "Mombasa" -> {
                val mombasa = carsList.filter { it.location == "Mombasa" }
                val bundle = bundleOf("carsList" to mombasa)
                bundle.putString("FragmentName", "Mombasa")
                Navigation.findNavController(requireActivity(), R.id.fragment).navigate(
                    R.id.listCarFragment,
                    bundle
                )
            }
            "Nakuru" -> {
                val nakuru = carsList.filter { it.location == "Nakuru" }
                val bundle = bundleOf("carsList" to nakuru)
                bundle.putString("FragmentName", "Nakuru")
                Navigation.findNavController(requireActivity(), R.id.fragment).navigate(
                    R.id.listCarFragment,
                    bundle
                )
            }
            "Kisumu" -> {
                val eldoret = carsList.filter { it.location == "Eldoret" }
                val bundle = bundleOf("carsList" to eldoret)
                bundle.putString("FragmentName", "Eldoret")
                Navigation.findNavController(requireActivity(), R.id.fragment).navigate(
                    R.id.listCarFragment,
                    bundle
                )
            }
            "Other Locations" -> {
                val noEldoret = carsList.filter { it.location != "Eldoret" }
                val noNakuru = noEldoret.filter { it.location != "Nakuru" }
                val noMombasa = noNakuru.filter { it.location != "Mombasa" }
                val noCommonLocation = noMombasa.filter { it.location != "Nairobi" }
                val bundle = bundleOf("carsList" to noCommonLocation)
                bundle.putString("FragmentName", "Other Locations")
                Navigation.findNavController(requireActivity(), R.id.fragment).navigate(
                    R.id.listCarFragment,
                    bundle
                )
            }
        }
    }


}