package com.example.peisw.hfgddhline;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
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

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Poi;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.navi.AmapNaviPage;
import com.amap.api.navi.AmapNaviParams;
import com.amap.api.navi.AmapNaviType;
import com.amap.api.navi.INaviInfoCallback;
import com.amap.api.navi.model.AMapNaviLocation;
import com.example.peisw.hfgddhline.blue.ConnectActivity;
import com.example.peisw.hfgddhline.myBlue.Demo_Bluetoothv2;
import com.example.peisw.hfgddhline.utils.ActionSheetDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wentiny on 2019/8/5.
 */

public class FirstFragment extends Fragment {

    private String context;

    public static FirstFragment newInstance(String context) {
        FirstFragment firstFragment = new FirstFragment();
        Bundle bundle = new Bundle();
        bundle.putString("context", context);
        firstFragment.setArguments(bundle);
        return firstFragment;
    }

    //tab01
    //地图控件对象
    MapView mMapView = null;
    AMap aMap;
    MyLocationStyle myLocationStyle;
    LatLng start;


    List<String> listRegions;
    List<String> listWorkName, listWorkId, listAreaName, listAreaId;
    List<String> listDvName,listDvId,listDvLat,listDvLon,listColFlag;
    List<String> cableLat, cableLon, cableName;
    String tempSelectedRegion,tempSelectedAreaid;
//    List<String> listWorkName2, listWorkId2, listAreaName2, listAreaId2;
//
//    String tempSelectedRegion, tmpSelectedDvname, tmpSelectedDvid, tmpSelectedAreaid;
//    String tmpCableLat, tmCableLon, tmpCablename;
//    Double tmpSelectedDvLat, tmpSelectedDvLon;
    EditText ed3;
    AlertDialog dialog;
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = null;
        final Bundle args = getArguments();

        if (args.getString("context").equals("tab01")) {
            view = inflater.inflate(R.layout.activity_tab01, container, false);

            mMapView = (MapView) view.findViewById(R.id.map1);
            mMapView.onCreate(savedInstanceState);
            if (aMap == null) {
                aMap = mMapView.getMap();

                myLocationStyle = new MyLocationStyle();
                myLocationStyle.interval(5000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
                aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
                aMap.setMyLocationEnabled(false);
                aMap.setOnMyLocationChangeListener(new AMap.OnMyLocationChangeListener() {
                    @Override
                    public void onMyLocationChange(Location location) {
                        start = new LatLng(location.getLatitude(), location.getLongitude());
                        //latLngs.add(new LatLng(location.getLatitude(),location.getLongitude()));
                        //Polyline polyline =aMap.addPolyline(new PolylineOptions().
                        //addAll(latLngs).width(10).color(Color.argb(255, 1, 1, 1)));
                    }
                });
            }

            listRegions = new ArrayList<>();
            listWorkName = new ArrayList<>();
            listAreaName = new ArrayList<>();
            listWorkId = new ArrayList<>();
            listAreaId = new ArrayList<>();

            listDvId = new ArrayList<>();
            listDvLat = new ArrayList<>();
            listDvLon = new ArrayList<>();
            listDvName = new ArrayList<>();
            listColFlag = new ArrayList<>();

            cableName = new ArrayList<>();
            cableLat = new ArrayList<>();
            cableLon = new ArrayList<>();

            LinearLayout linear_imgv3ed3btn15 = (LinearLayout)view.findViewById(R.id.linear_imgv3ed3btn15);
            ed3 = (EditText)view.findViewById(R.id.editText3);
            Button btn15 = (Button)view.findViewById(R.id.button15);

            linear_imgv3ed3btn15.bringToFront();
            WindowManager wmanager = (WindowManager) getActivity().getSystemService(getActivity().WINDOW_SERVICE);
            Display displays = wmanager.getDefaultDisplay();
            int w =displays.getWidth();
            LinearLayout.LayoutParams ed3_lp = (LinearLayout.LayoutParams)ed3.getLayoutParams();
            ed3_lp.weight = (float)0.56*w;
            LinearLayout.LayoutParams btn15_lp = ( LinearLayout.LayoutParams)btn15.getLayoutParams();
            btn15_lp.weight = (float)0.14*w;
            btn15.setLayoutParams(btn15_lp);
            ed3.setLayoutParams(ed3_lp);

            ed3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    final View cv = View.inflate(getActivity(), R.layout.dialog_search_data, null);
                    builder.setTitle("").setView(cv);
                    dialog = builder.create();

                    WindowManager wm = (WindowManager) getActivity().getSystemService(getActivity().WINDOW_SERVICE);
                    Display display = wm.getDefaultDisplay();
                    int width =display.getWidth();
                    int height=display.getHeight();
                    Window dialogWindow = dialog.getWindow();
                    WindowManager.LayoutParams lp = dialogWindow.getAttributes();
                    dialogWindow.setGravity(Gravity.LEFT | Gravity.TOP);//lp.x与lp.y表示相对于原始位置的偏移.
                    // 将对话框的大小按屏幕大小的百分比设置
                    lp.x = (int) (width*0.05); // 新位置X坐标
                    lp.y = (int) (height*0.2); // 新位置Y坐标
                    lp.width = (int) (width*0.9); // 宽度
                    lp.height = (int) (height*0.6); // 高度
                    dialogWindow.setAttributes(lp);

                    Spinner sp10 = (Spinner) cv.findViewById(R.id.spinner10);
                    final Spinner sp11 = (Spinner) cv.findViewById(R.id.spinner11);
                    final Spinner sp12 = (Spinner) cv.findViewById(R.id.spinner12);
                    Button btn16 = (Button)cv.findViewById(R.id.button16);
                    final RadioGroup radioGroup0304 = (RadioGroup)cv.findViewById(R.id.radio0304);
                    final RadioButton radio03 = (RadioButton)cv.findViewById(R.id.radioButton3);
                    final RadioButton radio04 = (RadioButton)cv.findViewById(R.id.radioButton4);
                    final TextView tv22 = (TextView)cv.findViewById(R.id.textView22);
                    radioGroup0304.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup radioGroup, @IdRes int checkedId) {
                            RadioButton radioBtn = (RadioButton)cv.findViewById(radioGroup.getCheckedRadioButtonId());
                            if(radioBtn.getText().toString().equals("重点设备")){
                                tv22.setVisibility(View.VISIBLE);sp12.setVisibility(View.VISIBLE);
                            }
                            if(radioBtn.getText().toString().equals("电缆路径")){
                                tv22.setVisibility(View.GONE);sp12.setVisibility(View.GONE);
                            }
                        }
                    });

                    listWorkId.clear();listWorkName.clear();
                    try {
                        JSONObject json1 = new JSONObject(method.doPostWithoutValue(method.url01 + "/returnAllwork"));
                        for (int i1 = 0; i1 < json1.length() / 2; i1++) {
                            listWorkId.add(json1.optString("orgid" + i1));
                            listWorkName.add(json1.optString("orgname" + i1));
                        }
                    } catch (JSONException e) {e.printStackTrace();}
                    final ArrayAdapter<String> adapter10 = new ArrayAdapter<String>(getActivity(), R.layout.shape_spinner_item_view,R.id.tv_spinner ,listWorkName);
                    adapter10.setDropDownViewResource(R.layout.shape_spinner_dropdown_view);
                    sp10.setAdapter(adapter10);
                    sp10.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position10, long l) {
                            listAreaId.clear();listAreaName.clear();//每查一次先清空列表
                            String tempWorkid = listWorkId.get(position10);
                            listAreaName.clear();
                            listAreaId.clear();
                            Map<String, String> map1 = new HashMap<String, String>();
                            map1.put("orgid", tempWorkid);
                            try {
                                JSONObject json2 = new JSONObject(method.doPostUseMap(method.url01 + "/returnPart", map1));
                                for (int i2 = 0; i2 < json2.length() / 2; i2++) {
                                    listAreaId.add(json2.optString("partid" + i2));
                                    listAreaName.add(json2.optString("partname" + i2));
                                }
                            } catch (JSONException e) {e.printStackTrace();}
                            ArrayAdapter<String> adapter11 = new ArrayAdapter<String>(getActivity(), R.layout.shape_spinner_item_view, R.id.tv_spinner,listAreaName);
                            adapter11.setDropDownViewResource(R.layout.shape_spinner_dropdown_view);
                            sp11.setAdapter(adapter11);
                            sp11.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int position11, long l) {
                                    listRegions.clear();
                                    tempSelectedAreaid = listAreaId.get(position11);
                                    final Map<String, String> map1 = new HashMap<String, String>();
                                    map1.put("orgid",listAreaId.get(position11));
                                    try {
                                        JSONObject json_1 = new JSONObject(method.doPostUseMap(method.url01 + "/INavi/selectRegion", map1));
                                        String[] tempRegions = json_1.optString("inavireg").split(",");
                                        for (int i = 0; i < tempRegions.length; i++) {
                                            listRegions.add(tempRegions[i]);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    ArrayAdapter<String> adapter12 = new ArrayAdapter<String>(getActivity(), R.layout.shape_spinner_item_view,R.id.tv_spinner ,listRegions);
                                    adapter12.setDropDownViewResource(R.layout.shape_spinner_dropdown_view);
                                    sp12.setAdapter(adapter12);
                                    sp12.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> adapterView, View view, int position12, long l) {
                                            tempSelectedRegion = listRegions.get(position12);
                                        }

                                        @Override
                                        public void onNothingSelected(AdapterView<?> adapterView) {
                                            tempSelectedRegion = listRegions.get(0);
                                        }
                                    });
                                }
                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {}
                            });

                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {}
                    });

                    final ListView lv3 = (ListView)cv.findViewById(R.id.listview3);
                    btn16.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(radio03.isChecked()==true){
                                listDvName.clear();listDvLon.clear();listDvId.clear();listDvLat.clear();
                                Map<String, String> map2 = new HashMap<String, String>();
                                map2.put("orgid", tempSelectedAreaid);
                                map2.put("region", tempSelectedRegion);
                                map2.put("empid",ENavi_Users_View.empid);
                                try {
                                    JSONObject json_2 = new JSONObject(method.doPostUseMap(method.url01 + "/INavi/selectDeviceV2", map2));
                                    for (int j = 0; j < json_2.length()/2; j++) {
                                        JSONObject jsonTemp = new JSONObject(json_2.optString("jarray" + j));
                                        listDvId.add(jsonTemp.optString("ROWID"));
                                        listDvLat.add(jsonTemp.optString("LATITUDE"));
                                        listDvLon.add(jsonTemp.optString("LONGTITUDE"));
                                        listDvName.add(jsonTemp.optString("DEVICE_NAME"));
                                        listColFlag.add(json_2.optString("colflag"+j));
                                    }
                                } catch (JSONException e) {e.printStackTrace();}
                                lv3.setAdapter(listAdapter3);
                            }
                            if(radio04.isChecked()==true){
                                cableName.clear();cableLon.clear();cableLat.clear();
                                Map<String, String> map3 = new HashMap<String, String>();
                                map3.put("orgid", tempSelectedAreaid);
                                try {
                                    JSONObject json3 = new JSONObject(method.doPostUseMap(method.url01 + "/newINavi/selectCable", map3));
                                    for (int i3 = 0; i3 < json3.length() / 3; i3++) {
                                        cableName.add(json3.optString("cablenm" + i3));
                                        cableLat.add(json3.optString("lat" + i3));
                                        cableLon.add(json3.optString("lon" + i3));
                                    }
                                } catch (JSONException e) {e.printStackTrace();}
                                lv3.setAdapter(listAdapter3_1);
                            }
                        }
                    });

                    dialog.show();
                }
            });

            btn15.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LatLng latLng = new LatLng(tempSelectDvLat,tempSelectDvLon);
                    Marker marker = aMap.addMarker(new MarkerOptions().position(latLng).title(tempSelectedDvName).snippet(""));
                    CameraUpdateFactory cmuf = new CameraUpdateFactory();
                    cmuf.zoomTo(18);
                    CameraUpdate cm = cmuf.changeLatLng(latLng);
                    aMap.moveCamera(cm);
                }
            });

        }
        if (args.getString("context").equals("tab02")) {
            view = inflater.inflate(R.layout.activity_tab02, container, false);
            TextView tv19 = (TextView)view.findViewById(R.id.textView19);
            tv19.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new ActionSheetDialog(getActivity()).builder()
                            .setTitle("选择仪器型号")
                            .setCancelable(false)
                            .setCanceledOnTouchOutside(false)
                            .addSheetItem("武汉攀达PD9", ActionSheetDialog.SheetItemColor.Blue,
                                    new ActionSheetDialog.OnSheetItemClickListener() {
                                        @Override
                                        public void onClick(int which) {
                                            new ActionSheetDialog(getActivity()).builder()
                                                    .setTitle("操作")
                                                    .setCancelable(false)
                                                    .setCanceledOnTouchOutside(false)
                                                    .addSheetItem("采集", ActionSheetDialog.SheetItemColor.Blue,
                                                            new ActionSheetDialog.OnSheetItemClickListener() {
                                                                @Override
                                                                public void onClick(int which) {
                                                                    if (ConnectActivity.isConnect) {
                                                                        //Intent intent = new Intent(getActivity(), Import_Hipate_v30Plus.class);
                                                                        Intent intent = new Intent(getActivity(), Demo_Bluetoothv2.class);
                                                                        intent.putExtra("orgid",ENavi_Users_View.orgid);
                                                                        intent.putExtra("empid",ENavi_Users_View.empid);
                                                                        intent.putExtra("empname",ENavi_Users_View.empname);
                                                                        startActivity(intent);
                                                                    } else {
                                                                        //Intent intent = new Intent(getActivity(), ConnectActivity.class);
                                                                        Intent intent = new Intent(getActivity(), Demo_Bluetoothv2.class);
                                                                        intent.putExtra("orgid",ENavi_Users_View.orgid);
                                                                        intent.putExtra("empid",ENavi_Users_View.empid);
                                                                        intent.putExtra("empname",ENavi_Users_View.empname);
                                                                        startActivity(intent);
                                                                    }
                                                                }
                                                            })
                                                    .addSheetItem("导入", ActionSheetDialog.SheetItemColor.Blue,
                                                            new ActionSheetDialog.OnSheetItemClickListener() {
                                                                @Override
                                                                public void onClick(int which) {
                                                                    Intent intent = new Intent(getActivity(),Import_Wuhan_Panda9.class);
                                                                    intent.putExtra("orgid",ENavi_Users_View.orgid);
                                                                    intent.putExtra("empid",ENavi_Users_View.empid);
                                                                    intent.putExtra("empname",ENavi_Users_View.empname);
                                                                    startActivity(intent);
                                                                }
                                                            })
                                                    .addSheetItem("关联", ActionSheetDialog.SheetItemColor.Blue,
                                                            new ActionSheetDialog.OnSheetItemClickListener() {
                                                                @Override
                                                                public void onClick(int which) {
                                                                    Intent intent = new Intent(getActivity(),Connect_Wuhan_Panda9.class);
                                                                    intent.putExtra("orgid",ENavi_Users_View.orgid);
                                                                    intent.putExtra("empid",ENavi_Users_View.empid);
                                                                    intent.putExtra("empname",ENavi_Users_View.empname);
                                                                    startActivity(intent);
                                                                }
                                                            }).show();
                                        }
                                    })
                            .addSheetItem("中海达V30plus", ActionSheetDialog.SheetItemColor.Blue,
                                    new ActionSheetDialog.OnSheetItemClickListener() {
                                        @Override
                                        public void onClick(int which) {
                                            if (ConnectActivity.isConnect) {
                                                Intent intent = new Intent(getActivity(), Import_Hipate_v30Plus.class);
                                                //Intent intent = new Intent(getActivity(), Demo_Bluetoothv2.class);
                                                intent.putExtra("orgid",ENavi_Users_View.orgid);
                                                intent.putExtra("empid",ENavi_Users_View.empid);
                                                intent.putExtra("empname",ENavi_Users_View.empname);
                                                startActivity(intent);
                                            } else {
                                                Intent intent = new Intent(getActivity(), ConnectActivity.class);
                                                intent.putExtra("orgid",ENavi_Users_View.orgid);
                                                intent.putExtra("empid",ENavi_Users_View.empid);
                                                intent.putExtra("empname",ENavi_Users_View.empname);
                                                //Intent intent = new Intent(getActivity(), Demo_Bluetoothv2.class);
                                                startActivity(intent);
                                            }
                                        }
                                    }).show();
                }
            });
            TextView tv27 = (TextView)view.findViewById(R.id.textView27);
            tv27.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(),My_Collection.class);
                    intent.putExtra("orgid",ENavi_Users_View.orgid);
                    intent.putExtra("empid",ENavi_Users_View.empid);
                    intent.putExtra("empname",ENavi_Users_View.empname);
                    startActivity(intent);
                }
            });
        }

        return view;
    }

    //点
    String tempSelectedDvName;
    Double tempSelectDvLat,tempSelectDvLon;
    BaseAdapter listAdapter3 = new BaseAdapter() {
        @Override
        public int getCount() {
            return listDvId.size();
        }

        @Override
        public Object getItem(int i) {
            return listDvId.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            LinearLayout ll = (LinearLayout)LayoutInflater.from(getActivity()).inflate(R.layout.list_item_view01,null);
            TextView tv24 = (TextView)ll.findViewById(R.id.textView24);
            tv24.setText(listDvName.get(i));
            tv24.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ed3.setText(listDvName.get(i));
                    tempSelectDvLat = Double.parseDouble(listDvLat.get(i));
                    tempSelectDvLon = Double.parseDouble(listDvLon.get(i));
                    tempSelectedDvName = listDvName.get(i);
                    dialog.dismiss();
                }
            });
            Button btn17 = (Button)ll.findViewById(R.id.button17);
            btn17.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Map<String,String> map = new HashMap<String, String>();
                    map.put("dvid",listDvId.get(i));
                    try {
                        JSONObject json = new JSONObject(method.doPostUseMap(method.url01+"/newINavi/selectNvByDvid",map));
                        JSONObject jsonTemp = new JSONObject(json.optString("jarray0"));
                        Double latTemp = Double.parseDouble(jsonTemp.optString("LATITUDE"));
                        Double lonTemp = Double.parseDouble(jsonTemp.optString("LONGTITUDE"));
                        Poi pois = new Poi("当前位置",start,"");
                        Poi poie = new Poi(listDvName.get(i),new LatLng(latTemp,lonTemp),"");
                        AmapNaviPage.getInstance().showRouteActivity(getActivity(), new AmapNaviParams(pois, null, poie, AmapNaviType.DRIVER), new INaviInfoCallback() {
                            @Override
                            public void onInitNaviFailure() {

                            }

                            @Override
                            public void onGetNavigationText(String s) {

                            }

                            @Override
                            public void onLocationChange(AMapNaviLocation aMapNaviLocation) {

                            }

                            @Override
                            public void onArriveDestination(boolean b) {

                            }

                            @Override
                            public void onStartNavi(int i) {

                            }

                            @Override
                            public void onCalculateRouteSuccess(int[] ints) {

                            }

                            @Override
                            public void onCalculateRouteFailure(int i) {

                            }

                            @Override
                            public void onStopSpeaking() {

                            }

                            @Override
                            public void onReCalculateRoute(int i) {

                            }

                            @Override
                            public void onExitPage(int i) {

                            }

                            @Override
                            public void onStrategyChanged(int i) {

                            }

                            @Override
                            public View getCustomNaviBottomView() {
                                return null;
                            }

                            @Override
                            public View getCustomNaviView() {
                                return null;
                            }

                            @Override
                            public void onArrivedWayPoint(int i) {

                            }
                        });
                    } catch (JSONException e) {e.printStackTrace();}
                }
            });
            CheckBox checkStar = (CheckBox)ll.findViewById(R.id.star);
            if(listColFlag.get(i).equals("true")){
               checkStar.setChecked(true);
            }else{
                checkStar.setChecked(false);
            }
            checkStar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    if(isChecked==true){
                        Map<String,String> map2 = new HashMap<String, String>();
                        map2.put("empid",ENavi_Users_View.empid);
                        map2.put("dvid",listDvId.get(i));
                        try {
                            JSONObject jobj = new JSONObject(method.doPostUseMap(method.url01+"/newINavi/importCollection",map2));
                        } catch (JSONException e) {e.printStackTrace();}

                    }
                }
            });
            return ll;
        }
    };

    //线
    BaseAdapter listAdapter3_1 = new BaseAdapter() {
        @Override
        public int getCount() {
            return cableName.size();
        }

        @Override
        public Object getItem(int i) {
            return cableName.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            LinearLayout ll = (LinearLayout)LayoutInflater.from(getActivity()).inflate(R.layout.list_item_view01,null);
            TextView tv24 = (TextView)ll.findViewById(R.id.textView24);
            tv24.setText(cableName.get(i));
            final List<LatLng> latLngs = new ArrayList<LatLng>();
            tv24.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    aMap.clear();latLngs.clear();
                    String[] latArray = cableLat.get(i).split(",");
                    String[] lonArray = cableLon.get(i).split(",");
                    for (int i = 0; i < latArray.length; i++) {
                        latLngs.add(new LatLng(Double.parseDouble(latArray[i]), Double.parseDouble(lonArray[i])));
                    }
                    Polyline polyline = aMap.addPolyline(new PolylineOptions().
                            addAll(latLngs).width(10).color(Color.argb(255, 1, 1, 1)));
                    CameraUpdateFactory cmuf = new CameraUpdateFactory();
                    CameraUpdate cu = cmuf.changeLatLng(latLngs.get(0));
                    aMap.moveCamera(cu);
                    dialog.dismiss();
                }
            });
            Button btn17 = (Button)ll.findViewById(R.id.button17);btn17.setVisibility(View.GONE);
            return ll;
        }
    };

}

