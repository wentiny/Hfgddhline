package com.example.peisw.hfgddhline;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wentiny on 2019/8/13.
 */

public class Connect_Wuhan_Panda9 extends Activity implements View.OnClickListener{

    private TextView tv13,tv14;
    private Spinner sp3,sp4,sp5;
    private Button btn8;
    private List<String> list_sp3,list_sp4,list_sp4id,list_sp5,list_sp5id;
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wuhan_panda_connect);

        getData();
        initView();
        doView();

        tv13.performClick();
        tv14.performClick();

    }

    String orgid;
    private void getData(){
        orgid = getIntent().getStringExtra("orgid");
    }

    private void initView(){
        tv13 = (TextView)findViewById(R.id.textView13);
        tv14 = (TextView)findViewById(R.id.textView14);
        sp3 = (Spinner)findViewById(R.id.spinner3);
        sp4 = (Spinner)findViewById(R.id.spinner4);
        sp5 = (Spinner)findViewById(R.id.spinner5);
        btn8 = (Button)findViewById(R.id.button8);

        list_sp3 = new ArrayList<>();
        list_sp4 = new ArrayList<>();
        list_sp5 = new ArrayList<>();
        list_sp4id = new ArrayList<>();
        list_sp5id = new ArrayList<>();
    }

    private void doView(){
        tv13.setOnClickListener(this);
        tv14.setOnClickListener(this);
        btn8.setOnClickListener(this);
    }

    String tempSelectedDevicePt,tempSelectedWalkline;
    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.textView13:
                Map<String,String> map1 = new HashMap<String, String>();
                map1.put("orgid",orgid);
                try {
                    JSONObject json13 = new JSONObject(method.doPostUseMap(method.url01+"/INavi/selectRegion",map1));
                    String[] listRegion = json13.optString("inavireg").split(",");
                    for(int i=0;i<listRegion.length;i++){
                        list_sp3.add(listRegion[i]);
                    }
                } catch (JSONException e) {e.printStackTrace();}
                final ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(Connect_Wuhan_Panda9.this,R.layout.shape_spinner_item_view,R.id.tv_spinner,list_sp3);
                adapter3.setDropDownViewResource(R.layout.shape_spinner_dropdown_view);
                sp3.setAdapter(adapter3);
                sp3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int position3, long l) {
                        Map<String,String> map2 = new HashMap<String, String>();
                        map2.put("orgid",orgid);
                        map2.put("region",list_sp3.get(position3));
                        try {
                            JSONObject json_sp3 = new JSONObject(method.doPostUseMap(method.url01+"/INavi/selectDevice",map2));
                            for(int i=0;i<json_sp3.length();i++){
                                JSONObject jsonTemp = new JSONObject(json_sp3.optString("jarray"+i));
                                list_sp4.add(jsonTemp.optString("DEVICE_NAME"));
                                list_sp4id.add(jsonTemp.optString("ROWID"));
                            }
                        } catch (JSONException e) {e.printStackTrace();}
                        ArrayAdapter<String> adapter4 = new ArrayAdapter<String>(Connect_Wuhan_Panda9.this,R.layout.shape_spinner_item_view,R.id.tv_spinner,list_sp4);
                        adapter4.setDropDownViewResource(R.layout.shape_spinner_dropdown_view);
                        sp4.setAdapter(adapter4);
                        sp4.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int position4, long l) {
                                tempSelectedDevicePt = list_sp4id.get(position4);
                            }
                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {
                                tempSelectedDevicePt = list_sp4id.get(0);
                            }
                        });
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {}
                });
                break;
            case R.id.textView14:
                Map<String,String> map2 = new HashMap<String, String>();
                map2.put("orgid",orgid);
                try {
                    JSONObject json14 = new JSONObject(method.doPostUseMap(method.url01+"/INavi/selectWalkLine2",map2));
                    for(int i=0;i<json14.length();i++){
                        JSONObject jsonTemp = new JSONObject(json14.optString("jarray"+i));
                        list_sp5.add(jsonTemp.optString("WALKLINE_NAME"));
                        list_sp5id.add(jsonTemp.optString("ROWID"));
                    }
                } catch (JSONException e) {e.printStackTrace();}
                ArrayAdapter<String> adapter5 = new ArrayAdapter<String>(Connect_Wuhan_Panda9.this,R.layout.shape_spinner_item_view,R.id.tv_spinner,list_sp5);
                adapter5.setDropDownViewResource(R.layout.shape_spinner_dropdown_view);
                sp5.setAdapter(adapter5);
                sp5.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int position5, long l) {
                        tempSelectedWalkline = list_sp5id.get(position5);
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                        tempSelectedWalkline = list_sp5id.get(0);
                    }
                });
                break;
            case R.id.button8:
                AlertDialog.Builder builder = new AlertDialog.Builder(Connect_Wuhan_Panda9.this);
                View cv = View.inflate(Connect_Wuhan_Panda9.this,R.layout.dialog_show_text,null);
                builder.setView(cv).setTitle("绑定");
                AlertDialog dialog = builder.create();
                TextView tv16 = (TextView)cv.findViewById(R.id.textView16);
                //Button btn9 = (Button)cv.findViewById(R.id.b)
                dialog.show();
                break;
        }

    }


}
