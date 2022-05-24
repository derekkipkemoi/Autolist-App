package org.carlistingapp.autolist.ui.home.listCar.views

import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import org.carlistingapp.autolist.R
import org.carlistingapp.autolist.data.db.entities.CarObject
import org.carlistingapp.autolist.data.network.ListingCarsAPI
import org.carlistingapp.autolist.data.repositories.UserRepository
import org.carlistingapp.autolist.databinding.FragmentSearchCarBinding
import org.carlistingapp.autolist.ui.home.listCar.adapters.ViewedItemAdapter
import org.carlistingapp.autolist.ui.home.listCar.adapters.SearchTextAdapter
import org.carlistingapp.autolist.ui.home.listCar.viewModels.ListCarViewModel
import org.carlistingapp.autolist.ui.home.listCar.viewModels.ListCarViewModelFactory
import org.carlistingapp.autolist.utils.Session
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import java.util.*
import kotlin.collections.ArrayList

class SearchCarFragment : Fragment(),KodeinAware,
ViewedItemAdapter.OnItemClickListener, SearchTextAdapter.OnTextItemClicked{
    override val kodein by kodein()
    val api: ListingCarsAPI by instance()
    val repository: UserRepository by instance()
    val factory: ListCarViewModelFactory by instance()
    private lateinit var viewModel: ListCarViewModel
    private lateinit var binding: FragmentSearchCarBinding
    private var carsList=  ArrayList<CarObject>()
    private lateinit var searchView: SearchView
    //private lateinit var adapter: FilteredCarsAdapter
    private lateinit var selectedMake : String
    private val session : Session by instance()
    private var viewedCarsObjectList = ArrayList<CarObject>()
    private var searchedNewTextList = ArrayList<String>()
    private lateinit var viewedItemAdapter : ViewedItemAdapter
    private lateinit var searchTextAdapter : SearchTextAdapter
    private lateinit var viewedCarsList : ArrayList<String>
    private lateinit var searchedTextList : ArrayList<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this, factory).get(ListCarViewModel::class.java)
        carsList = arguments?.getParcelableArrayList<CarObject>("carsList") as ArrayList<CarObject>
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View
    {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search_car, container, false)
        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        searchView = binding.searchView
        searchView.queryHint = "Type your search here ..."

        viewedCarsList = session.getRecentViewedCars()
        searchedTextList = session.getRecentSearchedText()
        searchedNewTextList.clear()
        viewedCarsObjectList.clear()
        for (car in carsList){
            if (viewedCarsList.contains(car.id)){
                viewedCarsObjectList.add(car)
            }
        }

        for (text in searchedTextList){
            if (searchedTextList.contains(text)){
                searchedNewTextList.add(text)
            }
        }

        if (viewedCarsObjectList.size > 0){
            binding.recentlyViewedCarsLayout.visibility =View.VISIBLE
        }
        if (searchedNewTextList.size > 0){
            binding.recentlySearchedLayout.visibility =View.VISIBLE
        }
        viewedItemAdapter = ViewedItemAdapter(viewedCarsObjectList,requireContext(), this)
        searchTextAdapter = SearchTextAdapter(searchedNewTextList,this)
        binding.recyclerView.also {
            it.layoutManager = LinearLayoutManager(requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false)
            it.setHasFixedSize(true)
            it.adapter = viewedItemAdapter
        }
        binding.recyclerViewList.also {
            it.layoutManager = LinearLayoutManager(requireContext())
            it.setHasFixedSize(true)
            it.adapter = searchTextAdapter
        }
        searchView.imeOptions = EditorInfo.IME_ACTION_DONE
        searchView.onActionViewExpanded()
        searchView.setIconifiedByDefault(false)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }

            override fun onQueryTextSubmit(constraint: String): Boolean {
                //adapter.filter.filter(query)
                session.saveRecentSearchedText(constraint.trim())
                val carsListWithQuery = ArrayList<CarObject>()
                if (constraint.isEmpty()){
                    carsListWithQuery.addAll(carsList)
                }else{
                    val filterPattern = constraint.toLowerCase(Locale.ROOT).trim()
                    for (car in carsList){
                        if (car.name?.toLowerCase(Locale.ROOT)?.contains(filterPattern) == true
                            || car.location?.toLowerCase(Locale.ROOT)?.contains(filterPattern) == true
                            || car.condition?.toLowerCase(Locale.ROOT)?.contains(filterPattern) == true
                            || car.body?.toLowerCase(Locale.ROOT)?.contains(filterPattern) == true
                            || car.color?.toLowerCase(Locale.ROOT)?.contains(filterPattern) == true
                            || car.description?.toLowerCase(Locale.ROOT)?.contains(filterPattern) == true
                            || car.interior?.toLowerCase(Locale.ROOT)?.contains(filterPattern) == true
                            || car.duty?.toLowerCase(Locale.ROOT)?.contains(filterPattern) == true
                            || car.description?.toLowerCase(Locale.ROOT)?.contains(filterPattern) == true){
                            carsListWithQuery.add(car)
                        }
                    }
                }

                val bundle = bundleOf("carsList" to carsListWithQuery)
                findNavController().navigate(R.id.listCarFragment, bundle)
                return false
            }
        })

        binding.clearCarList.setOnClickListener {
            session.clearRecentViewedCars()
            viewedCarsObjectList.clear()
            viewedItemAdapter.notifyDataSetChanged()
            binding.recentlyViewedCarsLayout.visibility = View.GONE
        }

        binding.clearTextList.setOnClickListener {
            session.clearRecentSearchedText()
            searchedNewTextList.clear()
            searchTextAdapter.notifyDataSetChanged()
            binding.recentlySearchedLayout.visibility = View.GONE
        }

    }


    override fun onCarClicked(position: Int) {
        val bundle = bundleOf("carsList" to viewedCarsObjectList)
        bundle.putInt("Position", position)
        Navigation.findNavController(requireActivity(), R.id.fragment).navigate(
            R.id.carDetailsFragment,
            bundle
        )
    }

    override fun onSearchedTextClicked(position: Int) {
        val clickedSearchedText = searchedNewTextList[position]
        val carsListWithQuery = ArrayList<CarObject>()
        for (car in carsList){
            if (car.name?.toLowerCase(Locale.ROOT)?.contains(clickedSearchedText.toLowerCase(Locale.ROOT)) == true
                || car.location?.toLowerCase(Locale.ROOT)?.contains(clickedSearchedText.toLowerCase(Locale.ROOT)) == true
                || car.condition?.toLowerCase(Locale.ROOT)?.contains(clickedSearchedText.toLowerCase(Locale.ROOT)) == true
                || car.body?.toLowerCase(Locale.ROOT)?.contains(clickedSearchedText.toLowerCase(Locale.ROOT)) == true
                || car.color?.toLowerCase(Locale.ROOT)?.contains(clickedSearchedText.toLowerCase(Locale.ROOT)) == true
                || car.description?.toLowerCase(Locale.ROOT)?.contains(clickedSearchedText.toLowerCase(Locale.ROOT)) == true
                || car.interior?.toLowerCase(Locale.ROOT)?.contains(clickedSearchedText.toLowerCase(Locale.ROOT)) == true
                || car.duty?.toLowerCase(Locale.ROOT)?.contains(clickedSearchedText.toLowerCase(Locale.ROOT)) == true
                || car.description?.toLowerCase(Locale.ROOT)?.contains(clickedSearchedText.toLowerCase(Locale.ROOT)) == true){
                carsListWithQuery.add(car)
            }
        }
        val bundle = bundleOf("carsList" to carsListWithQuery)
        findNavController().navigate(R.id.listCarFragment, bundle )
    }



//    private fun selectCarMake(){
//        val carMakeList = resources.getStringArray(R.array.cars_array)
//        val carMakeListDialog = AlertDialog.Builder(this.requireContext())
//        val title = SpannableString("SELECT CAR MAKE")
//        title.setSpan(ForegroundColorSpan(Color.parseColor("#009688")), 0, title.length, 0)
//        carMakeListDialog.setTitle(title)
//        carMakeListDialog.setSingleChoiceItems(carMakeList, 0) { _, which ->
//            selectedMake = carMakeList[which]
//            //Toast.makeText(requireContext(),selectedMake,Toast.LENGTH_LONG).show()
//        }
//
//        carMakeListDialog.setPositiveButton("Ok") { _, _ ->
//            val make = carsList.filter { it.make == selectedMake }
//            val bundle = bundleOf("carsList" to make)
//            bundle.putString("FragmentName", selectedMake)
//            findNavController().navigate(R.id.listCarFragment, bundle)
//        }
//        carMakeListDialog.setNegativeButton("Cancel", null)
//        val dialog = carMakeListDialog.create()
//        dialog.show()
//
//    }




}

