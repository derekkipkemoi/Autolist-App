package org.carlistingapp.autolist.ui.home.profile.views
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import org.carlistingapp.autolist.R
import org.carlistingapp.autolist.data.network.ListingCarsAPI
import org.carlistingapp.autolist.data.repositories.UserRepository
import org.carlistingapp.autolist.databinding.FragmentPaymentsBinding
import org.carlistingapp.autolist.ui.home.profile.adapter.PaymentsAdapter
import org.carlistingapp.autolist.ui.home.profile.viewModel.UserViewModel
import org.carlistingapp.autolist.ui.home.profile.viewModel.UserViewModelFactory
import org.carlistingapp.autolist.utils.Session
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
class PaymentsFragment : Fragment(), KodeinAware {
    override val kodein by kodein()
    private val api : ListingCarsAPI by instance()
    private val repository : UserRepository by instance()
    private val factory : UserViewModelFactory by instance()
    private lateinit var viewModel : UserViewModel
    private val session: Session by instance()
    private lateinit var binding: FragmentPaymentsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_payments, container, false)
        binding.progressBar.visibility = View.VISIBLE

        viewModel = ViewModelProvider(this,factory).get((UserViewModel::class.java))
        viewModel.getUserPayments(session.getSession()!!,requireContext())
        viewModel.userPayments.observe(viewLifecycleOwner, Observer { paymentsResponse ->
            if(paymentsResponse.size == 0){
                binding.text.visibility = View.VISIBLE
            }
            binding.progressBar.visibility = View.GONE
            binding.recyclerView.also{
                it.layoutManager = LinearLayoutManager(requireContext())
                it.setHasFixedSize(true)
                it.adapter = PaymentsAdapter(paymentsResponse)
            }
        })
        return  binding.root
    }
    companion object {

    }
}