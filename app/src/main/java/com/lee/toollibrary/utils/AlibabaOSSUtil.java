//package com.lee.toollibrary.utils;
//
//
//import com.alibaba.sdk.android.oss.OSS;
//import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
//import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
//import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
//import com.alibaba.sdk.android.oss.model.PutObjectRequest;
//import com.alibaba.sdk.android.oss.model.PutObjectResult;
//
///**
// * Created by Nick on 2017/5/12.
// * OSS服务器工具类
// */
//
//public class AlibabaOSSUtil {
//    private static AlibabaOSSUtil mInstance;
//
//    public static AlibabaOSSUtil getInstance() {
//        synchronized (AlibabaOSSUtil.class) {
//            if (mInstance == null) {
//                mInstance = new AlibabaOSSUtil();
//            }
//        }
//        return mInstance;
//    }
//
//    private AlibabaOSSUtil() {
//    }
//
//    /**
//     * 上传的节点地址
//     */
////    private String endpoint = MyApplication.OSS_UPLOAD;
//    private OSS oss;
//    /**
//     * 上传请求对象
//     */
//    PutObjectRequest put;
//
//    //STEP-1. 初始化OSSClient
//    public OSS init() {
//        // 明文设置secret的方式建议只在测试时使用，更多鉴权模式请参考后面的`访问控制`章节
//        // TODO: 2018/3/2 使用的话这里注意配置
////        OSSCredentialProvider credentialProvider = new OSSPlainTextAKSKCredentialProvider(MyApplication.OSSACCESSKEYID, MyApplication.OSSACCESSKEYSECRET);
////        oss = new OSSClient(MyApplication.getInstance(), endpoint, credentialProvider);
////        //设置网络
////        ClientConfiguration conf = new ClientConfiguration();
////        conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒
////        conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒
////        conf.setMaxConcurrentRequest(5); // 最大并发请求书，默认5个
////        conf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次
//        return oss;
//    }
//
//    /**
//     * 上传文件
//     *
//     * @param bucketName 这是后自己在OSS控制台管理的   piclao
//     * @param objectKey  保存在服务器的文件名，要确保唯一性，同名文件只能存在一个
//     * @param filePath   要传的文件路径
//     * @return 把 OSSAsyncTask 返回出去，方便外面控制
//     */
//    public OSSAsyncTask upLoad(String bucketName, String objectKey, String filePath, OSSCompletedCallback<PutObjectRequest, PutObjectResult> callback) {
//        put = new PutObjectRequest(bucketName, objectKey, filePath);
//        OSSAsyncTask task = oss.asyncPutObject(put, callback);
//        return task;
//    }
//    /**
//     * 上传文件 有进度回调
//     *
//     * @param bucketName 这是后自己在OSS控制台管理的   piclao
//     * @param objectKey  保存在服务器的文件名，要确保唯一性，同名文件只能存在一个
//     * @param filePath   要传的文件路径
//     * @return 把 OSSAsyncTask 返回出去，方便外面控制
//     */
//    public OSSAsyncTask upLoadProgress(String bucketName, String objectKey, String filePath,
//                                       OSSCompletedCallback<PutObjectRequest, PutObjectResult> callback
//    , OSSProgressCallback<PutObjectRequest> progressCallback) {
//        put = new PutObjectRequest(bucketName, objectKey, filePath);
//        put.setProgressCallback(progressCallback);
//        OSSAsyncTask task = oss.asyncPutObject(put, callback);
//        return task;
//    }
//}
