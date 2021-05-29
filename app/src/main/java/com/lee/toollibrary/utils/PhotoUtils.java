package com.lee.toollibrary.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileNotFoundException;

/**
 *
 * @author nicklxz
 * @date 2018/2/23
 * 调用系统相机--相册工具
 */

public class PhotoUtils {
    /**
     * 拍照后保存的文件
     */
    private File file;
    /**
     * 文件Uri
     */
    private Uri contentUri;

    private static PhotoUtils instance;
    private Activity mActivity;

    public static PhotoUtils getInstance(Activity activity) {
        if (instance == null) {
            instance = new PhotoUtils(activity);
        }
        return instance;
    }

    public PhotoUtils(Activity mActivity) {
        this.mActivity = mActivity;
    }

    /**
     * 调用系统相机拍照--得到的是原图的Uri
     * @param activity    上下文
     * @param requestCode 请求码
     * @param filePath    文件路径 eg: Environment.getExternalStorageDirectory().getPath+"/xxx"
     */
    public Uri takePicture(Activity activity, int requestCode, String filePath, String fileName) {
        // 启动系统相机
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File parentFile = new File(filePath);
        if (!parentFile.exists()) {
            parentFile.mkdir();
        }
        file = new File(parentFile, fileName);
        // 判断7.0android系统
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //临时添加一个拍照权限
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            //通过FileProvider获取uri-----注意在manifest注册
            contentUri = FileProvider.getUriForFile(activity,
                    "com.example.nicklxz.testphoto.fileProvider",
                    file);
            //指定图片存放位置，指定后，在onActivityResult里得到的Data将为null
            intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
        } else {
            contentUri = Uri.fromFile(file);
            //指定图片存放位置，指定后，在onActivityResult里得到的Data将为null
            intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
        }
        activity.startActivityForResult(intent, requestCode);
        return contentUri;
    }

    /**
     * 从相册选择图片--得到的是原图的Uri
     * @param requestCode 请求码
     */
    public void pohtoByAlbum(int requestCode){
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(
                MediaStore.Images.Media.INTERNAL_CONTENT_URI, "image/*");
        //返回结果和标识
        mActivity.startActivityForResult(intent, requestCode);
    }
    /**
     * 通知图库/相册更新  在onActivityResult里面调用
     *
     * @throws FileNotFoundException
     */
    public void updateAlbum() throws FileNotFoundException {
        MediaStore.Images.Media.insertImage(mActivity.getContentResolver(),
                file.getAbsolutePath(), "temp.jpg", null);
        mActivity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                Uri.parse("file://" + file.getAbsolutePath())));
    }

    /**
     * 根据需要显示的宽高进行缩放图片
     * @param bitmap 原图
     * @param width 目标宽
     * @param height  目标高
     */
    public Bitmap zoomPhoto(Bitmap bitmap,int width,int height){
        int bitmapWidth=bitmap.getWidth();
        int bitmapHeight=bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidht = ((float) width / bitmapWidth);
        float scaleHeight = ((float) height / bitmapHeight);
        /*
         * 通过Matrix类的postScale方法进行缩放
         */
        matrix.postScale(scaleWidht, scaleHeight);
        Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, bitmapWidth, bitmapHeight, matrix, true);
        return newbmp;
    }
    /**
     * 裁剪图片
     * @param uri
     * @param requestCode
     * @return
     */
    public Uri cutPhoto(Uri uri, int requestCode) {
        Uri uritempFile;
        Intent intent = new Intent("com.android.takePicture.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 60);
        intent.putExtra("outputY", 60);
        //uritempFile为Uri类变量，实例化uritempFile
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //开启临时权限
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            //重点:针对7.0以上的操作
            intent.setClipData(ClipData.newRawUri(MediaStore.EXTRA_OUTPUT, uri));
            uritempFile = uri;
        } else {
            uritempFile = Uri.parse("file://" + Environment.getExternalStorageDirectory().getPath() + "/" + "small.jpg");
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uritempFile);
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        mActivity.startActivityForResult(intent, requestCode);
        return uritempFile;
    }

    public File getFile() {
        return file;
    }

    /**
     * 适配api19及以上,根据uri获取图片的绝对路径
     * 
     * @param context 上下文对象
     * @param uri     图片的Uri
     * @return 如果Uri对应的图片存在, 那么返回该图片的绝对路径, 否则返回null
     */
    // TODO: 2018/2/23  还是有点问题的，拍照的图片的Uri得不到文件路劲
    @SuppressLint("NewApi")
    public static String getRealPathFromUriAboveApi19(Context context, Uri uri) {
        String filePath = null;
        String aaa=uri.getScheme();
        if (DocumentsContract.isDocumentUri(context, uri)) {
            // 如果是document类型的 uri, 则通过document id来进行处理
            String documentId = DocumentsContract.getDocumentId(uri);
            if (isMediaDocument(uri)) { // MediaProvider
                // 使用':'分割
                String id = documentId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=?";
                String[] selectionArgs = {id};
                filePath = getDataColumn(context, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection, selectionArgs);
            } else if (isDownloadsDocument(uri)) { // DownloadsProvider
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(documentId));
                filePath = getDataColumn(context, contentUri, null, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())){
            // 如果是 content 类型的 Uri
            filePath = getDataColumn(context, uri, null, null);
        } else if ("file".equals(uri.getScheme())) {
            // 如果是 file 类型的 Uri,直接获取图片对应的路径
            filePath = uri.getPath();
        }
        return filePath;
    }
    /**
     * 获取数据库表中的 _data 列，即返回Uri对应的文件路径
     * @return
     */
    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        String path = null;
        String[] projection = new String[]{MediaStore.Images.Media.DATA};
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(projection[0]);
                path = cursor.getString(columnIndex);
            }
        } catch (Exception e) {
            if (cursor != null) {
                cursor.close();
            }
        }
        return path;
    }

    /**
     * @param uri the Uri to check
     * @return Whether the Uri authority is MediaProvider
     */
    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri the Uri to check
     * @return Whether the Uri authority is DownloadsProvider
     */
    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

}
