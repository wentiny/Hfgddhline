package com.example.peisw.hfgddhline.blue;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.peisw.hfgddhline.R;
import com.example.peisw.hfgddhline.method;
import com.southgnss.location.SouthGnssManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;


public class ControlActivity extends Activity implements View.OnClickListener {

    public static boolean isRecording = false;// 线程控制标记


    private OutputStream outStream = null;

    private EditText _txtRead;

    private ConnectedThread manageThread;
    private Handler mHandler;

    private String encodeType = "GBK";
    private ImageView iv_go_back;
    private TextView tv_upload;
    private SouthGnssManager mGPSManager;
    private Button star, read, save;
    private TextView lon, lat, zhizhuhao, state,state2;
    private Button nfcid;
    private String nfcnum = "";
    private String zhizhunum = "";
    private String zhizhuname = "";
    private ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect);

        // 接收线程启动
        manageThread = new ConnectedThread();
        mHandler = new MyHandler();
        manageThread.Start();
        getData();
        initView();
        doView();

    }

    private void initView() {
        tv_upload = (TextView) findViewById(R.id.tv_upload);
        tv_upload.setText("坐标查看");
        iv_go_back = (ImageView) findViewById(R.id.iv_go_back);
        lon = (TextView) findViewById(R.id.lon);
        lat = (TextView) findViewById(R.id.lat);
        star = (Button) findViewById(R.id.star);
        state = (TextView) findViewById(R.id.state);
        state2 = (TextView) findViewById(R.id.state2);
        read = (Button) findViewById(R.id.read);
        save = (Button) findViewById(R.id.save);
        nfcid = (Button) findViewById(R.id.nfcid);
        zhizhuhao = (TextView) findViewById(R.id.zhizhuhao);

        String nfcidStr = "<cont color=\"#212121\">标签号：</font><cont color=\"#e91e63\">(点击后贴近标签)</font>";
        nfcid.setText(Html.fromHtml(nfcidStr));
        nfcid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* AlertDialog.Builder builder = new AlertDialog.Builder(ControlActivity.this);
                View cv = View.inflate(ControlActivity.this,R.layout.dialog_update_pillar_msg,null);
                builder.setTitle("读取NFC编码").setView(cv);
                final AlertDialog alertDialog = builder.create();
                Button btn14 = (Button)cv.findViewById(R.id.button14);
                Button btn13 = (Button)cv.findViewById(R.id.button13);btn13.setText("确定");
                final EditText ed2 = (EditText)cv.findViewById(R.id.editText2);
                btn13.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String str = ed2.getText().toString().substring(0,16);
                        String str1 = str.substring(0,2);
                        String str2 = str.substring(2,4);
                        String str3 = str.substring(4,6);
                        String str4 = str.substring(6,8);
                        String str5 = str.substring(8,10);
                        String str6 = str.substring(10,12);
                        String str7 = str.substring(12,14);
                        String str8 = str.substring(14,16);
                        str = str8+str7+str6+str5+str4+str3+str2+str1;
                        nfcid.setText(str);
                        alertDialog.dismiss();
                    }
                });
                btn14.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();*/
            }
        });

    }

    private void doView() {
        iv_go_back.setOnClickListener(this);
        star.setOnClickListener(this);
        save.setOnClickListener(this);
        zhizhuhao.setText("支柱号：" + zhizhuname);
    }

    private void getData() {
        zhizhunum = getIntent().getStringExtra("pid");
        zhizhuname = getIntent().getStringExtra("fpillar");
    }

    /*
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        // 注册位置监听
        super.onResume();
        //开启前台调度系统
        new NfcUtils(this);
        try {
            NfcUtils.mNfcAdapter.enableForegroundDispatch(this, NfcUtils.mPendingIntent, NfcUtils.mIntentFilter, NfcUtils.mTechList);
        } catch (Exception e) {

        }
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        // 移除位置监听
        //关闭前台调度系统
        try {
            NfcUtils.mNfcAdapter.disableForegroundDispatch(this);
        } catch (Exception e) {
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //当该Activity接收到NFC标签时，运行该方法
        //调用工具方法，读取NFC数据
        try {
            nfcnum = NfcUtils.readNFCId(intent);
            handler.sendEmptyMessage(1);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //nfcid.setText("标签号：" + nfcnum);
            nfcid.setText(nfcnum);
            Toast.makeText(ControlActivity.this, "扫描成功", Toast.LENGTH_SHORT).show();
        }
    };*/


    class ConnectedThread extends Thread {

        private InputStream inStream = null;// 蓝牙数据输入流
        private long wait;
        private Thread thread;

        public ConnectedThread() {
            isRecording = false;
            this.wait = 100;
            thread = new Thread(new ReadRunnable());
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
                        mHandler.sendEmptyMessage(02);
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

                                    mHandler.obtainMessage(01, 0, -1, xx).sendToTarget();

                            }
                            Thread.sleep(wait);// 延时一定时间缓冲数据
                        } catch (Exception e) {
                            Log.e("TAG", e.toString());
                            // TODO Auto-generated catch block
                            mHandler.sendEmptyMessage(00);
                        }

                    }
                }
            }
        }
    }

    private String gpslat="";
    private String gpslon="";
    private class MyHandler extends Handler {
        @Override
        public void dispatchMessage(Message msg) {
            switch (msg.what) {
                case 02:
//                    isRecording = false;
                    Toast.makeText(getApplicationContext(), " 请先连接设备.", Toast.LENGTH_SHORT);
                    finish();

                    // _txtRead.setText("inStream establishment Failed!");
                    break;

                case 01:
                    String info = (String) msg.obj;
                    Log.e("TAG", "---" + info);
                    if (info.split(",").length > 9) {
                        if("$GPGGA".contains(info.split(",")[0])||"$GNGGA".contains(info.split(",")[0])){
                    gpslat = DDtoDMS(info.split(",")[2]);
                    gpslon = DDtoDMS(info.split(",")[4]);
                    //                    String gpshigh = info.split(",")[9] + info.split(",")[10];
                    String gpsstate = "";
                    switch (info.split(",")[6]) {
                        case "0":
                            gpsstate = "无效解";
                            break;
                        case "1":
                            gpsstate = "单点定位解";
                            break;
                        case "2":
                            gpsstate = "伪距差分";
                            break;
                        case "4":
                            gpsstate = "固定解";
                            break;
                        case "5":
                            gpsstate = "浮动解";
                            break;
                    }

                    lon.setText("经度：" + gpslon);
                    lat.setText("纬度：" + gpslat);
                    state.setText("状态：" + gpsstate);
                }
                    }
                    state2.setText(info);
                    break;

                default:
                    break;
            }
        }
    }

    public static String DDtoDMS(String d) {
        if (d != null && !"".equals(d)) {
            d = Double.parseDouble(d) / 100 + "";
            String[] array = d.toString().split("[.]");
            double du = Double.parseDouble(array[0]);//得到度
            double fen = Double.parseDouble("0." + array[1]) * 100 / 60;

            return (du + fen) + "";
        } else {
            return "";
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_go_back:
                finish();
                break;
            case R.id.star:
                mGPSManager.startGnssInfo();
                break;
            case R.id.save:
                progressDialog = ProgressDialog.show(ControlActivity.this, null, "保存中...");

//                if (!"".equals(zhizhunum)||"".equals(nfcnum) ||"".equals(lon.getText().toString()) ||"".equals(lat.getText().toString())) {
//                    Toast.makeText(ControlActivity.this, "请将信息补全", Toast.LENGTH_SHORT).show();
//                    progressDialog.dismiss();
//                } else {
                    List<String> name = new ArrayList<String>();
                    List<String> value = new ArrayList<String>();
                    name.add("pillarid");
                    name.add("nfcid");
                    name.add("lat");
                    name.add("lng");
                    value.add(zhizhunum);
                    value.add(nfcid.getText().toString());
                    value.add(gpslon);
                    value.add(gpslat);
                    String re = method.doPost(method.url01 + "/xxys/bindLatLng", name, value);
                    try {
                        JSONObject j = new JSONObject(re);
                        Toast.makeText(getApplicationContext(), j.optString("msg"), Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                    }

//                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (manageThread!=null){
                isRecording=false;
                manageThread.interrupt();
            }
        }catch (Exception e){

        }
    }

}