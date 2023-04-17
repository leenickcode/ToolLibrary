package com.example.camera2demo

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.ImageFormat
import android.hardware.camera2.*
import android.media.ImageReader
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.*
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.Fragment
import com.example.camera2demo.databinding.FragmentCameraBinding
import java.nio.ByteBuffer

/**
 * Created by nicklee on 2022/8/16.
 * @author nicklee
 */
class CameraFragment2 : Fragment() {

    /** Android ViewBinding */
    private lateinit var fragmentCameraBinding: FragmentCameraBinding

    /**
     * 相机，默认个0，一般都是后置摄像头，具体前后先不管
     */
    var frontCameraId = "0"
    private val cameraManager: CameraManager by lazy {
        val context = requireContext().applicationContext
        context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    }

    /** [CameraCharacteristics] 指定相机的相关参数信息  */
    private val characteristics: CameraCharacteristics by lazy {
        cameraManager.getCameraCharacteristics(frontCameraId)
    }

    private lateinit var imageReader: ImageReader

    private lateinit var session: CameraCaptureSession
    /**
     * 最终预览的分辨率大小
     */
    lateinit var previewSize: Size

    /**
     * 用于拍照时的闪烁动画
     */
    private val animationTask: Runnable by lazy {
        Runnable {
            // Flash white animation
            fragmentCameraBinding.overlay.background = Color.argb(150, 255, 255, 255).toDrawable()
            // Wait for ANIMATION_FAST_MILLIS
            fragmentCameraBinding.overlay.postDelayed({
                // Remove white flash animation
                fragmentCameraBinding.overlay.background = null
            },50L)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentCameraBinding = FragmentCameraBinding.inflate(inflater, container, false)
        return fragmentCameraBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentCameraBinding.viewFinder.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder?) {
                previewSize = getExpectantSize(Size(1920, 1080))
                initImageReader()
                Log.d(
                    TAG,
                    "View finder size: ${fragmentCameraBinding.viewFinder.width} x ${fragmentCameraBinding.viewFinder.height}"
                )
                Log.d(TAG, "Selected preview size: $previewSize")
                fragmentCameraBinding.viewFinder.setAspectRatio(
                    previewSize.width,
                    previewSize.height
                )

                fragmentCameraBinding.viewFinder.post {
                    openCamera()
                }
//                Log.d(TAG, "surfaceCreated: "+fragmentCameraBinding.viewFinder.holder.surface.)

            }

            override fun surfaceChanged(
                holder: SurfaceHolder?,
                format: Int,
                width: Int,
                height: Int
            ) {

            }

            override fun surfaceDestroyed(holder: SurfaceHolder?) {

            }

        })
        fragmentCameraBinding.captureButton.setOnClickListener {
            takePhoto()
        }

    }

    //这个抑制下ide让我们做权限的警告，正常你应该去写权限逻辑，我这是为了缩减代码
    /**
     * 开启相机，只是单单的开启相机，界面上看不到任何效果
     */
    @SuppressLint("MissingPermission")
    fun openCamera() {
        cameraManager.openCamera(frontCameraId, object : CameraDevice.StateCallback() {
            override fun onOpened(camera: CameraDevice) {
                Log.d(TAG, "onOpened: 打开成功")
                //建立跟CameraDevice（即摄像头） 交互数据的通道
                createCaptureSession(
                    camera,
                    listOf(fragmentCameraBinding.viewFinder.holder.surface,imageReader.surface)
                )

            }


            override fun onDisconnected(camera: CameraDevice) {

            }

            override fun onError(camera: CameraDevice, error: Int) {
                Log.e(TAG, "onError: 打开相机错误 " + error)
            }

        }, null)
    }

    /**
     * 获取与摄像头支持的与期望分辨率最接近的分辨率
     * 保持16:9 or 4:3的比例
     * @param expectantSize 期望的分辨率 eg Size(1920,1080)
     */
    fun getExpectantSize(expectantSize: Size): Size {
        //摄像头支持的所有分辨率
        val outputSizeList =
            characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)!!
                .getOutputSizes(ImageFormat.JPEG)
        for (item in outputSizeList) {
            Log.d(TAG, "onOpened: " + item.width + "*" + item.height)
        }
        //假定我们期望相机的分辨率是这个

        //在所有支持的分辨率中找到与到相近的分辨率
        var selectSize: Size? = null
        val listWidthSize = ArrayList<Size>()
        for (itemSize in outputSizeList) {
            if (selectSize == null) {
                selectSize = itemSize
            } else {
                //当前遍历的size的宽度 与 期望size差的绝对值
                val itemAbs = Math.abs(itemSize.width - expectantSize.width)
                //之前获取的接近的size与期望size差的绝对值
                val selectAbs = Math.abs(selectSize.width - expectantSize.width)
                if (itemAbs < selectAbs) {
                    //绝对值更小的跟接近目标分辨率
                    selectSize = itemSize
                    listWidthSize.clear()
                    listWidthSize.add(itemSize)
                } else if (itemAbs == selectAbs) {
                    //遍历size 宽度与之前最接近的size宽度相等，放入集合，下一步在过滤
                    listWidthSize.add(itemSize)
                } else {
                    //遍历的差值更大，不用管
                }
            }
        }
        //将宽度最近的size根据高度在筛选一遍，
        for (itemSize in listWidthSize) {
            val tempAbs = Math.abs(itemSize.height - expectantSize.height)
            val currentAbs = Math.abs(selectSize!!.height - expectantSize.height)
            if (tempAbs < currentAbs) {
                selectSize = itemSize
            }
        }
        Log.d(TAG, "onOpened: 最终的分辨率= " + selectSize!!.width + "*" + selectSize.height)
        return selectSize
    }

    /**
     * 创建app跟相机交互数据通道
     */
    fun createCaptureSession(device: CameraDevice, targets: List<Surface>) {
        device.createCaptureSession(targets, object : CameraCaptureSession.StateCallback() {
            override fun onConfigured(session: CameraCaptureSession) {
                this@CameraFragment2.session = session
                Log.d(TAG, "onConfigured: 创建CameraCaptureSession 成功")
                //通道建立好了，可以发送请求，让CameraDevice返回它捕获到的界面数据给我们
                startPreview(device, session)
//                startAnalyse(device,session)
            }

            override fun onConfigureFailed(session: CameraCaptureSession) {
                Log.e(TAG, "onConfigureFailed: CameraCaptureSession失败")
            }
        }, null)

    }
   private fun takePhoto(){
        val captureRequest=session.device.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
            .apply {
                addTarget(imageReader.surface)
            }
       session.capture(captureRequest.build(),object :CameraCaptureSession.CaptureCallback(){
           override fun onCaptureStarted(
               session: CameraCaptureSession,
               request: CaptureRequest,
               timestamp: Long,
               frameNumber: Long
           ) {
               super.onCaptureStarted(session, request, timestamp, frameNumber)
               fragmentCameraBinding.viewFinder.post(animationTask)
           }

           override fun onCaptureCompleted(
               session: CameraCaptureSession,
               request: CaptureRequest,
               result: TotalCaptureResult
           ) {

               super.onCaptureCompleted(session, request, result)
               val image=imageReader.acquireNextImage()
               val buffer: ByteBuffer = image.getPlanes().get(0).getBuffer()
               val bytes = ByteArray(buffer.remaining())
               buffer[bytes]

               val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size, null)
               Log.d(TAG, "onCaptureCompleted: 拍照完成")
           }

           override fun onCaptureFailed(
               session: CameraCaptureSession,
               request: CaptureRequest,
               failure: CaptureFailure
           ) {
               super.onCaptureFailed(session, request, failure)
               Log.e(TAG, "onCaptureFailed: 拍照失败")
           }
       },null)
    }


    /**
     * 开启预览
     */
    fun startPreview(device: CameraDevice, session: CameraCaptureSession) {
        //创建一个用于预览的请求，并将这个请求返回的结果展示在surFaceView 上。
        val captureRequest = device.createCaptureRequest(
            CameraDevice.TEMPLATE_PREVIEW
        ).apply {
            //用于展现预览画面
            addTarget(fragmentCameraBinding.viewFinder.holder.surface)
//            //用于分析图片数据
//            addTarget(imageReader.surface)
        }
        //重复发送即重复收到来着cameraDevice 返回的数据，即是每一帧的数据
        session.setRepeatingRequest(captureRequest.build(), null, null)

    }



    fun initImageReader() {
        imageReader = ImageReader.newInstance(
            previewSize.width,
            previewSize.height,
            ImageFormat.YUV_420_888,
            3
        )
        imageReader.setOnImageAvailableListener({
            val image = it.acquireLatestImage()

            Log.d(TAG, "initImageReader: 格式"+image.format)
            Log.d(TAG, "initImageReader: width="+image.width+" height="+image.height)

            image.close()
        }, null)
    }

    companion object {
        private const val TAG = "CameraFragment2"
    }
}