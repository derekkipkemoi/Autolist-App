package org.carlistingapp.autolist.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.navigateUp
import com.google.android.gms.ads.MobileAds
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.activity_home.*
import org.carlistingapp.autolist.R
import org.carlistingapp.autolist.data.db.entities.UserObject
import org.carlistingapp.autolist.databinding.ActivityHomeBinding
import org.carlistingapp.autolist.ui.auth.views.LogInUserFragment
import org.carlistingapp.autolist.utils.Session
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


class HomeActivity : AppCompatActivity(), KodeinAware {
    override val kodein by kodein()
    private val session: Session by instance()
    private lateinit var binding: ActivityHomeBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var drawerLayout: DrawerLayout

    @SuppressLint("PackageManagerGetSignatures", "WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseMessagingService()


        applicationInfo.targetSdkVersion = 14
        // Initialize the Google Mobile Ads SDK
        MobileAds.initialize(this)
        //Get facebook keys
        printKeyHash()
        hashFromSHA1("96:DD:C0:6A:03:64:22:EA:18:A6:FD:67:95:8C:20:D5:7D:48:64:97")
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        setSupportActionBar(toolBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)



        navController = Navigation.findNavController(this, R.id.fragment)
        appBarConfiguration = AppBarConfiguration(navController.graph)



        NavigationUI.setupActionBarWithNavController(this, navController)
        //binding.bottomNavigationView.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.postCarDetailsFragment || destination.id == R.id.editCarFragment
                || destination.id == R.id.settingsFragment || destination.id == R.id.featureCarFragment
                || destination.id == R.id.userCarDetailsFragment || destination.id == R.id.LogInUserFragment
                || destination.id == R.id.registerUserFragment || destination.id == R.id.phoneNumberFragment
                || destination.id == R.id.registerUserPhoneEmailFragment || destination.id == R.id.verifyPhoneCodeFragment
                || destination.id == R.id.editUserProfileFragment || destination.id == R.id.viewImageFragment
                || destination.id == R.id.forgotPasswordFragment || destination.id == R.id.passwordResetFragment
                || destination.id == R.id.passwordResetLinkSentFragment || destination.id == R.id.paymentsFragment
                || destination.id == R.id.termAndConditionsAndPrivacyPolicy || destination.id == R.id.contactUsFragment
            ) {
                binding.bottomNavigation.visibility = View.GONE
            } else {
                binding.bottomNavigation.visibility = View.VISIBLE
            }

            if (destination.id == R.id.carDetailsFragment || destination.id == R.id.userProfilePFragment
                || destination.id == R.id.searchCarFragment || destination.id == R.id.listCarFragment
            ) {
                binding.toolBar.visibility = View.GONE
            } else {
                binding.toolBar.visibility = View.VISIBLE
            }

        }



        if (session.getSession() !== "userLoggedOut") {
            NavigationUI.setupWithNavController(binding.bottomNavigation, navController)
        }

        binding.bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homeDashboardFragment -> {
                    navController.navigate(R.id.homeDashboardFragment)
                }
                R.id.postCarDetailsFragment -> {
                    if (session.getSession() == "userLoggedOut") {
                        val logInUserFragment = LogInUserFragment()
                        logInUserFragment.setStyle(
                            BottomSheetDialogFragment.STYLE_NO_FRAME,
                            0
                        )
                        logInUserFragment.show(supportFragmentManager, "LogInUserFragment")
                    } else {
                        val user = session.get<UserObject>("UserObject")
                            if (user?.phoneNumber?.verified == false ){
                                val bundle = bundleOf("FromPostCar" to "From post car" )
                               navController.navigate(R.id.editUserProfileFragment, bundle)
                            }else{
                                navController.navigate(R.id.postCarDetailsFragment)
                            }


                    }
                }

                R.id.favouriteCarsFragment -> {
                    if (session.getSession() == "userLoggedOut") {
                        val logInUserFragment = LogInUserFragment()
                        logInUserFragment.setStyle(
                            BottomSheetDialogFragment.STYLE_NO_FRAME,
                            0
                        )
                        logInUserFragment.show(supportFragmentManager, "LogInUserFragment")
                    } else {
                        navController.navigate(R.id.favouriteCarsFragment)
                    }
                }

                R.id.userProfilePFragment -> {
                    if (session.getSession() == "userLoggedOut") {
                        val logInUserFragment = LogInUserFragment()
                        logInUserFragment.setStyle(
                            BottomSheetDialogFragment.STYLE_NO_FRAME,
                            0
                        )
                        logInUserFragment.show(supportFragmentManager, "LogInUserFragment")
                    } else {
                        navController.navigate(R.id.userProfilePFragment)
                    }
                }
            }
            false
        }

        binding.bottomNavigation.setOnNavigationItemReselectedListener { item ->
            when (item.itemId) {
                R.id.homeDashboardFragment -> {
                    if (navController.currentDestination?.id == R.id.homeDashboardFragment) {
                        Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show()
                    } else {
                        navController.navigate(R.id.homeDashboardFragment)
                    }

                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)
        val newOverride = Configuration(newBase?.resources?.configuration)
        newOverride.fontScale = 0.8f
        applyOverrideConfiguration(newOverride)
    }


    @SuppressLint("PackageManagerGetSignatures")
    private fun printKeyHash() {
        // Add code to print out the key hash
        try {
            val info: PackageInfo = packageManager.getPackageInfo(
                "org.carlistingapp.autolist",
                PackageManager.GET_SIGNATURES
            )
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT))
            }
        } catch (e: PackageManager.NameNotFoundException) {
        } catch (e: NoSuchAlgorithmException) {
        }
    }

    private fun hashFromSHA1(sha1: String) {
        val arr = sha1.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val byteArr = ByteArray(arr.size)
        for (i in arr.indices) {
            byteArr[i] = Integer.decode("0x" + arr[i]).toByte()
        }
        Log.e("hash : ", Base64.encodeToString(byteArr, Base64.NO_WRAP))
    }

    private fun firebaseMessagingService() {
//        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
//            if (!task.isSuccessful) {
//                //Log.w(TAG, "Fetching FCM registration token failed", task.exception)
//                Toast.makeText(baseContext, "Fetching FCM registration token failed", Toast.LENGTH_SHORT).show()
//                return@OnCompleteListener
//            }
//            val token = task.result
//            session.saveUserFcmToken(token)
//            Log.d("Refreshed token",token)
//        })

    }
}
