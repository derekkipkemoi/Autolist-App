package org.carlistingapp.autolist.ui.home.profile.views

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import org.carlistingapp.autolist.BuildConfig
import org.carlistingapp.autolist.R
import org.carlistingapp.autolist.data.db.entities.UserObject
import org.carlistingapp.autolist.databinding.FragmentUserProfileBinding
import org.carlistingapp.autolist.utils.Session
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance


class UserProfilePFragment : Fragment() , KodeinAware{
    private lateinit var binding: FragmentUserProfileBinding
    override val kodein by kodein()
    private val session : Session by instance()
    private var reviewInfo: ReviewInfo? = null
    private lateinit var reviewManager: ReviewManager


    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val user = session.get<UserObject>("UserObject")
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_user_profile,
            container,
            false
        )
        val view = binding.root



        updateUserProfileUI()
        reviewApp()

        binding.ads.setOnClickListener {
            findNavController().navigate(R.id.userCarsFragment)
        }

        binding.payments.setOnClickListener {
            findNavController().navigate(R.id.paymentsFragment)
        }

        binding.termsAndConditions.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(
                "Url",
                "https://sites.google.com/view/motiitopcarstermsandconditions/home"
            )
            findNavController().navigate(R.id.termAndConditionsAndPrivacyPolicy, bundle)
        }
        binding.privacyPolicy.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("Url", "https://sites.google.com/view/motiitopcars/home")
            findNavController().navigate(R.id.termAndConditionsAndPrivacyPolicy, bundle)
        }

        binding.rateApp.setOnClickListener {
            val appPackageName = BuildConfig.APPLICATION_ID
            val uri: Uri = Uri.parse("market://details?id=$appPackageName")
            val goToMarket = Intent(Intent.ACTION_VIEW, uri)
            // To count with Play market backstack, After pressing back button,
            // to taken back to our application, we need to add following flags to intent.
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
            try {
                startActivity(goToMarket)
            } catch (e: ActivityNotFoundException) {
                startActivity(Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=$appPackageName")))
            }
        }

        binding.shareApp.setOnClickListener {
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(
                Intent.EXTRA_TEXT,
                "Motii!!. Top cars. Top cars selling app. Download now: https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID
            )
            sendIntent.type = "text/plain"
            startActivity(sendIntent)
        }


        binding.feedBack.setOnClickListener {
            findNavController().navigate(R.id.contactUsFragment)
        }
        binding.logout.setOnClickListener {
            val session: SharedPreferences = requireActivity().getSharedPreferences(
                "privatePreferenceName",
                Context.MODE_PRIVATE
            )
            session.edit().clear().apply()
            Firebase.auth.signOut()
            findNavController().popBackStack()
        }



        return view
    }

    @SuppressLint("SetTextI18n")
    private fun updateUserProfileUI() {

        if (session.getUserPicture() !== "noUserPicture"){
            binding.imageProgressBar.visibility = View.VISIBLE
            Glide.with(requireContext())
                .load(session.getUserPicture())
                .listener(object : RequestListener<Drawable?> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable?>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding.imageProgressBar.visibility = View.GONE
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable?>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding.imageProgressBar.visibility = View.GONE
                        return false
                    }

                })
                .into(binding.userImage)
        }


        binding.userName.text = session.getUserName()
        binding.userProfile.setOnClickListener {
            findNavController().navigate(R.id.editUserProfileFragment)
        }

        if(session.getUserFcmToken() !== "noUserFcmToken"){
            //Toast.makeText(requireContext(),session.getUserFcmToken(), Toast.LENGTH_LONG).show()
//            viewModel = ViewModelProvider(this, factory).get(UserViewModel::class.java)
//            val token = Token(session.getUserFcmToken().toString())
//            viewModel.userFcmToken(session.getSession().toString(),token, requireContext())
//            viewModel.responseGeneralMessage.observe(viewLifecycleOwner, Observer {
//                Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
//            })

            FirebaseMessaging.getInstance().subscribeToTopic("General").addOnCompleteListener { task ->
//                var msg = getString(R.string.msg_subscribed)
//                if (!task.isSuccessful) {
//                    msg = getString(R.string.msg_subscribe_failed)
//                }
                //Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun reviewApp(){
        reviewManager = ReviewManagerFactory.create(requireContext())
        val request = reviewManager.requestReviewFlow()
        request.addOnCompleteListener { request ->
            if (request.isSuccessful) {
                reviewInfo = request.result
                reviewInfo?.let {
                    val flow = reviewManager.launchReviewFlow(requireActivity(),it)
                    flow.addOnSuccessListener {}
                    flow.addOnFailureListener {}
                    flow.addOnCompleteListener {}
                }
            }
            else {
                reviewInfo = null
            }
        }
    }

    }
