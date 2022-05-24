package org.carlistingapp.autolist.ui.home.profile.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.carlistingapp.autolist.R
import org.carlistingapp.autolist.data.db.entities.CarObject
import org.carlistingapp.autolist.data.network.ListingCarsAPI
import org.carlistingapp.autolist.data.repositories.UserRepository
import org.carlistingapp.autolist.databinding.FragmentFavouriteCarsBinding
import org.carlistingapp.autolist.ui.auth.views.LogInUserFragment
import org.carlistingapp.autolist.ui.home.listCar.adapters.ListCarsAdapter
import org.carlistingapp.autolist.ui.home.listCar.views.ContactSellerDialog
import org.carlistingapp.autolist.ui.home.profile.viewModel.UserViewModel
import org.carlistingapp.autolist.ui.home.profile.viewModel.UserViewModelFactory
import org.carlistingapp.autolist.utils.Session
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class FavouriteCarsFragment : Fragment(), KodeinAware, ListCarsAdapter.OnItemClickListener {
    override val kodein by kodein()
    private lateinit var viewModel: UserViewModel
    private val repository : UserRepository by instance()
    private val factory: UserViewModelFactory by instance()
    private val api : ListingCarsAPI by instance()
    private val session: Session by instance()
    private lateinit var binding: FragmentFavouriteCarsBinding
    private var userFavouriteCars = ArrayList<Any>()
    private lateinit var contactSellerDialog: ContactSellerDialog


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (session.getSession() == "userLoggedOut") {
            val logInUserFragment = LogInUserFragment()
            logInUserFragment.setStyle(
                BottomSheetDialogFragment.STYLE_NO_FRAME,
                0
            )
            logInUserFragment.show(requireActivity().supportFragmentManager, "LogInUserFragment")
        }else{
            viewModel = ViewModelProvider(this, factory).get(UserViewModel::class.java)
            val userId = session.getSession()
            binding.progressBar.visibility = View.VISIBLE
            viewModel.getUserFavouriteCars(userId!!, requireContext())
            viewModel.userCars.observe(viewLifecycleOwner, Observer { carObjecttResponseList ->
                userFavouriteCars = carObjecttResponseList as ArrayList<Any>
                binding.progressBar.visibility = View.INVISIBLE
                binding.recyclerView.also {
                    it.layoutManager = LinearLayoutManager(requireContext())
                    it.setHasFixedSize(true)
                    it.adapter = ListCarsAdapter(userFavouriteCars,requireContext(), this)
                }
            })
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_favourite_cars, container, false)
        return binding.root
    }

    override fun onItemClick(position: Int) {
        val bundle = bundleOf("carsList" to userFavouriteCars)
        bundle.putInt("Position", position)
        findNavController().navigate(
            R.id.carDetailsFragment,
            bundle
        )
    }

    override fun contactSellerOnClick(position: Int) {
        contactSellerDialog = ContactSellerDialog(requireActivity())
        val carObject = userFavouriteCars[position] as CarObject
        contactSellerDialog.startLoadingContactDialog(carObject)
    }

}