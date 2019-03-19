package com.lee.toollibrary.utils;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by nick on 2018/6/7.
 * 装换工具类  进制转换，数据转换。。。
 * @author nick
 */
public class ConvertUtil {
    /** List集合转Sting
     * @param list
     * @return String
     */
    public static String listToString(List<String> list){
        StringBuilder stringBuilder=new StringBuilder();
        for (int i = 0; i <list.size() ; i++) {
            stringBuilder.append(list.get(i));
        }
        return stringBuilder.toString();
    }

    /**
     *  int 转16进制字符串 两位一个字节
     * @param a
     * @return
     */
   public static String intToHexString(int a){
       //一个字节8位，只能表示两位的16进制数，当长度为奇数时，前面补0
        if (Integer.toHexString(a).length()%2!=0|| Integer.toHexString(a).length()==1){
          return "0"+ Integer.toHexString(a);
        }else {
            return Integer.toHexString(a);
        }
   }



    /**
     * 将多个字节按高低拼接，低位在前  例如 getMergeByte（0,1） 结果为2^16
     * @param value
     * @return
     */
    public static int getMergeByte(int...value){
        int number=value[0]&0xff;
        for (int i = 1; i <value.length ; i++) {
          number= number|((value[i]&0xff)<<(i*8));
        }
        return number;
    }

    /**
     * 16进制字符串转Int
     *
     * @param s
     */
    public static int strHexInt(String s) {
        return Integer.parseInt(s, 16);
    }

    /**
     * 二进制字符串转换为十六进制字符串
     * <p>
     * 二进制字符串位数必须满足是4的倍数
     *
     * @param binaryStr
     * @return
     */
    public static String binaryStrToHexStr(String binaryStr) {

        if (binaryStr == null || binaryStr.equals("") || binaryStr.length() % 4 != 0) {
            return null;
        }

        StringBuffer sbs = new StringBuffer();
        // 二进制字符串是4的倍数，所以四位二进制转换成一位十六进制
        for (int i = 0; i < binaryStr.length() / 4; i++) {
            String subStr = binaryStr.substring(i * 4, i * 4 + 4);
            String hexStr = Integer.toHexString(Integer.parseInt(subStr, 2));
            sbs.append(hexStr);
        }

        return sbs.toString();
    }
    /**
     * 将十六进制的字符串转换成二进制的字符串
     *
     * @param hexString
     * @return
     */
    public static String hexStrToBinaryStr(String hexString) {

        if (hexString == null || hexString.equals("")) {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        // 将每一个十六进制字符分别转换成一个四位的二进制字符
        for (int i = 0; i < hexString.length(); i++) {
            String indexStr = hexString.substring(i, i + 1);
            String binaryStr = Integer.toBinaryString(Integer.parseInt(indexStr, 16));
            while (binaryStr.length() < 4) {
                binaryStr = "0" + binaryStr;
            }
            sb.append(binaryStr);
        }

        return sb.toString();
    }
    /**
     * byte 转 bit
     *
     * @param by
     * @returnf
     */
    public static String getBit(byte by) {
        StringBuffer sb = new StringBuffer();
        sb.append((by >> 7) & 0x1)
                .append((by >> 6) & 0x1)
                .append((by >> 5) & 0x1)
                .append((by >> 4) & 0x1)
                .append((by >> 3) & 0x1)
                .append((by >> 2) & 0x1)
                .append((by >> 1) & 0x1)
                .append((by >> 0) & 0x1);
        return sb.toString();
    }

    /**
     * 根据日期得到周几
     * @param dateString
     * @return
     */
    public static  String getWeek(String dateString){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");// 日期格式
        Date date = null;
        try {
            date = format.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("E");
        String week = sdf.format(date);
        return week;
    }

    /**
     * 将数字格式化  比如1 变成01
     * @param i
     * @return
     */
    public static String formatNumber(int i){
        DecimalFormat mFormat = new DecimalFormat("00");//确定格式，把1转换为01
        String s = mFormat.format(i);
        return s;
    }
}
