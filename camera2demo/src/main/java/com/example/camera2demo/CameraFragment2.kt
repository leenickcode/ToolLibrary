package com.example.camera2demo

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.ImageFormat
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.*
import androidx.fragment.app.Fragment
import com.example.camera2demo.databinding.FragmentCameraBinding

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
                openCamera()
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
                    listOf(fragmentCameraBinding.viewFinder.holder.surface)
                )

                val outputSizeList = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)!!.getOutputSizes(ImageFormat.JPEG)
                for (item in outputSizeList) {
                    Log.d(TAG, "onOpened: "+item.height+"*"+item.width)
                }
                //假定我们期望相机的分辨率是这个  手机屏幕宽高
                val expectantSize=Size(1080,1902)

                //在所有支持的分辨率中找到与到相近的分辨率
                var selectSize:Size?=null
                for ( itemSize in outputSizeList){
                        if (selectSize==null){
                            selectSize = itemSize
                        }else{
                            //
                            if (Math.abs(itemSize.height-expectantSize.width)<Math.abs(selectSize.height-expectantSize.width)){
                                //绝对值更小的跟接近目标分辨率
                                selectSize = itemSize
                            }else{
//                                //原本想这里跳出，提高性能，发现outputSizeList 并不是按大小排序的，所以要是要整个执行完
//                                Log.d(TAG, "onOpened: 最终的分辨率= "+selectSize.width+"*"+selectSize.height)
//                                break
                            }
                        }

                }
                Log.d(TAG, "onOpened: 最终的分辨率= "+selectSize!!.width+"*"+selectSize.height)

            }


            override fun onDisconnected(camera: CameraDevice) {

            }

            override fun onError(camera: CameraDevice, error: Int) {
                Log.e(TAG, "onError: 打开相机错误 " + error)
            }

        }, null)
    }


    /**
     * 创建app跟相机交互数据通道
     */
    fun createCaptureSession(device: CameraDevice, targets: List<Surface>) {
        device.createCaptureSession(targets, object : CameraCaptureSession.StateCallback() {
            override fun onConfigured(session: CameraCaptureSession) {
                Log.d(TAG, "onConfigured: 创建CameraCaptureSession 成功")
                //通道建立好了，可以发送请求，让CameraDevice返回它捕获到的界面数据给我们
                startPreview(device, session)

            }

            override fun onConfigureFailed(session: CameraCaptureSession) {
                Log.e(TAG, "onConfigureFailed: CameraCaptureSession失败")
            }
        }, null)

    }

    /**
     * 开启预览
     */
    fun startPreview(device: CameraDevice, session: CameraCaptureSession) {
        //创建一个用于预览的请求，并将这个请求返回的结果展示在surFaceView 上。
        val captureRequest = device.createCaptureRequest(
            CameraDevice.TEMPLATE_PREVIEW
        ).apply { addTarget(fragmentCameraBinding.viewFinder.holder.surface) }
        //重复发送即重复收到来着cameraDevice 返回的数据，即是每一帧的数据
        session.setRepeatingRequest(captureRequest.build(), null, null)

    }

    companion object {
        private const val TAG = "CameraFragment2"
    }
}