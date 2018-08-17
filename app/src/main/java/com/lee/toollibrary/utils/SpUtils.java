package com.lee.toollibrary.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.util.Base64;
import android.widget.ImageView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;

/**
 * SharedPreferences工具类 save保存 load读取
 * @author nick
 */
public class SpUtils {
    private static volatile SpUtils sp = null;
    private SharedPreferences preferences;
    /**
     * 编辑器
     */
    private Editor editor;
    private String strBase64;
    private byte[] base64Bytes;
    /**
     * //搜索记录
     */
    private final static String KEY_SEARCH_HISTORY = "history_record";

    private SpUtils(Context context) {
        //参数是保存的文件名
        preferences = context.getSharedPreferences("userinfo", Activity.MODE_PRIVATE);
        //获取编辑器
        editor = preferences.edit();
    }

    public static SpUtils getInstance() {
        if (sp == null) {
            synchronized (SpUtils.class) {
                if (sp == null) {
                    sp = new SpUtils(BaseUtils.application);
                }
            }
        }
        return sp;
    }

    /**
     * 清除所有
     */
    public void clear() {
        editor.clear();
        editor.commit();
    }

    /**
     * 保存token
     *
     * @param token token
     */
    public void saveToken(String token) {
        saveString(token, "token");
    }

    /**
     * 拿
     */
    public String loadToken() {
        return loadString("token").toString();
    }

    public void saveName(String user) {
        saveString(user, "name");
    }

    public String loadName() {
        return loadString("name").toString();
    }

    public void saveLoginname(String loginname) {
        saveString(loginname, "loginname");
    }

    public String loadLoginname() {
        return loadString("loginname").toString();
    }

    /**
     * 保存手机号
     *
     * @param mobile
     */
    public void saveMobile(String mobile) {
        saveString(mobile, "mobile");
    }

    /**
     * 提取手机号
     *
     * @return
     */
    public String loadMobile() {
        return loadString("mobile").toString();
    }

    public void saveOrderHistory(String orderHistory) {
        saveString(orderHistory, "orderHistory");
    }

    public String loadOrderHistory() {
        return loadString("orderHistory").toString();
    }

    public void saveScore(String count) {
        saveString(count, "count");
    }

    public String loadScore() {
        return loadString("count").toString();
    }

    /**
     * 0通过 1审核中 2审核失败
     *
     * @param emailvalidate,realnamevalidate
     */
    public void saveEmailValidate(String emailvalidate) {
        saveString(emailvalidate, "emailvalidate");
    }

    public String loadEmailValidate() {
        return loadString("emailvalidate").toString();
    }

    public void saveRealnamevalidate(String realnamevalidate) {
        saveString(realnamevalidate, "realnamevalidate");
    }

    public String loadRealnamevalidate() {
        return loadString("realnamevalidate").toString();
    }

    public void saveUserID(String memberid) {
        saveString(memberid, "memberid");
    }

    public String loadUserID() {
        return loadString("memberid").toString();
    }

    private boolean saveString(String str, String name) {
        return editor.putString(name, str).commit();
    }

    private String loadString(String name) {
        return preferences.getString(name, "");
    }


    /**
     * @param obj
     * @param name
     * @return
     * @Author guyj
     * @date 2015年11月10日 下午4:20:41
     */
    public boolean saveObj(Object obj, String name) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        // 将Product对象转换成byte数组，并将其进行base64编码
        strBase64 = new String(Base64.encode(baos.toByteArray(), Base64.DEFAULT));
        // 将编码后的字符串写到xml文件中
        editor.putString(name, strBase64);
        editor.commit();
        if (oos != null) {
            try {
                oos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    /**
     * @param name
     * @return
     * @Author guyj
     * @date 2015年11月10日 下午4:45:00
     */
    public Object loadObj(String name) {
        Object obj = null;
        strBase64 = preferences.getString(name, "");
        if (TextUtils.isEmpty(strBase64)) {
            return "";
        }
        // 对Base64格式的字符串进行解码
        base64Bytes = Base64.decode(strBase64.getBytes(), Base64.DEFAULT);
        ByteArrayInputStream bais = new ByteArrayInputStream(base64Bytes);
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(bais);
            // 从ObjectInputStream中读取Product对象
            obj = ois.readObject();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (ois != null) {
            try {
                ois.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return obj;
    }

    /**
     * @param img
     * @param name
     * @return
     * @Author guyj
     * @date 2015年11月11日 下午3:17:33
     */
    public boolean saveImage(ImageView img, String name) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // 将ImageView组件中的图像压缩成JPEG格式，并将压缩结果保存在ByteArrayOutputStream对象中
        ((BitmapDrawable) img.getDrawable()).getBitmap().compress(CompressFormat.PNG, 100, baos);
        strBase64 = new String(Base64.encode(baos.toByteArray(), Base64.DEFAULT));
        // 保存由图像字节流转换成的Base64格式字符串
        editor.putString(name, strBase64);
        editor.commit();
        if (baos != null) {
            try {
                baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * @param name
     * @return
     * @Author guyj
     * @date 2015年11月11日 下午3:34:06
     */
    public Bitmap loadImage(String name) {
        strBase64 = preferences.getString(name, "");
        base64Bytes = Base64.decode(strBase64.getBytes(), Base64.DEFAULT);
        ByteArrayInputStream bais = new ByteArrayInputStream(base64Bytes);
        Bitmap bitmap = BitmapFactory.decodeStream(bais);
        if (bais != null) {
            try {
                bais.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    //3-10日 nick 添加---------

    /**
     * 获取用户搜索记录
     *
     * @return 搜索记录
     */
    public String loadSeachHistory() {
        return loadString(KEY_SEARCH_HISTORY);
    }

    /**
     * 获取用户搜索记录
     *
     * @param title  搜索关键字
     */
    public void saveSeachHistory(String title) {
        String oldSeach = preferences.getString(KEY_SEARCH_HISTORY, "");
        StringBuilder stringBuilder = new StringBuilder(title);
        if ("".equals(oldSeach)) {
            saveString(title, KEY_SEARCH_HISTORY);
        } else {
            stringBuilder.append("," + oldSeach);
            //避免重复添加
            if (!oldSeach.contains(title)) {
                saveString(stringBuilder.toString(), KEY_SEARCH_HISTORY);
            }
        }

    }

    /**
     * 清空搜索记录
     */
    public void clearSeachHistory() {
        editor.remove(KEY_SEARCH_HISTORY);
        editor.commit();
    }
    //3-10日 nick 添加---------结束
}
