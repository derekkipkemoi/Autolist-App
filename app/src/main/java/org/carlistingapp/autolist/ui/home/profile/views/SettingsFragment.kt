package org.carlistingapp.autolist.ui.home.profile.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_home.*
import org.carlistingapp.autolist.R
import org.carlistingapp.autolist.databinding.FragmentSettingsBinding
import org.carlistingapp.autolist.utils.Session
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class SettingsFragment : Fragment() , KodeinAware{
    override val kodein by kodein()
    val session : Session by instance()
    private lateinit var binding : FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_settings, container, false)
        val view = binding.root
        requireActivity().toolBar.title = "Settings"



        return view
    }

    private fun editPhoneNumber() {
        val editUserPhone = EditUserPhoneFragment()
        editUserPhone.setStyle(DialogFragment.STYLE_NO_FRAME, 0)
        editUserPhone.show(requireActivity().supportFragmentManager,"EditUserPhone")
    }


    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
    }

}