package org.carlistingapp.autolist.ui.home.postCar.views

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.exifinterface.media.ExifInterface
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePicker
import kotlinx.android.synthetic.main.activity_home.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.carlistingapp.autolist.R
import org.carlistingapp.autolist.data.db.entities.CarObject
import org.carlistingapp.autolist.data.db.entities.UserObject
import org.carlistingapp.autolist.data.network.ListingCarsAPI
import org.carlistingapp.autolist.data.repositories.UserRepository
import org.carlistingapp.autolist.databinding.FragmentPostCarDetailsBinding
import org.carlistingapp.autolist.ui.auth.views.LogInUserFragment
import org.carlistingapp.autolist.ui.home.postCar.adapters.MoreImagesAdapter
import org.carlistingapp.autolist.ui.home.postCar.viewModels.PostCarViewModel
import org.carlistingapp.autolist.ui.home.postCar.viewModels.PostCarViewModelFactory
import org.carlistingapp.autolist.utils.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.*
import kotlin.properties.Delegates

class PostCarDetailsFragment : Fragment(), KodeinAware {
    override val kodein by kodein()

    private val api : ListingCarsAPI by instance()
    private val repository : UserRepository by instance()
    private val factory: PostCarViewModelFactory by instance()
    private lateinit var viewModel: PostCarViewModel
    private lateinit var binding : FragmentPostCarDetailsBinding
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

    private var checkedCarFuel by Delegates.notNull<Int>()
    private var checkedCarInterior by Delegates.notNull<Int>()
    private var checkedCarColor by Delegates.notNull<Int>()
    private var checkedCarLocation by Delegates.notNull<Int>()
    private val commonFeaturesList = HashSet<String>()
    private val commonFeaturesCheckedStates = booleanArrayOf(false, false, false, false, false, false, false, false, false, false, false, false, false)
    private val extraFeaturesCheckedStates = booleanArrayOf(false, false, false, false, false, false, false, false, false, false, false, false, false)

    private val imageResizer : ImageResizer by instance()

    private var selectedImageFront: Uri? = null
    private var selectedImageRight: Uri? = null
    private var selectedImageLeft: Uri? = null
    private var selectedImageBack: Uri? = null
    private var selectedImageDashBoard: Uri? = null
    private var selectedImageInterior: Uri? = null

    private val uploadImageList = ArrayList<Uri>()
    private var uploadImageMoreList = ArrayList<Uri>()
    private var imagePickerMoreList = ArrayList<com.nguyenhoanglam.imagepicker.model.Image>()
    private val uploadImageBodyPart = ArrayList<MultipartBody.Part>()

    private lateinit var carObject: CarObject
    private val session : Session by instance()
    private lateinit var userID : String
    private lateinit var carID : String


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_post_car_details, container, false)
        val view = binding.root
        if (session.getSession() == "userLoggedOut") {
            val logInUserFragment = LogInUserFragment()
            logInUserFragment.setStyle(
                BottomSheetDialogFragment.STYLE_NO_FRAME,
                0
            )
            logInUserFragment.show(requireActivity().supportFragmentManager, "LogInUserFragment")
        }


        requireActivity().toolBar.title = "Sell A Car"
        checkedCarMake = -1
        checkedCarYear = -1
        checkedCarBody = -1
        checkedCarCondition = -1
        checkedCarTransmission = -1
        checkedCarDuty = -1
        checkedCarFuel = -1
        checkedCarInterior = -1
        checkedCarColor = -1
        checkedCarLocation = -1
        binding.carFeature.editTextCarMileage.addTextChangedListener(NumberTextWatcherForThousand(binding.carFeature.editTextCarMileage))
        binding.carFeature.editTextCarPrice.addTextChangedListener(NumberTextWatcherForThousand(binding.carFeature.editTextCarPrice))
        binding.carModelButton.isEnabled = false
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
        binding.carFeature.textViewCarEngineSize.addTextChangedListener(NumberTextWatcherForThousand(binding.carFeature.textViewCarEngineSize))
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

        binding.carImages.frontImagePicker.setOnClickListener {
//            Intent(Intent.ACTION_PICK).also {
//                it.type = "image/*"
//                it.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
//                startActivityForResult(it, FRONT_IMAGE_REQUEST_CODE)
//            }
            ImagePicker.with(this)
                .setFolderMode(true)
                .setFolderTitle("Album")
                .setDirectoryName("Image Picker")
                .setMultipleMode(true)
                .setShowNumberIndicator(true)
                .setMaxSize(1)
                .setLimitMessage("You can select up to 10 images")
                .setRequestCode(FRONT_IMAGE_REQUEST_CODE)
                .start()
        }

        binding.carImages.rightImagePicker.setOnClickListener {
            ImagePicker.with(this)
                .setFolderMode(true)
                .setFolderTitle("Album")
                .setDirectoryName("Image Picker")
                .setMultipleMode(true)
                .setShowNumberIndicator(true)
                .setMaxSize(1)
                .setLimitMessage("You can select up to 10 images")
                .setRequestCode(RIGHT_IMAGE_REQUEST_CODE)
                .start()
        }

        binding.carImages.leftImagePicker.setOnClickListener {
            ImagePicker.with(this)
                .setFolderMode(true)
                .setFolderTitle("Album")
                .setDirectoryName("Image Picker")
                .setMultipleMode(true)
                .setShowNumberIndicator(true)
                .setMaxSize(1)
                .setLimitMessage("You can select up to 10 images")
                .setRequestCode(LEFT_IMAGE_REQUEST_CODE)
                .start()
        }

        binding.carImages.backImagePicker.setOnClickListener {
            ImagePicker.with(this)
                .setFolderMode(true)
                .setFolderTitle("Album")
                .setDirectoryName("Image Picker")
                .setMultipleMode(true)
                .setShowNumberIndicator(true)
                .setMaxSize(1)
                .setLimitMessage("You can select up to 10 images")
                .setRequestCode(BACK_IMAGE_REQUEST_CODE)
                .start()
        }

        binding.carImages.dashBoardImagePicker.setOnClickListener {
            ImagePicker.with(this)
                .setFolderMode(true)
                .setFolderTitle("Album")
                .setDirectoryName("Image Picker")
                .setMultipleMode(true)
                .setShowNumberIndicator(true)
                .setMaxSize(1)
                .setLimitMessage("You can select up to 10 images")
                .setRequestCode(DASHBOARD_IMAGE_REQUEST_CODE)
                .start()
        }

        binding.carImages.interiorImagePicker.setOnClickListener {
            ImagePicker.with(this)
                .setFolderMode(true)
                .setFolderTitle("Album")
                .setDirectoryName("Image Picker")
                .setMultipleMode(true)
                .setShowNumberIndicator(true)
                .setMaxSize(1)
                .setLimitMessage("You can select up to 10 images")
                .setRequestCode(INTERIOR_IMAGE_REQUEST_CODE)
                .start()
        }

        binding.carImages.selectMoreImagesButton.setOnClickListener {
            uploadImageMoreList.clear()
            ImagePicker.with(this)
                .setFolderMode(true)
                .setFolderTitle("Motii! Cars Listing")
                .setDirectoryName("Motii! Image Picker")
                .setMultipleMode(true)
                .setShowNumberIndicator(true)
                .setMaxSize(6)
                .setLimitMessage("You can select up to 6 images")
                .setRequestCode(MULTIPLE_IMAGE_REQUEST_CODE)
                .start()
        }

        binding.carImages.buttonPost.setOnClickListener {
            postCarDetails()
        }
        return view
    }
    companion object{
        private const val FRONT_IMAGE_REQUEST_CODE = 100
        private const val RIGHT_IMAGE_REQUEST_CODE = 110
        private const val LEFT_IMAGE_REQUEST_CODE = 120
        private const val BACK_IMAGE_REQUEST_CODE = 130
        private const val DASHBOARD_IMAGE_REQUEST_CODE = 140
        private const val INTERIOR_IMAGE_REQUEST_CODE = 150
        private const val MULTIPLE_IMAGE_REQUEST_CODE = 160
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK ){
            when (requestCode){
                FRONT_IMAGE_REQUEST_CODE -> {
                    val images = ImagePicker.getImages(data)
                    selectedImageFront = images[0].uri
                    //Toast.makeText(requireContext(), selectedImageFront.toString(), Toast.LENGTH_LONG).show()
                    binding.carImages.frontImage.setImageURI(selectedImageFront)
                    if (uploadImageList.contains(selectedImageFront!!)) {
                        uploadImageList.remove(selectedImageFront!!)
                    }
                    uploadImageList.add(selectedImageFront!!)
                }
                RIGHT_IMAGE_REQUEST_CODE -> {
                    val images = ImagePicker.getImages(data)
                    selectedImageRight = images[0].uri
                    binding.carImages.rightImage.setImageURI(selectedImageRight)
                    if (uploadImageList.contains(selectedImageRight!!)) {
                        uploadImageList.remove(selectedImageRight!!)
                    }
                    uploadImageList.add(selectedImageRight!!)
                }
                LEFT_IMAGE_REQUEST_CODE -> {
                    val images = ImagePicker.getImages(data)
                    selectedImageLeft = images[0].uri
                    binding.carImages.leftImage.setImageURI(selectedImageLeft)
                    if (uploadImageList.contains(selectedImageLeft!!)) {
                        uploadImageList.remove(selectedImageLeft!!)
                    }
                    uploadImageList.add(selectedImageLeft!!)
                }
                BACK_IMAGE_REQUEST_CODE -> {
                    val images = ImagePicker.getImages(data)
                    selectedImageBack = images[0].uri
                    binding.carImages.backImage.setImageURI(selectedImageBack)
                    if (uploadImageList.contains(selectedImageBack!!)) {
                        uploadImageList.remove(selectedImageBack!!)
                    }
                    uploadImageList.add(selectedImageBack!!)

                }
                DASHBOARD_IMAGE_REQUEST_CODE -> {
                    val images = ImagePicker.getImages(data)
                    selectedImageDashBoard = images[0].uri
                    binding.carImages.dashBoardImage.setImageURI(selectedImageDashBoard)
                    if (uploadImageList.contains(selectedImageDashBoard!!)) {
                        uploadImageList.remove(selectedImageDashBoard!!)
                    }
                    uploadImageList.add(selectedImageDashBoard!!)
                }

                INTERIOR_IMAGE_REQUEST_CODE -> {
                    val images = ImagePicker.getImages(data)
                    selectedImageInterior = images[0].uri
                    binding.carImages.interiorImage.setImageURI(selectedImageInterior)
                    if (uploadImageList.contains(selectedImageInterior!!)) {
                        uploadImageList.remove(selectedImageInterior!!)
                    }
                    uploadImageList.add(selectedImageInterior!!)
                }
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
        carMakeList = resources.getStringArray(R.array.cars_array)
        val carMakeListDialog = androidx.appcompat.app.AlertDialog.Builder(this.requireContext())
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
        val carModelListDialog = androidx.appcompat.app.AlertDialog.Builder(this.requireContext())
        val title = SpannableString((carMakeList[checkedCarMake]?.toUpperCase(Locale.ROOT) ) +" MODELS")
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
        val carYearDialog = androidx.appcompat.app.AlertDialog.Builder(this.requireContext())
        val carYearList = resources.getStringArray(R.array.cars_year_array)
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
        val carBodyDialog = androidx.appcompat.app.AlertDialog.Builder(this.requireContext())
        val carBodyList = resources.getStringArray(R.array.cars_body_types_array)
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
        val carConditionDialog = androidx.appcompat.app.AlertDialog.Builder(this.requireContext())
        val carConditionList = resources.getStringArray(R.array.cars_condition_array)
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
        val carTransmissionDialog = androidx.appcompat.app.AlertDialog.Builder(this.requireContext())
        val carTransmissionList = resources.getStringArray(R.array.cars_transmission_array)
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
        val carDutyDialog = androidx.appcompat.app.AlertDialog.Builder(this.requireContext())
        val carDutyList = resources.getStringArray(R.array.cars_duty_array)
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
        val carFuelDialog = androidx.appcompat.app.AlertDialog.Builder(this.requireContext())
        val carFuelList = resources.getStringArray(R.array.cars_fuel_array)
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
        val carInteriorDialog = androidx.appcompat.app.AlertDialog.Builder(this.requireContext())
        val carInteriorList = resources.getStringArray(R.array.cars_interior_array)
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
        val carColorDialog = androidx.appcompat.app.AlertDialog.Builder(this.requireContext())
        val carColorList = resources.getStringArray(R.array.cars_color_array)
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
        val carLocationDialog = androidx.appcompat.app.AlertDialog.Builder(this.requireContext())
        val carLocationList = resources.getStringArray(R.array.cars_location_array)
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

        val carMileage = binding.carFeature.editTextCarMileage.text.toString().trim()
        val carPrice = binding.carFeature.editTextCarPrice.text.toString().trim()
        carPriceNegotiable = binding.carFeature.priceNegotiableCheckBox.isChecked
        binding.carFeature.mileageTextInputLayout.error = null
        binding.carFeature.priceTextInputLayout.error = null

        val carEngineSize = binding.carFeature.textViewCarEngineSize.text.toString().trim()
        val  carDescription = binding.carFeature.textViewCarDescription.text.toString().trim()

        binding.carFeature.carEngineSizeTextInputLayout.error = null
        binding.carFeature.carDescriptionTextInputLayout.error = null

        if(checkedCarMake == -1){
            binding.root.snackBar("Please Select Car Make")
            return
        }
        if (checkedCarModel == -1){
            binding.root.snackBar("Please Select "+carMakeList[checkedCarMake]+" Model")
            return
        }
        if (checkedCarYear == -1){
            binding.root.snackBar("Please Select Vehicle Manufacture Year")
            return
        }
        if (checkedCarBody == -1){
            binding.root.snackBar("Please Select Vehicle Body Type")
            return
        }
        if (checkedCarCondition == -1){
            binding.root.snackBar("Please Select Vehicle Condition")
            return
        }
        if (checkedCarTransmission == -1){
            binding.root.snackBar("Please Select Vehicle Transmission Type")
            return
        }

        if (checkedCarFuel == -1){
            binding.root.snackBar("Please Select Car Fuel Type")
            return
        }
        if (checkedCarInterior == -1){
            binding.root.snackBar("Please Select Car Interior Type")
            return
        }
        if (checkedCarColor == -1){
            binding.root.snackBar("Please Select Car Color Type")
            return
        }
        if (checkedCarLocation == -1){
            binding.root.snackBar("Please Select County Where Vehicle is Located")
            return
        }
        if (checkedCarDuty == -1){
            binding.root.snackBar("Please Select Vehicle Duty")
            return
        }
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
        if (selectedImageFront == null || selectedImageRight == null || selectedImageLeft == null || selectedImageBack == null ||
            selectedImageDashBoard == null || selectedImageInterior == null) {
            binding.root.snackBar("Front | RightSide| LeftSide | BackSide| DashBoard | Interior | Images are Required")
           return
        }
        for (image in uploadImageMoreList){
            uploadImageList.add(image)
        }
        uploadImageMoreList.clear()


        val carName  = carMakeSelected.plus(" ").plus(carModelSelected).plus(" ").plus(
            carYearSelected
        )

        carObject = CarObject(
            carName,carMakeSelected,carModelSelected,carYearSelected.toFloat(),carBodyTypeSelected,carConditionSelected,
            carTransMissionSelected,carDutySelected,trimCommaOfString(carMileage).toFloat(),trimCommaOfString(carPrice).toFloat(),carPriceNegotiable,carSelectedFuel,carSelectedInterior,
            carSelectedColor,trimCommaOfString(carEngineSize).toFloat(),carDescription,commonFeaturesList,carSelectedLocation)


        viewModel = ViewModelProvider(this.requireActivity(),factory).get(PostCarViewModel::class.java)
        userID = session.getSession().toString()
        val customAlertDialog = CustomAlertDialog(requireActivity())
        customAlertDialog.startLoadingDialog("Uploading Car")
        try {
            viewModel.postUserCar(carObject, userID, binding.root, requireContext())
            viewModel.postCarResponse.observe(viewLifecycleOwner, Observer {
                carID = it.carObject?.id.toString()
                carObject = it.carObject!!
                for (imageFile in uploadImageList) {
                    val imageStream = requireContext().contentResolver.openInputStream(imageFile)
                    val fullSizeBitmap = BitmapFactory.decodeStream(imageStream)
                    val reducedImageBitMap = imageResizer.reduceBitmapSize(fullSizeBitmap, 614400)
                    val reducedImageFile = getFileFromBitmap(reducedImageBitMap,imageFile)
                    //val body = UploadImageRequestBody(file,"image")
                    val body = RequestBody.create(MediaType.parse("image/*"), reducedImageFile)
                    val multipartBody = MultipartBody.Part.createFormData("photos", reducedImageFile.name, body)
                    uploadImageBodyPart.add(multipartBody)
                }

                viewModel.postUserCarImages(uploadImageBodyPart, carID, binding.root, requireContext())
                viewModel.postCarImagesResponse.observe(
                    viewLifecycleOwner,
                    Observer { postCarImagesResponse ->
                        customAlertDialog.stopDialog()
                        Toast.makeText(
                            requireContext(),
                            "${postCarImagesResponse.carObject?.name}" + "Uploaded Successfully",
                            Toast.LENGTH_LONG
                        ).show()
                        uploadImageBodyPart.clear()
                        uploadImageList.clear()
                        requireActivity().viewModelStore.clear()
                        requireContext().cacheDir.deleteRecursively()
                        loadHomeActivity()
                    })
            })
        }catch (e: NoInternetException){
            binding.root.snackBar(e.message)
        }
    }


    private fun commonFeaturesDialog() {
        val commonFeaturesAlertDialog = AlertDialog.Builder(this.requireContext())
        val commonFeaturesChecked = resources.getStringArray(R.array.car_common_features_array)
        val title = SpannableString("COMMON FEATURES")
        title.setSpan(ForegroundColorSpan(Color.parseColor("#009688")), 0, title.length, 0)
        commonFeaturesAlertDialog.setTitle(title)
        commonFeaturesAlertDialog.setMultiChoiceItems(R.array.car_common_features_array,commonFeaturesCheckedStates) { _: DialogInterface, which: Int, isChecked: Boolean ->
            if (isChecked){
                commonFeaturesCheckedStates[which] = isChecked
                commonFeaturesList.add(commonFeaturesChecked[which])
            }
            else if (commonFeaturesList.contains(commonFeaturesChecked[which])){
                commonFeaturesList.remove(commonFeaturesChecked[which])
            }
        }
        commonFeaturesAlertDialog.setPositiveButton("ADD FEATURES") { _, _ ->
            Snackbar.make(this.requireView(),"$commonFeaturesList Added to Features", Snackbar.LENGTH_LONG).show()
        }
        commonFeaturesAlertDialog.setNegativeButton("CANCEL") { dialog, _ ->
            dialog.dismiss()
        }
        commonFeaturesAlertDialog.create()
        commonFeaturesAlertDialog.show()
    }
    private fun extraFeaturesDialog() {
        val extraFeaturesAlertDialog = AlertDialog.Builder(this.requireContext())
        val extraFeaturesChecked = resources.getStringArray(R.array.car_extra_features_array)
        val title = SpannableString("EXTRA FEATURES")
        title.setSpan(ForegroundColorSpan(Color.parseColor("#009688")), 0, title.length, 0)
        extraFeaturesAlertDialog.setTitle(title)
        extraFeaturesAlertDialog.setMultiChoiceItems(R.array.car_extra_features_array,extraFeaturesCheckedStates) { _: DialogInterface, which: Int, isChecked: Boolean ->
            if (isChecked){
                extraFeaturesCheckedStates[which] = isChecked
                commonFeaturesList.add(extraFeaturesChecked[which])
            }
            else if (commonFeaturesList.contains(extraFeaturesChecked[which])){
                commonFeaturesList.remove(extraFeaturesChecked[which])
            }
        }
        extraFeaturesAlertDialog.setPositiveButton("ADD FEATURES") { _, _ ->
            Snackbar.make(this.requireView(),"$commonFeaturesList Added to Features", Snackbar.LENGTH_LONG).show()
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
    override fun onStart() {
        super.onStart()
    }
    override fun onResume() {
        super.onResume()
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
        findNavController().popBackStack(R.id.postCarDetailsFragment, true)
        findNavController().navigate(R.id.userProfilePFragment)
        findNavController().navigate(R.id.userCarsFragment)
        val bundle = bundleOf("carObject" to carObject)
        //findNavController().navigate(R.id.featureCarFragment,bundle)
        findNavController().navigate(R.id.featureCarFragment, bundle)
    }

}