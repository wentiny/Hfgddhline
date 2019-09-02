package com.example.peisw.hfgddhline;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.peisw.hfgddhline.blue.ConnectActivity;
import com.southgnss.location.SouthGnssManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wentiny on 2019/8/26.
 */

public class Import_Hipate_v30Plus extends Activity implements View.OnClickListener {
    public static boolean isRecording = false;// 线程控制标记


    private OutputStream outStream = null;

    private EditText _txtRead;

    private Import_Hipate_v30Plus.ConnectedThread manageThread;
    private Handler mHandler;

    private String encodeType = "GBK";
    private ImageView iv_go_back;
    private SouthGnssManager mGPSManager;
    private Button star, read, save;
    private TextView lon, lat, state,state2,qujian;
    private ProgressDialog progressDialog;
    String orgid,empid,empname,tmpSelectedRegion;
    List<String> listRegions;
    private Spinner sp13;
    private EditText ed4;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hipate_import_v30plus);

        // 接收线程启动
        manageThread = new Import_Hipate_v30Plus.ConnectedThread();
        mHandler = new Import_Hipate_v30Plus.MyHandler();
        manageThread.Start();
        getData();
        initView();
        doView();
        qujian.performClick();

    }

    private void initView() {
        iv_go_back = (ImageView) findViewById(R.id.iv_go_back);
        lon = (TextView) findViewById(R.id.lon);
        lat = (TextView) findViewById(R.id.lat);
        star = (Button) findViewById(R.id.star);
        state = (TextView) findViewById(R.id.state);
        state2 = (TextView) findViewById(R.id.state2);
        read = (Button) findViewById(R.id.read);
        save = (Button) findViewById(R.id.save);
        qujian = (TextView)findViewById(R.id.qujian);
        sp13 = (Spinner)findViewById(R.id.spinner13);

        ed4 = (EditText)findViewById(R.id.editText4);
        listRegions = new ArrayList<>();
    }

    private void doView() {
        iv_go_back.setOnClickListener(this);
        star.setOnClickListener(this);
        save.setOnClickListener(this);
        qujian.setOnClickListener(this);
    }

    private void getData() {
        orgid = getIntent().getStringExtra("orgid");
        empid = getIntent().getStringExtra("empid");
        empname = getIntent().getStringExtra("empname");
    }

    class ConnectedThread extends Thread {

        private InputStream inStream = null;// 蓝牙数据输入流
        private long wait;
        private Thread thread;

        public ConnectedThread() {
            isRecording = false;
            this.wait = 100;
            thread = new Thread(new Import_Hipate_v30Plus.ConnectedThread.ReadRunnable());
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
                    //Toast.makeText(getApplicationContext(),(String)msg.obj,Toast.LENGTH_LONG).show();
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


    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_go_back:
                finish();
                break;
            case R.id.qujian:
                Map<String,String> map1 = new HashMap<String,String>();
                map1.put("orgid",orgid);
                try {
                    JSONObject json = new JSONObject(method.doPostUseMap(method.url01+"/INavi/selectRegion",map1));
                    String[] arrayStr = json.optString("inavireg").split(",");
                    for(int i=0;i<arrayStr.length;i++){
                        listRegions.add(arrayStr[i]);
                    }
                } catch (JSONException e) {e.printStackTrace();}
                ArrayAdapter<String> adapter13 = new ArrayAdapter<String>(Import_Hipate_v30Plus.this,R.layout.shape_spinner_item_view,R.id.tv_spinner,listRegions);
                adapter13.setDropDownViewResource(R.layout.shape_spinner_dropdown_view);
                sp13.setAdapter(adapter13);
                sp13.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int position13, long l) {
                        tmpSelectedRegion = listRegions.get(position13);
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                        tmpSelectedRegion = listRegions.get(0);
                    }
                });
                break;
//            case R.id.star:
//                mGPSManager.startGnssInfo();
//                break;
            case R.id.save:
                progressDialog = ProgressDialog.show(Import_Hipate_v30Plus.this, null, "保存中...");

                Map<String,String> map2 = new HashMap<String, String>();
                map2.put("type","devicept");map2.put("name",ed4.getText().toString());
                map2.put("ifsetting","yes");map2.put("lat",gpslat);map2.put("lon",gpslon);
                map2.put("region",tmpSelectedRegion);map2.put("navipt","");
                map2.put("empname",empname);map2.put("orgid",orgid);
                try {
                    JSONObject j = new JSONObject(method.doPostUseMap(method.url01+"/INavi/Import",map2));
                    Toast.makeText(getApplicationContext(), j.optString("msg"), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "保存失败", Toast.LENGTH_SHORT).show();
                    //progressDialog.dismiss();
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
