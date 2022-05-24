package org.carlistingapp.autolist.ui.home.profile.views


import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.exifinterface.media.ExifInterface
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.nguyenhoanglam.imagepicker.model.Image
import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePicker
import kotlinx.android.synthetic.main.activity_home.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.carlistingapp.autolist.R
import org.carlistingapp.autolist.data.db.entities.CarObject
import org.carlistingapp.autolist.data.db.entities.ImageUrl
import org.carlistingapp.autolist.data.network.ListingCarsAPI
import org.carlistingapp.autolist.data.repositories.UserRepository
import org.carlistingapp.autolist.databinding.FragmentEditCarBinding
import org.carlistingapp.autolist.ui.home.postCar.adapters.MoreImagesAdapter
import org.carlistingapp.autolist.ui.home.profile.adapter.ImagesAdapter
import org.carlistingapp.autolist.ui.home.profile.viewModel.UserViewModel
import org.carlistingapp.autolist.ui.home.profile.viewModel.UserViewModelFactory
import org.carlistingapp.autolist.utils.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList
import kotlin.properties.Delegates

class EditCarFragment : Fragment(), KodeinAware, ImagesAdapter.OnClickListener {
    override val kodein by kodein()
    private val api : ListingCarsAPI by instance()
    private val repository : UserRepository by instance()
    private val factory: UserViewModelFactory by instance()
    private lateinit var viewModel: UserViewModel
    private lateinit var binding : FragmentEditCarBinding
    private lateinit var carMakeSelected : String
    private lateinit var carModelList: Array<String?>
    private lateinit var carMakeList: Array<String?>
    private lateinit var carModelSelected : String
    private lateinit var carYearSelected : String
    private lateinit var carBodyTypeSelected : String
    private lateinit var carConditionSelected : String
    private lateinit var carTransMissionSelected : String
    private lateinit var carDutySelected : String
    private lateinit var carSelectedFuel : String
    private lateinit var carSelectedInterior : String
    private lateinit var carSelectedColor : String
    private lateinit var carSelectedLocation : String
    private var carPriceNegotiable : Boolean = true
    private var checkedCarMake by Delegates.notNull<Int>()
    private var checkedCarModel by Delegates.notNull<Int>()
    private var checkedCarYear by Delegates.notNull<Int>()
    private var checkedCarBody by Delegates.notNull<Int>()
    private var checkedCarCondition by Delegates.notNull<Int>()
    private var checkedCarTransmission by Delegates.notNull<Int>()
    private var checkedCarDuty by Delegates.notNull<Int>()

    private lateinit var carName : String
    private lateinit var carEngineSize : String
    private lateinit var carMileage : String
    private lateinit var carDescription : String
    private lateinit var carPrice : String
    private var checkedCarFuel by Delegates.notNull<Int>()
    private var checkedCarInterior by Delegates.notNull<Int>()
    private var checkedCarColor by Delegates.notNull<Int>()
    private var checkedCarLocation by Delegates.notNull<Int>()
    private val commonFeaturesList = HashSet<String>()
    private val commonFeaturesCheckedStates = booleanArrayOf(
        false,
        false,
        false,
        false,
        false,
        false,
        false,
        false,
        false,
        false,
        false,
        false,
        false
    )
    private val extraFeaturesCheckedStates = booleanArrayOf(
        false,
        false,
        false,
        false,
        false,
        false,
        false,
        false,
        false,
        false,
        false,
        false,
        false
    )
    private lateinit var carYearList : Array<String?>
    private lateinit var carBodyList : Array<String?>
    private lateinit var carConditionList : Array<String?>
    private lateinit var carTransmissionList : Array<String?>
    private lateinit var carFuelList : Array<String?>
    private lateinit var carInteriorList : Array<String?>
    private lateinit var carColorList : Array<String?>
    private lateinit var carLocationList : Array<String?>
    private lateinit var carDutyList : Array<String?>
    private lateinit var commonFeaturesChecked : Array<String?>
    private lateinit var extraFeaturesChecked : Array<String?>

    private val imageResizer : ImageResizer by instance()
    private lateinit var imagesAdapter : ImagesAdapter
    private var selectedImage: Uri? = null
    private val imagesList = ArrayList<Uri>()
    private val uploadImageBodyPart = ArrayList<MultipartBody.Part>()

    private var uploadImageMoreList = java.util.ArrayList<Uri>()
    private var imagePickerMoreList = java.util.ArrayList<Image>()

    private lateinit var carObject: CarObject
    private lateinit var carObjectUpdate: CarObject
    private val session : Session by instance()
    private lateinit var userID : String
    private lateinit var carID : String
    private lateinit var  imageToDelete : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_car, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        carObject = (arguments?.getParcelable("carObject") as CarObject?)!!
        requireActivity().toolBar.title = "Edit ${carObject.name}"
        carMakeList = resources.getStringArray(R.array.cars_array)
        carYearList = resources.getStringArray(R.array.cars_year_array)
        carBodyList = resources.getStringArray(R.array.cars_body_types_array)
        carConditionList = resources.getStringArray(R.array.cars_condition_array)
        carTransmissionList = resources.getStringArray(R.array.cars_transmission_array)
        carFuelList = resources.getStringArray(R.array.cars_fuel_array)
        carInteriorList = resources.getStringArray(R.array.cars_interior_array)
        carColorList = resources.getStringArray(R.array.cars_color_array)
        carLocationList = resources.getStringArray(R.array.cars_location_array)
        carDutyList = resources.getStringArray(R.array.cars_duty_array)
        commonFeaturesChecked = resources.getStringArray(R.array.car_common_features_array)
        extraFeaturesChecked = resources.getStringArray(R.array.car_extra_features_array)
        viewModel = ViewModelProvider(this.requireActivity(), factory).get(UserViewModel::class.java)

        for (image in carObject.images!!){
            imagesList.add(Uri.parse(image))
        }

        imagesAdapter = ImagesAdapter(imagesList, requireContext(), this)

        carName = carObject.name.toString()
        carID = carObject.id!!
        val id = requireActivity().resources.getIdentifier(carObject.make, "array", requireActivity().packageName)
        carModelList = resources.getStringArray(id)
        checkedCarMake = carMakeList.indexOf(carObject.make)
        checkedCarModel = carModelList.indexOf(carObject.model)
        checkedCarYear =  carYearList.indexOf(carObject.year?.toString())
        checkedCarBody = carBodyList.indexOf(carObject.body)
        checkedCarCondition = carConditionList.indexOf(carObject.condition)
        checkedCarTransmission = carTransmissionList.indexOf(carObject.transmission)
        checkedCarDuty = carDutyList.indexOf(carObject.duty)
        checkedCarFuel = carFuelList.indexOf(carObject.fuel)
        checkedCarInterior = carInteriorList.indexOf(carObject.interior)
        checkedCarColor = carColorList.indexOf(carObject.color)
        checkedCarLocation = carLocationList.indexOf(carObject.location)

        binding.carFeature.editTextCarMileage.addTextChangedListener(
            NumberTextWatcherForThousand(
                binding.carFeature.editTextCarMileage
            )
        )
        binding.carFeature.editTextCarPrice.addTextChangedListener(
            NumberTextWatcherForThousand(
                binding.carFeature.editTextCarPrice
            )
        )
        binding.carFeature.textViewCarEngineSize.addTextChangedListener(
            NumberTextWatcherForThousand(
                binding.carFeature.textViewCarEngineSize
            )
        )

        binding.carMakeButton.text = carObject.make
        carMakeSelected = carObject.make.toString()

        binding.carModelButton.text = carObject.model
        carModelSelected = carObject.model.toString()

        binding.carYearButton.text = carObject.year.toString()
        carYearSelected = carObject.year.toString()

        binding.carBodyButton.text = carObject.body
        carBodyTypeSelected = carObject.body.toString()

        binding.carConditionButton.text = carObject.condition
        carConditionSelected = carObject.condition.toString()

        binding.carTransmissionButton.text = carObject.transmission
        carTransMissionSelected = carObject.transmission.toString()

        binding.carFuelButton.text = carObject.fuel
        carSelectedFuel = carObject.fuel.toString()

        binding.carInteriorButton.text = carObject.interior
        carSelectedInterior = carObject.interior.toString()

        binding.carColorButton.text = carObject.color
        carSelectedColor = carObject.color.toString()

        binding.carLocationButton.text = carObject.location
        carSelectedLocation = carObject.location.toString()

        binding.carDutyButton.text = carObject.duty
        carDutySelected = carObject.duty.toString()

        binding.carFeature.textViewCarEngineSize.text = Editable.Factory.getInstance().newEditable(
            carObject.engineSize.toString()
        )

        binding.carFeature.editTextCarMileage.text = Editable.Factory.getInstance().newEditable(
            carObject.mileage.toString()
        )

        binding.carFeature.textViewCarDescription.text = Editable.Factory.getInstance().newEditable(
            carObject.description
        )

        binding.carFeature.editTextCarPrice.text = Editable.Factory.getInstance().newEditable(
            carObject.price.toString()
        )
        carPriceNegotiable = carObject.priceNegotiable!!
        if (carObject.features?.isNotEmpty()!!){
            for (feature in carObject.features!!){
                commonFeaturesList.add(feature)
            }
            for ((index, value) in  carObject.features!!.withIndex()) {
                if (commonFeaturesChecked.contains(value)){
                    val featureIndex = commonFeaturesChecked.indexOf(value)
                    commonFeaturesCheckedStates[featureIndex] = true
                }
                if (extraFeaturesChecked.contains(value)){
                    val featureIndex = extraFeaturesChecked.indexOf(value)
                    extraFeaturesCheckedStates[featureIndex] = true
                }
            }
        }



        //binding.carModelButton.isEnabled = false
        binding.carMakeButton.setOnClickListener{
            selectCarMake()
        }
        binding.carModelButton.setOnClickListener {
            selectCarModel()
        }
        binding.carYearButton.setOnClickListener {
            selectCarYear()
        }
        binding.carBodyButton.setOnClickListener {
            selectCarBody()
        }
        binding.carConditionButton.setOnClickListener {
            selectCarCondition()
        }
        binding.carTransmissionButton.setOnClickListener {
            selectCarTransmission()
        }
        binding.carDutyButton.setOnClickListener {
            selectCarDuty()
        }

        binding.carFuelButton.setOnClickListener {
            selectCarFuel()
        }
        binding.carInteriorButton.setOnClickListener {
            selectCarInterior()
        }
        binding.carColorButton.setOnClickListener {
            selectCarColor()
        }
        binding.carLocationButton.setOnClickListener {
            selectCarLocation()
        }
        binding.carFeature.commonFeaturesButton.setOnClickListener {
            commonFeaturesDialog()
        }
        binding.carFeature.extraFeaturesButton.setOnClickListener {
            extraFeaturesDialog()
        }

        binding.carImages.buttonPost.setOnClickListener {
            postCarDetails()
        }

        if (imagesList.size < 12){
            binding.carImages.selectMoreImagesButton.visibility = View.VISIBLE
        }

        binding.carImages.selectMoreImagesButton.setOnClickListener {
            uploadImageMoreList.clear()
            ImagePicker.with(this)
                .setFolderMode(true)
                .setFolderTitle("Motii! Cars Listing")
                .setDirectoryName("Motii! Image Picker")
                .setMultipleMode(true)
                .setShowNumberIndicator(true)
                .setMaxSize(12-imagesList.size)
                .setLimitMessage("You can pick a maximum of "+(12-imagesList.size).toString())
                .setRequestCode(MULTIPLE_IMAGE_REQUEST_CODE)
                .start()
        }

        binding.carImages.recyclerviewImages.also {
            it.layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )
            it.setHasFixedSize(true)
            it.adapter = imagesAdapter
        }
    }
    companion object{
        private const val MULTIPLE_IMAGE_REQUEST_CODE = 160
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK ){
            when (requestCode){
                MULTIPLE_IMAGE_REQUEST_CODE -> {
                    imagePickerMoreList = ImagePicker.getImages(data)
                    for (imagePicked in imagePickerMoreList){
                        uploadImageMoreList.add(imagePicked.uri)
                    }
                    imagePickerMoreList.clear()
                    binding.carImages.recyclerviewMoreImages.also {
                        it.layoutManager = LinearLayoutManager(
                            requireContext(),
                            LinearLayoutManager.HORIZONTAL,
                            false
                        )
                        it.setHasFixedSize(true)
                        it.adapter = MoreImagesAdapter(uploadImageMoreList, requireContext())
                    }
                }
            }
        }
    }

    private fun selectCarMake(){
        val carMakeListDialog = AlertDialog.Builder(this.requireContext())
        val title = SpannableString("SELECT CAR MAKE")
        title.setSpan(ForegroundColorSpan(Color.parseColor("#009688")), 0, title.length, 0)
        carMakeListDialog.setTitle(title)
        carMakeListDialog.setSingleChoiceItems(carMakeList, checkedCarMake) { _, which ->
            when(which){

                0 -> {
                    carModelList = resources.getStringArray(R.array.Toyota)
                    checkedCarMake = which
                    checkedCarModel = -1
                    binding.carMakeButton.text = carMakeList[which]
                    binding.carModelButton.isEnabled = true
                }
                1 -> {
                    carModelList = resources.getStringArray(R.array.Nissan)
                    checkedCarMake = which
                    checkedCarModel = -1
                    binding.carMakeButton.text = carMakeList[which]
                    binding.carModelButton.isEnabled = true
                }
                2 -> {
                    carModelList = resources.getStringArray(R.array.Subaru)
                    checkedCarMake = which
                    checkedCarModel = -1
                    binding.carMakeButton.text = carMakeList[which]
                    binding.carModelButton.isEnabled = true
                }
                3 -> {
                    carModelList = resources.getStringArray(R.array.Honda)
                    checkedCarMake = which
                    checkedCarModel = -1
                    binding.carMakeButton.text = carMakeList[which]
                    binding.carModelButton.isEnabled = true
                }
                4 -> {
                    carModelList = resources.getStringArray(R.array.Mitsubishi)
                    checkedCarMake = which
                    checkedCarModel = -1
                    binding.carMakeButton.text = carMakeList[which]
                    binding.carModelButton.isEnabled = true
                }
                5 -> {
                    carModelList = resources.getStringArray(R.array.Mercedes)
                    checkedCarMake = which
                    checkedCarModel = -1
                    binding.carMakeButton.text = carMakeList[which]
                    binding.carModelButton.isEnabled = true
                }
                6 -> {
                    carModelList = resources.getStringArray(R.array.Mazda)
                    checkedCarMake = which
                    checkedCarModel = -1
                    binding.carMakeButton.text = carMakeList[which]
                    binding.carModelButton.isEnabled = true
                }
                7 -> {
                    carModelList = resources.getStringArray(R.array.Volkswagen)
                    checkedCarMake = which
                    checkedCarModel = -1
                    binding.carMakeButton.text = carMakeList[which]
                    binding.carModelButton.isEnabled = true
                }
                8 -> {
                    carModelList = resources.getStringArray(R.array.BMW)
                    checkedCarMake = which
                    checkedCarModel = -1
                    binding.carMakeButton.text = carMakeList[which]
                    binding.carModelButton.isEnabled = true
                }
                9 -> {
                    carModelList = resources.getStringArray(R.array.LandRover)
                    checkedCarMake = which
                    checkedCarModel = -1
                    binding.carMakeButton.text = carMakeList[which]
                    binding.carModelButton.isEnabled = true
                }
                10 -> {
                    carModelList = resources.getStringArray(R.array.Isuzu)
                    checkedCarMake = which
                    checkedCarModel = -1
                    binding.carMakeButton.text = carMakeList[which]
                    binding.carModelButton.isEnabled = true
                }
                11 -> {
                    carModelList = resources.getStringArray(R.array.Audi)
                    checkedCarMake = which
                    checkedCarModel = -1
                    binding.carMakeButton.text = carMakeList[which]
                    binding.carModelButton.isEnabled = true
                }
                12 -> {
                    carModelList = resources.getStringArray(R.array.Suzuki)
                    checkedCarMake = which
                    checkedCarModel = -1
                    binding.carMakeButton.text = carMakeList[which]
                    binding.carModelButton.isEnabled = true
                }
                13 -> {
                    carModelList = resources.getStringArray(R.array.Lexus)
                    checkedCarMake = which
                    checkedCarModel = -1
                    binding.carMakeButton.text = carMakeList[which]
                    binding.carModelButton.isEnabled = true
                }
                14 -> {
                    carModelList = resources.getStringArray(R.array.Ford)
                    checkedCarMake = which
                    checkedCarModel = -1
                    binding.carMakeButton.text = carMakeList[which]
                    binding.carModelButton.isEnabled = true
                }
                15 -> {
                    carModelList = resources.getStringArray(R.array.AlfaRomeo)
                    checkedCarMake = which
                    checkedCarModel = -1
                    binding.carMakeButton.text = carMakeList[which]
                    binding.carModelButton.isEnabled = true
                }
                16 -> {
                    carModelList = resources.getStringArray(R.array.Audi)
                    checkedCarMake = which
                    checkedCarModel = -1
                    binding.carMakeButton.text = carMakeList[which]
                    binding.carModelButton.isEnabled = true
                }
                17 -> {
                    carModelList = resources.getStringArray(R.array.Bajaj)
                    checkedCarMake = which
                    checkedCarModel = -1
                    binding.carMakeButton.text = carMakeList[which]
                    binding.carModelButton.isEnabled = true
                }
                18 -> {
                    carModelList = resources.getStringArray(R.array.BMW)
                    checkedCarMake = which
                    checkedCarModel = -1
                    binding.carMakeButton.text = carMakeList[which]
                    binding.carModelButton.isEnabled = true
                }
                19 -> {
                    carModelList = resources.getStringArray(R.array.Cadillac)
                    checkedCarMake = which
                    checkedCarModel = -1
                    binding.carMakeButton.text = carMakeList[which]
                    binding.carModelButton.isEnabled = true
                }
                20 -> {
                    carModelList = resources.getStringArray(R.array.Caterpillar)
                    checkedCarMake = which
                    checkedCarModel = -1
                    binding.carMakeButton.text = carMakeList[which]
                    binding.carModelButton.isEnabled = true
                }
                21 -> {
                    carModelList = resources.getStringArray(R.array.Cherry)
                    checkedCarMake = which
                    checkedCarModel = -1
                    binding.carMakeButton.text = carMakeList[which]
                    binding.carModelButton.isEnabled = true
                }
                22 -> {
                    carModelList = resources.getStringArray(R.array.DAF)
                    checkedCarMake = which
                    checkedCarModel = -1
                    binding.carMakeButton.text = carMakeList[which]
                    binding.carModelButton.isEnabled = true
                }
                23 -> {
                    carModelList = resources.getStringArray(R.array.Daihatsu)
                    checkedCarMake = which
                    checkedCarModel = -1
                    binding.carMakeButton.text = carMakeList[which]
                    binding.carModelButton.isEnabled = true
                }
                24 -> {
                    carModelList = resources.getStringArray(R.array.FAW)
                    checkedCarMake = which
                    checkedCarModel = -1
                    binding.carMakeButton.text = carMakeList[which]
                    binding.carModelButton.isEnabled = true
                }
                25 -> {
                    carModelList = resources.getStringArray(R.array.Ford
                    )
                    checkedCarMake = which
                    checkedCarModel = -1
                    binding.carMakeButton.text = carMakeList[which]
                    binding.carModelButton.isEnabled = true
                }
                26 -> {
                    carModelList = resources.getStringArray(R.array.Foton)
                    checkedCarMake = which
                    checkedCarModel = -1
                    binding.carMakeButton.text = carMakeList[which]
                    binding.carModelButton.isEnabled = true
                }
                27 -> {
                    carModelList = resources.getStringArray(R.array.Hino)
                    checkedCarMake = which
                    checkedCarModel = -1
                    binding.carMakeButton.text = carMakeList[which]
                    binding.carModelButton.isEnabled = true
                }
                28 -> {
                    carModelList = resources.getStringArray(R.array.Honda)
                    checkedCarMake = which
                    checkedCarModel = -1
                    binding.carMakeButton.text = carMakeList[which]
                    binding.carModelButton.isEnabled = true
                }
                29 -> {
                    carModelList = resources.getStringArray(R.array.Hyundai)
                    checkedCarMake = which
                    checkedCarModel = -1
                    binding.carMakeButton.text = carMakeList[which]
                    binding.carModelButton.isEnabled = true
                }
                30 -> {
                    carModelList = resources.getStringArray(R.array.Infiniti)
                    checkedCarMake = which
                    checkedCarModel = -1
                    binding.carMakeButton.text = carMakeList[which]
                    binding.carModelButton.isEnabled = true
                }
                31 -> {
                    carModelList = resources.getStringArray(R.array.Isuzu)
                    checkedCarMake = which
                    checkedCarModel = -1
                    binding.carMakeButton.text = carMakeList[which]
                    binding.carModelButton.isEnabled = true
                }
                32 -> {
                    carModelList = resources.getStringArray(R.array.Jaguar)
                    checkedCarMake = which
                    checkedCarModel = -1
                    binding.carMakeButton.text = carMakeList[which]
                    binding.carModelButton.isEnabled = true
                }
                33 -> {
                    carModelList = resources.getStringArray(R.array.Jeep)
                    checkedCarMake = which
                    checkedCarModel = -1
                    binding.carMakeButton.text = carMakeList[which]
                    binding.carModelButton.isEnabled = true
                }
                34 -> {
                    carModelList = resources.getStringArray(R.array.Kia)
                    checkedCarMake = which
                    checkedCarModel = -1
                    binding.carMakeButton.text = carMakeList[which]
                    binding.carModelButton.isEnabled = true
                }
                35 -> {
                    carModelList = resources.getStringArray(R.array.Lamborghini)
                    checkedCarMake = which
                    checkedCarModel = -1
                    binding.carMakeButton.text = carMakeList[which]
                    binding.carModelButton.isEnabled = true
                }
                36 -> {
                    carModelList = resources.getStringArray(R.array.LandRover)

                }
                37 -> {
                    carModelList = resources.getStringArray(R.array.Lexus)
                    checkedCarMake = which
                    checkedCarModel = -1
                    binding.carMakeButton.text = carMakeList[which]
                    binding.carModelButton.isEnabled = true
                }
                38 -> {
                    carModelList = resources.getStringArray(R.array.LeyLand)
                    checkedCarMake = which
                    checkedCarModel = -1
                    binding.carMakeButton.text = carMakeList[which]
                    binding.carModelButton.isEnabled = true
                }
                39 -> {
                    carModelList = resources.getStringArray(R.array.Mahindra)
                    checkedCarMake = which
                    checkedCarModel = -1
                    binding.carMakeButton.text = carMakeList[which]
                    binding.carModelButton.isEnabled = true
                }
                40 -> {
                    carModelList = resources.getStringArray(R.array.Man)
                    checkedCarMake = which
                    checkedCarModel = -1
                    binding.carMakeButton.text = carMakeList[which]
                    binding.carModelButton.isEnabled = true
                }
                41 -> {
                    carModelList = resources.getStringArray(R.array.Massey)
                    checkedCarMake = which
                    checkedCarModel = -1
                    binding.carMakeButton.text = carMakeList[which]
                    binding.carModelButton.isEnabled = true
                }
                42 -> {
                    carModelList = resources.getStringArray(R.array.Mazda)
                    checkedCarMake = which
                    checkedCarModel = -1
                    binding.carMakeButton.text = carMakeList[which]
                    binding.carModelButton.isEnabled = true
                }
                43 -> {
                    carModelList = resources.getStringArray(R.array.Mercedes)
                    checkedCarMake = which
                    checkedCarModel = -1
                    binding.carMakeButton.text = carMakeList[which]
                    binding.carModelButton.isEnabled = true
                }
                44 -> {
                    carModelList = resources.getStringArray(R.array.Nissan)
                    checkedCarMake = which
                    checkedCarModel = -1
                    binding.carMakeButton.text = carMakeList[which]
                    binding.carModelButton.isEnabled = true
                }
                45 -> {
                    carModelList = resources.getStringArray(R.array.Opel)
                    checkedCarMake = which
                    checkedCarModel = -1
                    binding.carMakeButton.text = carMakeList[which]
                    binding.carModelButton.isEnabled = true
                }
                46 -> {
                    carModelList = resources.getStringArray(R.array.Perodua)
                    checkedCarMake = which
                    checkedCarModel = -1
                    binding.carMakeButton.text = carMakeList[which]
                    binding.carModelButton.isEnabled = true
                }
                47 -> {
                    carModelList = resources.getStringArray(R.array.Peugeot)
                    checkedCarMake = which
                    checkedCarModel = -1
                    binding.carMakeButton.text = carMakeList[which]
                    binding.carModelButton.isEnabled = true
                }
                48 -> {
                    carModelList = resources.getStringArray(R.array.Porsche)
                    checkedCarMake = which
                    checkedCarModel = -1
                    binding.carMakeButton.text = carMakeList[which]
                    binding.carModelButton.isEnabled = true
                }
                49 -> {
                    carModelList = resources.getStringArray(R.array.Renault)
                    checkedCarMake = which
                    checkedCarModel = -1
                    binding.carMakeButton.text = carMakeList[which]
                    binding.carModelButton.isEnabled = true
                }
                50 -> {
                    carModelList = resources.getStringArray(R.array.Rover)
                    checkedCarMake = which
                    checkedCarModel = -1
                    binding.carMakeButton.text = carMakeList[which]
                    binding.carModelButton.isEnabled = true
                }
                51 -> {
                    carModelList = resources.getStringArray(R.array.Royal)
                    checkedCarMake = which
                    checkedCarModel = -1
                    binding.carMakeButton.text = carMakeList[which]
                    binding.carModelButton.isEnabled = true
                }
                52 -> {
                    carModelList = resources.getStringArray(R.array.Scania)
                    checkedCarMake = which
                    checkedCarModel = -1
                    binding.carMakeButton.text = carMakeList[which]
                    binding.carModelButton.isEnabled = true
                }
                53 -> {
                    carModelList = resources.getStringArray(R.array.Shineray)
                    checkedCarMake = which
                    checkedCarModel = -1
                    binding.carMakeButton.text = carMakeList[which]
                    binding.carModelButton.isEnabled = true
                }
                54 -> {
                    carModelList = resources.getStringArray(R.array.Sonalika)
                    checkedCarMake = which
                    checkedCarModel = -1
                    binding.carMakeButton.text = carMakeList[which]
                    binding.carModelButton.isEnabled = true
                }
                55 -> {
                    carModelList = resources.getStringArray(R.array.Subaru)
                    checkedCarMake = which
                    checkedCarModel = -1
                    binding.carMakeButton.text = carMakeList[which]
                    binding.carModelButton.isEnabled = true
                }
                56 -> {
                    carModelList = resources.getStringArray(R.array.Suzuki)
                    checkedCarMake = which
                    checkedCarModel = -1
                    binding.carMakeButton.text = carMakeList[which]
                    binding.carModelButton.isEnabled = true
                }
                57 -> {
                    carModelList = resources.getStringArray(R.array.Tata)
                    checkedCarMake = which
                    checkedCarModel = -1
                    binding.carMakeButton.text = carMakeList[which]
                    binding.carModelButton.isEnabled = true
                }
                58 -> {
                    carModelList = resources.getStringArray(R.array.Toyota)
                    checkedCarMake = which
                    checkedCarModel = -1
                    binding.carMakeButton.text = carMakeList[which]
                    binding.carModelButton.isEnabled = true
                }
                59 -> {
                    carModelList = resources.getStringArray(R.array.Trailer)
                    checkedCarMake = which
                    checkedCarModel = -1
                    binding.carMakeButton.text = carMakeList[which]
                    binding.carModelButton.isEnabled = true
                }
                60 -> {
                    carModelList = resources.getStringArray(R.array.TVS)
                    checkedCarMake = which
                    checkedCarModel = -1
                    binding.carMakeButton.text = carMakeList[which]
                    binding.carModelButton.isEnabled = true
                }
                61 -> {
                    carModelList = resources.getStringArray(R.array.Vauxhaull)
                    checkedCarMake = which
                    checkedCarModel = -1
                    binding.carMakeButton.text = carMakeList[which]
                    binding.carModelButton.isEnabled = true
                }
                62 -> {
                    carModelList = resources.getStringArray(R.array.Vector)
                    checkedCarMake = which
                    checkedCarModel = -1
                    binding.carMakeButton.text = carMakeList[which]
                    binding.carModelButton.isEnabled = true
                }
                63 -> {
                    carModelList = resources.getStringArray(R.array.Volkswagen)
                    checkedCarMake = which
                    checkedCarModel = -1
                    binding.carMakeButton.text = carMakeList[which]
                    binding.carModelButton.isEnabled = true
                }
                64 -> {
                    carModelList = resources.getStringArray(R.array.Volvo)
                    checkedCarMake = which
                    checkedCarModel = -1
                    binding.carMakeButton.text = carMakeList[which]
                    binding.carModelButton.isEnabled = true
                }
                65 -> {
                    carModelList = resources.getStringArray(R.array.Yamaha)
                    checkedCarMake = which
                    checkedCarModel = -1
                    binding.carMakeButton.text = carMakeList[which]
                    binding.carModelButton.isEnabled = true
                }
                66 -> {
                    carModelList = resources.getStringArray(R.array.Zongshen)
                    checkedCarMake = which
                    checkedCarModel = -1
                    binding.carMakeButton.text = carMakeList[which]
                    binding.carModelButton.isEnabled = true
                }
                67 -> {
                    carModelList = resources.getStringArray(R.array.Zontes)
                    checkedCarMake = which
                    checkedCarModel = -1
                    binding.carMakeButton.text = carMakeList[which]
                    binding.carModelButton.isEnabled = true
                }
            }
        }

        carMakeListDialog.setPositiveButton("Ok") { _, _ ->
        }
        carMakeListDialog.setNegativeButton("Cancel", null)
        val dialog = carMakeListDialog.create()
        dialog.show()

    }
    private fun selectCarModel() {
        val carModelListDialog = AlertDialog.Builder(this.requireContext())
        val title = SpannableString((carMakeList[checkedCarMake]?.toUpperCase(Locale.ROOT)) + " MODELS")
        carMakeSelected = carMakeList[checkedCarMake].toString()
        title.setSpan(ForegroundColorSpan(Color.parseColor("#009688")), 0, title.length, 0)
        carModelListDialog.setTitle(title)
        carModelListDialog.setSingleChoiceItems(carModelList, checkedCarModel) { _, which ->
            checkedCarModel = which
            binding.carModelButton.text = carModelList[which]
            carModelSelected = carModelList[which].toString()
        }
        carModelListDialog.setPositiveButton("Ok") { _, _ ->
        }
        carModelListDialog.setNegativeButton("Cancel", null)
        val dialog = carModelListDialog.create()
        dialog.show()
    }
    private fun selectCarYear() {
        val carYearDialog = AlertDialog.Builder(this.requireContext())
        val title = SpannableString("SELECT CAR YEAR")
        title.setSpan(ForegroundColorSpan(Color.parseColor("#009688")), 0, title.length, 0)
        carYearDialog.setTitle(title)
        carYearDialog.setSingleChoiceItems(carYearList, checkedCarYear) { _, which ->
            checkedCarYear = which
            binding.carYearButton.text = carYearList[which]
            carYearSelected = carYearList[which].toString()
        }
        carYearDialog.setPositiveButton("Ok") { _, _ ->
        }
        carYearDialog.setNegativeButton("Cancel", null)
        val dialog = carYearDialog.create()
        dialog.show()
    }
    private fun selectCarBody() {
        val carBodyDialog = AlertDialog.Builder(this.requireContext())

        val title = SpannableString("SELECT CAR BODY")
        title.setSpan(ForegroundColorSpan(Color.parseColor("#009688")), 0, title.length, 0)
        carBodyDialog.setTitle(title)
        carBodyDialog.setSingleChoiceItems(carBodyList, checkedCarBody) { _, which ->
            checkedCarBody = which
            binding.carBodyButton.text = carBodyList[which]
            carBodyTypeSelected = carBodyList[which].toString()
        }
        carBodyDialog.setPositiveButton("Ok") { _, _ ->
        }
        carBodyDialog.setNegativeButton("Cancel", null)
        val dialog = carBodyDialog.create()
        dialog.show()
    }
    private fun selectCarCondition() {
        val carConditionDialog = AlertDialog.Builder(this.requireContext())
        val title = SpannableString("SELECT CAR CONDITION")
        title.setSpan(ForegroundColorSpan(Color.parseColor("#009688")), 0, title.length, 0)
        carConditionDialog.setTitle(title)
        carConditionDialog.setSingleChoiceItems(carConditionList, checkedCarCondition) { _, which ->
            checkedCarCondition = which
            binding.carConditionButton.text = carConditionList[which]
            carConditionSelected = carConditionList[which].toString()
        }
        carConditionDialog.setPositiveButton("Ok") { _, _ ->
        }
        carConditionDialog.setNegativeButton("Cancel", null)
        val dialog = carConditionDialog.create()
        dialog.show()
    }
    private fun selectCarTransmission() {
        val carTransmissionDialog = AlertDialog.Builder(this.requireContext())
        val title = SpannableString("SELECT CAR TRANSMISSION")
        title.setSpan(ForegroundColorSpan(Color.parseColor("#009688")), 0, title.length, 0)
        carTransmissionDialog.setTitle(title)
        carTransmissionDialog.setSingleChoiceItems(carTransmissionList, checkedCarTransmission) { _, which ->
            checkedCarTransmission = which
            binding.carTransmissionButton.text = carTransmissionList[which]
            carTransMissionSelected = carTransmissionList[which].toString()
        }
        carTransmissionDialog.setPositiveButton("Ok") { _, _ ->
        }
        carTransmissionDialog.setNegativeButton("Cancel", null)
        val dialog = carTransmissionDialog.create()
        dialog.show()
    }
    private fun selectCarDuty() {
        val carDutyDialog = AlertDialog.Builder(this.requireContext())
        val title = SpannableString("SELECT CAR DUTY")
        title.setSpan(ForegroundColorSpan(Color.parseColor("#009688")), 0, title.length, 0)
        carDutyDialog.setTitle(title)
        carDutyDialog.setSingleChoiceItems(carDutyList, checkedCarDuty) { _, which ->
            checkedCarDuty = which
            binding.carDutyButton.text = carDutyList[which]
            carDutySelected = carDutyList[which].toString()
        }
        carDutyDialog.setPositiveButton("Ok") { _, _ ->
        }
        carDutyDialog.setNegativeButton("Cancel", null)
        val dialog = carDutyDialog.create()
        dialog.show()
    }
    private fun selectCarFuel() {
        val carFuelDialog = AlertDialog.Builder(this.requireContext())

        val title = SpannableString("SELECT CAR FUEL")
        title.setSpan(ForegroundColorSpan(Color.parseColor("#009688")), 0, title.length, 0)
        carFuelDialog.setTitle(title)
        carFuelDialog.setSingleChoiceItems(carFuelList, checkedCarFuel) { _, which ->
            checkedCarFuel = which
            binding.carFuelButton.text = carFuelList[which]
            carSelectedFuel = carFuelList[which].toString()
        }
        carFuelDialog.setPositiveButton("Ok") { _, _ ->
        }
        carFuelDialog.setNegativeButton("Cancel", null)
        val dialog = carFuelDialog.create()
        dialog.show()
    }
    private fun selectCarInterior() {
        val carInteriorDialog = AlertDialog.Builder(this.requireContext())

        val title = SpannableString("SELECT CAR INTERIOR")
        title.setSpan(ForegroundColorSpan(Color.parseColor("#009688")), 0, title.length, 0)
        carInteriorDialog.setTitle(title)
        carInteriorDialog.setSingleChoiceItems(carInteriorList, checkedCarInterior) { _, which ->
            checkedCarInterior = which
            binding.carInteriorButton.text = carInteriorList[which]
            carSelectedInterior = carInteriorList[which].toString()
        }
        carInteriorDialog.setPositiveButton("Ok") { _, _ ->
        }
        carInteriorDialog.setNegativeButton("Cancel", null)
        val dialog = carInteriorDialog.create()
        dialog.show()
    }
    private fun selectCarColor() {
        val carColorDialog = AlertDialog.Builder(this.requireContext())
        val title = SpannableString("SELECT CAR COLOR")
        title.setSpan(ForegroundColorSpan(Color.parseColor("#009688")), 0, title.length, 0)
        carColorDialog.setTitle(title)
        carColorDialog.setSingleChoiceItems(carColorList, checkedCarColor) { _, which ->
            checkedCarColor = which
            binding.carColorButton.text = carColorList[which]
            carSelectedColor = carColorList[which].toString()
        }
        carColorDialog.setPositiveButton("Ok") { _, _ ->
        }
        carColorDialog.setNegativeButton("Cancel", null)
        val dialog = carColorDialog.create()
        dialog.show()
    }
    private fun selectCarLocation() {
        val carLocationDialog = AlertDialog.Builder(this.requireContext())
        val title = SpannableString("SELECT CAR LOCATION")
        title.setSpan(ForegroundColorSpan(Color.parseColor("#009688")), 0, title.length, 0)
        carLocationDialog.setTitle(title)
        carLocationDialog.setSingleChoiceItems(carLocationList, checkedCarLocation) { _, which ->
            checkedCarLocation = which
            binding.carLocationButton.text = carLocationList[which]
            carSelectedLocation = carLocationList[which].toString()
        }
        carLocationDialog.setPositiveButton("Ok") { _, _ ->
        }
        carLocationDialog.setNegativeButton("Cancel", null)
        val dialog = carLocationDialog.create()
        dialog.show()
    }
    private fun postCarDetails(){
        carMileage = binding.carFeature.editTextCarMileage.text.toString().trim()
        carPrice = binding.carFeature.editTextCarPrice.text.toString().trim()
        carPriceNegotiable = binding.carFeature.priceNegotiableCheckBox.isChecked
        binding.carFeature.mileageTextInputLayout.error = null
        binding.carFeature.priceTextInputLayout.error = null
        carEngineSize = binding.carFeature.textViewCarEngineSize.text.toString().trim()
        carDescription = binding.carFeature.textViewCarDescription.text.toString().trim()
        binding.carFeature.carEngineSizeTextInputLayout.error = null
        binding.carFeature.carDescriptionTextInputLayout.error = null
        if (carEngineSize.isEmpty()) {
            binding.carFeature.carEngineSizeTextInputLayout.error = "Engine Size Required"
            return
        }
        if (carMileage.isEmpty()){
            binding.carFeature.mileageTextInputLayout.error = "Mileage Required"
            return
        }
        if (carDescription.length <= 10){
            binding.carFeature.carDescriptionTextInputLayout.error = "Description too Short"
            return
        }
        if (carPrice.isEmpty()){
            binding.carFeature.priceTextInputLayout.error = "Price Required"
            return
        }
        if (commonFeaturesList.isEmpty()){
            binding.root.snackBar("Please Select Your Car Features")
            return
        }


        carName  = carMakeSelected.plus(" ").plus(carModelSelected).plus(" ").plus(
            carYearSelected
        )

        carObjectUpdate = CarObject(
            carName,carMakeSelected,carModelSelected,carYearSelected.toFloat(),carBodyTypeSelected,carConditionSelected,
            carTransMissionSelected,carDutySelected,trimCommaOfString(carMileage).toFloat(),trimCommaOfString(carPrice).toFloat(),carPriceNegotiable,carSelectedFuel,carSelectedInterior,
            carSelectedColor,trimCommaOfString(carEngineSize).toFloat(),carDescription,commonFeaturesList,carSelectedLocation)

            userID = session.getSession().toString()
            val customAlertDialog = CustomAlertDialog(requireActivity())
            customAlertDialog.startLoadingDialog("Uploading Car")

                viewModel.updateUserCar(carObjectUpdate, carID, requireContext())
                viewModel.updateUserCarResponse.observe(viewLifecycleOwner, Observer {
                    //Toast.makeText(requireContext(),"${carObjectResponse.carObject}",Toast.LENGTH_LONG).show()
                    if (uploadImageMoreList.isNotEmpty()){
                        for (imageFile in uploadImageMoreList) {
                            val imageStream =
                                requireContext().contentResolver.openInputStream(imageFile)
                            val fullSizeBitmap = BitmapFactory.decodeStream(imageStream)
                            val reducedImageBitMap = imageResizer.reduceBitmapSize(
                                fullSizeBitmap,
                                814400
                            )
                            val reducedImageFile = getFileFromBitmap(reducedImageBitMap, imageFile)
                            //val body = UploadImageRequestBody(file,"image")
                            val body = RequestBody.create(MediaType.parse("image/*"), reducedImageFile)
                            val multipartBody = MultipartBody.Part.createFormData(
                                "photos",
                                reducedImageFile.name,
                                body
                            )
                            uploadImageBodyPart.add(multipartBody)
                        }
                        viewModel.postCarImages(
                            uploadImageBodyPart,
                            carID,
                            requireContext()
                        )
                        viewModel.postCarImagesResponse.observe(
                            viewLifecycleOwner,
                            Observer {
                                customAlertDialog.stopDialog()
                                uploadImageMoreList.clear()
                                uploadImageBodyPart.clear()
                                requireActivity().viewModelStore.clear()
                                requireActivity().cacheDir.deleteRecursively()
                                loadHomeActivity()
                            })
                    }else{
                        customAlertDialog.stopDialog()
                        Toast.makeText(
                            requireContext(),
                            "$carName Updated Successfully",
                            Toast.LENGTH_LONG
                        ).show()
                        uploadImageMoreList.clear()
                        uploadImageBodyPart.clear()
                        requireActivity().viewModelStore.clear()
                        requireContext().cacheDir.deleteRecursively()
                        loadHomeActivity()
                    }
                })


    }


    private fun commonFeaturesDialog() {
        val commonFeaturesAlertDialog = AlertDialog.Builder(this.requireContext())
        val title = SpannableString("COMMON FEATURES")
        title.setSpan(ForegroundColorSpan(Color.parseColor("#009688")), 0, title.length, 0)
        commonFeaturesAlertDialog.setTitle(title)
        commonFeaturesAlertDialog.setMultiChoiceItems(
            R.array.car_common_features_array,
            commonFeaturesCheckedStates
        ) { _: DialogInterface, which: Int, isChecked: Boolean ->
            if (isChecked){
                commonFeaturesCheckedStates[which] = isChecked
                commonFeaturesList.add(commonFeaturesChecked[which]!!)
            }
            else if (commonFeaturesList.contains(commonFeaturesChecked[which])){
                commonFeaturesList.remove(commonFeaturesChecked[which])
            }
        }
        commonFeaturesAlertDialog.setPositiveButton("ADD FEATURES") { _, _ ->
            Snackbar.make(
                this.requireView(),
                "$commonFeaturesList Added to Features",
                Snackbar.LENGTH_LONG
            ).show()
        }
        commonFeaturesAlertDialog.setNegativeButton("CANCEL") { dialog, _ ->
            dialog.dismiss()
        }
        commonFeaturesAlertDialog.create()
        commonFeaturesAlertDialog.show()
    }
    private fun extraFeaturesDialog() {
        val extraFeaturesAlertDialog = AlertDialog.Builder(this.requireContext())
        val title = SpannableString("EXTRA FEATURES")
        title.setSpan(ForegroundColorSpan(Color.parseColor("#009688")), 0, title.length, 0)
        extraFeaturesAlertDialog.setTitle(title)
        extraFeaturesAlertDialog.setMultiChoiceItems(
            R.array.car_extra_features_array,
            extraFeaturesCheckedStates
        ) { _: DialogInterface, which: Int, isChecked: Boolean ->
            if (isChecked){
                extraFeaturesCheckedStates[which] = isChecked
                commonFeaturesList.add(extraFeaturesChecked[which]!!)
        }
            else if (commonFeaturesList.contains(extraFeaturesChecked[which])){
                commonFeaturesList.remove(extraFeaturesChecked[which])
            }
    }
        extraFeaturesAlertDialog.setPositiveButton("ADD FEATURES") { _, _ ->
            Snackbar.make(
                this.requireView(),
                "$commonFeaturesList Added to Features",
                Snackbar.LENGTH_LONG
            ).show()
        }
        extraFeaturesAlertDialog.setNegativeButton("CANCEL") { dialog, _ ->
            dialog.dismiss()
        }
        extraFeaturesAlertDialog.create()
        extraFeaturesAlertDialog.show()
    }
    private fun trimCommaOfString(string: String): String {
        return if (string.contains(",")) {
            string.replace(",", "")
        } else {
            string
        }
    }

    private fun getFileFromBitmap(reducedBitmap: Bitmap?, imageFile: Uri) : File {
        val file = File(requireContext().cacheDir, requireContext().contentResolver.getFileName(imageFile))


        val inputFile = requireContext().contentResolver.openInputStream(imageFile);
        val exif = ExifInterface(inputFile!!)
        val rotation = exif.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )

        val rotationInDegrees = imageResizer.exifToDegrees(rotation)


        val matrix = Matrix()
        if (rotation != 0) {
            matrix.preRotate(rotationInDegrees.toFloat())
        }

        val adjustedBitmap = Bitmap.createBitmap(
            reducedBitmap!!,
            0,
            0,
            reducedBitmap.width,
            reducedBitmap.height,
            matrix,
            true
        )
        val bitmapOutputStream = ByteArrayOutputStream()
        adjustedBitmap.compress(Bitmap.CompressFormat.JPEG, 50, bitmapOutputStream)
        val bitmapData = bitmapOutputStream.toByteArray()
        val fileOutputStream = FileOutputStream(file)
        fileOutputStream.write(bitmapData)
        fileOutputStream.flush()
        fileOutputStream.close()
        return file
    }
    private fun loadHomeActivity() {
        findNavController().popBackStack()
    }


    override fun onImageDelete(position: Int) {
        val alertDialog : AlertDialog? = activity?.let {
                val builder = AlertDialog.Builder(it)
                builder.apply {
                    setPositiveButton("Remove",
                        DialogInterface.OnClickListener { dialog, id ->
                            val url = URL(imagesList[position].toString()).toString()
                            val imageDelete = ImageUrl(url)
                            binding.carImages.progressBar.visibility = View.VISIBLE
                            viewModel.deleteCarImage(imageDelete,carID,requireContext())
                            viewModel.deleteCarImageResponse.observe(viewLifecycleOwner, Observer {imageDeleteResponse->
                                binding.root.snackBar(imageDeleteResponse.message)
                            })
                            imagesList.removeAt(position)
                            imagesAdapter.notifyItemRemoved(position)
                            binding.carImages.progressBar.visibility = View.INVISIBLE
                            if (imagesList.size < 12){
                                binding.carImages.selectMoreImagesButton.visibility = View.VISIBLE
                            }
                            dialog.dismiss()
                        })
                    setNegativeButton(R.string.cancel
                    ) { dialog, _ ->
                        dialog.dismiss()
                    }
                }.setTitle("Remove Image")
                builder.setMessage("Are you sure you want to remove this image?")
                builder.create()
            }
        alertDialog?.show()
        }

}

