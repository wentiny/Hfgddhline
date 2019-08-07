package com.example.peisw.hfgddhline.blue;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.peisw.hfgddhline.R;

import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ConnectActivity extends Activity {
    ArrayList<Map<String, Object>> data;
    Button btnopen;
    Button btnclose;
    Button btnserch;
    ListView lv;
    SimpleAdapter adapter;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothDevice mBluetoothDevice;

    static final String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";
    private UUID uuid;
    private static final String TAG = "BluetoothTest";
    private static final boolean STATE_CONNECTED = true;

    public static BluetoothSocket socket = null;
    public static BluetoothSocket btSocket;
    public static AcceptThread serverThread;
    private ImageView iv_go_back;
    private TextView tv_upload;
    private ProgressDialog progressDialog;
    public static boolean isConnect = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lanyalist);
        tv_upload = (TextView) findViewById(R.id.tv_upload);
        tv_upload.setText("蓝牙连接");
        iv_go_back = (ImageView) findViewById(R.id.iv_go_back);
        iv_go_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        lv = (ListView) findViewById(R.id.lylist);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(ConnectActivity.this, "本机没有找到蓝牙硬件或驱动！", Toast.LENGTH_SHORT).show();
            //finish();
        } else {
            // 如果本地蓝牙没有开启，则开启
            if (!mBluetoothAdapter.isEnabled()) {
                // 我们通过startActivityForResult()方法发起的Intent将会在onActivityResult()回调方法中获取用户的选择，比如用户单击了Yes开启，
                // 那么将会收到RESULT_OK的结果，
                // 如果RESULT_CANCELED则代表用户不愿意开启蓝牙
                Intent mIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                try {
                    startActivityForResult(mIntent, 1);
                    mBluetoothAdapter.enable();
                    Toast.makeText(ConnectActivity.this, "蓝牙打开中……", Toast.LENGTH_LONG).show();
                } catch (Error ex) {
                    Toast.makeText(ConnectActivity.this, ex.toString(), Toast.LENGTH_LONG).show();
                }

                // 用enable()方法来开启，无需询问用户(实惠无声息的开启蓝牙设备),这时就需要用到android.permission.BLUETOOTH_ADMIN权限。

                // mBluetoothAdapter.disable();//关闭蓝牙
            }
        }
        PrepareData();
        getmessage();
        openlanya();
        serchlanya();
        closelanya();
        lv.setOnItemClickListener(mDeviceClickListener);
    }


    // 选择设备响应函数
    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        @SuppressWarnings("unchecked")
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            Map<String, Object> map = (Map<String, Object>) adapter.getItem(arg2);
//        	String str = lstDevices.get(arg2);
            progressDialog = ProgressDialog.show(ConnectActivity.this, null, "连接中...");
            String address = (String) map.get("id");


            uuid = UUID.fromString(SPP_UUID);
            Log.e("uuid", uuid.toString());

            BluetoothDevice btDev = mBluetoothAdapter.getRemoteDevice(address);//"00:11:00:18:05:45"
            Method m;
            try {
                m = btDev.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
                btSocket = (BluetoothSocket) m.invoke(btDev, Integer.valueOf(1));
            } catch (Exception e1) {
                e1.printStackTrace();
                Toast.makeText(ConnectActivity.this, "蓝牙未打开……", Toast.LENGTH_LONG).show();

                progressDialog.dismiss();
            }
            setTitle("连接……");
            mBluetoothAdapter.cancelDiscovery();
            try

            {
                //btSocket = btDev.createRfcommSocketToServiceRecord(uuid);
                //btSocket.close();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            btSocket.connect();
                            String Data = "log com1 gpgga ontime 1";
                            byte[] data = Data.getBytes();
                            DataOutputStream dataOutputStream = new DataOutputStream(ConnectActivity.btSocket.getOutputStream());
                            dataOutputStream.write(data);
                        } catch (Exception e) {

                        }
                    }
                }).start();

                Log.e(TAG, " BT connection established, data transfer link open.");

                Toast.makeText(ConnectActivity.this, "连接成功,进入控制界面", Toast.LENGTH_SHORT).show();
                setTitle("连接成功");
                progressDialog.dismiss();
//TODO 跳转到采集列表页面
//                Intent intent = new Intent();
//                intent.setClass(ConnectActivity.this, Select_Pillar.class);
//                intent.putExtra("empid",getIntent().getStringExtra("empid"));
//                intent.putExtra("empname",getIntent().getStringExtra("empname"));
//                intent.putExtra("orgid",getIntent().getStringExtra("orgid"));
//                startActivity(intent);
                isConnect = true;
                finish();

            } catch (Exception e) {
                Log.e(TAG, " Connection failed.", e);
                //Toast.makeText(getApplicationContext(), "连接失败", Toast.LENGTH_SHORT);
                setTitle("连接失败..");
                progressDialog.dismiss();
            }

        }
    };

    class AcceptThread extends Thread {
        private final BluetoothServerSocket serverSocket;

        public AcceptThread() {

            BluetoothServerSocket tmp = null;
            try {

                Log.e(TAG, "++BluetoothServerSocket established!++");
                Method listenMethod = mBluetoothAdapter.getClass().getMethod("listenUsingRfcommOn", new Class[]{int.class});
                tmp = (BluetoothServerSocket) listenMethod.invoke(mBluetoothAdapter, Integer.valueOf(1));

            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }

            serverSocket = tmp;
        }

        public void run() {
            while (true) {
                try {
                    socket = serverSocket.accept();
                    Log.e(TAG, "++BluetoothSocket established! DataLink open.++");
                } catch (IOException e) {
                    break;
                }
                // If a connection was accepted
                if (socket != null) {
                    // Do work to manage the connection (in a separate thread)
                    manageConnectedSocket();
                    try {
                        serverSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }

        /**
         * Will cancel the listening socket, and cause the thread to finish
         */
        public void cancel() {
            try {
                serverSocket.close();
            } catch (IOException e) {
            }
        }
    }


    private void manageConnectedSocket() {
        //setTitle("检测到蓝牙接入！");
        btSocket = socket;
        //TODO 跳转到采集列表页面
//        Intent intent = new Intent();
//        intent.setClass(ConnectActivity.this, Select_Pillar.class);
//        intent.putExtra("empid",getIntent().getStringExtra("empid"));
//        intent.putExtra("empname",getIntent().getStringExtra("empname"));
//        intent.putExtra("orgid",getIntent().getStringExtra("orgid"));
//        startActivity(intent);
        isConnect = true;
        finish();
    }


    //给listview绑定信息
    public void getmessage() {
        if (data != null) {
            adapter = new SimpleAdapter(this, data, R.layout.lanya, new String[]{"姓名", "id", "devices"}, new int[]{R.id.lanyaid, R.id.lanyanumber, R.id.txtdevice});
            lv.setAdapter(adapter);
        }
    }


    //搜索蓝牙设备
    public void serchlanya() {
        btnserch = (Button) findViewById(R.id.btnserch);
        btnserch.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if (mBluetoothAdapter != null) {
                    if (mBluetoothAdapter.isEnabled()) {
                        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
                        Set<BluetoothDevice> devices = adapter.getBondedDevices();
                        data = new ArrayList<Map<String, Object>>();
                        Map<String, Object> item;
                        for (BluetoothDevice device : devices) {
                            item = new HashMap<String, Object>();
                            item.put("姓名", "蓝牙名称: " + device.getName().toString());
                            item.put("id", device.getAddress().toString());
                            item.put("devices", device);
                            data.add(item);
                        }
                        getmessage();
                    } else {
                        Toast.makeText(ConnectActivity.this, "蓝牙未打开！", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ConnectActivity.this, "蓝牙未打开！", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    //关闭蓝牙
    public void closelanya() {
        btnclose = (Button) findViewById(R.id.btnclose);
        btnclose.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if (mBluetoothAdapter != null) {
                    if (mBluetoothAdapter.isEnabled()) {
                        mBluetoothAdapter.disable();
                        Toast.makeText(ConnectActivity.this, "蓝牙已经关闭", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }


    //打开蓝牙
    public void openlanya() {
        btnopen = (Button) findViewById(R.id.btnopen);
        btnopen.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {


            }
        });


    }


    /*
     * 绑定数据（用键值对的形式绑定数据）
     * */
    public void PrepareData() {
        data = new ArrayList<Map<String, Object>>();
//        Map<String, Object> item;
//        item = new HashMap<String, Object>();
//        item.put("姓名", "张三小朋友");
//        item.put("id", "1");
//        item.put("devices", "1");
//        data.add(item);

    }

}
