package com.lee.toollibrary.utils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by nick on 2018/4/28.
 *  蓝牙 连接 数据接收发送工具类
 * @author nick
 */
public class BlueToothUtil {
    private BluetoothGatt mBluetoothGatt;
    private BluetoothGattCharacteristic writeCharacteristic;
    private BluetoothGattCharacteristic readCharacteristic;
    private static final String TAG = "BlueToothUtil";
    public static final String DATA_ACTION = "com.hutlon.bluetooth.data";
    private String data;
    /**
     * 原子操作的Integer类，其实就是线程安全的Integer类
     */
    private final AtomicInteger integer = new AtomicInteger();
    /**
     * 执行重复发送数据任务的线程池，参数二为了方便指定线程名称，追溯问题
     */
    private ScheduledThreadPoolExecutor executor =
            new ScheduledThreadPoolExecutor(5, new ThreadFactory() {
                @Override
                public Thread newThread(@NonNull Runnable r) {
                    /*
      发送数据的线程名的前缀
     */
                    String threadName = "senddata-";
                    return new Thread(r, threadName + integer.getAndIncrement());
                }
            });
    private ScheduledFuture scheduledFuture;
    /**
     * 是否收到响应
     */
    private boolean isResponcel;
    /**
     * 发送次数 最多6次没响应就不发
     */
    private int sendCount;
    /**
     * 循环次数
     */
    private int cycles = 6;
    /**
     * 一次写入的最大字节
     */
    private int maxByteLength = 20;
    /**
     * 是否写入成功
     */
    private boolean isWriteSuccess;
    /**
     * 蓝牙适配器
     */
    private BluetoothAdapter bluetoothAdapter;
    private static BlueToothUtil instance;
    /**
     * 设备连接状态，true 已连接，false 断开连接
     */
    private boolean connectionState;
    /**
     * 读取数据用的特征值的UUID
     */
    private static final String UUID_READER_CHARACTERISTIC = "0000ffe4-0000-1000-8000-00805f9b34fb";
    /**
     * 读取数据服务的UUID
     */
    private static final String UUID_READER_SERVICE = "0000ffe0-0000-1000-8000-00805f9b34fb";
    /**
     * 写服务的uuid
     */
    private static final String UUID_WRITE_SERVICE = "0000ffe5-0000-1000-8000-00805f9b34fb";
    /**
     * 写服务的特征值的uuid
     */
    private static final String UUID_WRITE_CHARACTERISTIC = "0000ffe9-0000-1000-8000-00805f9b34fb";

    public static BlueToothUtil getInstance() {
        if (instance == null) {
            instance = new BlueToothUtil();
        }
        return instance;
    }

    private BlueToothUtil() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }


    /**
     * 连接设备
     *
     * @param context 上下文
     * @param device  BLE设备
     */
    public void connection(final Context context, BluetoothDevice device) {
        if (mBluetoothGatt != null) {
            //多次创建gatt连接对象的直接结果是创建过6个以上gatt后就会再也连接不上任何设备，原因应该是Android中对BLE限制了同时连接的数量为6个
            //即使连上了也会获取不到服务
            mBluetoothGatt.close();
        }
        mBluetoothGatt = device.connectGatt(context, false, new BluetoothGattCallback() {
            /**
             * 蓝牙连接状态的回调
             * @param gatt  BluetoothGatt 对象
             * @param status 操作前的状态
             * @param newState  连接状态
             */
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                super.onConnectionStateChange(gatt, status, newState);
                //收到设备notify值 （设备上报值）
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    //扫描改设备下的服务
                    mBluetoothGatt.discoverServices();
                    connectionState = true;
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    connectionState = false;
                }
                Log.e(TAG, "连接状态: " + connectionState);
//                RxBus.getInstance().post(new BLEConnectionEvent(connectionState));
            }

            //获取设备服务的回调函数
            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                Log.e(TAG, "onServicesDiscovered: ");
                super.onServicesDiscovered(gatt, status);
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    List<BluetoothGattService> bluetoothGattServices = gatt.getServices();
                    for (BluetoothGattService service : bluetoothGattServices) {
                        if (service.getUuid().toString().equals(UUID_READER_SERVICE)) {
                            //读
                            readCharacteristic = service.getCharacteristic(UUID.fromString(UUID_READER_CHARACTERISTIC));
                            //开启新数据接收通知
                            gatt.setCharacteristicNotification(readCharacteristic, true);
                        }
                        if (service.getUuid().toString().equals(UUID_WRITE_SERVICE)) {
                            //写
                            writeCharacteristic = service.getCharacteristic(UUID.fromString(UUID_WRITE_CHARACTERISTIC));
                        }
                    }
                }
            }

            //Characteristic  改变时回调  说明有新数据
            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                super.onCharacteristicChanged(gatt, characteristic);
                //判断下当前特征值是不是 读取数据得
                if (UUID_READER_CHARACTERISTIC.equals(characteristic.getUuid().toString())) {
                    final byte[] data = characteristic.getValue();
                    if (data != null && data.length > 0) {
                        final StringBuilder stringBuilder = new StringBuilder(data.length);
                        for (byte byteChar : data) {
                            stringBuilder.append(String.format("%02X ", byteChar));
                        }
                        Log.d(TAG, "收到对方设备发来的消息---: " + stringBuilder.toString());
                        if (executor != null) {
                            scheduledFuture.cancel(true);
                            executor.remove((Runnable) scheduledFuture);
                        }
                        Intent intent = new Intent(DATA_ACTION);
                        intent.putExtra("data", stringBuilder.toString());
                        context.sendBroadcast(intent);

                    }
                }
            }

            //写数据的回调
            @Override
            public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                super.onCharacteristicWrite(gatt, characteristic, status);
                switch (status) {
                    case BluetoothGatt.GATT_SUCCESS:
                        Log.d(TAG, "onCharacteristicWrite: " + "写入成功" + sendCount);
                        isWriteSuccess = true;
                        if (sendCount >= cycles && executor != null) {
                            Log.d(TAG, "超过了6次: 退出循环");
                            executor.remove((Runnable) scheduledFuture);
                            scheduledFuture.cancel(true);
                        }
                        break;
                    case BluetoothGatt.GATT_FAILURE:
                        Log.d(TAG, "onCharacteristicWrite: " + "写入失败");
                        break;
                    default:
                }
            }
        });
    }

    /**
     * 唤醒指令
     */
    public void writeAA() {
        if (!connectionState) {
            return;
        }
        if (writeCharacteristic == null) {
            return;
        }
        final int charaProp = writeCharacteristic.getProperties();
        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
            mBluetoothGatt.setCharacteristicNotification(
                    readCharacteristic, true);
        }
        //如果该char可写
        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_WRITE) > 0) {
            byte[] value = new byte[20];
            value[0] = (byte) 0x00;
            writeCharacteristic.setValue(value[0],
                    BluetoothGattCharacteristic.FORMAT_UINT8, 0);
            writeCharacteristic.setValue(hex2byte("AA55AA55AA55AA55AA55AA55AA55AA55AA55".getBytes()));
            mBluetoothGatt.writeCharacteristic(writeCharacteristic);
        }
    }


    /**
     * 新的命令
     *
     * @param data 写入的数据
     */
    public void write(final String data) {
        this.data = data;
        sendCount = 0;
        isResponcel = false;
        isWriteSuccess = true;
        if (!connectionState) {
            return;
        }
        if (executor.getQueue().size() != 0) {
            //防止重复点击
            ToastUtil.showToast("任务还在执行");
            return;
        }
        scheduledFuture = executor.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                //写入成功，并且没有响应，就循环发送这个数据包，否则不发送
                if (isWriteSuccess && !isResponcel) {
                    isWriteSuccess = false;
                    sendCount++;
                    final int charaProp = writeCharacteristic.getProperties();
                    if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                        mBluetoothGatt.setCharacteristicNotification(
                                readCharacteristic, true);
                    }

                    //如果该char可写
                    if ((charaProp | BluetoothGattCharacteristic.PROPERTY_WRITE) > 0) {
                        byte[] value = new byte[20];
                        value[0] = (byte) 0x00;
                        writeCharacteristic.setValue(value[0],
                                BluetoothGattCharacteristic.FORMAT_UINT8, 0);
                        writeCharacteristic.setValue(hex2byte(data.getBytes()));
                        mBluetoothGatt.writeCharacteristic(writeCharacteristic);
                    }
                }
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);

    }


    /**
     * 重载方法 一条数据超过20个字节
     *
     * @param list 写入的数据集
     */
    public void writeListData(final List<String> list) {
        if (!connectionState) {
            return;
        }
        if (writeCharacteristic == null) {
            return;
        }
        //一个数据包要发送的次数  一次20字节
        final int count;
        if (list.size() % maxByteLength != 0) {
            count = list.size() / 20 + 1;
        } else {
            count = list.size() / 20;
        }
        for (int i = 0; i < count; i++) {
            //发送76个字节
            final int charaProp = writeCharacteristic.getProperties();
            if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                mBluetoothGatt.setCharacteristicNotification(
                        readCharacteristic, true);
            }
            //如果该char可写
            if ((charaProp | BluetoothGattCharacteristic.PROPERTY_WRITE) > 0) {
                byte[] value = new byte[20];
                value[0] = (byte) 0x00;
                writeCharacteristic.setValue(value[0],
                        BluetoothGattCharacteristic.FORMAT_UINT8, 0);
                if ((i + 1) * 20 <= list.size()) {
                    Log.d(TAG, "run: 写入的内容" + ConvertUtil.listToString(list.subList(i * 20, (i + 1) * 20)));
                    writeCharacteristic.setValue(hex2byte(ConvertUtil.listToString(list.subList(i * 20, (i + 1) * 20)).getBytes()));
                } else {
                    Log.d(TAG, "run: 写入的内容" + ConvertUtil.listToString(list.subList(i * 20, list.size())));
                    writeCharacteristic.setValue(hex2byte(ConvertUtil.listToString(list.subList(i * 20, list.size())).getBytes()));
                }
                mBluetoothGatt.writeCharacteristic(writeCharacteristic);
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 把16进制的字符串 两位一组标识一个字节
     *
     * @param b byte数组
     * @return byte数组
     */
    public static byte[] hex2byte(byte[] b) {
        int length = 2;
        if ((b.length % length) != 0) {
            throw new IllegalArgumentException("长度不是偶数");
        }
        byte[] b2 = new byte[b.length / length];
        for (int n = 0; n < b.length; n += length) {
            String item = new String(b, n, length);
            // 两位一组，表示一个字节,把这样表示的16进制字符串，还原成一个进制字节
            b2[n / 2] = (byte) Integer.parseInt(item, 16);
        }
        return b2;
    }


    /**
     * 关闭连接
     */
    public void close() {
        if (mBluetoothGatt != null) {
            mBluetoothGatt.disconnect();
        }
    }

    /**
     * 配对
     *
     * @param device ble设备
     */
    public void createBond(BluetoothDevice device) {
        BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
        try {
            Method method = BluetoothDevice.class.getMethod("createBond");
            method.invoke(device);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * 取消配对
     *
     * @param device BLE设备
     */
    public void removeBond(BluetoothDevice device) {
        BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
        try {
            Method method = BluetoothDevice.class.getMethod("removeBond");
            method.invoke(device);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }


    /**
     * 将4个字节的数拼成一个int值
     *
     * @param valueH     16进制 字符串 最高位
     * @param valueTwo   16进制 字符串
     * @param valueThree 16进制 字符串
     * @param valueL     16进制 字符串 最低位
     * @return 十进制的 int值
     */
    public static int getMergeByte(String valueH, String valueTwo, String valueThree, String valueL) {
        return (Integer.parseInt(valueH, 16) & 0xff) << 24 |
                (Integer.parseInt(valueTwo, 16) & 0xff) << 16 |
                (Integer.parseInt(valueThree, 16) & 0xff) << 8 |
                Integer.parseInt(valueL, 16) & 0xff;
    }


}
