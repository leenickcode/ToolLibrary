package com.lee.toollibrary.utils;

import android.util.Log;

import java.util.List;

/**
 * Created by nick on 2018/5/3.
 * 数学运算工具类
 * @author nick
 */
public class MathUtil {

    /**
     * 求异或值
     * @return 异或后的结果
     */
    public static  int exclusiveOrInt(int... nums ){
        int value=0;
        for (int i = 0; i <nums.length -1; i++) {
            if (i==0){
                value=nums[i];
            }
                value=value^nums[i+1];
        }
        return value;
    }

    /**
     * 求异或
     * @param list 16进制的字符串 集合
     * @return 异或结果
     */
    public static int exclusiveOrString(List<String> list){
        int value=0;
        for (int i = 0; i <list.size()-1; i++) {
            if (i==0){
                value=Integer.valueOf(list.get(i),16);
                Log.d("MathUtil", "exclusiveOrString: "+value);
                Log.d("MathUtil", "exclusiveOrString: "+Integer.valueOf(list.get(i+1),16));
            }
            value=value^Integer.valueOf(list.get(i+1),16);
        }
        return value;
    }
}
