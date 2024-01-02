package com.bekircaglar.geminiai.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context.CAMERA_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CaptureRequest
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.provider.MediaStore
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.Surface
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import com.bekircaglar.geminiai.R
import com.bekircaglar.geminiai.databinding.FragmentAskImageAndTextBinding
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream

class AskImageAndTextFragment : Fragment() {

    private lateinit var binding: FragmentAskImageAndTextBinding
    // Get your API key from https://makersuite.google.com/app/apikey
    private val API_KEY = "YOUR_API_KEY"
    private lateinit var permissionLauncher : ActivityResultLauncher<String>
    private lateinit var activityResultLanuncher : ActivityResultLauncher<Intent>
    var imagedata : Uri?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerLauncher()

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAskImageAndTextBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.selectImageImage.setOnClickListener {
            if (Build.VERSION.SDK_INT >= 33){
                if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {

                    if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.READ_MEDIA_IMAGES)){
                        Snackbar.make(view,"Need Permission", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission",View.OnClickListener {
                            permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)

                        }).show()



                    }
                    else {
                        permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)

                    }
                } else {
                    val intenttogallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    activityResultLanuncher.launch(intenttogallery)



                }


            }
            else{
                if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                    if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)){
                        Snackbar.make(view,"Need Permission", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission",View.OnClickListener {
                            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)

                        }).show()



                    }
                    else {
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)

                    }
                } else {
                    val intenttogallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    activityResultLanuncher.launch(intenttogallery)



                }
            }
        }

        binding.goButton.setOnClickListener {
            val question = binding.askEditText.text.toString()
            val generativeModel = GenerativeModel(
                modelName = "gemini-pro-vision",
                apiKey = API_KEY
            )
            val job = CoroutineScope(Dispatchers.IO).launch {
                val inputContent = content {
                    val contentResolver = requireActivity().contentResolver
                    val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imagedata)
                    image(bitmap)
                    text(question)
                }



                val response = generativeModel.generateContent(inputContent)
                binding.answerText.text = response.text

            }

        }

        

        return binding.root
    }
    private fun registerLauncher(){
        activityResultLanuncher =  registerForActivityResult(ActivityResultContracts.StartActivityForResult(),){ result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK){
                val intentfromresult = result.data
                if (intentfromresult != null){

                    imagedata = intentfromresult.data
                    imagedata?.let {
                        binding.selectedImage.setImageURI(it)

                    }

                }
            }

        }


        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){ result ->
            if (result){
                val intenttogallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLanuncher.launch(intenttogallery)

            }
            else{
                Toast.makeText(requireContext(),"Need Permission", Toast.LENGTH_LONG)
            }

        }



    }

}