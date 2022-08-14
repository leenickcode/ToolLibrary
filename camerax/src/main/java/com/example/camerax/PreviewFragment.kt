package com.example.camerax

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment


/**
 * A simple [Fragment] subclass.
 * Use the [PreviewFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PreviewFragment : Fragment() {
    lateinit var previewView:PreviewView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_preview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        previewView = view.findViewById(R.id.viewFinder)
        startCamera()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener(Runnable {
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            // Preview 创建一个用于预览的userCase,并将我们xml的preview设置进去
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(previewView.createSurfaceProvider())
                }


            // 选择摄像头，只能选前、后两个，如果你的android设备是定制的，可能实际打开的跟这个前后对应不上
            //如果想打开多个摄像头，比如一个RGB摄像头，一个红外摄像头，目前CameraX好像不支持，得用Camera2,
            //如果您发现了CameraX也能打开多个，希望您不吝告知
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()
                // 将preview绑定到相机。
            val camera=    cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview)
                //bindToLifecycle 返回的Camera对象，可以获取cameInfo。查缩放状态，角度等。
            } catch(exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        },
            //这里只能用UI线程，原因是bindToLifecycle函数必须在UI线程调用。
            ContextCompat.getMainExecutor(requireContext()))

    }



    companion object {
        private const val TAG = "PreviewFragment"
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PreviewFragment()
    }
}