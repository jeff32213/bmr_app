package com.example.bmr;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.app.AlertDialog;
import android.content.DialogInterface;

import java.net.URL;
import java.net.HttpURLConnection;
import java.io.*;
import java.util.ArrayList;
import java.util.*;

import org.json.*;

public class MainActivity extends AppCompatActivity {


    Button btn_create;
    ListView listview;

    String result;
    ArrayList<String> output = new ArrayList<String>();

    HashMap<String, String> hash = new HashMap<String, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_create = findViewById(R.id.btn_create);
        listview = findViewById(R.id.listview);

        Thread thread = new Thread(mutiThread);
        thread.start(); // 開始執行


        //android.R.layout.simple_list_item_1 為內建樣式，還有其他樣式可自行研究
        ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, output);
        listview.setAdapter(adapter);

        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("delete this data?")
                        .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String[] name = output.get(i).split("\\s+");
                                hash.put("name", name[1]);
                                delete(hash);

                                Intent it = new Intent();
                                it.setClass(MainActivity.this, MainActivity.class);
                                startActivity(it);
                            }
                        })
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                builder.show();

                return false;
            }
        });

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String[] name = output.get(i).split("\\s+");

                Bundle bundle = new Bundle();
                bundle.putString("name", name[1]);

                Intent it = new Intent();
                it.putExtras(bundle);
                it.setClass(MainActivity.this, Modify.class);
                startActivity(it);
            }
        });

        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            // 按鈕事件
            public void onClick(View view) {
                // 按下之後會執行的程式碼，可以直接寫也可以呼叫方法

                Intent it = new Intent();
                it.setClass(MainActivity.this, Creating_Record.class);
                startActivity(it);


            }
        });


    }

    private Runnable mutiThread = new Runnable(){
        public void run()
        {
            try {
                URL url = new URL("http://10.0.2.2/Getdata.php");
                // 開始宣告 HTTP 連線需要的物件，這邊通常都是一綑的
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                // 建立 Google 比較挺的 HttpURLConnection 物件
                connection.setRequestMethod("POST");
                // 設定連線方式為 POST
                connection.setDoOutput(true); // 允許輸出
                connection.setDoInput(true); // 允許讀入
                connection.setUseCaches(false); // 不使用快取
                connection.connect(); // 開始連線

                int responseCode =
                        connection.getResponseCode();
                // 建立取得回應的物件
                if(responseCode ==
                        HttpURLConnection.HTTP_OK){
                    // 如果 HTTP 回傳狀態是 OK ，而不是 Error
                    InputStream inputStream =
                            connection.getInputStream();
                    // 取得輸入串流
                    BufferedReader bufReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"), 8);
                    // 讀取輸入串流的資料
                    String box = ""; // 宣告存放用字串
                    String line = null; // 宣告讀取用的字串
                    while((line = bufReader.readLine()) != null) {
                        box += line + "\n";

                        // 每當讀取出一列，就加到存放字串後面
                    }
                    //System.out.println(box);
                    inputStream.close(); // 關閉輸入串流
                    result = box; // 把存放用字串放到全域變數
                }
                // 讀取輸入串流並存到字串的部分
                // 取得資料後想用不同的格式
                // 例如 Json 等等，都是在這一段做處理

                try{
                    JSONArray jary = new JSONArray(result);
                    for(int i=0; i<jary.length(); i++){
                        JSONObject jobt =jary.getJSONObject(i);
                        String name = jobt.getString("name");
                        String bmr = jobt.getString("bmr");

                        output.add("                 " + name + "                    |                     " + bmr +  "              " );

                    }

                } catch(JSONException e){
                    e.printStackTrace();
                }



            } catch(Exception e) {
                result = e.toString(); // 如果出事，回傳錯誤訊息
            }





            // 當這個執行緒完全跑完後執行
            runOnUiThread(new Runnable() {
                public void run() {

                }
            });
        }
    };

    private synchronized void delete(HashMap<String, String> map){
        Thread thread = new Thread(new Runnable() {
            HashMap<String, String> _map;

            @Override
            public void run() {

                String path = "http://10.0.2.2/bmr/delete.php";
                executeHttpPost(path, _map);
                Log.d("internet thread", "End");
            }

            public Runnable init(HashMap<String, String> map){
                _map = map;
                return this;
            }
        }.init(map));
        thread.start();

        while(thread.isAlive()){

        }
        Log.d("insertRecord()", "End");
    }


    private void executeHttpPost(String path, HashMap<String, String> map){

        Map<String, String> tmp;
        tmp = map;
        JSONObject tmp2 = new JSONObject(tmp);


        try {
            URL url = new URL(path);
            // 開始宣告 HTTP 連線需要的物件，這邊通常都是一綑的
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            // 建立 Google 比較挺的 HttpURLConnection 物件
            conn.setRequestMethod("POST");
            // 設定連線方式為 POST
            conn.setDoOutput(true); // 允許輸出
            conn.setDoInput(true);
            //conn.setRequestProperty("Accept-Charset", "UTF-8");
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestProperty("Content-Type","application/json; charset=UTF-8");
            conn.setRequestProperty("Accept", "application/json");



            conn.connect(); // 開始連線


            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.writeBytes(tmp2.toString());
            wr.flush();
            wr.close();


            InputStream in = new BufferedInputStream(conn.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder result = new StringBuilder();
            String line;
            while((line= reader.readLine()) != null){
                result.append(line);
            }


            conn.disconnect();
        } catch (IOException e) {

            e.printStackTrace();
        }







    }


}



