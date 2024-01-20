package com.example.movieapp.uimodules.uploadmovies

import android.Manifest
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.widget.ListPopupWindow
import androidx.core.content.FileProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.example.movieapp.BR
import com.example.movieapp.R
import com.example.movieapp.base.BaseFragment
import com.example.movieapp.data.api.UploadMovieResponse
import com.example.movieapp.databinding.FragmentUploadMoviesBinding
import com.example.movieapp.datamanager.PreferenceConnector
import com.example.movieapp.datamanager.PreferenceManager
import com.example.movieapp.helper.NetworkResponse
import com.example.movieapp.uimodules.MainActivity
import com.example.movieapp.utils.DialogUtil
import com.example.movieapp.utils.Utils
import com.example.movieapp.utils.compressor.FileUtil
import com.example.movieapp.utils.compressor.ImageCompressor
import com.example.movieapp.utils.compressor.createImageFile
import com.example.movieapp.utils.permission.PermissionManager
import com.example.movieapp.utils.permission.PermissionResult
import com.example.movieapp.viewmodels.UploadMovieViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import javax.inject.Inject


@AndroidEntryPoint
class UploadMoviesFragment : BaseFragment<FragmentUploadMoviesBinding, UploadMovieViewModel>(),
    UploadMovieNavigator, View.OnClickListener {

    private val mViewModel: UploadMovieViewModel by viewModels()
    private lateinit var mBinding: FragmentUploadMoviesBinding
    private lateinit var mainActivity: MainActivity
    private val listPopupView by lazy { activity?.let { ListPopupWindow(it) } }
    private var isDialogShown = false
    private var imageUri: Uri? = null
    private var mPermissionDialog: Dialog? = null
    private var pictureCaptureFailureDialog: Dialog? = null
    private var photoFile: File? = null
    private var compressedFilePath: String? = null
    private var dialog: Dialog? = null

    @Inject
    lateinit var preferenceManager: PreferenceManager

    companion object {
        private const val SELECT_PICTURE = 1
        private const val CAPTURE_PICTURE = 2
        private const val ACTIVITY_RESULT_CAMERA_PERMISSION = 3
        private const val REQUEST_TAKE_PHOTO = 4
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setDataBinding(inflater, container)
        mBinding = getViewDataBinding()
        mViewModel.setNavigator(this)
        return mBinding.root

    }

    override fun onResume() {
        super.onResume()
        mainActivity.supportActionBar?.title = activity?.resources?.getString(R.string.add_movie)
        mainActivity.showUpButton(true)
    }

    override fun onPause() {
        super.onPause()
        mainActivity.supportActionBar?.title = ""
        mainActivity.hideUpButton()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        mViewModel.addMovieResponse.observe(viewLifecycleOwner) {
            dismissProgress()
            if (it != null) {
                if (it.data?.success == true) {
                    it.data.message?.let { it1 -> showSuccessModal(it1) }
                } else {
                    it.data?.message?.let { it1 ->
                        showErrorDialog(
                            it1,
                            activity?.resources?.getString(R.string.error)
                        )
                    }
                }
            }
        }
    }

    private fun showSuccessModal(message: String) {
        if (dialog == null || activity != null && !activity?.isFinishing!!) {
            dialog = DialogUtil.genericAlertDialog(
                this.context, activity?.resources?.getString(R.string.success),
                message, activity?.resources?.getString(R.string.ok)
            ) {
                dialog?.dismiss()
                dialog = null
                popPreviousFragment()
            }
            dialog?.show()
        }
    }

    private fun initView() {
        loadListPopUpView()

        val category = preferenceManager.readString(PreferenceConnector.CATEGORY_NAME, "")
        mBinding.tvCategoryName.text = category

        mBinding.tvSelectedRating.setOnClickListener(this)
        mBinding.tvChooseImage.setOnClickListener(this)
        mBinding.tvRemove.setOnClickListener(this)
        mBinding.btnCancel.setOnClickListener(this)
        mBinding.btnConfirm.setOnClickListener(this)
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_upload_movies
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getViewModel(): UploadMovieViewModel {
        return mViewModel
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tvSelectedRating -> {
                if (!isDialogShown) {
                    openPopUi()
                } else {
                    closePopUi()
                }
            }
            R.id.tvChooseImage -> {
                mBinding.etMovieTitle.clearFocus()
                mBinding.etMovieTitle.clearFocus()
                activity?.let { Utils.hideKeyboard(it) }
                showDialogPicker()
            }

            R.id.tvRemove -> {
                mBinding.ivMovieIcon.setImageDrawable(null)
                updateRemoveUiVisibility(0.4F)
            }
            R.id.btnCancel -> {
                popPreviousFragment()
            }
            R.id.btnConfirm -> {
                validateUiElements()
            }
        }
    }

    private fun popPreviousFragment() {
        if (view != null) {
            Navigation.findNavController(requireView()).popBackStack()
        }
        onDetach()
    }

    private fun validateUiElements() {
        if (activity?.let { Utils.isNetworkConnected(it) } == true) {
            val categoryId = preferenceManager.readInt(PreferenceConnector.CATEGORY_ID, 0)
            categoryId?.let { id ->
                if (mBinding.etMovieTitle.text.trim()
                        .isNotEmpty() && mBinding.tvSelectedRating.text.trim().isNotEmpty()
                    && mBinding.etMovieDescription.text.trim()
                        .isNotEmpty() && !compressedFilePath?.trim().isNullOrEmpty()
                ) {

                    mViewModel.uploadMovie(
                        id,
                        mBinding.etMovieTitle.text.toString(),
                        mBinding.tvSelectedRating.text.toString(),
                        mBinding.etMovieDescription.text.toString(),
                        compressedFilePath!!
                    )
                } else {
                    Toast.makeText(
                        activity,
                        activity?.resources?.getString(R.string.please_fill_all_fields),
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
        } else {
            activity?.resources?.let {
                showErrorDialog(
                    it.getString(R.string.no_internet),
                    ""
                )
            }
        }
    }

    private fun updateRemoveUiVisibility(alpha: Float) {
        mBinding.tvRemove.alpha = alpha
        mBinding.tvRemove.isEnabled = alpha == 1F
    }


    private fun showDialogPicker() {
        val optionsMenu = activity?.resources?.getStringArray(R.array.options) as Array
        val builder = AlertDialog.Builder(context)
        builder.setItems(optionsMenu) { dialogInterface, i ->
            if (optionsMenu[i] == activity?.resources?.getStringArray(R.array.options)!![0]) {
                // Open the camera and get the photo
                this.checkCameraPermission()
            } else if (optionsMenu[i] == activity?.resources?.getStringArray(R.array.options)!![1]) {
                // choose from  external storage
                pickImageFromGallery()
            } else if (optionsMenu[i] == activity?.resources?.getStringArray(R.array.options)!![2]) {
                dialogInterface.dismiss()
            }
        }
        builder.show()
    }

    private fun pickImageFromGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent, getString(R.string.choose_image)),
            SELECT_PICTURE
        )
    }

    private fun checkCameraPermission() {
        lifecycleScope.launch {
            when (PermissionManager.requestPermissions(
                requireActivity(),
                CAPTURE_PICTURE,
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )) {
                is PermissionResult.PermissionGranted -> {
                    dispatchTakePictureIntent()
                }
                is PermissionResult.PermissionDenied -> {
                }
                is PermissionResult.PermissionDeniedPermanently -> {
                    showPermissionRequiredDialog()
                }
                is PermissionResult.ShowRational -> {
                    showPermissionRequiredDialog()
                }
            }
        }
    }

    private fun dispatchTakePictureIntent() {
        if (activity != null) {
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                // Ensure that there's a camera activity to handle the intent
                takePictureIntent.resolveActivity(requireActivity().packageManager)?.also {
                    // Create the File where the photo should go
                    photoFile = try {
                        createImageFile(requireActivity())
                    } catch (ex: IOException) {
                        null
                    }
                    Log.d(
                        "UploadMoviesFragment",
                        "dispatchTakePictureIntent : photoFile : $photoFile, \n absolute path : ${photoFile?.absolutePath} "
                    )
                    // Continue only if the File was successfully created
                    photoFile?.also {
                        imageUri = FileProvider.getUriForFile(
                            requireContext(),
                            mainActivity.applicationContext.packageName + ".provider",
                            it
                        )
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                        startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
                    }
                }
            }
        }
    }

    private fun showPermissionRequiredDialog() {
        if (mPermissionDialog == null && activity != null && !requireActivity().isFinishing) {
            mPermissionDialog = DialogUtil.getErrorDialogAccessDialog(requireActivity(),
                resources.getString(R.string.contact_permission_required_title),
                resources.getString(R.string.camera_permission_required),
                resources.getString(R.string.allow),
                {
                    mPermissionDialog?.dismiss()
                    mPermissionDialog = null
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", requireActivity().packageName, null)
                    intent.data = uri
                    startActivityForResult(intent, ACTIVITY_RESULT_CAMERA_PERMISSION)
                },
                resources.getString(R.string.deny),
                {
                    mPermissionDialog?.dismiss()
                    mPermissionDialog = null
                })
            mPermissionDialog?.show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            SELECT_PICTURE -> {
                if (resultCode == RESULT_OK) {
                    if (data == null) {
                        return
                    } else {
                        Log.d("UploadMoviesFragment", "onActivityResult : data.data : ${data.data}")
                        imageUri = data.data
                        setImage(imageUri!!)
                    }
                }
            }
            ACTIVITY_RESULT_CAMERA_PERMISSION -> {
                if (resultCode == RESULT_OK) {
                    dispatchTakePictureIntent()
                }
            }
            REQUEST_TAKE_PHOTO -> {
                Log.d("UploadMoviesFragment", "REQUEST_TAKE_PHOTO : data : $imageUri")
                if (resultCode == RESULT_OK) {
                    if (imageUri != null && imageUri?.toString()?.isNotEmpty() == true) {
                        setImage(imageUri!!)
                    } else {
                        showPictureCaptureFailureDialog()
                    }
                }
            }

        }
    }

    private fun setImage(uri: Uri) {
        FileUtil.from(requireActivity(), uri).also {
            lifecycleScope.launch {
                val imageCompressorTask = ImageCompressor(requireActivity(), it)
                imageCompressorTask.execute()
                imageCompressorTask.getCompressedImagePath().observe(viewLifecycleOwner) {
                    val file = it.path?.let { it1 -> File(it1) }
                    Log.d("UploadMoviesFragment", "Compressed URI :  ${file?.absolutePath}")
                    compressedFilePath = it.toString()
                    mBinding.ivMovieIcon.setImageURI(it)
                    updateRemoveUiVisibility(1F)
                }

            }
        }
    }


    private fun showPictureCaptureFailureDialog() {
        if (pictureCaptureFailureDialog == null && activity != null && !activity?.isFinishing!!) {
            pictureCaptureFailureDialog = DialogUtil.genericAlertDialog(
                activity,
                activity?.resources?.getString(R.string.failure_capturing_image_title),
                activity?.resources?.getString(R.string.failure_capturing_image_message),
                activity?.resources?.getString(R.string.ok)
            ) {
                pictureCaptureFailureDialog?.dismiss()
                pictureCaptureFailureDialog = null
            }
        }
    }


    private fun closePopUi() {
        isDialogShown = false
        mBinding.ivDropArrow.animate().rotation(0F).setDuration(200).start()
        listPopupView?.dismiss()
    }

    private fun openPopUi() {
        isDialogShown = true
        listPopupView?.show()
        mBinding.ivDropArrow.animate().rotation(180F).setDuration(200).start()
    }


    private fun loadListPopUpView() {
        val dataList = activity?.resources?.getStringArray(R.array.ratings) as Array
        listPopupView?.setAdapter(activity?.let {
            ArrayAdapter(
                it,
                android.R.layout.simple_list_item_1,
                dataList
            )
        })
        listPopupView?.setOnItemClickListener { _, _, position, _ ->
            mBinding.tvSelectedRating.text = dataList[position]
            closePopUi()
        }
        listPopupView?.anchorView = mBinding.tvSelectedRating
    }

    override fun showErrorResponse(result: NetworkResponse.Error<UploadMovieResponse>) {
        dismissProgress()
        result.message?.let { showErrorDialog(it, activity?.resources?.getString(R.string.error)) }
    }

    override fun showProgress() {
        showProgress(resources.getString(R.string.progress_loading_text))
    }
}