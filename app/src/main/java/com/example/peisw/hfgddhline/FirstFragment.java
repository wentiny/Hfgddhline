package com.example.peisw.hfgddhline;

import android.app.AlertDialog;
import android.app.Fragment;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
    public static FirstFragment newInstance(String context){
        FirstFragment firstFragment = new FirstFragment();
        Bundle bundle = new Bundle();
        bundle.putString("context",context);
        firstFragment.setArguments(bundle);
        return firstFragment;
    }

    //tab01
    //地图控件对象
    MapView mMapView = null;
    AMap aMap;
    MyLocationStyle myLocationStyle;
    LatLng start;

    List<String> listRegions,listDvLat,listDvLon,listDvId,listDvName;
    List<String> listWorkName,listWorkId,listAreaName,listAreaId;
    List<String> cableLat,cableLon,cableName;
    String tempSelectedRegion,tmpSelectedDvname,tmpSelectedDvid;
    String tmpCableLat,tmCableLon;
    Double tmpSelectedDvLat,tmpSelectedDvLon;
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState){
        View view = null;
        final Bundle args = getArguments();

        if(args.getString("context").equals("tab01")){
            view = inflater.inflate(R.layout.activity_tab01,container,false);


            listRegions = new ArrayList<>();
            listDvId = new ArrayList<>();
            listDvLat = new ArrayList<>();
            listDvLon = new ArrayList<>();
            listDvName = new ArrayList<>();

            listWorkName = new ArrayList<>();
            listAreaName = new ArrayList<>();
            listWorkId = new ArrayList<>();
            listAreaId = new ArrayList<>();

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
                        start = new LatLng(location.getLatitude(),location.getLongitude());
                        //latLngs.add(new LatLng(location.getLatitude(),location.getLongitude()));
                        //Polyline polyline =aMap.addPolyline(new PolylineOptions().
                        //addAll(latLngs).width(10).color(Color.argb(255, 1, 1, 1)));
                    }
                });
            }

            final TextView tv19 = (TextView)view.findViewById(R.id.textView19);
            tv19.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    View cv = View.inflate(getActivity(),R.layout.dialog_four_spinner,null);
                    builder.setTitle("选择设备点").setView(cv);
                    final AlertDialog dialog = builder.create();
                    Spinner sp8 = (Spinner)cv.findViewById(R.id.spinner8);
                    final Spinner sp9 = (Spinner)cv.findViewById(R.id.spinner9);
                    final Spinner sp6 = (Spinner)cv.findViewById(R.id.spinner6);
                    final Spinner sp7 = (Spinner)cv.findViewById(R.id.spinner7);
                    Button btn2 = (Button)cv.findViewById(R.id.button2);
                    Button btn12 = (Button)cv.findViewById(R.id.button12);

                    listWorkId.clear();listWorkName.clear();
                    try {
                        JSONObject json1 = new JSONObject(method.doPostWithoutValue(method.url01+"/returnAllwork"));
                        for(int i1=0;i1<json1.length()/2;i1++){
                            listWorkId.add(json1.optString("orgid"+i1));
                            listWorkName.add(json1.optString("orgname"+i1));
                        }
                    } catch (JSONException e) {e.printStackTrace();}
                    final ArrayAdapter<String> adapter8 = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,listWorkName);
                    adapter8.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    sp8.setAdapter(adapter8);
                    sp8.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position8, long l) {
                            listAreaId.clear();listAreaName.clear();
                            String tempWorkid = listWorkId.get(position8);
                            listAreaName.clear();listAreaId.clear();
                            Map<String,String> map1 = new HashMap<String, String>();
                            map1.put("orgid",tempWorkid);
                            try {
                                JSONObject json2 = new JSONObject(method.doPostUseMap(method.url01+"/returnPart",map1));
                                for(int i2=0;i2<json2.length()/2;i2++){
                                    listAreaId.add(json2.optString("partid"+i2));
                                    listAreaName.add(json2.optString("partname"+i2));
                                }
                            } catch (JSONException e) {e.printStackTrace();}

                            ArrayAdapter<String> adapter9 = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,listAreaName);
                            adapter9.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            sp9.setAdapter(adapter9);
                            sp9.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int position9, long l) {
                                    listRegions.clear();
                                    final Map<String,String> map1 = new HashMap<String, String>();
                                    map1.put("orgid",listAreaId.get(position9));
                                    try {
                                        JSONObject json_1 = new JSONObject(method.doPostUseMap(method.url01+"/INavi/selectRegion",map1));
                                        String[] tempRegions = json_1.optString("inavireg").split(",");
                                        for(int i=0;i<tempRegions.length;i++){
                                            listRegions.add(tempRegions[i]);
                                        }
                                    } catch (JSONException e) {e.printStackTrace();}
                                    ArrayAdapter<String> adapter6 = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,listRegions);
                                    adapter6.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    sp6.setAdapter(adapter6);
                                    sp6.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> adapterView, View view, int position6, long l) {
                                            tempSelectedRegion = listRegions.get(position6);
                                            //
                                            listDvName.clear();listDvLon.clear();listDvId.clear();listDvLat.clear();
                                            Map<String,String> map2 = new HashMap<String, String>();
                                            map2.put("orgid",ENavi_Users_View.orgid);
                                            map2.put("region",tempSelectedRegion);
                                            try {
                                                JSONObject json_2 = new JSONObject(method.doPostUseMap(method.url01+"/INavi/selectDevice",map2));
                                                for(int j=0;j<json_2.length();j++){
                                                    JSONObject jsonTemp = new JSONObject(json_2.optString("jarray"+j));
                                                    listDvId.add(jsonTemp.optString("ROWID"));
                                                    listDvLat.add(jsonTemp.optString("LATITUDE"));
                                                    listDvLon.add(jsonTemp.optString("LONGTITUDE"));
                                                    listDvName.add(jsonTemp.optString("DEVICE_NAME"));
                                                }
                                            } catch (JSONException e) {e.printStackTrace();}
                                            ArrayAdapter<String> adapter7 = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,listDvName);
                                            adapter7.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                            sp7.setAdapter(adapter7);
                                            sp7.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                @Override
                                                public void onItemSelected(AdapterView<?> adapterView, View view, int position7, long l) {
                                                    //LatLng latLng = new LatLng(Double.parseDouble(listDvLat.get(position7)),Double.parseDouble(listDvLon.get(position7)));
                                                    //final Marker marker = aMap.addMarker(new MarkerOptions().position(latLng).title("北京").snippet("DefaultMarker"));
                                                    tmpSelectedDvLat = Double.parseDouble(listDvLat.get(position7));
                                                    tmpSelectedDvLon = Double.parseDouble(listDvLon.get(position7));
                                                    tmpSelectedDvname = listDvName.get(position7);
                                                    tmpSelectedDvid = listDvId.get(position7);
                                                }

                                                @Override
                                                public void onNothingSelected(AdapterView<?> adapterView) {
                                                    tmpSelectedDvLat = Double.parseDouble(listDvLat.get(0));
                                                    tmpSelectedDvLon = Double.parseDouble(listDvLon.get(0));
                                                    tmpSelectedDvname = listDvName.get(0);
                                                    tmpSelectedDvid = listDvId.get(0);
                                                }
                                            });

                                        }
                                        @Override
                                        public void onNothingSelected(AdapterView<?> adapterView) {
                                            //tempSelectedRegion = listRegions.get(0);
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

                    btn2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            tv19.setText(tmpSelectedDvname);
                            dialog.dismiss();
                        }
                    });
                    btn12.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });

                    dialog.show();
                }
            });

            Button btn13 = (Button)view.findViewById(R.id.button13);
            btn13.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LatLng latLng = new LatLng(tmpSelectedDvLat,tmpSelectedDvLon);
                    final Marker marker = aMap.addMarker(new MarkerOptions().position(latLng).title(tmpSelectedDvname).snippet("DefaultMarker"));
                    CameraUpdateFactory cmuf = new CameraUpdateFactory();
                    cmuf.zoomIn();cmuf.zoomIn();cmuf.zoomIn();cmuf.zoomIn();
                    CameraUpdate cm = cmuf.changeLatLng(latLng);
                    aMap.moveCamera(cm);
                }
            });

            Button btn14 = (Button)view.findViewById(R.id.button14);
            btn14.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Poi pois = new Poi("当前位置",start,"");
                    Poi poie = new Poi(tmpSelectedDvname,new LatLng(tmpSelectedDvLat,tmpSelectedDvLon),"");
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
                }
            });

            //aMap.setOnMarkerClickListener(markerClickListener);
        }
        if(args.getString("context").equals("tab02")){
            view = inflater.inflate(R.layout.activity_tab02,container,false);

            listWorkName = new ArrayList<>();
            listAreaName = new ArrayList<>();
            listWorkId = new ArrayList<>();
            listAreaId = new ArrayList<>();

            cableLat = new ArrayList<>();
            cableLon = new ArrayList<>();
            cableName = new ArrayList<>();

            mMapView = (MapView) view.findViewById(R.id.map2);
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
                        start = new LatLng(location.getLatitude(),location.getLongitude());
                        //latLngs.add(new LatLng(location.getLatitude(),location.getLongitude()));
                        //Polyline polyline =aMap.addPolyline(new PolylineOptions().
                        //addAll(latLngs).width(10).color(Color.argb(255, 1, 1, 1)));
                    }
                });
            }

            TextView tv19_1 = (TextView)view.findViewById(R.id.textView19_1);
            tv19_1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    View cv = View.inflate(getActivity(),R.layout.dialog_four_spinner,null);
                    builder.setTitle("选择电缆").setView(cv);
                    final AlertDialog dialog = builder.create();
                    Spinner sp8 = (Spinner)cv.findViewById(R.id.spinner8);
                    final Spinner sp9 = (Spinner)cv.findViewById(R.id.spinner9);
                    final Spinner sp6 = (Spinner)cv.findViewById(R.id.spinner6);
                    final Spinner sp7 = (Spinner)cv.findViewById(R.id.spinner7);sp7.setVisibility(View.GONE);
                    Button btn2 = (Button)cv.findViewById(R.id.button2);
                    Button btn12 = (Button)cv.findViewById(R.id.button12);

                    listWorkId.clear();listWorkName.clear();
                    try {
                        JSONObject json1 = new JSONObject(method.doPostWithoutValue(method.url01+"/returnAllwork"));
                        for(int i1=0;i1<json1.length()/2;i1++){
                            listWorkId.add(json1.optString("orgid"+i1));
                            listWorkName.add(json1.optString("orgname"+i1));
                        }
                    } catch (JSONException e) {e.printStackTrace();}
                    final ArrayAdapter<String> adapter8 = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,listWorkName);
                    adapter8.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    sp8.setAdapter(adapter8);
                    sp8.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, final int position8, long l) {
                            String tempWorkid = listWorkId.get(position8);
                            listAreaName.clear();listAreaId.clear();
                            Map<String,String> map1 = new HashMap<String, String>();
                            map1.put("orgid",tempWorkid);
                            try {
                                JSONObject json2 = new JSONObject(method.doPostUseMap(method.url01+"/returnPart",map1));
                                for(int i2=0;i2<json2.length()/2;i2++){
                                    listAreaId.add(json2.optString("partid"+i2));
                                    listAreaName.add(json2.optString("partname"+i2));
                                }
                            } catch (JSONException e) {e.printStackTrace();}

                            ArrayAdapter<String> adapter9 = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,listAreaName);
                            adapter9.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            sp9.setAdapter(adapter9);
                            sp9.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int position9, long l) {
                                    cableName.clear();cableLon.clear();cableLat.clear();
                                    Map<String,String> map3 = new HashMap<String, String>();
                                    map3.put("orgid",listAreaId.get(position9));
                                    try {
                                        JSONObject json3 = new JSONObject(method.doPostUseMap(method.url01+"/newINavi/selectCable",map3));
                                        for(int i3=0;i3<json3.length()/3;i3++){
                                            //JSONObject jsonTemp = new JSONObject(json3.optString("jarray"+i3));
//                                            cableName.add(jsonTemp.optString("CABLE_NAME"));
//                                            cableLat.add(jsonTemp.optString("LATITUDE"));
//                                            cableLon.add(jsonTemp.optString("LONGTITUDE"));
                                            cableName.add(json3.optString("cablenm"+i3));
                                            cableLat.add(json3.optString("lat"+i3));
                                            cableLon.add(json3.optString("lon"+i3));
                                        }
                                    } catch (JSONException e) {e.printStackTrace();}

                                    ArrayAdapter<String> adapter6 = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,cableName);
                                    adapter6.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    sp6.setAdapter(adapter6);
                                    sp6.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> adapterView, View view, int position6, long l) {
                                            tmpCableLat = cableLat.get(position6);
                                            tmCableLon = cableLon.get(position6);
                                        }
                                        @Override
                                        public void onNothingSelected(AdapterView<?> adapterView) {
                                            tmpCableLat = cableLat.get(0);
                                            tmCableLon = cableLon.get(0);
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

                    dialog.show();
                }
            });

            Button btn13_1 = (Button)view.findViewById(R.id.button13_1);
            btn13_1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String[] latArray = tmpCableLat.split(",");
                    String[] lonArray = tmCableLon.split(",");
                    List<LatLng> latLngs = new ArrayList<LatLng>();
                    for(int i=0;i<latArray.length;i++){
                        latLngs.add(new LatLng(Double.parseDouble(latArray[i]),Double.parseDouble(lonArray[i])));
                    }
                    Polyline polyline =aMap.addPolyline(new PolylineOptions().
                            addAll(latLngs).width(10).color(Color.argb(255, 1, 1, 1)));
                }
            });

        }
        return view;
    }

//    double lat=0,lon=0;
//    AMap.OnMarkerClickListener markerClickListener = new AMap.OnMarkerClickListener() {
//        @Override
//        public boolean onMarkerClick(Marker marker) {
////            double lat = marker.getPosition().latitude;
////            double lon = marker.getPosition().longitude;
//            String[] temp = marker.getTitle().split(".");
//            final String dvid = temp[0];
//            final String dvnm = temp[1];
//            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//            builder.setPositiveButton("导航", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialogInterface, int i) {
//                    Map<String,String> mapTemp = new HashMap<String, String>();
//                    mapTemp.put("dvid",dvid);
//                    try {
//                        JSONObject jsonTemp = new JSONObject(method.doPostUseMap(method.url01+"/newINavi/selectNvByDvid",mapTemp));
//                        JSONObject j = new JSONObject(jsonTemp.optString("jarray0"));
//                        lat = Double.parseDouble(j.optString("LATITUDE"));
//                        lon = Double.parseDouble(j.optString("LONGTITUDE"));
//                    } catch (JSONException e) {e.printStackTrace();}
//                    LatLng end = new LatLng(lat,lon);
//                    Poi pois = new Poi("当前位置",start,"");
//                    Poi poie = new Poi(dvnm,end,"");
//                    AmapNaviPage.getInstance().showRouteActivity(getActivity(), new AmapNaviParams(pois, null,poie, AmapNaviType.DRIVER), new INaviInfoCallback() {
//                        @Override
//                        public void onInitNaviFailure() {
//
//                        }
//
//                        @Override
//                        public void onGetNavigationText(String s) {
//
//                        }
//
//                        @Override
//                        public void onLocationChange(AMapNaviLocation aMapNaviLocation) {
//
//                        }
//
//                        @Override
//                        public void onArriveDestination(boolean b) {
//
//                        }
//
//                        @Override
//                        public void onStartNavi(int i) {
//
//                        }
//
//                        @Override
//                        public void onCalculateRouteSuccess(int[] ints) {
//
//                        }
//
//                        @Override
//                        public void onCalculateRouteFailure(int i) {
//
//                        }
//
//                        @Override
//                        public void onStopSpeaking() {
//
//                        }
//
//                        @Override
//                        public void onReCalculateRoute(int i) {
//
//                        }
//
//                        @Override
//                        public void onExitPage(int i) {
//
//                        }
//
//                        @Override
//                        public void onStrategyChanged(int i) {
//
//                        }
//
//                        @Override
//                        public View getCustomNaviBottomView() {
//                            return null;
//                        }
//
//                        @Override
//                        public View getCustomNaviView() {
//                            return null;
//                        }
//
//                        @Override
//                        public void onArrivedWayPoint(int i) {
//
//                        }
//                    });
//                }
//            });builder.show();
//            return true;
//        }
//    };

}
