package com.example.camerax

import android.graphics.ImageFormat
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class AnalysisFragment : Fragment() {

    lateinit var previewView: PreviewView
    private  val cameraExecutor: ExecutorService by lazy {
        Executors.newSingleThreadExecutor()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_analysis, container, false)
    }
    @ExperimentalGetImage
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        previewView = view.findViewById(R.id.viewFinder)
        startCamera()
    }



    @ExperimentalGetImage
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

            val imageAnalyzer = ImageAnalysis.Builder()
                .build()
                .also {
                    //分析是个持续耗时的操作，要放子线程
                    it.setAnalyzer(cameraExecutor,ImageAnalysis.Analyzer {imageProxy->
                        // imageProxy.image有个错误，需要 @ExperimentalGetImage，意思是imageProxy不一定只转载
                        //image，可能装的是别的，注解是提醒我们要注意关闭图像不要用 image.close.应该用imageProxy。close
                            Log.d(TAG,"输出格式："+imageProxy.image?.format+"---"+ImageFormat.YUV_420_888)
                        //输出格式默认是 YUV_420_888  也可以设置为RGBA_8888
                        Log.d(TAG, "startCamera: 相机跟屏幕的选择角度="+imageProxy.imageInfo.rotationDegrees)
                        //这个必须调用，表示一次分析完成。释放imageProxy对象，否则该回调走一次就不走了。
                        imageProxy.close()
                    })
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
                    this, cameraSelector, preview,imageAnalyzer)
                //bindToLifecycle 返回的Camera对象，可以获取cameInfo。查缩放状态，角度等。
            } catch(exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        },
            //这里只能用UI线程，原因是bindToLifecycle函数必须在UI线程调用。
            ContextCompat.getMainExecutor(requireContext()))

    }

    companion object {
        private const val TAG = "AnalysisFragment"
        @JvmStatic
        fun newInstance() =
            AnalysisFragment()
    }
}