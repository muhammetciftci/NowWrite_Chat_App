package com.mtc.nowwrite.ui.fragments.profile

import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.mtc.nowwrite.R
import com.mtc.nowwrite.model.User
import com.mtc.nowwrite.databinding.FragmentProfileBinding
import com.mtc.nowwrite.utils.Resource
import com.mtc.nowwrite.utils.Util
import com.mtc.nowwrite.utils.gone
import com.mtc.nowwrite.utils.show
import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException


@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private lateinit var fragmentBinding: FragmentProfileBinding
    private lateinit var currentUser: User
    private lateinit var currentUserId: String
    private val viewModel by viewModels<ProfileFragmentViewModel>()
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    var selectedPicture: Uri? = null
    var selectedBitmap: Bitmap? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentBinding = FragmentProfileBinding.inflate(inflater, container, false)
        val view: View = fragmentBinding.root

        Util.bottomNavActive(requireActivity())

        currentUserId = viewModel.auth.currentUser!!.uid
        getUserInfoObserve()
        registerLauncher()
        clickListener()


        return view
    }


    private fun clickListener() {
        saveButtonClick()
        editButtonClick()
        backButtonClick()
        imageEditIconClick()
        cancelButtonClick()
        deleteImageClick()
        deleteAccountTextClick()
    }

    private fun saveButtonClick() {
        fragmentBinding.apply {
            saveButtonProfile.setOnClickListener() {
                val userId = currentUserId
                val userName = usernameProfile.text.toString()
                val userEmail = emailEditTextProfile.text.toString()
                val downloadUrl = currentUser!!.downloadUrl
                val user = User(userId, userName, userEmail, downloadUrl)

                if (selectedPicture != null) {
                    viewModel.updateUserFirebase(user, selectedPicture!!)
                } else {
                    viewModel.updateUserFirebase(user)
                }
                editButtonProfile.show()
                deleteProfileImageProfile.show()
                saveButtonProfile.gone()
                cancelButtonProfile.gone()
                editProfileImageProfile.gone()
                newProfileImageProfile.gone()
                imageSwitchIconProfile.gone()
                deleteAccountTextView.gone()
                usernameProfile.isEnabled = false
            }
        }
    }

    private fun editButtonClick() {
        fragmentBinding.apply {
            editButtonProfile.setOnClickListener(View.OnClickListener {
                editButtonProfile.gone()
                deleteProfileImageProfile.gone()
                saveButtonProfile.show()
                cancelButtonProfile.show()
                editProfileImageProfile.show()
                deleteAccountTextView.show()
                usernameProfile.isEnabled = true
            })
        }
    }

    private fun cancelButtonClick() {
        fragmentBinding.apply {
            cancelButtonProfile.setOnClickListener(View.OnClickListener {
                editButtonProfile.show()
                deleteProfileImageProfile.show()
                saveButtonProfile.gone()
                cancelButtonProfile.gone()
                editProfileImageProfile.gone()
                newProfileImageProfile.gone()
                imageSwitchIconProfile.gone()
                deleteAccountTextView.gone()
                selectedPicture = null
                usernameProfile.isEnabled = false
                imageUrlCheck()
                updateUIUserInfo()
            })
        }
    }

    private fun deleteAccountTextClick() {
        fragmentBinding.deleteAccountTextView.setOnClickListener {
            showDeleteDialog()
        }
    }

    private fun deleteImageClick() {
        fragmentBinding.deleteProfileImageProfile.setOnClickListener {
            viewModel.removeUserImage(currentUser)
        }
    }


    private fun imageEditIconClick() {
        fragmentBinding.editProfileImageProfile.setOnClickListener() { view ->
            permissionCheck(view)
        }
    }

    private fun backButtonClick() {
        fragmentBinding.backButtonProfile.setOnClickListener(View.OnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_usersFragment)
        })
    }

    private fun showDeleteDialog() {
        val builder = AlertDialog.Builder(this@ProfileFragment.requireContext())
        val dialog = builder.create()
        dialog.show()
        dialog.setContentView(R.layout.delete_dialog)
        val yesButton = dialog.findViewById(R.id.yesButton_DeleteDialog) as Button
        val noButton = dialog.findViewById(R.id.noButton_DeleteDialog) as Button
        yesButton.setOnClickListener() {
            viewModel.deleteAccount(currentUser)
            findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
            viewModel.auth.signOut()
            Util.toastMessage(requireContext(), "your account has been deleted")
            dialog.dismiss()
        }
        noButton.setOnClickListener() {
            dialog.dismiss()
        }
    }

    private fun getUserInfoObserve() {
        viewModel.getUserFirebase(currentUserId)
        viewModel.userInfoState.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Loading -> {}
                is Resource.Failure -> {}
                is Resource.Success -> {
                    currentUser = it.data
                    updateUIUserInfo()
                    imageUrlCheck()
                }
            }
        })
    }

    private fun updateUIUserInfo() {
        fragmentBinding.user = currentUser
    }

    private fun imageUrlCheck() {
        if (currentUser.downloadUrl == "") {
            Glide.with(requireContext()).load(R.drawable.ic_outline_person_outline_24)
                .into(fragmentBinding.profileImageProfile)
        } else {
            Glide.with(requireContext()).load(currentUser.downloadUrl)
                .into(fragmentBinding.profileImageProfile)
        }
    }


    // permission methods
    private fun permissionCheck(view: View) {

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                )
            ) {
                Snackbar.make(view, "Permission needed for gallery", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Give Permission",
                        View.OnClickListener {
                            permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        }).show()
            } else {
                permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        } else {
            val intentToGallery =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            activityResultLauncher.launch(intentToGallery)

        }
    }
    private fun registerLauncher() {
        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val intentFromResult = result.data
                    if (intentFromResult != null) {
                        selectedPicture = intentFromResult.data
                        try {
                            if (Build.VERSION.SDK_INT >= 28) {
                                val source = ImageDecoder.createSource(
                                    requireActivity().contentResolver, selectedPicture!!
                                )
                                selectedBitmap = ImageDecoder.decodeBitmap(source)
                                fragmentBinding.apply {
                                    newProfileImageProfile.show()
                                    imageSwitchIconProfile.show()
                                    newProfileImageProfile.setImageBitmap(selectedBitmap)
                                }
                            } else {
                                selectedBitmap = MediaStore.Images.Media.getBitmap(
                                    requireActivity().contentResolver,
                                    selectedPicture
                                )
                                fragmentBinding.apply {
                                    newProfileImageProfile.show()
                                    imageSwitchIconProfile.show()
                                    newProfileImageProfile.setImageBitmap(selectedBitmap)
                                }
                            }
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { result ->
            if (result) {
                //permission granted
                val intentToGallery =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            } else {
                //permission denied
                Toast.makeText(requireContext(), "Permisson needed!", Toast.LENGTH_LONG).show()
            }
        }
    }

}

