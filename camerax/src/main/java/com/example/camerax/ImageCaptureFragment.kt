package com.example.camerax

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class ImageCaptureFragment : Fragment() {
    lateinit var previewView: PreviewView

    /**
     * 拍照按钮
     */
    lateinit var btnCapture: Button

    /**
     * 用来拍照的
     */
    private var imageCapture: ImageCapture? = null

    /**
     * 拍照保存的照片位置
     */
    private lateinit var outputDirectory: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_image_capture, container, false)
    }

    private fun getOutputDirectory(): File {
        val mediaDir = requireActivity().externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else requireActivity().filesDir
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        outputDirectory = getOutputDirectory()
        previewView = view.findViewById(R.id.viewFinder)
        btnCapture = view.findViewById(R.id.camera_capture_button)
        btnCapture.setOnClickListener {
            takePhoto()
        }
        startCamera()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener(
            Runnable {
                // Used to bind the lifecycle of cameras to the lifecycle owner
                val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
                // Preview 创建一个用于预览的userCase,并将我们xml的preview设置进去
                val preview = Preview.Builder()
                    .build()
                    .also {
                        it.setSurfaceProvider(previewView.createSurfaceProvider())
                    }
                imageCapture = ImageCapture.Builder()
                    .build()

                // 选择摄像头，只能选前、后两个，如果你的android设备是定制的，可能实际打开的跟这个前后对应不上
                //如果想打开多个摄像头，比如一个RGB摄像头，一个红外摄像头，目前CameraX好像不支持，得用Camera2,
                //如果您发现了CameraX也能打开多个，希望您不吝告知
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                try {
                    // Unbind use cases before rebinding
                    cameraProvider.unbindAll()
                    // 将preview绑定到相机。
                    val camera = cameraProvider.bindToLifecycle(
                        this, cameraSelector, preview, imageCapture
                    )
                    //bindToLifecycle 返回的Camera对象，可以获取cameInfo。查缩放状态，角度等。
                } catch (exc: Exception) {
                    Log.e(TAG, "Use case binding failed", exc)
                }

            },
            //这里只能用UI线程，原因是bindToLifecycle函数必须在UI线程调用。
            ContextCompat.getMainExecutor(requireContext())
        )

    }

    private fun takePhoto() {
        // Get a stable reference of the modifiable image capture use case
        //imageCapture 这个要在开启预览的时候初始并跟相机绑定，否则返回
        val imageCapture = imageCapture ?: return

        // 创建一个文件来保存图片
        val photoFile = File(
            outputDirectory,
            SimpleDateFormat(
                FILENAME_FORMAT, Locale.US
            ).format(System.currentTimeMillis()) + ".jpg"
        )

        // Create output options object which contains file + metadata
        // 创建一个输出选项，告诉ImageCapture 拍照图片保存的位置
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        //，开启拍照，并设置监听，当拍照完成后触发
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    //拍照失败
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    //拍照完成
                    val savedUri = Uri.fromFile(photoFile)
                    val msg = "Photo capture succeeded: $savedUri"
                    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, msg)
                }
            })
    }

    companion object {
        private const val TAG = "ImageCaptureFragment"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"

        @JvmStatic
        fun newInstance() = ImageCaptureFragment()
    }
}