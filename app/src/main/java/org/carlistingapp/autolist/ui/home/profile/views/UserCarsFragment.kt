package org.carlistingapp.autolist.ui.home.profile.views

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ismaeldivita.chipnavigation.ChipNavigationBar
import kotlinx.android.synthetic.main.fragment_user_cars.*
import org.carlistingapp.autolist.R
import org.carlistingapp.autolist.data.db.entities.CarObject
import org.carlistingapp.autolist.data.network.ListingCarsAPI
import org.carlistingapp.autolist.data.repositories.UserRepository
import org.carlistingapp.autolist.databinding.FragmentUserCarsBinding
import org.carlistingapp.autolist.ui.home.profile.adapter.UserCarsAdapter
import org.carlistingapp.autolist.ui.home.profile.viewModel.UserViewModel
import org.carlistingapp.autolist.ui.home.profile.viewModel.UserViewModelFactory
import org.carlistingapp.autolist.utils.Session
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class UserCarsFragment : Fragment(), KodeinAware, UserCarsAdapter.OnItemClickListener, SoldUserCarDialogFragment.UpdateCarDetails {
    override val kodein by kodein()
    val api : ListingCarsAPI by instance()
    val repository : UserRepository by instance()
    val factory: UserViewModelFactory by instance()
    val session : Session by instance()
    private lateinit var viewModel: UserViewModel
    private lateinit var binding: FragmentUserCarsBinding
    private var carsList = ArrayList<CarObject>()
    private var filteredCarsList = ArrayList<CarObject>()
    private var activeCarsList = ArrayList<CarObject>()
    private var underReviewCarsList = ArrayList<CarObject>()
    private var declinedCarsList = ArrayList<CarObject>()
    private var soldCarsList = ArrayList<CarObject>()
    private lateinit var userId : String
    private val navBuilder = NavOptions.Builder()
    private lateinit var userCarsAdapter: UserCarsAdapter
    private lateinit var userMenu : ChipNavigationBar

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateUi()
    }

    override fun onResume() {
        super.onResume()
        userMenu.setItemSelected(R.id.active)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_cars, container, false)
        return binding.root
    }

    override fun onItemClick(position: Int) {
        val bundle = bundleOf("carObject" to filteredCarsList[position])
        Navigation.findNavController(requireActivity(), R.id.fragment).navigate(
            R.id.userCarDetailsFragment,
            bundle
        )
    }

    override fun onClickEditUserCar(position: Int) {
        val bundle = bundleOf("carObject" to filteredCarsList[position])
        findNavController().navigate(R.id.editCarFragment, bundle)
    }

    override fun onClickDeleteUserCar(position: Int) {
        val bundle = bundleOf("carObject" to filteredCarsList[position])
        val deleteCarDialogFragment = DeleteUserCarDialogFragment()
        deleteCarDialogFragment.setStyle(DialogFragment.STYLE_NO_FRAME, 0)
        deleteCarDialogFragment.arguments = bundle
        deleteCarDialogFragment.show(
            requireActivity().supportFragmentManager,
            "DeleteCarDialogFragment"
        )
    }

    override fun onClickSoldUserCar(position: Int) {
        val bundle = bundleOf("carObject" to carsList[position])
        val soldCarDialogFragment = SoldUserCarDialogFragment(this)
        soldCarDialogFragment.setStyle(DialogFragment.STYLE_NO_FRAME, 0)
        soldCarDialogFragment.arguments = bundle
        soldCarDialogFragment.show(
            requireActivity().supportFragmentManager,
            "SoldCarDialogFragment"
        )
    }

    @SuppressLint("SimpleDateFormat")
    override fun onFeatureUserCar(position: Int) {
        val car = carsList[position]
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss'Z'")
        val endDate = format.parse(car.featured!!.endDay)
        val today = Calendar.getInstance().time
        val diff: Long = endDate!!.time - today.time
        val seconds = diff / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24
        if (days <= 0){
            val bundle = bundleOf("carObject" to carsList[position])
            bundle.putString("userId", userId)
            findNavController().navigate(R.id.featureCarFragment, bundle)
        }
        else{
            val bundle = bundleOf("carObject" to car)
            bundle.putString("Days", days.toString())
            val carIsFeaturedFragmentDialog = CarIsFeaturedFragmentDialog()
            carIsFeaturedFragmentDialog.setStyle(DialogFragment.STYLE_NO_FRAME, 0)
            carIsFeaturedFragmentDialog.arguments = bundle
            carIsFeaturedFragmentDialog.show(
                requireActivity().supportFragmentManager,
                "CarIsFeaturedFragmentDialog"
            )
        }
    }

    override fun onCarSold() {
        updateUi()
    }

    private fun updateUi(){
        userId = session.getSession().toString()
        viewModel = ViewModelProvider(this, factory).get(UserViewModel::class.java)
        viewModel.getUserCars(userId, requireContext())
        binding.progressBar.visibility = View.VISIBLE
        viewModel.userCars.observe(viewLifecycleOwner, Observer { userCars ->
            carsList = userCars
            filteredCarsList.clear()
            for (car in carsList){
                if (car.status == "active"){
                    filteredCarsList.add(car)
                }
            }
            userCarsAdapter = UserCarsAdapter(filteredCarsList, this.requireActivity(), this)
            binding.progressBar.visibility = View.INVISIBLE
            recycler_view.also {
                it.layoutManager = LinearLayoutManager(requireContext())
                it.setHasFixedSize(true)
                it.adapter = userCarsAdapter
            }
        })
        userMenu = binding.menuUser
        userMenu.setItemSelected(R.id.active)

        activeCarsList.clear()
        underReviewCarsList.clear()
        declinedCarsList.clear()
        soldCarsList.clear()
        for (car in carsList){
            if (car.status == "active"){
                activeCarsList.add(car)
            }
            if (car.status == "underreview"){
                underReviewCarsList.add(car)
            }
            if (car.status == "declined"){
                declinedCarsList.add(car)
            }
            if (car.status == "sold"){
                soldCarsList.add(car)
            }
        }

        userMenu.showBadge(R.id.active, activeCarsList.size)
        userMenu.showBadge(R.id.underReview, underReviewCarsList.size)
        userMenu.showBadge(R.id.declined, declinedCarsList.size)
        userMenu.showBadge(R.id.sold, soldCarsList.size)


        userMenu.setOnItemSelectedListener { menuButton ->
            when (menuButton) {
                R.id.active -> {
                    filteredCarsList.clear()
                    for (car in carsList){
                        if (car.status == "active"){
                            filteredCarsList.add(car)
                        }
                    }
                    userCarsAdapter.notifyDataSetChanged()
                }
                R.id.underReview -> {
                    filteredCarsList.clear()
                    for (car in carsList){
                        if (car.status == "underreview"){
                            filteredCarsList.add(car)
                        }
                    }
                    userCarsAdapter.notifyDataSetChanged()
                }
                R.id.declined -> {
                    filteredCarsList.clear()
                    for (car in carsList){
                        if (car.status == "declined"){
                            filteredCarsList.add(car)
                        }
                    }
                    userCarsAdapter.notifyDataSetChanged()

                }
                R.id.sold -> {
                    filteredCarsList.clear()
                    for (car in carsList){
                        if (car.status == "sold"){
                            filteredCarsList.add(car)
                        }
                    }
                    userCarsAdapter.notifyDataSetChanged()
                }
            }
        }
    }
}