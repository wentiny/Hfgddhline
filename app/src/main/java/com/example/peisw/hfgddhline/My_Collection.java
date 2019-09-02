package com.example.peisw.hfgddhline;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
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
 * Created by wentiny on 2019/8/30.
 */

public class My_Collection extends Activity {

    ListView listView4;
    MapView mMapView = null;
    AMap aMap;
    MyLocationStyle myLocationStyle;
    LatLng start;

    List<String> listDeviceName,listDeviceId,listDeviceLat,listDeviceLon
            ,listDeviceRegion,listNaviLat,listNaviLon;
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_collections);

        getData();
        initView();

        mMapView = (MapView)findViewById(R.id.map2);
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

        Map<String,String> map1 = new HashMap<String, String>();
        map1.put("empid",empid);
        try {
            JSONObject json = new JSONObject(method.doPostUseMap(method.url01+"/newINavi/selectCollections",map1));
            for(int i=0;i<json.length()/3;i++){
                JSONObject jsonTemp = new JSONObject(json.optString("jarray"+i));
                listDeviceId.add(jsonTemp.optString("ROWID"));
                listDeviceName.add(jsonTemp.optString("DEVICE_NAME"));
                listDeviceLat.add(jsonTemp.optString("LATITUDE"));
                listDeviceLon.add(jsonTemp.optString("LONGTITUDE"));
                listDeviceRegion.add(jsonTemp.optString("T_SECTION"));
                listNaviLat.add(json.optString("lat"+i));
                listNaviLon.add(json.optString("lon"+i));
            }
        } catch (JSONException e) {e.printStackTrace();}
        listView4.setAdapter(listViewAdapter);

    }

    String empid;
    public void getData(){
        empid = getIntent().getStringExtra("empid");

        listDeviceId = new ArrayList<>();
        listDeviceName = new ArrayList<>();
        listDeviceLat = new ArrayList<>();
        listDeviceLon = new ArrayList<>();
        listNaviLat = new ArrayList<>();
        listNaviLon = new ArrayList<>();
        listDeviceRegion = new ArrayList<>();
    }

    public void initView(){
        mMapView = (MapView)findViewById(R.id.map2);
        listView4 = (ListView)findViewById(R.id.listview4);
    }

    BaseAdapter listViewAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return listDeviceId.size();
        }

        @Override
        public Object getItem(int i) {
            return listDeviceId.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            LinearLayout ll = (LinearLayout) LayoutInflater.from(My_Collection.this).inflate(R.layout.list_item_view01,null);
            CheckBox checkStar = (CheckBox)ll.findViewById(R.id.star);checkStar.setVisibility(View.GONE);
            TextView tv24 = (TextView)ll.findViewById(R.id.textView24);tv24.setText(listDeviceRegion.get(i)+"--"+listDeviceName.get(i));
            Button btn17 = (Button)ll.findViewById(R.id.button17);
            tv24.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LatLng latLng = new LatLng(Double.parseDouble(listDeviceLat.get(i)),Double.parseDouble(listDeviceLon.get(i)));
                    Marker marker = aMap.addMarker(new MarkerOptions().position(latLng).title(listDeviceName.get(i)).snippet(""));
                    CameraUpdateFactory cmuf = new CameraUpdateFactory();
                    cmuf.zoomTo(18);
                    CameraUpdate cm = cmuf.changeLatLng(latLng);
                    aMap.moveCamera(cm);
                }
            });
            btn17.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Poi pois = new Poi("当前位置",start,"");
                    Poi poie = new Poi(listDeviceName.get(i),new LatLng(Double.parseDouble(listDeviceLat.get(i)),Double.parseDouble(listDeviceLon.get(i))),"");
                    AmapNaviPage.getInstance().showRouteActivity(My_Collection.this, new AmapNaviParams(pois, null, poie, AmapNaviType.DRIVER), new INaviInfoCallback() {
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
            return ll;
        }
    };

}
