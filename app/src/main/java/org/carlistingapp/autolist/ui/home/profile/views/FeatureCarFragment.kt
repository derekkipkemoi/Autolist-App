package org.carlistingapp.autolist.ui.home.profile.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.activity_home.*
import org.carlistingapp.autolist.R
import org.carlistingapp.autolist.data.db.entities.CarObject
import org.carlistingapp.autolist.databinding.FragmentFeatureCarBinding
import org.carlistingapp.autolist.utils.Session
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance


class FeatureCarFragment : Fragment(), KodeinAware {
    override val kodein by kodein()
    private val session : Session by instance()
    private lateinit var binding: FragmentFeatureCarBinding
    private lateinit var car : CarObject
    private lateinit var userId : String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_feature_car, container, false)
        car = (arguments?.getParcelable("carObject") as CarObject?)!!
        userId = session.getSession().toString()
        requireActivity().toolBar.title = "Sell Faster Your "+car.name



        binding.goldPackageButton.setOnClickListener {
            binding.goldPackageLayout.visibility = View.VISIBLE
            binding.silverPackageLayout.visibility = View.GONE
            binding.bronzePackageLayout.visibility = View.GONE
            binding.freePackageLayout.visibility = View.GONE
        }

        binding.silverPackageButton.setOnClickListener {
          binding.goldPackageLayout.visibility = View.GONE
            binding.silverPackageLayout.visibility = View.VISIBLE
            binding.bronzePackageLayout.visibility = View.GONE
            binding.freePackageLayout.visibility = View.GONE
        }

        binding.bronzePackageButton.setOnClickListener {
            binding.goldPackageLayout.visibility = View.GONE
            binding.silverPackageLayout.visibility = View.GONE
            binding.bronzePackageLayout.visibility = View.VISIBLE
            binding.freePackageLayout.visibility = View.GONE
        }

        binding.freePackageButton.setOnClickListener {
            binding.goldPackageLayout.visibility = View.GONE
            binding.silverPackageLayout.visibility = View.GONE
            binding.bronzePackageLayout.visibility = View.GONE
            binding.freePackageLayout.visibility = View.VISIBLE
        }


        binding.confirmGoldPackageButton.setOnClickListener {
            val bundle = bundleOf("carObject" to car)
            bundle.putString("Package", "3000")
            bundle.putString("Name", "Gold Package")
            bundle.putString("userId", userId)
            val verifyPaymentsFragmentDialog = VerifyPaymentsFragmentDialog()
            verifyPaymentsFragmentDialog.setStyle(DialogFragment.STYLE_NO_FRAME, 0)
            verifyPaymentsFragmentDialog.arguments = bundle
            verifyPaymentsFragmentDialog.show(
                requireActivity().supportFragmentManager,
                "VerifyPaymentsFragmentDialog"
            )
        }

        binding.confirmsilverPackageButton.setOnClickListener {
            val bundle = bundleOf("carObject" to car)
            bundle.putString("Package", "1500")
            bundle.putString("Name", "Silver Package")
            bundle.putString("userId", userId)
            val verifyPaymentsFragmentDialog = VerifyPaymentsFragmentDialog()
            verifyPaymentsFragmentDialog.setStyle(DialogFragment.STYLE_NO_FRAME, 0)
            verifyPaymentsFragmentDialog.arguments = bundle
            verifyPaymentsFragmentDialog.show(
                requireActivity().supportFragmentManager,
                "VerifyPaymentsFragmentDialog"
            )
        }

        binding.confirmBronzePackageButton.setOnClickListener {
            val bundle = bundleOf("carObject" to car)
            bundle.putString("Package", "900")
            bundle.putString("Name", "Bronze Package")
            bundle.putString("userId", userId)
            val verifyPaymentsFragmentDialog = VerifyPaymentsFragmentDialog()
            verifyPaymentsFragmentDialog.setStyle(DialogFragment.STYLE_NO_FRAME, 0)
            verifyPaymentsFragmentDialog.arguments = bundle
            verifyPaymentsFragmentDialog.show(
                requireActivity().supportFragmentManager,
                "VerifyPaymentsFragmentDialog"
            )
        }


        binding.confirmFreePackageButton.setOnClickListener {
            findNavController().popBackStack()
        }


        return binding.root
    }
}