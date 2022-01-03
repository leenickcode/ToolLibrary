package com.example.camera2demo

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.params.OutputConfiguration
import android.hardware.camera2.params.SessionConfiguration
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Surface
import android.view.TextureView
import androidx.annotation.MainThread
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    var frontCameraId = ""

    var backCameraId = ""

    var backCameraCharacteristics: CameraCharacteristics? = null
    var frontCameraCharacteristics: CameraCharacteristics? = null
    lateinit var previewSurfaceTexture: SurfaceTexture
    private val cameraManager: CameraManager by lazy { getSystemService(CameraManager::class.java) }

    companion object {
        private const val REQUEST_PERMISSION_CODE: Int = 1
        private val REQUIRED_PERMISSIONS: Array<String> = arrayOf(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        private const val TAG = "MainActivity"
    }

    lateinit var cameraPreview: TextureView
    lateinit var previewSurface: Surface
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 遍历所有可用的摄像头 ID，只取出其中的前置和后置摄像头信息。
        val cameraIdList = cameraManager.cameraIdList
        cameraIdList.forEach { cameraId ->
            val cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId)
            if (cameraCharacteristics[CameraCharacteristics.LENS_FACING] == CameraCharacteristics.LENS_FACING_FRONT) {
                //前置摄像头
                frontCameraId = cameraId
                frontCameraCharacteristics = cameraCharacteristics
            } else if (cameraCharacteristics[CameraCharacteristics.LENS_FACING] == CameraCharacteristics.LENS_FACING_BACK) {
                //后置摄像头
                backCameraId = cameraId
                backCameraCharacteristics = cameraCharacteristics
            }

        }
        if (checkRequiredPermissions()) {
            openCamera()
        }

        cameraPreview = findViewById<TextureView>(R.id.camera_preview)
        cameraPreview.surfaceTextureListener = PreviewSurfaceTextureListener()

    }


    /**
     * 判断我们需要的权限是否被授予，只要有一个没有授权，我们都会返回 false，并且进行权限申请操作。
     * @return true 权限都被授权
     */
    @RequiresApi(Build.VERSION_CODES.M)
    private fun checkRequiredPermissions(): Boolean {
        val deniedPermissions = mutableListOf<String>()
        for (permission in REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    permission
                ) == PackageManager.PERMISSION_DENIED
            ) {
                deniedPermissions.add(permission)
            }
        }
        if (deniedPermissions.isEmpty().not()) {
            requestPermissions(deniedPermissions.toTypedArray(), REQUEST_PERMISSION_CODE)
        }
        return deniedPermissions.isEmpty()
    }

    /**
     * 权限回调
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        for (item in grantResults) {
            if (item != PackageManager.PERMISSION_GRANTED) {
                //有权限被拒绝
                Log.d(TAG, "onRequestPermissionsResult: 权限被拒绝")
                return
            }
        }
        openCamera()
    }

    /**
     * 打开相机
     */
    @SuppressLint("MissingPermission")
    private fun openCamera() {
        // 有限选择后置摄像头，其次才是前置摄像头。
        cameraManager.openCamera(backCameraId, object : CameraDevice.StateCallback() {
            @RequiresApi(Build.VERSION_CODES.P)
            override fun onOpened(camera: CameraDevice) {
                Log.d(TAG, "onOpened: ")
                //成功打开相机并连接
                val outConfig = OutputConfiguration(previewSurface)
                // TODO: 2022/1/3 这里不建议 mainExecutor 要开个子线程，避免创建对应的handler
                val sessionConfig: SessionConfiguration = SessionConfiguration(
                    SessionConfiguration.SESSION_REGULAR,
                    listOf(outConfig),
                    mainExecutor,
                    object : CameraCaptureSession.StateCallback() {
                        override fun onConfigured(session: CameraCaptureSession) {
                            val requestBuilder =
                                camera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
                            requestBuilder.addTarget(previewSurface)
                            val request = requestBuilder.build()
                            session.setRepeatingRequest(
                                request,
                                object : CameraCaptureSession.CaptureCallback() {
                                    
                                },
                                null
                            )

                        }

                        override fun onConfigureFailed(session: CameraCaptureSession) {
                            Log.e(TAG, "onConfigureFailed: ", )
                        }
                    }
                )
                camera.createCaptureSession(sessionConfig)


            }

            override fun onDisconnected(camera: CameraDevice) {
                Log.d(TAG, "onDisconnected: ")
                //相机不可用时触发（比如有个高优先级的APP正在占用相机），需要关闭相机，源码注释有说明
                camera.close()

            }

            override fun onError(camera: CameraDevice, error: Int) {
                Log.d(TAG, "onError: ")
                //过程中发生错误时触发（比如传入了错误的相机ID），需要关闭相机
                camera.close()

            }
        }, null)

    }


    private inner class PreviewSurfaceTextureListener : TextureView.SurfaceTextureListener {
        @MainThread
        override fun onSurfaceTextureSizeChanged(
            surfaceTexture: SurfaceTexture,
            width: Int,
            height: Int
        ) = Unit

        @MainThread
        override fun onSurfaceTextureUpdated(surfaceTexture: SurfaceTexture) = Unit

        @MainThread
        override fun onSurfaceTextureDestroyed(surfaceTexture: SurfaceTexture): Boolean = false

        @MainThread
        override fun onSurfaceTextureAvailable(
            surfaceTexture: SurfaceTexture,
            width: Int,
            height: Int
        ) {
            previewSurfaceTexture = surfaceTexture
            previewSurface = Surface(previewSurfaceTexture)
        }
    }

    private inner class SessionStateCallback : CameraCaptureSession.StateCallback() {
        @MainThread
        override fun onConfigureFailed(session: CameraCaptureSession) {

        }

        @MainThread
        override fun onConfigured(session: CameraCaptureSession) {

        }

        @MainThread
        override fun onClosed(session: CameraCaptureSession) {

        }
    }

    val cameraTaskExecutor: ThreadPoolExecutor by lazy {
        ThreadPoolExecutor(5, 5, 10, TimeUnit.SECONDS, ArrayBlockingQueue(10))
    }
}