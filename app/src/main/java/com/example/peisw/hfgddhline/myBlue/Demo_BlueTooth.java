package com.example.peisw.hfgddhline.myBlue;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.peisw.hfgddhline.R;
import com.example.peisw.hfgddhline.blue.ConnectActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Created by wentiny on 2019/8/13.
 */

public class Demo_BlueTooth extends Activity implements AdapterView.OnItemClickListener {
// 获取到蓝牙适配器
	private BluetoothAdapter mBluetoothAdapter;// 用来保存搜索到的设备信息
    private List<String> bluetoothDevices = new ArrayList<String>();// ListView组件
    private ListView lvDevices;//ListView的字符串数组适配器
    private ArrayAdapter<String> arrayAdapter;// UUID，蓝牙建立链接需要的
	private final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");// 为其链接创建一个名称
    private final String NAME = "BluetoothTest";// 选中发送数据的蓝牙设备，全局变量，否则连接在方法执行完就结束了
    private BluetoothDevice selectDevice;	// 获取到选中设备的客户端串口，全局变量，否则连接在方法执行完就结束了
    private BluetoothSocket clientSocket;	// 获取到向设备写的输出流，全局变量，否则连接在方法执行完就结束了
 	private OutputStream os;	// 服务端利用线程不断接受客户端信息
 	private AcceptThread thread;
    public static boolean isRecording = false;// 线程控制标记

    private TextView tv26;
    private Button btn13;
    private Thread manageThread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_bluetooth);
        // 获取到蓝牙默认的适配器
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();		// 获取到ListView组件
        lvDevices = (ListView) findViewById(R.id.lvDevices);		// 为listview设置字符换数组适配器
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, bluetoothDevices);		// 为listView绑定适配器
        lvDevices.setAdapter(arrayAdapter);		// 为listView设置item点击事件侦听
        lvDevices.setOnItemClickListener(this); 		// 用Set集合保持已绑定的设备
        Set<BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();
        if (devices.size() > 0) {
            for (BluetoothDevice bluetoothDevice : devices) {
                // 保存到arrayList集合中
                bluetoothDevices.add(bluetoothDevice.getName() + ":" + bluetoothDevice.getAddress() + "\n");
            }
        }
        // 因为蓝牙搜索到设备和完成搜索都是通过广播来告诉其他应用的
        // 这里注册找到设备和完成搜索广播
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(receiver, filter);
        filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter); 		// 实例接收客户端传过来的数据线

        checkBluetoothPermission();
//        manageThread = new Demo_BlueTooth.ConnectedThread();
//        manageThread.start();
         thread = new AcceptThread();		// 线程开始
         thread.start();

        tv26 = (TextView)findViewById(R.id.textView26);
        btn13 = (Button)findViewById(R.id.button13);
    }

    public void onClick_Search(View view) {
        setTitle("正在扫描...");		// 点击搜索周边设备，如果正在搜索，则暂停搜索
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
        mBluetoothAdapter.startDiscovery();
    }

    // 注册广播接收者
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context arg0, Intent intent) {
            // 获取到广播的action
            String action = intent.getAction();
            // 判断广播是搜索到设备还是搜索完成
            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                // 找到设备后获取其设备
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // 判断这个设备是否是之前已经绑定过了，如果是则不需要添加，在程序初始化的时候已经添加了
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    // 设备没有绑定过，则将其保持到arrayList集合中
                    bluetoothDevices.add(device.getName() + ":" + device.getAddress() + "\n");
                    // 更新字符串数组适配器，将内容显示在listView中
                    arrayAdapter.notifyDataSetChanged();
                }
            } else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
                setTitle("搜索完成");
            }
        }
    };

    // 点击listView中的设备，传送数据
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // 获取到这个设备的信息
        String s = arrayAdapter.getItem(position);// 对其进行分割，获取到这个设备的地址
        String address = s.substring(s.indexOf(":") + 1).trim();// 判断当前是否还是正在搜索周边设备，如果是则暂停搜索
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
        // 如果选择设备为空则代表还没有选择设备
        if (selectDevice == null) {
            //通过地址获取到该设备
            selectDevice = mBluetoothAdapter.getRemoteDevice(address);
        }
        // 这里需要try catch一下，以防异常抛出
        try {
            // 判断客户端接口是否为空
            if (clientSocket == null) {
                // 获取到客户端接口
                clientSocket = selectDevice.createRfcommSocketToServiceRecord(MY_UUID);// 向服务端发送连接
                clientSocket.connect();
                // 获取到输出流，向外写数据
                os = clientSocket.getOutputStream();
            }
            // 判断是否拿到输出流
            if (os != null) {
                // 需要发送的信息
                String text = "成功发送信息";
                // 以utf-8的格式发送出去
                os.write(text.getBytes("UTF-8"));
            }
            // 吐司一下，告诉用户发送成功
            Toast.makeText(this, "发送信息成功，请查收",  Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            // 如果发生异常则告诉用户发送失败
            Toast.makeText(this, "发送信息失败", Toast.LENGTH_LONG).show();
        }
    }

    // 创建handler，因为我们接收是采用线程来接收的，在线程中无法操作UI，所以需要handler
	Handler handler = new Handler() {
        @Override
        /*public void dispatchMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            // 通过msg传递过来的信息，吐司一下收到的信息
            //Toast.makeText(Demo_BlueTooth.this, (String) msg.obj, Toast.LENGTH_LONG).show();
            switch (msg.what) {
                case 01:
                    String[] temp = ((String)msg.obj).split(",");
                    String tempStr = null;
                    for (int i = 0; i < temp.length; i++) {
                        tempStr = tempStr + temp[i] + "//";
                    }
                    tv26.setText(tempStr);
                    Toast.makeText(getApplicationContext(),(String)msg.obj,Toast.LENGTH_LONG).show();
                    break;
            }*/
            public void handleMessage(final Message msg){
                super.handleMessage(msg);
                btn13.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        tv26.setText((String) msg.obj);
                    }
                });
                Toast.makeText(Demo_BlueTooth.this, (String) msg.obj, Toast.LENGTH_LONG).show();
            }

        };

        // 服务端接收信息线程
       private class AcceptThread extends Thread {
            private BluetoothServerSocket serverSocket;// 服务端接口
            private BluetoothSocket socket;// 获取到客户端的接口
            private InputStream is;// 获取到输入流
            private OutputStream os;// 获取到输出流

            public AcceptThread() {
                try {
                    // 通过UUID监听请求，然后获取到对应的服务端接口
                    serverSocket = mBluetoothAdapter
                            .listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
                } catch (Exception e) {
                    // TODO: handle exception
                }
            }

            public void run() {
                try {
                    // 接收其客户端的接口
                    socket = serverSocket.accept();
                    // 获取到输入流
                    is = socket.getInputStream();
                    // 获取到输出流
                    os = socket.getOutputStream();
                    // 无线循环来接收数据
                    while (true) {
                        // 创建一个128字节的缓冲
                        byte[] buffer = new byte[128];// 每次读取128字节，并保存其读取的角标
                        int count = is.read(buffer);// 创建Message类，向handler发送数据
                        Message msg = new Message();// 发送一个String的数据，让他向上转型为obj类型
                        msg.obj = new String(buffer, 0, count, "utf-8");
                        // 发送数据
                        handler.sendMessage(msg);
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                    e.printStackTrace();
                }
            }
        }


        class ConnectedThread extends Thread {

            private InputStream inStream = null;// 蓝牙数据输入流
            private long wait;
            private Thread thread;

            public ConnectedThread() {
                isRecording = false;
                this.wait = 100;
                thread = new Thread(new Demo_BlueTooth.ConnectedThread.ReadRunnable());
            }

            public void Stop() {
                isRecording = false;
                // thread.stop();
                // State bb = thread.getState();
            }

            public void Start() {
                isRecording = true;
                State aa = thread.getState();
                if (aa == State.NEW) {
                    thread.start();
                } else
                    thread.resume();
            }

            private class ReadRunnable implements Runnable {
                public void run() {

                    while (isRecording) {

                        try {
                            inStream = ConnectActivity.btSocket.getInputStream();
                        } catch (Exception e) {
                            handler.sendEmptyMessage(02);
                            // TODO Auto-generated catch block
                            // Log.e(TAG, "ON RESUME: Output stream creation
                            // failed.", e);
                        }
                        // char[]dd= new char[40];
                        int length = 1024;
                        byte[] temp = new byte[length];
                        // String readStr="";
                        // keep listening to InputStream while connected
                        if (inStream != null) {
                            try {
                                int count = 0;
                                while (count == 0) {
                                    count = inStream.available();
                                }
                                if (count != 0) {
                                    byte[] bt = new byte[count];
                                    int readCount = 0;
                                    while (readCount < count) {
                                        readCount += inStream.read(bt, readCount, count - readCount);
                                    }
                                    String xx = new String(bt);
                                    xx = xx.replaceAll("\r|\n", "");
                                /*if (xx.contains("$GPGGA"))
                                    xx = StringUtils.substringAfterLast(xx, "$");
                                if (xx.contains("GPGGA"))*/
//                                if (xx.contains("$GPGGA")||xx.contains("$GNGGA"))
//                                    xx = StringUtils.substringAfterLast(xx, "$");
//                                if (xx.contains("GPGGA")||xx.contains("GNGGA"))

                                    handler.obtainMessage(01, 0, -1, xx).sendToTarget();

                                }
                                Thread.sleep(wait);// 延时一定时间缓冲数据
                            } catch (Exception e) {
                                Log.e("TAG", e.toString());
                                // TODO Auto-generated catch block
                                handler.sendEmptyMessage(00);
                            }

                        }
                    }
                }
            }
        }

    private Context context = this;
    private static final int REQUEST_ENABLE_BT = 1;
    /*
  校验蓝牙权限
 */
    private void checkBluetoothPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            //校验是否已具有模糊定位权限
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(Demo_BlueTooth.this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        REQUEST_ENABLE_BT);
            } else {
                //具有权限

            }
        } else {
            //系统不高于6.0直接执行

        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_ENABLE_BT) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //同意权限
                Toast.makeText(context,"获取蓝牙权限成功",Toast.LENGTH_LONG).show();

            } else {
                // 权限拒绝
                Toast.makeText(context,"没有蓝牙权限",Toast.LENGTH_LONG).show();
            }
        }
    }


}


