package com.example.peisw.hfgddhline;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * Created by peisw on 2019/3/27.
 */

public class method {
    //public static String url01 = "http://192.168.191.1:3011/newlxjiaserver1";
    public static String url01 = "http://218.22.41.234:3011/newlxjiaserver1";
    //public static String url01 = "http://192.168.56.1:3011/newlxjiaserver1";
    //post方式，优先使用此方式
    public static String doPost(final String url, final List<String> name, final List<String> value) {
        final StringBuilder sb = new StringBuilder();
        FutureTask<String> task = new FutureTask<String>(new Callable<String>() {
            @Override
            public String call() throws Exception {
                BufferedReader br = null;
                InputStreamReader isr = null;
                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(url);
                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    for(int i=0;i<name.size();i++){
                        params.add(new BasicNameValuePair(name.get(i),value.get(i)));
                    }
                    UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params,"UTF-8");
                    httpPost.setEntity(entity);
                    HttpResponse response =  httpClient.execute(httpPost);
                    isr = new InputStreamReader(response.getEntity().getContent(),"UTF-8");
                    br = new BufferedReader(isr);
                    String line;
                    while((line = br.readLine())!=null){
                        sb.append(line);
                    }
                    System.out.println(sb.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {//执行流的关闭
                    if (br != null) {
                        try {
                            if (br != null) {br.close();}
                            if (isr != null) {isr.close();}
                        } catch (IOException e) {
                            e.printStackTrace();
                        } }}
                return sb.toString();
            }
        });
        new Thread(task).start();
        String s = null;
        try {
            s = task.get();//异步获取返回值
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }
    //get方式
    public static String doGet(final String url) {
        final StringBuilder sb = new StringBuilder();
        FutureTask<String> task = new FutureTask<String>(new Callable<String>() {
            @Override
            public String call() throws Exception {
                BufferedReader br = null;
                InputStreamReader isr = null;
                URLConnection conn;
                try {
                    URL geturl = new URL(url);
                    conn = geturl.openConnection();//创建连接
                    conn.connect();//get连接
                    InputStreamReader isr1 = new InputStreamReader(conn.getInputStream());//输入流
                    br = new BufferedReader(isr1);
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        sb.append(line);//获取输入流数据
                    }
                    System.out.println(sb.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {//执行流的关闭
                    if (br != null) {
                        try {
                            if (br != null) {br.close();}
                            if (isr != null) {isr.close();}
                        } catch (IOException e) {
                            e.printStackTrace();
                        } }}
                return sb.toString();
            }
        });
        new Thread(task).start();
        String s = null;
        try {
            s = task.get();//异步获取返回值
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }
    public static String doPostWithoutValue(final String url) {
        final StringBuilder sb = new StringBuilder();
        FutureTask<String> task = new FutureTask<String>(new Callable<String>() {
            @Override
            public String call() throws Exception {
                BufferedReader br = null;
                InputStreamReader isr = null;
                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(url);
                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params,"UTF-8");
                    httpPost.setEntity(entity);
                    HttpResponse response =  httpClient.execute(httpPost);
                    isr = new InputStreamReader(response.getEntity().getContent(),"UTF-8");
                    br = new BufferedReader(isr);
                    String line;
                    while((line = br.readLine())!=null){
                        sb.append(line);
                    }
                    System.out.println(sb.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {//执行流的关闭
                    if (br != null) {
                        try {
                            if (br != null) {br.close();}
                            if (isr != null) {isr.close();}
                        } catch (IOException e) {
                            e.printStackTrace();
                        } }}
                return sb.toString();
            }
        });
        new Thread(task).start();
        String s = null;
        try {
            s = task.get();//异步获取返回值
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }
    public static String doPostUseMap(final String url, final Map<String,String> map) {
        final StringBuilder sb = new StringBuilder();
        FutureTask<String> task = new FutureTask<String>(new Callable<String>() {
            @Override
            public String call() throws Exception {
                BufferedReader br = null;
                InputStreamReader isr = null;

                List<String> list1 = new ArrayList<>();
                List<String> list2 = new ArrayList<>();

                Set<String> keySet = map.keySet();
                Iterator<String> it = keySet.iterator();
                while (it.hasNext()) {
                    String key = it.next();
                    list1.add(key);
                    //有了键可以通过map集合的get方法获取其对应的值。
                    String value = map.get(key);
                    list2.add(value);
                    //获得key和value值
                }
                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(url);
                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    for(int i=0;i<list1.size();i++){
                        params.add(new BasicNameValuePair(list1.get(i),list2.get(i)));
                    }
                    UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params,"UTF-8");
                    httpPost.setEntity(entity);
                    HttpResponse response =  httpClient.execute(httpPost);
                    isr = new InputStreamReader(response.getEntity().getContent(),"UTF-8");
                    br = new BufferedReader(isr);
                    String line;
                    while((line = br.readLine())!=null){
                        sb.append(line);
                    }
                    System.out.println(sb.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {//执行流的关闭
                    if (br != null) {
                        try {
                            if (br != null) {br.close();}
                            if (isr != null) {isr.close();}
                        } catch (IOException e) {
                            e.printStackTrace();
                        } }}
                return sb.toString();
            }
        });
        new Thread(task).start();
        String s = null;
        try {
            s = task.get();//异步获取返回值
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }
}
