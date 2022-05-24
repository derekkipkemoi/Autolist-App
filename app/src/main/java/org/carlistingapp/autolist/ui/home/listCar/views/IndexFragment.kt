package org.carlistingapp.autolist.ui.home.listCar.views

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
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
import org.carlistingapp.autolist.data.network.ListingCarsAPI
import org.carlistingapp.autolist.data.network.NetworkConnectionInterceptor
import org.carlistingapp.autolist.data.repositories.UserRepository
import org.carlistingapp.autolist.databinding.FragmentIndexBinding
import org.carlistingapp.autolist.ui.home.listCar.adapters.MainCarsAdapter
import org.carlistingapp.autolist.ui.home.listCar.adapters.SliderAdapter
import org.carlistingapp.autolist.ui.home.listCar.viewModels.ListCarViewModel
import org.carlistingapp.autolist.ui.home.listCar.viewModels.ListCarViewModelFactory
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import java.util.*
import kotlin.collections.ArrayList


class IndexFragment : Fragment() , KodeinAware, SliderAdapter.OnItemClickListener, MainCarsAdapter.OnItemClickListener{
    override val kodein by kodein()
    val api : ListingCarsAPI by instance()
    private val networkConnectionInterceptor : NetworkConnectionInterceptor by instance()
    val repository : UserRepository by instance()
    val factory : ListCarViewModelFactory by instance()
    private lateinit var viewModel: ListCarViewModel
    private lateinit var binding : FragmentIndexBinding
    private val sliderHandler = Handler()
    private lateinit var viewPager2 : ViewPager2
    private lateinit var adapter: MainCarsAdapter
    private lateinit var contactSellerDialog: ContactSellerDialog

    private lateinit var carsList : ArrayList<CarObject>
    private lateinit var trendingCarsList : ArrayList<CarObject>
    private val imageList = ArrayList<CarObject>()
    @SuppressLint("ResourceType")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =  DataBindingUtil.inflate(inflater, R.layout.fragment_index, container, false)
        val view = binding.root
        view.setOnScrollChangeListener(null)
        requireActivity().toolBar.title = ""

        viewModel = ViewModelProvider(this, factory).get(ListCarViewModel::class.java)
        viewPager2 = binding.viewPagerImageSlider


            viewModel.getCars(requireContext())
            binding.progressBar.visibility = View.VISIBLE
            viewModel.cars.observe(viewLifecycleOwner, Observer { cars ->
                binding.progressBar.visibility = View.INVISIBLE
                carsList = cars
                trendingCarsList = cars

                imageList.clear()
                for (image in cars){
                    imageList.add(image)
                }
//                viewPager2.adapter = SliderAdapter(imageList,viewPager2,requireContext(),this)
//                viewPager2.registerOnPageChangeCallback( object : ViewPager2.OnPageChangeCallback(){
//                    override fun onPageSelected(position: Int) {
//                        super.onPageSelected(position)
//                        sliderHandler.removeCallbacks(sliderRunnable)
//                        sliderHandler.postDelayed(sliderRunnable, 3000)
//                    }
//                })
                
                trendingCarsList.sortByDescending {
                    it.views
                }
                binding.recyclerView.also {
                    it.layoutManager = LinearLayoutManager(requireContext())
                    it.setHasFixedSize(true)
                    it.adapter = MainCarsAdapter(trendingCarsList, requireActivity(), this)
                }
            })


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

        binding.saloonsVehicles.setOnClickListener {
            val saloons = carsList.filter { it.body == "Saloons" }
            val bundle = bundleOf("carsList" to saloons)
            bundle.putString("FragmentName", "Saloons")
            Navigation.findNavController(requireActivity(), R.id.fragment).navigate(
                R.id.listCarFragment,
                bundle
            )
        }

        binding.hatchbacksVehicles.setOnClickListener {
            val hatchBacks = carsList.filter { it.body == "Hatchbacks" }
            val bundle = bundleOf("carsList" to hatchBacks)
            bundle.putString("FragmentName", "Hatchbacks")
            Navigation.findNavController(requireActivity(), R.id.fragment).navigate(
                R.id.listCarFragment,
                bundle
            )
        }

        binding.wagonsVehicles.setOnClickListener {
            val stationWagons = carsList.filter { it.body == "Station Wagons" }
            val bundle = bundleOf("carsList" to stationWagons)
            bundle.putString("FragmentName", "Station Wagons")
            Navigation.findNavController(requireActivity(), R.id.fragment).navigate(
                R.id.listCarFragment,
                bundle
            )
        }

        binding.suvVehicles.setOnClickListener {
            val suv = carsList.filter { it.body == "SUV" }
            val bundle = bundleOf("carsList" to suv)
            bundle.putString("FragmentName", "Sport Utility Vehicles")
            Navigation.findNavController(requireActivity(), R.id.fragment).navigate(
                R.id.listCarFragment,
                bundle
            )
        }

        binding.vanVehicles.setOnClickListener {
            val vanBuses = carsList.filter { it.body == "Van/Buses" }
            val bundle = bundleOf("carsList" to vanBuses)
            bundle.putString("FragmentName", "Van and Buses")
            Navigation.findNavController(requireActivity(), R.id.fragment).navigate(
                R.id.listCarFragment,
                bundle
            )
        }

        binding.truckVehicles.setOnClickListener {
            val tracksTrailers = carsList.filter { it.body == "Trucks/Trailers" }
            val bundle = bundleOf("carsList" to tracksTrailers)
            bundle.putString("FragmentName", "Trucks and Trailers")
            Navigation.findNavController(requireActivity(), R.id.fragment).navigate(
                R.id.listCarFragment,
                bundle
            )
        }

        binding.machineryVehicles.setOnClickListener {
            val tractorsMachinery = carsList.filter { it.body == "Tractors/Machinery" }
            val bundle = bundleOf("carsList" to tractorsMachinery)
            bundle.putString("name", "Tractors and Machinery")
            Navigation.findNavController(requireActivity(), R.id.fragment).navigate(
                R.id.listCarFragment,
                bundle
            )
        }

        binding.motorcycles.setOnClickListener {
            val motorBikes = carsList.filter { it.body == "MotorBikes" }
            val bundle = bundleOf("carsList" to motorBikes)
            bundle.putString("FragmentName", "Motorcycles")
            Navigation.findNavController(requireActivity(), R.id.fragment).navigate(
                R.id.listCarFragment,
                bundle
            )
        }

        binding.toyota.setOnClickListener {
            val toyota = carsList.filter { it.make == "Toyota" }
            val bundle = bundleOf("carsList" to toyota)
            bundle.putString("FragmentName", "Toyota")
            Navigation.findNavController(requireActivity(), R.id.fragment).navigate(
                R.id.listCarFragment,
                bundle
            )
        }

        binding.nissan.setOnClickListener {
            val nissan = carsList.filter { it.make == "Nissan" }
            val bundle = bundleOf("carsList" to nissan)
            bundle.putString("FragmentName", "Nissan")
            Navigation.findNavController(requireActivity(), R.id.fragment).navigate(
                R.id.listCarFragment,
                bundle
            )
        }

        binding.subaru.setOnClickListener {
            val subaru = carsList.filter { it.make == "Subaru" }
            val bundle = bundleOf("carsList" to subaru)
            bundle.putString("FragmentName", "Subaru")
            Navigation.findNavController(requireActivity(), R.id.fragment).navigate(
                R.id.listCarFragment,
                bundle
            )
        }

        binding.honda.setOnClickListener {
            val honda = carsList.filter { it.make == "Honda" }
            val bundle = bundleOf("carsList" to honda)
            bundle.putString("FragmentName", "Honda")
            Navigation.findNavController(requireActivity(), R.id.fragment).navigate(
                R.id.listCarFragment,
                bundle
            )
        }

        binding.mitsubishi.setOnClickListener {
            val mitsubishi = carsList.filter { it.make == "Mitsubishi" }
            val bundle = bundleOf("carsList" to mitsubishi)
            bundle.putString("FragmentName", "Mitsubishi")
            Navigation.findNavController(requireActivity(), R.id.fragment).navigate(
                R.id.listCarFragment,
                bundle
            )
        }

        binding.mercedes.setOnClickListener {
            val mercedes = carsList.filter { it.make == "Mercedes" }
            val bundle = bundleOf("carsList" to mercedes)
            bundle.putString("FragmentName", "Mercedes")
            Navigation.findNavController(requireActivity(), R.id.fragment).navigate(
                R.id.listCarFragment,
                bundle
            )
        }

        binding.mazda.setOnClickListener {
            val mazda = carsList.filter { it.make == "Mazda" }
            val bundle = bundleOf("carsList" to mazda)
            bundle.putString("FragmentName", "Mazda")
            Navigation.findNavController(requireActivity(), R.id.fragment).navigate(
                R.id.listCarFragment,
                bundle
            )
        }

        binding.volkswagen.setOnClickListener {
            val volkswagen = carsList.filter { it.make == "Volkswagen" }
            val bundle = bundleOf("carsList" to volkswagen)
            bundle.putString("FragmentName", "Volkswagen")
            Navigation.findNavController(requireActivity(), R.id.fragment).navigate(
                R.id.listCarFragment,
                bundle
            )
        }

        binding.bmw.setOnClickListener {
            val bmw = carsList.filter { it.make == "BMW" }
            val bundle = bundleOf("carsList" to bmw)
            bundle.putString("FragmentName", "BMW")
            Navigation.findNavController(requireActivity(), R.id.fragment).navigate(
                R.id.listCarFragment,
                bundle
            )
        }

        binding.landRover.setOnClickListener {
            val landRovers = carsList.filter { it.make == "Land Rovers&Range Rover" }
            val bundle = bundleOf("carsList" to landRovers)
            bundle.putString("FragmentName", "LandRovers")
            Navigation.findNavController(requireActivity(), R.id.fragment).navigate(
                R.id.listCarFragment,
                bundle
            )
        }

        binding.isuzu.setOnClickListener {
            val isuzu = carsList.filter { it.make == "Isuzu" }
            val bundle = bundleOf("carsList" to isuzu)
            bundle.putString("FragmentName", "Isuzu")
            Navigation.findNavController(requireActivity(), R.id.fragment).navigate(
                R.id.listCarFragment,
                bundle
            )
        }

        binding.audi.setOnClickListener {
            val audi = carsList.filter { it.make == "Audi" }
            val bundle = bundleOf("carsList" to audi)
            bundle.putString("FragmentName", "Audi")
            Navigation.findNavController(requireActivity(), R.id.fragment).navigate(
                R.id.listCarFragment,
                bundle
            )
        }

        binding.nairobi.setOnClickListener {
            val nairobi = carsList.filter { it.location == "Nairobi" }
            val bundle = bundleOf("carsList" to nairobi)
            bundle.putString("FragmentName", "Nairobi")
            Navigation.findNavController(requireActivity(), R.id.fragment).navigate(
                R.id.listCarFragment,
                bundle
            )
        }

        binding.mombasa.setOnClickListener {
            val mombasa = carsList.filter { it.location == "Mombasa" }
            val bundle = bundleOf("carsList" to mombasa)
            bundle.putString("FragmentName", "Mombasa")
            Navigation.findNavController(requireActivity(), R.id.fragment).navigate(
                R.id.listCarFragment,
                bundle
            )
        }

        binding.nakuru.setOnClickListener {
            val nakuru = carsList.filter { it.location == "Nakuru" }
            val bundle = bundleOf("carsList" to nakuru)
            bundle.putString("FragmentName", "Nakuru")
            Navigation.findNavController(requireActivity(), R.id.fragment).navigate(
                R.id.listCarFragment,
                bundle
            )
        }

        binding.eldoret.setOnClickListener {
            val eldoret = carsList.filter { it.location == "Eldoret" }
            val bundle = bundleOf("carsList" to eldoret)
            bundle.putString("FragmentName", "Eldoret")
            Navigation.findNavController(requireActivity(), R.id.fragment).navigate(
                R.id.listCarFragment,
                bundle
            )
        }

        binding.otherLocations.setOnClickListener {
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
        return view
    }

    val sliderRunnable = Runnable {
        viewPager2.currentItem = viewPager2.currentItem +1
    }

    override fun onItemClick(position: Int) {
        val bundle = bundleOf("carsList" to trendingCarsList)
        bundle.putInt("Position",position)
        Navigation.findNavController(requireActivity(), R.id.fragment).navigate(
            R.id.carDetailsFragment,
            bundle
        )
    }

    override fun onSliderItemClick(position: Int) {
        val bundle = bundleOf("carsList" to carsList)
        bundle.putInt("Position",position)
        Navigation.findNavController(requireActivity(), R.id.fragment).navigate(
            R.id.carDetailsFragment,
            bundle
        )
    }

    override fun contactSellerOnClick(position: Int) {
        contactSellerDialog = ContactSellerDialog(requireActivity())
        contactSellerDialog.startLoadingContactDialog(carsList[position])
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


}


