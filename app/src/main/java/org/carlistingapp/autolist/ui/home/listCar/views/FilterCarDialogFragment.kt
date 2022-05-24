package org.carlistingapp.autolist.ui.home.listCar.views

import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import org.carlistingapp.autolist.R
import org.carlistingapp.autolist.data.db.entities.CarObject
import org.carlistingapp.autolist.databinding.FragmentFilterCarDialogBinding
import java.util.*
import kotlin.collections.ArrayList
import kotlin.properties.Delegates

class FilterCarDialogFragment(private val listener: OnCarsListFiltered) : DialogFragment(){
    private lateinit var carMakeList: Array<String?>
    private lateinit var carModelList: Array<String?>
    private lateinit var carMakeSelected: String
    private lateinit var carModelSelected: String
    private lateinit var carYearSelected: String
    private lateinit var carBodyTypeSelected: String
    private lateinit var carConditionSelected: String
    private lateinit var carLocationSelected: String
    private var checkedCarMake by Delegates.notNull<Int>()
    private var checkedCarModel by Delegates.notNull<Int>()
    private var checkedCarYear by Delegates.notNull<Int>()
    private var checkedCarBody by Delegates.notNull<Int>()
    private var checkedCarCondition by Delegates.notNull<Int>()
    private var checkedCarLocation by Delegates.notNull<Int>()
    private lateinit var binding: FragmentFilterCarDialogBinding
    private lateinit var carsList: ArrayList<CarObject>
    private lateinit var filteredCarList: List<CarObject>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_filter_car_dialog, container, false)
        val view = binding.root
        checkedCarMake = -1
        checkedCarModel = -1
        checkedCarYear = -1
        checkedCarBody = -1
        checkedCarCondition = -1
        checkedCarLocation = -1
        binding.carModelButton.isEnabled = false

        carsList = arguments?.getParcelableArrayList<CarObject>("carsList")!!
        filteredCarList = carsList

        binding.carMakeButton.setOnClickListener {
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

        binding.carLocationButton.setOnClickListener {
            selectCarLocation()
        }

        binding.buttonFilter.setOnClickListener {
            filterCarsList()
        }

        binding.buttonCancel.setOnClickListener {
            dismiss()
        }
        return view
    }


    private fun selectCarMake() {
        carMakeList = resources.getStringArray(R.array.cars_array)
        val carMakeListDialog = androidx.appcompat.app.AlertDialog.Builder(this.requireContext())
        val title = SpannableString("SELECT CAR MAKE")
        title.setSpan(ForegroundColorSpan(Color.parseColor("#009688")), 0, title.length, 0)
        carMakeListDialog.setTitle(title)
        carMakeListDialog.setSingleChoiceItems(carMakeList, checkedCarMake) { _, which ->
            when (which) {
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
            carMakeSelected = carMakeList[checkedCarMake].toString()
        }
        carMakeListDialog.setNegativeButton("Cancel", null)
        val dialog = carMakeListDialog.create()
        dialog.show()

    }

    private fun selectCarModel() {
        val carModelListDialog = androidx.appcompat.app.AlertDialog.Builder(this.requireContext())
        val title = SpannableString((carMakeList[checkedCarMake]?.toUpperCase(Locale.ROOT)) + " MODELS")
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

    private fun selectCarLocation() {
        val carLocationDialog = androidx.appcompat.app.AlertDialog.Builder(this.requireContext())
        val carLocationList = resources.getStringArray(R.array.cars_location_array)
        val title = SpannableString("SELECT CAR LOCATION")
        title.setSpan(ForegroundColorSpan(Color.parseColor("#009688")), 0, title.length, 0)
        carLocationDialog.setTitle(title)
        carLocationDialog.setSingleChoiceItems(carLocationList, checkedCarLocation) { _, which ->
            checkedCarLocation = which
            binding.carLocationButton.text = carLocationList[which]
            carLocationSelected = carLocationList[which].toString()
        }
        carLocationDialog.setPositiveButton("Ok") { _, _ ->
        }
        carLocationDialog.setNegativeButton("Cancel", null)
        val dialog = carLocationDialog.create()
        dialog.show()
    }

    private fun filterCarsList() {


        if (checkedCarMake >= 0) {
             filteredCarList = filteredCarList.filter { it.make == carMakeSelected }
        }

        if (checkedCarModel >= 0) {
            filteredCarList = filteredCarList.filter { it.model == carModelSelected }
        }


        if (checkedCarYear >= 0) {
            filteredCarList = filteredCarList.filter { it.year == carYearSelected.toFloat() }
        }

        if (checkedCarBody >= 0) {
            filteredCarList = filteredCarList.filter { it.body == carBodyTypeSelected }
        }

        if (checkedCarCondition >= 0) {
            filteredCarList = filteredCarList.filter { it.condition == carConditionSelected }
        }

        if (checkedCarLocation >= 0) {
            filteredCarList = filteredCarList.filter { it.location == carLocationSelected }
        }
        
        listener.onFiltered(filteredCarList as ArrayList<CarObject>)
        dismiss()
//        val bundle = bundleOf("filteredCarList" to filteredCarList)
//        findNavController().navigate(R.id.filterCarsFragment, bundle)
    }

    interface OnCarsListFiltered{
        fun onFiltered(carListFiltered : ArrayList<CarObject>)
    }
}