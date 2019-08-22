package com.example.peisw.hfgddhline;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.peisw.hfgddhline.utils.ActionSheetDialog;
import com.example.peisw.hfgddhline.utils.MyUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wentiny on 2019/8/8.
 */

public class Import_Wuhan_Panda9 extends Activity implements View.OnClickListener{

    List<String> listData8,listPointnm,listLatlng,listRegions,
            list_nvptName,list_nvptRowid,list_nvptLat,list_nvptLon,
            list_walkpt_lat,list_walkpt_lon,list_walkpt_name;
    String tmpSelectedKmlfile;
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wuhan_panda9_import);

        getData();
        initView();
        doView();

        tv8.performClick();
    }

    String orgid,empid,empname;
    private void getData(){
        orgid = getIntent().getStringExtra("orgid");
        empid = getIntent().getStringExtra("empid");
        empname = getIntent().getStringExtra("empname");
    }

    private Button btn5,btn7,btn11;
    private Spinner sp1;
    private TextView tv8;
    private ListView lv1;
    private void initView(){
        btn5 = (Button)findViewById(R.id.button5);
        btn7 = (Button)findViewById(R.id.button7);
        btn11 = (Button)findViewById(R.id.button11);
        sp1 = (Spinner)findViewById(R.id.spinner);
        tv8 = (TextView)findViewById(R.id.textView8);
        lv1 = (ListView)findViewById(R.id.listview1);

        listData8 = new ArrayList<>();
        listPointnm = new ArrayList<>();
        listLatlng = new ArrayList<>();
        listRegions = new ArrayList<>();

        list_nvptLat = new ArrayList<>();
        list_nvptLon = new ArrayList<>();
        list_nvptName = new ArrayList<>();
        list_nvptRowid = new ArrayList<>();

        list_walkpt_lat = new ArrayList<>();
        list_walkpt_lon = new ArrayList<>();
        list_walkpt_name = new ArrayList<>();
    }

    private void doView(){
        tv8.setOnClickListener(this);
        btn5.setOnClickListener(this);
        btn7.setOnClickListener(this);
        btn11.setOnClickListener(this);
    }

    String temp="";
    String tempLat="";
    String tempLon="";
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            //初始化自动执行，加载spinner的数据KML文件名
            case R.id.textView8:
                listData8.clear();
                Map<String,String> map = new HashMap<String, String>();
                map.put("orgid",getIntent().getStringExtra("orgid"));
                try {
                    JSONObject json8 = new JSONObject(method.doPostUseMap(method.url01+"/INavi/readFile",map));
                    for(int i=0;i<json8.length();i++){
                        listData8.add(json8.optString("filenm"+i));
                    }
                } catch (JSONException e) {e.printStackTrace();}
                ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(Import_Wuhan_Panda9.this,R.layout.shape_spinner_item_view,R.id.tv_spinner,listData8);
                adapter1.setDropDownViewResource(R.layout.shape_spinner_dropdown_view);
                sp1.setAdapter(adapter1);
                sp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int position1, long l) {
                        tmpSelectedKmlfile = listData8.get(position1);
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                        tmpSelectedKmlfile = listData8.get(0);
                    }
                });
                break;
            //查询KML文件下的数据
            case R.id.button5:
                listPointnm.clear();listLatlng.clear();
                Map<String,String> map1 = new HashMap<String, String>();
                map1.put("orgid",getIntent().getStringExtra("orgid"));
                map1.put("kmlfilenm",tmpSelectedKmlfile);
                try {
                    JSONObject json5 = new JSONObject(method.doPostUseMap(method.url01+"/INavi/selectKmlData",map1));
                    for(int i=0;i<json5.length()/2;i++){
                        listPointnm.add(json5.optString("name"+i));
                        listLatlng.add(json5.optString("lalng"+i));
                    }
                } catch (JSONException e) {e.printStackTrace();}
                lv1.setAdapter(listAdapter5);
                MyUtils.setListHeight(lv1);
                break;
            case R.id.button7:
                AlertDialog.Builder builder = new AlertDialog.Builder(Import_Wuhan_Panda9.this);
                View cv = View.inflate(Import_Wuhan_Panda9.this,R.layout.dialog_show_text,null);
                builder.setTitle("提交步行路线").setView(cv);
                AlertDialog dialog = builder.create();

                for(int i=0;i<list_walkpt_name.size();i++){
                    temp = temp+"->("+list_walkpt_name.get(i)+")";
                    tempLat = tempLat+list_walkpt_lat.get(i)+",";
                    tempLon = tempLon+list_walkpt_lon.get(i)+",";
                }

                final EditText editText2 = (EditText)cv.findViewById(R.id.editText2);
                TextView tv16 = (TextView)cv.findViewById(R.id.textView16);
                tv16.setText("您选择的路径是:\n"+temp);
                Button btn9 = (Button)cv.findViewById(R.id.button9);
                btn9.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Map<String,String> map3 = new HashMap<String, String>();
                        map3.put("orgid",orgid);
                        map3.put("name",editText2.getText().toString());
                        map3.put("listlat",tempLat);
                        map3.put("listlon",tempLon);
                        map3.put("empname",empname);
                        try {
                            JSONObject json9 = new JSONObject(method.doPostUseMap(method.url01+"/INavi/importWalkLine",map3));
                            Toast.makeText(getApplicationContext(),json9.optString("msg"),Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {e.printStackTrace();}
                    }
                });
                dialog.show();
                break;
            case R.id.button11:
                AlertDialog.Builder builder1 = new AlertDialog.Builder(Import_Wuhan_Panda9.this);
                View cv1 = View.inflate(Import_Wuhan_Panda9.this,R.layout.dialog_show_text,null);
                builder1.setTitle("提交电缆路径").setView(cv1);
                AlertDialog dialog1 = builder1.create();

                for(int i=0;i<list_walkpt_name.size();i++){
                    temp = temp+"->("+list_walkpt_name.get(i)+")";
                    tempLat = tempLat+list_walkpt_lat.get(i)+",";
                    tempLon = tempLon+list_walkpt_lon.get(i)+",";
                }

                final EditText editText2_1 = (EditText)cv1.findViewById(R.id.editText2);
                TextView tv16_1 = (TextView)cv1.findViewById(R.id.textView16);
                tv16_1.setText("您选择的路径是:\n"+temp);
                Button btn9_1 = (Button)cv1.findViewById(R.id.button9);
                btn9_1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Map<String,String> map3 = new HashMap<String, String>();
                        map3.put("orgid",orgid);
                        map3.put("name",editText2_1.getText().toString());
                        map3.put("listlat",tempLat);
                        map3.put("listlon",tempLon);
                        map3.put("empname",empname);
                        try {
                            JSONObject json9 = new JSONObject(method.doPostUseMap(method.url01+"/INavi/importCableTrend",map3));
                            Toast.makeText(getApplicationContext(),json9.optString("msg"),Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {e.printStackTrace();}
                    }
                });
                dialog1.show();
                break;
        }
    }

    String tmpSelectRegion,tmpIfSetting;
    BaseAdapter listAdapter5 = new BaseAdapter() {
        @Override
        public int getCount() {
            return listPointnm.size();
        }

        @Override
        public Object getItem(int i) {
            return listPointnm.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            LinearLayout ll = (LinearLayout) LayoutInflater.from(Import_Wuhan_Panda9.this).inflate(R.layout.list_item_show_kmldata,null);
            TextView tv10 = (TextView)ll.findViewById(R.id.textView10);
            TextView tv9 = (TextView)ll.findViewById(R.id.textView9);
            final String[] temp = listLatlng.get(i).split(",");
            tv9.setText(listPointnm.get(i));
            tv10.setText("经度: "+temp[0]+" 纬度: "+temp[1]);
            Button btn6 = (Button)ll.findViewById(R.id.button6);
            btn6.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new ActionSheetDialog(Import_Wuhan_Panda9.this).builder()
                            .setTitle("请选择坐标点类型")
                            .setCancelable(false)
                            .setCanceledOnTouchOutside(false)
                            .addSheetItem("设备定位点", ActionSheetDialog.SheetItemColor.Blue,
                                    new ActionSheetDialog.OnSheetItemClickListener() {
                                        @Override
                                        public void onClick(int which) {
                                            Map<String,String> map = new HashMap<String, String>();
                                            map.put("orgid",orgid);
                                            try {
                                                JSONObject json6 = new JSONObject(method.doPostUseMap(method.url01+"/INavi/selectRegion",map));
                                                String[] temp = json6.optString("inavireg").split(",");
                                                for(int i=0;i<temp.length;i++){listRegions.add(temp[i]);}
                                            } catch (JSONException e) {e.printStackTrace();}

                                            AlertDialog.Builder builder = new AlertDialog.Builder(Import_Wuhan_Panda9.this);
                                            final View cv = View.inflate(Import_Wuhan_Panda9.this,R.layout.dialog_add_devicepoint,null);
                                            builder.setView(cv).setTitle("设置设备定位点");
                                            AlertDialog dialog = builder.create();
                                            Spinner sp2 = (Spinner)cv.findViewById(R.id.spinner2);
                                            ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(Import_Wuhan_Panda9.this,R.layout.shape_spinner_item_view,R.id.tv_spinner,listRegions);
                                            adapter2.setDropDownViewResource(R.layout.shape_spinner_dropdown_view);
                                            sp2.setAdapter(adapter2);
                                            sp2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                @Override
                                                public void onItemSelected(AdapterView<?> adapterView, View view, int position2, long l) {
                                                    tmpSelectRegion = listRegions.get(position2);
                                                }
                                                @Override
                                                public void onNothingSelected(AdapterView<?> adapterView) {
                                                    tmpSelectRegion = listRegions.get(0);
                                                }
                                            });

                                            final EditText editText = (EditText)cv.findViewById(R.id.editText);
                                            final ListView lv2 = (ListView)cv.findViewById(R.id.listview2);
                                            RadioGroup radio0102 = (RadioGroup)cv.findViewById(R.id.radio0102);
                                            radio0102.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                                @Override
                                                public void onCheckedChanged(RadioGroup radioGroup, @IdRes int pos) {
                                                    String textTemp = ((RadioButton)cv.findViewById(radioGroup.getCheckedRadioButtonId())).getText().toString();
                                                    if (textTemp.equals("是")){
                                                        lv2.setVisibility(View.GONE);
                                                        tmpIfSetting = "yes";
                                                    }
                                                    if (textTemp.equals("否")){
                                                        lv2.setVisibility(View.VISIBLE);
                                                        tmpIfSetting = "no";

                                                        Map<String,String> map2 = new HashMap<String, String>();
                                                        map2.put("orgid",orgid);
                                                        try {
                                                            JSONObject jsonM2 = new JSONObject(method.doPostUseMap(method.url01+"/INavi/selectNaviPt",map2));
                                                            for(int i=0;i<jsonM2.length();i++){
                                                                JSONObject jsonTemp = new JSONObject(jsonM2.optString("jarray"+i));
                                                                list_nvptRowid.add(jsonTemp.optString("ROWID"));
                                                                list_nvptName.add(jsonTemp.optString("NAVIPOINT_NAME"));
                                                                list_nvptLat.add(jsonTemp.optString("LATITUDE"));
                                                                list_nvptLon.add(jsonTemp.optString("LONGTITUDE"));
                                                            }
                                                        } catch (JSONException e) {e.printStackTrace();}
                                                        lv2.setAdapter(listAdapter2);

                                                    }
                                                }
                                            });
                                            Button btn3 = (Button)cv.findViewById(R.id.button3);
                                            btn3.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {

                                                    Map<String,String> map2 = new HashMap<String, String>();
                                                    map2.put("type","devicept");
                                                    map2.put("name",editText.getText().toString());
                                                    map2.put("ifsetting",tmpIfSetting);
                                                    map2.put("lat",temp[1]);
                                                    map2.put("lon",temp[0]);
                                                    map2.put("region",tmpSelectRegion);
                                                    map2.put("navipt",tmpCheckedNvId);
                                                    map2.put("empname",empname);
                                                    map2.put("orgid",orgid);
                                                    try {
                                                        JSONObject json3 = new JSONObject(method.doPostUseMap(method.url01+"/INavi/Import",map2));
                                                        Toast.makeText(getApplicationContext(),json3.optString("msg"),Toast.LENGTH_SHORT).show();
                                                    } catch (JSONException e) {e.printStackTrace();}
                                                }
                                            });
                                            dialog.show();
                                        }
                                    })
                            .addSheetItem("汽车导航点", ActionSheetDialog.SheetItemColor.Blue,
                                    new ActionSheetDialog.OnSheetItemClickListener() {
                                        @Override
                                        public void onClick(int which) {
                                            Map<String,String> map = new HashMap<String, String>();
                                            map.put("orgid",orgid);
                                            try {
                                                JSONObject json6 = new JSONObject(method.doPostUseMap(method.url01+"/INavi/selectRegion",map));
                                                String[] temp = json6.optString("inavireg").split(",");
                                                for(int i=0;i<temp.length;i++){listRegions.add(temp[i]);}
                                            } catch (JSONException e) {e.printStackTrace();}

                                            AlertDialog.Builder builder = new AlertDialog.Builder(Import_Wuhan_Panda9.this);
                                            final View cv = View.inflate(Import_Wuhan_Panda9.this,R.layout.dialog_add_devicepoint,null);
                                            builder.setView(cv).setTitle("设置汽车导航点");
                                            AlertDialog dialog = builder.create();
                                            //LinearLayout linear_tv8sp2 = (LinearLayout)cv.findViewById(R.id.linear_tv8sp2);
                                            LinearLayout linear_tv12radio1_2 = (LinearLayout)cv.findViewById(R.id.linear_tv12radio1_2);
                                            //linear_tv8sp2.setVisibility(View.GONE);
                                            linear_tv12radio1_2.setVisibility(View.GONE);

                                            Spinner sp2 = (Spinner)cv.findViewById(R.id.spinner2);
                                            ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(Import_Wuhan_Panda9.this,R.layout.shape_spinner_item_view,R.id.tv_spinner,listRegions);
                                            adapter2.setDropDownViewResource(R.layout.shape_spinner_dropdown_view);
                                            sp2.setAdapter(adapter2);
                                            sp2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                @Override
                                                public void onItemSelected(AdapterView<?> adapterView, View view, int position2, long l) {
                                                    tmpSelectRegion = listRegions.get(position2);
                                                }
                                                @Override
                                                public void onNothingSelected(AdapterView<?> adapterView) {
                                                    tmpSelectRegion = listRegions.get(0);
                                                }
                                            });

                                            final EditText editText = (EditText)cv.findViewById(R.id.editText);
                                            Button btn3 = (Button)cv.findViewById(R.id.button3);
                                            btn3.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {

                                                    Map<String,String> map2 = new HashMap<String, String>();
                                                    map2.put("type","navipoint");
                                                    map2.put("name",editText.getText().toString());
                                                    map2.put("ifsetting",tmpIfSetting);
                                                    map2.put("lat",temp[1]);
                                                    map2.put("lon",temp[0]);
                                                    map2.put("region",tmpSelectRegion);
                                                    map2.put("navipt","");
                                                    map2.put("empname",empname);
                                                    map2.put("orgid",orgid);
                                                    try {
                                                        JSONObject json3 = new JSONObject(method.doPostUseMap(method.url01+"/INavi/Import",map2));
                                                        Toast.makeText(getApplicationContext(),json3.optString("msg"),Toast.LENGTH_SHORT).show();
                                                    } catch (JSONException e) {e.printStackTrace();}
                                                }
                                            });
                                            dialog.show();
                                        }
                                    }).show();
                }
            });

            CheckBox ckb = (CheckBox)ll.findViewById(R.id.checkBox);
            ckb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(b==true){
                        list_walkpt_lat.add(temp[1]);
                        list_walkpt_lon.add(temp[0]);
                        list_walkpt_name.add(listPointnm.get(i));
                    }else{
                        list_walkpt_lat.remove(temp[1]);
                        list_walkpt_lon.remove(temp[0]);
                        list_walkpt_name.remove(listPointnm.get(i));
                    }

                }
            });
            return ll;
        }
    };

    String tmpCheckedNvId;
    BaseAdapter listAdapter2 = new BaseAdapter() {
        @Override
        public int getCount() {
            return list_nvptName.size();
        }

        @Override
        public Object getItem(int i) {
            return list_nvptName.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            LinearLayout ll = (LinearLayout)LayoutInflater.from(Import_Wuhan_Panda9.this).inflate(R.layout.list_item_show_kmldata,null);
            Button btn6 = (Button)ll.findViewById(R.id.button6);btn6.setVisibility(View.GONE);
            TextView tv9 = (TextView)ll.findViewById(R.id.textView9);tv9.setText(list_nvptName.get(i));
            TextView tv10 = (TextView)ll.findViewById(R.id.textView10);tv10.setText("经度: "+list_nvptLon.get(i)+" 纬度: "+list_nvptLat.get(i));
            CheckBox ck = (CheckBox)ll.findViewById(R.id.checkBox);
            ck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    if(isChecked==true){
                        tmpCheckedNvId = list_nvptRowid.get(i);
                    }
                }
            });
            return ll;
        }
    };

}
