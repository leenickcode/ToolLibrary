package com.example.mlkitbarcode

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.media.Image
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.io.File
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CameraFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CameraFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
   private lateinit var  viewFinder:PreviewView

    private var imageCapture: ImageCapture? = null

    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService
    val options = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(
            Barcode.FORMAT_QR_CODE,
            Barcode.FORMAT_AZTEC)
        .build()
    // [END set_detector_options]

    // [START get_detector]
    var scanner = BarcodeScanning.getClient(options)
    private var scannerIsClose=false
    val mHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(@NonNull msg: Message) {

            when (msg.what) {
                MSG_START_SCAN -> {
                    Log.d(TAG, "handleMessage: ")
                    scanner =  BarcodeScanning.getClient(options)
                    scannerIsClose=false
                }
                MSG_UPDATE_HISTORY->{
//                    mCurrentQrCodeResp?.let {
//                        updateHistory(it)
//                    }
                }
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_camera, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewFinder= view.findViewById(R.id.viewFinder)

        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(), REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        // Set up the listener for take photo button
//        binding.cameraCaptureButton.setOnClickListener { takePhoto() }

        outputDirectory = getOutputDirectory()

        cameraExecutor = Executors.newSingleThreadExecutor()


    }
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(requireActivity(),
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT).show()
                requireActivity().finish()
            }
        }
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener(Runnable {
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewFinder.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder()
                .build()

            val imageAnalyzer = ImageAnalysis.Builder()
                .build()
                .also {
//                    it.setAnalyzer(cameraExecutor, YourImageAnalyzer{image, mediaImage, imageProxy ->
////                        Log.d(TAG, "startCamera: 分析")
//                        scanBarcodes(image,mediaImage,imageProxy)
//
//                    })
                    it.setAnalyzer(cameraExecutor, ImageAnalysis.Analyzer {imageProxy->

                        if (scannerIsClose){
                            imageProxy.close()
                            return@Analyzer
                        }

                        val mediaImage = imageProxy.image
                        Log.d(TAG, "startCamera: "+mediaImage)
                        if (mediaImage != null) {
                            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                            // Pass image to an ML Kit Vision API
                            // ...

                            scanner.process(image)
                                .addOnSuccessListener { barcodes ->
                                    // Task completed successfully
                                    // [START_EXCLUDE]
                                    // [START get_barcodes]
                                    Log.d(TAG, "scanBarcodes: aaa")
                                    for (barcode in barcodes) {
                                        val bounds = barcode.boundingBox
                                        val corners = barcode.cornerPoints

                                        val rawValue = barcode.rawValue
                                        Log.d(TAG, "scanBarcodes值: "+rawValue)
                                        scanner.close()
                                        scannerIsClose=true

                                        mHandler.sendEmptyMessageDelayed(MSG_START_SCAN,3000)
                                        val valueType = barcode.valueType
                                        // See API reference for complete list of supported types
                                        when (valueType) {
                                            Barcode.TYPE_WIFI -> {
                                                val ssid = barcode.wifi!!.ssid
                                                val password = barcode.wifi!!.password
                                                val type = barcode.wifi!!.encryptionType
                                            }
                                            Barcode.TYPE_URL -> {
                                                val title = barcode.url!!.title
                                                val url = barcode.url!!.url
                                            }
                                        }
                                    }

                                    // [END get_barcodes]
                                    // [END_EXCLUDE]
                                }
                                .addOnFailureListener {
                                    // Task failed with an exception
                                    // ...
                                    it.printStackTrace()
                                    Log.d(TAG, "scanBarcodes: "+it.message)
                                }.addOnCompleteListener{
                                    mediaImage.close()
                                    imageProxy.close()
                                }
                        }
                    })
                }

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    viewLifecycleOwner, cameraSelector, preview, imageCapture,imageAnalyzer)

            } catch(exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    private fun getOutputDirectory(): File {
        val mediaDir = requireActivity().externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() } }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else requireActivity().filesDir
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CameraFragment.
         */
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CameraFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
        private const val MSG_START_SCAN = 2
        const val MSG_UPDATE_HISTORY=1
        private const val TAG = "CameraFragment"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }

}
