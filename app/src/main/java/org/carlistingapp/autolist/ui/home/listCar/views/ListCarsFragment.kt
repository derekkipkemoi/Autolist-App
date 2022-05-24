package org.carlistingapp.autolist.ui.home.listCar.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.formats.UnifiedNativeAd
import org.carlistingapp.autolist.R
import org.carlistingapp.autolist.data.db.entities.CarObject
import org.carlistingapp.autolist.data.network.ListingCarsAPI
import org.carlistingapp.autolist.data.repositories.UserRepository
import org.carlistingapp.autolist.databinding.FragmentListCarsBinding
import org.carlistingapp.autolist.ui.home.listCar.adapters.SearchCarsAdapter
import org.carlistingapp.autolist.ui.home.listCar.viewModels.ListCarViewModel
import org.carlistingapp.autolist.ui.home.listCar.viewModels.ListCarViewModelFactory
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import kotlin.collections.ArrayList

class ListCarsFragment : Fragment(), KodeinAware, SearchCarsAdapter.OnItemClickListener,
    FilterCarDialogFragment.OnCarsListFiltered{
    override val kodein by kodein()
    val api : ListingCarsAPI by instance()
    val repository : UserRepository by instance()
    val factory : ListCarViewModelFactory by instance()
    private lateinit var viewModel: ListCarViewModel
    private lateinit var binding: FragmentListCarsBinding
    private var carsList = ArrayList<CarObject>()
    private var allCarsList = ArrayList<CarObject>()
    private lateinit var contactSellerDialog: ContactSellerDialog
    private val NUMBER_OF_ADS = 5
    private lateinit var adLoader: AdLoader
    private lateinit var adapter: SearchCarsAdapter
    private var mNativeAds: ArrayList<UnifiedNativeAd> = arrayListOf()
    private var fragmentName = String
    private lateinit var searchView: SearchView


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View
    {
       binding = DataBindingUtil.inflate(inflater, R.layout.fragment_list_cars, container, false)
        viewModel.cars.observe(viewLifecycleOwner, Observer { cars->
            allCarsList = cars
        })
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this,factory).get(ListCarViewModel::class.java)
        viewModel.getCars(requireContext())
        carsList =  arguments?.getParcelableArrayList<CarObject>("carsList") as ArrayList<CarObject>
        val filter = arguments?.getString("Filter")

        if (filter == "Filter icon clicked"){
            val bundle = bundleOf("carsList" to carsList)
            val filterCarDialogFragment = FilterCarDialogFragment(this)
            filterCarDialogFragment.setStyle(DialogFragment.STYLE_NO_FRAME, 0)
            filterCarDialogFragment.arguments = bundle
            filterCarDialogFragment.show(
                requireActivity().supportFragmentManager,
                "filterCarDialogFragment"
            )
        }
        //loadNativeAds()
        adapter = SearchCarsAdapter(carsList, this.requireActivity(), this)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

//        fragmentName = arguments?.getString("FragmentName").toString()
//
//        requireActivity().toolBar.title = fragmentName

        binding.recyclerView.also {
            it.layoutManager = LinearLayoutManager(requireContext())
            it.setHasFixedSize(true)
            it.adapter = adapter
        }

        searchView = binding.searchView
        searchView.queryHint = "Type your search here ..."
        //searchView.imeOptions = EditorInfo.IME_ACTION_DONE
        //searchView.onActionViewExpanded()
        searchView.setIconifiedByDefault(false)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                adapter.filter.filter(newText)
                return false
            }
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }
        })



        binding.filterImageView.setOnClickListener {
            val bundle = bundleOf("carsList" to allCarsList)
            val filterCarDialogFragment = FilterCarDialogFragment(this)
            filterCarDialogFragment.setStyle(DialogFragment.STYLE_NO_FRAME, 0)
            filterCarDialogFragment.arguments = bundle
            filterCarDialogFragment.show(
                requireActivity().supportFragmentManager,
                "filterCarDialogFragment"
            )
        }
    }

    override fun onItemClick(position: Int) {
        val bundle = bundleOf("carsList" to carsList)
        bundle.putInt("Position", position)
        Navigation.findNavController(requireActivity(), R.id.fragment).navigate(
            R.id.carDetailsFragment,
            bundle
        )
    }

    override fun onFiltered(carListFiltered: ArrayList<CarObject>) {
        carsList.clear()
        carsList.addAll(carListFiltered)
        adapter.notifyDataSetChanged()
    }


//    private fun insertAdsInCarsList() {
//        if (mNativeAds.isEmpty()) {
//            return
//        }
//        val offset: Int = mRecyclerViewItems.size / mNativeAds.size + 2
//        var index = 2
//        for (ad in mNativeAds) {
//            mRecyclerViewItems.add(index, ad)
//            index += offset
//        }
//        adapter.notifyDataSetChanged()
//    }

//    private fun loadNativeAds() {
//        val builder: AdLoader.Builder = AdLoader.Builder(requireActivity(), getString(R.string.ad_unit_id))
//        adLoader = builder.forUnifiedNativeAd { unifiedNativeAd ->
//                mNativeAds.add(unifiedNativeAd)
//                if (!adLoader.isLoading) {
//                    insertAdsInCarsList()
//                }
//            }.withAdListener(
//                object : AdListener() {
//                    override fun onAdFailedToLoad(errorCode: Int) {
//                        Log.e("MainActivity", "The previous native ad failed to load. Attempting to" + " load another.")
//                        if (!adLoader.isLoading) {
//                            insertAdsInCarsList()
//                        }
//                    }
//                }).build()
//        adLoader.loadAds(AdRequest.Builder().build(), NUMBER_OF_ADS)
//    }


}

