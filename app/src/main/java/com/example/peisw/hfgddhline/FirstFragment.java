package com.example.peisw.hfgddhline;

import android.app.Fragment;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.MyLocationStyle;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState){
        View view = null;
        Bundle args = getArguments();
        if(args.getString("context").equals("tab01")){
            view = inflater.inflate(R.layout.activity_tab01,container,false);
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
                        //start = new LatLng(location.getLatitude(),location.getLongitude());
                        //latLngs.add(new LatLng(location.getLatitude(),location.getLongitude()));
                        //Polyline polyline =aMap.addPolyline(new PolylineOptions().
                                //addAll(latLngs).width(10).color(Color.argb(255, 1, 1, 1)));
                    }
                });
            }

            TextView tv5 = (TextView)view.findViewById(R.id.textView5);
            tv5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });


        }
        if(args.getString("context").equals("tab02")){
            view = inflater.inflate(R.layout.activity_tab02,container,false);

        }
        return view;
    }
}
