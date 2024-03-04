package com.greildev.erdmovee.ui.prelogin

import android.Manifest
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.greildev.core.base.BaseFragment
import com.greildev.core.utils.UIState
import com.greildev.erdmovee.BuildConfig
import com.greildev.erdmovee.R
import com.greildev.erdmovee.databinding.FragmentProfileBinding
import com.greildev.erdmovee.ui.component.MoveeSnackbar
import com.greildev.erdmovee.ui.component.StateSnackbar
import com.greildev.erdmovee.utils.Analytics
import com.greildev.erdmovee.utils.Constant
import com.greildev.erdmovee.utils.ImageUtils.createTempFile
import com.greildev.erdmovee.utils.ImageUtils.reduceFileImage
import com.greildev.erdmovee.utils.ImageUtils.uriToFile
import com.greildev.erdmovee.utils.launchAndCollectIn
import com.greildev.erdmovee.utils.onCreated
import com.greildev.erdmovee.utils.onValue
import com.greildev.erdmovee.utils.tncText
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class ProfileFragment :
    BaseFragment<FragmentProfileBinding, PreloginViewModel>(FragmentProfileBinding::inflate) {
    private var getFile: File? = null

    override val viewModel: PreloginViewModel by viewModels()

    private lateinit var currentPhotoPath: String

    private val intentImageGaleri =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                val data: Uri = it.data?.data as Uri
                val result = context?.let { context -> uriToFile(data, context) }
                getFile = result
                binding.ivProfile.setImageURI(data)
                binding.ivProfile.scaleType = ImageView.ScaleType.CENTER_CROP
            }
        }

    private val intentImageKamera =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                val data = File(currentPhotoPath)
                val result = BitmapFactory.decodeFile(data.path)
                getFile = data
                binding.ivProfile.setImageBitmap(result)
                binding.ivProfile.scaleType = ImageView.ScaleType.CENTER_CROP
            }
        }


    private val requestCameraPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            openCamera()
        } else {
            Toast.makeText(context, TOAST_PERMISSION_DENIED_MESSAGE, Toast.LENGTH_SHORT).show()
        }
    }

    private val requestGalleryPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            openGallery()
        } else {
            Toast.makeText(context, TOAST_PERMISSION_DENIED_MESSAGE, Toast.LENGTH_SHORT).show()
        }
    }

    override fun initView() {
        binding.loading.visibility = View.GONE
        binding.tvTnc.movementMethod = LinkMovementMethod.getInstance()
        binding.tvTnc.text = context?.let {
            resources.getString(R.string.tnc_auth)
                .tncText(it, resources.configuration.locales[0].language)
        }
    }

    override fun observeData() {
        viewModel.userProfile.launchAndCollectIn(viewLifecycleOwner) {
            binding.apply {
                when (it) {
                    is UIState.Loading -> {
                        loading.isVisible = true
                    }

                    is UIState.Error -> {
                        loading.isVisible = false
                        context?.let { it1 ->
                            MoveeSnackbar.showSnackbarCustom(
                                context = it1,
                                root = root,
                                text = it.message.toString(),
                                state = StateSnackbar.ERROR,
                                action = {}
                            )
                        }
                    }

                    is UIState.Success -> {
                        loading.isVisible = false
                        context?.let { it1 ->
                            MoveeSnackbar.showSnackbarCustom(
                                context = it1,
                                root = root,
                                text = getString(R.string.selamat_datang, it.data),
                                state = StateSnackbar.SUCCESS
                            ) {
                                binding.loading.cancelAnimation()
                                val logBundle = Bundle()
                                logBundle.putString("username", binding.tifUsername.text.toString())
                                Analytics.logEvent(Constant.UPDATE_PROFILE, logBundle)
                                findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToHomePageFragment())
                            }
                        }
                    }

                    else -> {}
                }
            }
        }
    }

    override fun initListener() {
        super.initListener()
        binding.ivProfile.setOnClickListener {
            showPickImageDialog()
        }

        binding.btnDone.setOnClickListener {
            val username = binding.tifUsername.text.toString().trim()
            viewModel.validateProfileName(username)
            viewModel.validateProfileName.launchAndCollectIn(viewLifecycleOwner) { state ->
                state.onCreated { }
                    .onValue {
                        if (!it) {
                            binding.tilUsername.isErrorEnabled = true
                            binding.tilUsername.error = getString(R.string.field_tidak_boleh_kosong)
                            context?.let { it1 ->
                                MoveeSnackbar.showSnackbarCustom(
                                    it1,
                                    binding.root,
                                    getString(R.string.field_tidak_boleh_kosong),
                                    StateSnackbar.ERROR
                                ) {}
                            }
                        } else {
                            setProfileData(username)
                        }
                    }
            }

        }
    }

    private fun setProfileData(username: String) {
        if (getFile != null) {
            val file = reduceFileImage(getFile as File)
            viewModel.updateProfile(username = username, photo = file)
        } else {
            showConfirmationDialog(username)
        }
    }

    private fun showPickImageDialog() {

        val dialog = context?.let { MaterialAlertDialogBuilder(it) }

        dialog?.setTitle(getString(R.string.pilih_gambar))
        val items = arrayOf(getString(R.string.galeri), getString(R.string.kamera))
        dialog?.setItems(items) { _, which ->
            when (which) {
                0 -> {
                    checkGalleryPermission()
                }

                1 -> {
                    checkCameraPermission()
                }
            }
        }
        dialog?.show()
    }

    private fun showConfirmationDialog(username: String) {
        AlertDialog.Builder(context)
            .setTitle(getString(R.string.logout_title_dialog))
            .setMessage(getString(R.string.profile_confirmation_message))
            .setPositiveButton(getString(R.string.ya)) { _, _ ->
                viewModel.updateProfile(photo = null, username = username)
            }
            .setNegativeButton(getString(R.string.no)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }




    private fun checkCameraPermission() {
        if (context?.let {
                ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.CAMERA
                )
            } == PackageManager.PERMISSION_GRANTED
        ) {
            openCamera()
        } else {
            requestCameraPermission.launch(Manifest.permission.CAMERA)
        }
    }

    private fun checkGalleryPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (context?.let {
                    ContextCompat.checkSelfPermission(
                        it,
                        Manifest.permission.READ_MEDIA_IMAGES
                    )
                } == PackageManager.PERMISSION_GRANTED
            ) {
                openGallery()
            } else {
                requestGalleryPermission.launch(Manifest.permission.READ_MEDIA_IMAGES)
            }
        } else {
            if (context?.let {
                    ContextCompat.checkSelfPermission(
                        it,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                } == PackageManager.PERMISSION_GRANTED
            ) {
                openGallery()
            } else {
                requestGalleryPermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = FILETYPE
        intentImageGaleri.launch(intent)
    }


    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        activity?.packageManager?.let {
            intent.resolveActivity(it)
        }

        activity?.applicationContext?.let { appContext ->
            createTempFile(appContext).also {
                val photoURI: Uri? = context?.let { it1 ->
                    FileProvider.getUriForFile(
                        it1,
                        BuildConfig.APPLICATION_ID,
                        it
                    )
                }
                currentPhotoPath = it.absolutePath
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                intentImageKamera.launch(intent)
            }
        }
    }

    companion object {
        const val FILETYPE = "image/*"
        const val TOAST_PERMISSION_DENIED_MESSAGE = "Permission Denied"
    }
}