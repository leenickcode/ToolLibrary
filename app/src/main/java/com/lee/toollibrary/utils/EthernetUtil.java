package com.lee.toollibrary.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件名:    EthernetUtil
 * 创建时间:  2020/8/21 on 11:22
 * 描述:     TODO 设置以太网静态ip 动态ip地址
 *
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

    public static String getIpAssignment(Context context) {
        try {
            Class<?> ethernetManagerCls = Class.forName("android.net.EthernetManager");
            //获取EthernetManager实例
            Object ethManager = context.getSystemService("ethernet");

//        ethernetManagerCls.getDeclaredField("")
            Method getConfigurationMethod = ethernetManagerCls.getDeclaredMethod("getConfiguration");
            Object config = getConfigurationMethod.invoke(ethManager);
            Class<?> ipConfigurationCls = Class.forName("android.net.IpConfiguration");
            Field ipAssignmentFiled = ipConfigurationCls.getField("ipAssignment");

            String  ipAssignment =  ipAssignmentFiled.get(config).toString();

            return ipAssignment;
        } catch (Exception e) {
            e.printStackTrace();
            return "NONE";
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

    /**
     * @return 获取所有有效的网卡
     */
    public static String[] getAllNetInterface() {
        ArrayList<String> availableInterface = new ArrayList<>();
        String[] interfaces = null;
        try {
            //获取本地设备的所有网络接口
            Enumeration nis = NetworkInterface.getNetworkInterfaces();
            InetAddress ia = null;
            while (nis.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) nis.nextElement();
                Enumeration<InetAddress> ias = ni.getInetAddresses();
                while (ias.hasMoreElements()) {
                    ia = ias.nextElement();
                    if (ia instanceof Inet6Address) {
                        continue;// skip ipv6
                    }
                    String ip = ia.getHostAddress();
                    // 过滤掉127段的ip地址
                    if (!"127.0.0.1".equals(ip)) {
                        if (ni.getName().substring(0, 3).equals("eth")) {//筛选出以太网
                            availableInterface.add(ni.getName());
                        }
                    }
                }
            }

        } catch (SocketException e) {
            e.printStackTrace();
        }
        int size = availableInterface.size();
        if (size > 0) {
            interfaces = new String[size];
            for (int i = 0; i < size; i++) {
                interfaces[i] = availableInterface.get(i);
            }
        }
        return interfaces;
    }


    public static String getGateWay() {
        String[] arr;
        try {
            Process process = Runtime.getRuntime().exec("ip route list table 0");
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String string = in.readLine();
            arr = string.split("\\s+");
            return arr[2];
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "0.0.0.0";
    }

    /*获取子网掩码*/
    public static String getIpAddressMaskForInterfaces() {

        String interfaceName = "eth0";

        try {
            //获取本机所有的网络接口
            Enumeration<NetworkInterface> networkInterfaceEnumeration = NetworkInterface.getNetworkInterfaces();
            //判断 Enumeration 对象中是否还有数据
            while (networkInterfaceEnumeration.hasMoreElements()) {

                //获取 Enumeration 对象中的下一个数据
                NetworkInterface networkInterface = networkInterfaceEnumeration.nextElement();
                if (!networkInterface.isUp() && !interfaceName.equals(networkInterface.getDisplayName())) {

                    //判断网口是否在使用，判断是否时我们获取的网口
                    continue;
                }

                for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                    if (interfaceAddress.getAddress() instanceof Inet4Address) {

                        //仅仅处理ipv4
                        //获取掩码位数，通过 calcMaskByPrefixLength 转换为字符串
                        return calcMaskByPrefixLength(interfaceAddress.getNetworkPrefixLength());
                    }
                }
            }
        } catch (SocketException e) {

            e.printStackTrace();
        }

        return "0.0.0.0";
    }

    /*通过子网掩码的位数计算子网掩码*/
    private static String calcMaskByPrefixLength(int length) {

        int mask = 0xffffffff << (32 - length);
        int partsNum = 4;
        int bitsOfPart = 8;
        int maskParts[] = new int[partsNum];
        int selector = 0x000000ff;

        for (int i = 0; i < maskParts.length; i++) {

            int pos = maskParts.length - 1 - i;
            maskParts[pos] = (mask >> (i * bitsOfPart)) & selector;
        }

        String result = "";
        result = result + maskParts[0];

        for (int i = 1; i < maskParts.length; i++) {

            result = result + "." + maskParts[i];
        }

        return result;
    }
    public static String getIpAddressForInterfaces() {

        String interfaceName = "eth0";

        try {
            Enumeration<NetworkInterface> enNetworkInterface = NetworkInterface.getNetworkInterfaces(); //获取本机所有的网络接口
            while (enNetworkInterface.hasMoreElements()) {  //判断 Enumeration 对象中是否还有数据
                NetworkInterface networkInterface = enNetworkInterface.nextElement();   //获取 Enumeration 对象中的下一个数据
                if (!networkInterface.isUp()) { // 判断网口是否在使用
                    continue;
                }
                if (!interfaceName.equals(networkInterface.getDisplayName())) { // 网口名称是否和需要的相同
                    continue;
                }
                Enumeration<InetAddress> enInetAddress = networkInterface.getInetAddresses();   //getInetAddresses 方法返回绑定到该网卡的所有的 IP 地址。
                while (enInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enInetAddress.nextElement();
                    if (inetAddress instanceof Inet4Address) {  //判断是否未ipv4
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "0.0.0.0";
    }
    public static String getLocalMacAddress() {
        String mac = "";
        try {
            String path = "sys/class/net/eth0/address";
            FileInputStream fis_name = new FileInputStream(path);
            byte[] buffer_name = new byte[1024 * 8];
            int byteCount_name = fis_name.read(buffer_name);
            if (byteCount_name > 0) {
                mac = new String(buffer_name, 0, byteCount_name, "utf-8");
            }

            if (mac.length() == 0) {
                path = "sys/class/net/eth0/wlan0";
                FileInputStream fis = new FileInputStream(path);
                byte[] buffer = new byte[1024 * 8];
                int byteCount = fis.read(buffer);
                if (byteCount > 0) {
                    mac = new String(buffer, 0, byteCount, "utf-8");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("mac=====" + mac);
        return mac.trim();
    }

}
