package com.lee.toollibrary.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件名:    EthernetUtil
 * 创建时间:  2020/8/21 on 11:22
 * 描述:     TODO 设置以太网静态ip 动态ip地址 需要系统权限及系统签名
 * @author 阿钟
 */

public class EthernetUtil {

    /**
     * 设置以太网动态获取IP
     */
    public static boolean setDynamicIp(Context context) {
        try {
            Class<?> ethernetManagerCls = Class.forName("android.net.EthernetManager");
            //获取EthernetManager实例
            Object ethManager = context.getSystemService("ethernet");
            //创建IpConfiguration
            Class<?> ipConfigurationCls = Class.forName("android.net.IpConfiguration");
            Object ipConfiguration = ipConfigurationCls.newInstance();
            //获取ipAssignment、proxySettings的枚举值
            Map<String, Object> ipConfigurationEnum = getIpConfigurationEnum(ipConfigurationCls);
            //设置ipAssignment
            Field ipAssignment = ipConfigurationCls.getField("ipAssignment");
            ipAssignment.set(ipConfiguration, ipConfigurationEnum.get("IpAssignment.DHCP"));
            //设置proxySettings
            Field proxySettings = ipConfigurationCls.getField("proxySettings");
            proxySettings.set(ipConfiguration, ipConfigurationEnum.get("ProxySettings.NONE"));
            //获取EthernetManager的setConfiguration()
            Method setConfigurationMethod = ethernetManagerCls.getDeclaredMethod("setConfiguration", ipConfiguration.getClass());
            //设置动态IP
            setConfigurationMethod.invoke(ethManager, ipConfiguration);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 设置以太网静态IP地址
     *
     * @param address ip地址
     * @param mask    子网掩码
     * @param gate    网关
     * @param dns     dns
     */
    public static boolean setEthernetStaticIp(Context context, String address, String mask, String gate, String dns) {
        try {
            Class<?> ethernetManagerCls = Class.forName("android.net.EthernetManager");
            //获取EthernetManager实例
            Object ethManager = context.getSystemService("ethernet");
            //创建StaticIpConfiguration
            Object staticIpConfiguration = newStaticIpConfiguration(address, gate, mask, dns);
            //创建IpConfiguration
            Object ipConfiguration = newIpConfiguration(staticIpConfiguration);
            //获取EthernetManager的setConfiguration()
            Method setConfigurationMethod = ethernetManagerCls.getDeclaredMethod("setConfiguration", ipConfiguration.getClass());
            //保存静态ip设置
            saveIpSettings(context, address, mask, gate, dns);
            //设置静态IP
            setConfigurationMethod.invoke(ethManager, ipConfiguration);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 获取StaticIpConfiguration实例
     */
    private static Object newStaticIpConfiguration(String address, String gate, String mask, String dns) throws Exception {
        Class<?> staticIpConfigurationCls = Class.forName("android.net.StaticIpConfiguration");
        //实例化StaticIpConfiguration
        Object staticIpConfiguration = staticIpConfigurationCls.newInstance();
        Field ipAddress = staticIpConfigurationCls.getField("ipAddress");
        Field gateway = staticIpConfigurationCls.getField("gateway");
        Field domains = staticIpConfigurationCls.getField("domains");
        Field dnsServers = staticIpConfigurationCls.getField("dnsServers");
        //设置ipAddress
        ipAddress.set(staticIpConfiguration, newLinkAddress(address, mask));
        //设置网关
        gateway.set(staticIpConfiguration, InetAddress.getByName(gate));
        //设置掩码
        domains.set(staticIpConfiguration, mask);
        //设置dns
        ArrayList<InetAddress> dnsList = (ArrayList<InetAddress>) dnsServers.get(staticIpConfiguration);
        dnsList.add(InetAddress.getByName(dns));
        return staticIpConfiguration;
    }

    /**
     * 获取LinkAddress实例
     */
    private static Object newLinkAddress(String address, String mask) throws Exception {
        Class<?> linkAddressCls = Class.forName("android.net.LinkAddress");
        Constructor<?> linkAddressConstructor = linkAddressCls.getDeclaredConstructor(InetAddress.class, int.class);
        return linkAddressConstructor.newInstance(InetAddress.getByName(address), getPrefixLength(mask));
    }

    /**
     * 获取IpConfiguration实例
     */
    private static Object newIpConfiguration(Object staticIpConfiguration) throws Exception {
        Class<?> ipConfigurationCls = Class.forName("android.net.IpConfiguration");
        Object ipConfiguration = ipConfigurationCls.newInstance();
        //设置StaticIpConfiguration
        Field staticIpConfigurationField = ipConfigurationCls.getField("staticIpConfiguration");
        staticIpConfigurationField.set(ipConfiguration, staticIpConfiguration);
        //获取ipAssignment、proxySettings的枚举值
        Map<String, Object> ipConfigurationEnum = getIpConfigurationEnum(ipConfigurationCls);
        //设置ipAssignment
        Field ipAssignment = ipConfigurationCls.getField("ipAssignment");
        ipAssignment.set(ipConfiguration, ipConfigurationEnum.get("IpAssignment.STATIC"));
        //设置proxySettings
        Field proxySettings = ipConfigurationCls.getField("proxySettings");
        proxySettings.set(ipConfiguration, ipConfigurationEnum.get("ProxySettings.STATIC"));
        return ipConfiguration;
    }

    /**
     * 获取IpConfiguration的枚举值
     */
    private static Map<String, Object> getIpConfigurationEnum(Class<?> ipConfigurationCls) {
        Map<String, Object> enumMap = new HashMap<>();
        Class<?>[] enumClass = ipConfigurationCls.getDeclaredClasses();
        for (Class<?> enumC : enumClass) {
            Object[] enumConstants = enumC.getEnumConstants();
            if (enumConstants == null) continue;
            for (Object enu : enumConstants) {
                enumMap.put(enumC.getSimpleName() + "." + enu.toString(), enu);
            }
        }
        return enumMap;
    }

    /**
     * 保存静态ip设置
     */
    private static void saveIpSettings(Context context, String address, String mask, String gate, String dns) {
        ContentResolver contentResolver = context.getContentResolver();
        Settings.Global.putString(contentResolver, "ethernet_static_ip", address);
        Settings.Global.putString(contentResolver, "ethernet_static_mask", mask);
        Settings.Global.putString(contentResolver, "ethernet_static_gateway", gate);
        Settings.Global.putString(contentResolver, "ethernet_static_dns1", dns);
    }

    /**
     * 获取长度
     */
    private static int getPrefixLength(String mask) {
        String[] strs = mask.split("\\.");
        int count = 0;
        for (String str : strs) {
            if (str.equals("255")) {
                ++count;
            }
        }
        return count * 8;
    }
}
