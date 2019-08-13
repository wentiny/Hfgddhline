package com.example.peisw.hfgddhline.utils;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * Created by wentiny on 2019/8/13.
 */

public class MyUtils {

    public static void setListHeight(ListView listView){
        ListAdapter adapter = listView.getAdapter();
        if(adapter==null){return;}
        int totalHeight = 0;
        for(int i=0,len=adapter.getCount();i<len;i++){
            View listItem = adapter.getView(i,null,listView);
            listItem.measure(0,0);
            totalHeight = totalHeight+listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight+(listView.getDividerHeight()*(adapter.getCount()-1));
        listView.setLayoutParams(params);
    }

}
