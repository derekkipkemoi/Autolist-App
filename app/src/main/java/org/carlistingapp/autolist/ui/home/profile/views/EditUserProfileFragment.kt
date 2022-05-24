package org.carlistingapp.autolist.ui.home.profile.views

import android.annotation.SuppressLint
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.exifinterface.media.ExifInterface
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePicker
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.carlistingapp.autolist.R
import org.carlistingapp.autolist.data.db.entities.NameUpdate
import org.carlistingapp.autolist.data.db.entities.UserObject
import org.carlistingapp.autolist.data.network.ListingCarsAPI
import org.carlistingapp.autolist.data.network.NetworkConnectionInterceptor
import org.carlistingapp.autolist.data.repositories.UserRepository
import org.carlistingapp.autolist.databinding.FragmentEditUserProfileBinding
import org.carlistingapp.autolist.ui.home.profile.viewModel.UserViewModel
import org.carlistingapp.autolist.ui.home.profile.viewModel.UserViewModelFactory
import org.carlistingapp.autolist.utils.ImageResizer
import org.carlistingapp.autolist.utils.Session
import org.carlistingapp.autolist.utils.getFileName
import org.carlistingapp.autolist.utils.snackBar
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.regex.Matcher
import java.util.regex.Pattern


class EditUserProfileFragment : Fragment(), KodeinAware{
    override val kodein by kodein()
    private val networkConnectionInterceptor : NetworkConnectionInterceptor by instance()
    val api : ListingCarsAPI by instance()
    val repository : UserRepository by instance()
    val factory: UserViewModelFactory by instance()
    private val session : Session by instance()
    private lateinit var viewModel: UserViewModel
    private lateinit var binding : FragmentEditUserProfileBinding
    private lateinit var userInitialName : String
    private lateinit var userName : String
    private val imageResizer : ImageResizer by instance()

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =  DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_edit_user_profile,
            container,
            false
        )
        val view = binding.root
        viewModel = ViewModelProvider(this, factory).get(UserViewModel::class.java)

        val fromPostCar = arguments?.getString("FromPostCar").toString()



        val user = session.get<UserObject>("UserObject")
        if (session.getUserPicture() !== "noUserPicture"){

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

        userName = session.getUserName().toString()

        val parts  = userName.split(" ").toMutableList()
        val firstName = parts.firstOrNull()
        parts.removeAt(0)
        val lastName = parts.joinToString(" ")

        binding.textViewFirstName.text = Editable.Factory.getInstance().newEditable(
            firstName
        )

        binding.textViewLastName.text = Editable.Factory.getInstance().newEditable(
            lastName
        )

        binding.buttonAdd.setOnClickListener {
            ImagePicker.with(this)
                .setFolderMode(true)
                .setFolderTitle("Album")
                .setDirectoryName("Image picker")
                .setMultipleMode(true)
                .setShowNumberIndicator(true)
                .setMaxSize(1)
                .setLimitMessage("You can select 1 image only")
                .setRequestCode(USER_IMAGE_PICKER_CODE)
                .start()
        }

        if(session.getUserEmail()?.isEmpty()!!){
            binding.emailTextInputLayout.visibility = View.GONE
        }else{
            binding.textViewEmail.text =  Editable.Factory.getInstance().newEditable(
                session.getUserEmail()
            )
            binding.textViewEmail.isEnabled = false
        }

        binding.buttonUpdate.setOnClickListener {
            updateUserProfile()
        }

        binding.updateGif.setOnClickListener {
            val phoneNumberFragment = PhoneNumberFragment()
            phoneNumberFragment.show(requireActivity().supportFragmentManager,"phoneNumberFragment")
        }

        binding.editPhone.setOnClickListener {
            val phoneNumberFragment = PhoneNumberFragment()
            phoneNumberFragment.show(requireActivity().supportFragmentManager,"phoneNumberFragment")
        }


        if (user?.phoneNumber?.verified == false ){
            if (fromPostCar == "From post car"){
                binding.updateGif.visibility = View.VISIBLE
                binding.toContinue.visibility = View.VISIBLE
            }else{
                binding.editPhone.visibility = View.VISIBLE
                binding.editPhone.text = "Add +"
            }
        }
        else{
            binding.editPhone.visibility = View.VISIBLE
            binding.textViewPhone.text = Editable.Factory.getInstance().newEditable(
                "0".plus(user?.phoneNumber?.number)
            )
        }

        binding.textViewPhone.isEnabled = false
        return view
    }

    companion object{
        private const val USER_IMAGE_PICKER_CODE = 100
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK ){
            when (requestCode){
                USER_IMAGE_PICKER_CODE -> {
                    val alertDialog: AlertDialog? = activity?.let {
                        val builder = AlertDialog.Builder(it)
                        builder.apply {
                            setPositiveButton("YES",
                                DialogInterface.OnClickListener { dialog, id ->
                                    binding.imageProgressBar.visibility = View.VISIBLE
                                    viewModel = ViewModelProvider(requireActivity(), factory).get(
                                        UserViewModel::class.java
                                    )
                                    val images = ImagePicker.getImages(data)
                                    val selectedImage = images[0].uri


                                    val imageStream =
                                        requireContext().contentResolver.openInputStream(
                                            selectedImage
                                        )
                                    val fullSizeBitmap = BitmapFactory.decodeStream(imageStream)
                                    val reducedImageBitMap = imageResizer.reduceBitmapSize(
                                        fullSizeBitmap,
                                        614400
                                    )
                                    val reducedImageFile = getFileFromBitmap(
                                        reducedImageBitMap,
                                        selectedImage
                                    )
                                    val body = RequestBody.create(
                                        MediaType.parse("image/*"),
                                        reducedImageFile
                                    )
                                    val multipartBody = MultipartBody.Part.createFormData(
                                        "photos",
                                        reducedImageFile.name,
                                        body
                                    )
                                    viewModel.updateProfileImage(
                                        multipartBody,
                                        session.getSession(),
                                        requireContext()
                                    )
                                    viewModel.updateImageResponse.observe(
                                        viewLifecycleOwner,
                                        Observer { imageUpdateResponse ->
                                            session.saveUserPicture(imageUpdateResponse.imageUrl)
                                            Glide.with(requireContext())
                                                .load(imageUpdateResponse.imageUrl)
                                                .listener(object : RequestListener<Drawable?> {
                                                    override fun onLoadFailed(
                                                        e: GlideException?,
                                                        model: Any?,
                                                        target: Target<Drawable?>?,
                                                        isFirstResource: Boolean
                                                    ): Boolean {
                                                        binding.imageProgressBar.visibility =
                                                            View.GONE
                                                        return false
                                                    }

                                                    override fun onResourceReady(
                                                        resource: Drawable?,
                                                        model: Any?,
                                                        target: Target<Drawable?>?,
                                                        dataSource: DataSource?,
                                                        isFirstResource: Boolean
                                                    ): Boolean {
                                                        binding.imageProgressBar.visibility =
                                                            View.GONE
                                                        return false
                                                    }

                                                })
                                                .into(binding.userImage)

                                            dialog.dismiss()
                                        })
                                    binding.userImage.setImageURI(selectedImage)
                                })
                            setNegativeButton("NO",
                                DialogInterface.OnClickListener { dialog, id ->
                                    dialog.dismiss()
                                })
                        }
                        builder.setTitle("Update profile image ?")
                        builder.create()
                    }
                    alertDialog?.show()
                }
            }
        }
    }

    private fun updateUserProfile() {
        binding.firstNameTextInputLayout.error = null
        binding.lastNameTextInputLayout.error = null
        val firstName = binding.textViewFirstName.text?.trim().toString()
        val lastName = binding.textViewLastName.text?.trim().toString()

        val regx = "^[A-Za-z\\s]+[.]?[A-Za-z\\s]*\$"
        val pattern = Pattern.compile(regx, Pattern.CASE_INSENSITIVE)
        val firstNameMatcher: Matcher = pattern.matcher(firstName)
        val lastNameMatcher: Matcher = pattern.matcher(lastName)

        if (!firstNameMatcher.matches()) {
            binding.firstNameTextInputLayout.error = "Valid first name required!!"
            return
        }
        if (!lastNameMatcher.matches()) {
            binding.lastNameTextInputLayout.error = "Valid last name required!!"
            return
        }
        val name = firstName.plus(" ").plus(lastName)

        if (name == userName){
            binding.root.snackBar("You cant update the same name")
            return
        }

        val userNameUpdate = NameUpdate(name)
        binding.progressBar.visibility = View.VISIBLE
        viewModel.updateUserName(
            userNameUpdate,
            session.getSession()!!,
            requireContext()
        )
        viewModel.updateName.observe(viewLifecycleOwner, Observer { updateName ->
            binding.progressBar.visibility = View.INVISIBLE
            Toast.makeText(requireContext(), updateName.message, Toast.LENGTH_LONG).show()
            session.saveUserName(name)
            findNavController().popBackStack()
        })

    }

    private fun getFileFromBitmap(reducedBitmap: Bitmap?, imageFile: Uri) : File {
        val file = File(
            requireContext().cacheDir, requireContext().contentResolver.getFileName(
                imageFile
            )
        )

        val inputFile = requireContext().contentResolver.openInputStream(imageFile);
        val exif = ExifInterface(inputFile!!)
        val rotation = exif.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )

        val rotationInDegrees = imageResizer.exifToDegrees(rotation)


        val matrix = Matrix()
        if (rotation != 0) {
            matrix.preRotate(rotationInDegrees.toFloat())
        }

        val adjustedBitmap = Bitmap.createBitmap(
            reducedBitmap!!,
            0,
            0,
            reducedBitmap.width,
            reducedBitmap.height,
            matrix,
            true
        )
  val bitmapOutputStream = ByteArrayOutputStream()
        adjustedBitmap?.compress(Bitmap.CompressFormat.JPEG, 50, bitmapOutputStream)
        val bitmapData = bitmapOutputStream.toByteArray()
        val fileOutputStream = FileOutputStream(file)
        fileOutputStream.write(bitmapData)
        fileOutputStream.flush()
        fileOutputStream.close()
        return file
    }


}