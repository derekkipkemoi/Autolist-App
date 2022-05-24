package org.carlistingapp.autolist.ui.auth.views

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import org.carlistingapp.autolist.R
import org.carlistingapp.autolist.databinding.FragmentPasswordResetLinkSentBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PasswordResetLinkSentFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PasswordResetLinkSentFragment : Fragment() {

    private lateinit var binding: FragmentPasswordResetLinkSentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_password_reset_link_sent, container, false)
        binding.button.setOnClickListener {
            findNavController().popBackStack()
            findNavController().popBackStack()
        }
        return binding.root
    }

}