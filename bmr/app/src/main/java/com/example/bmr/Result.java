package com.example.bmr;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;


public class Result extends AppCompatActivity {

    TextView show_name;
    TextView show_bmi;
    TextView show_bmr;
    Button btn_cancel2;
    Button btn_save;

    HashMap<String, String> hash= new HashMap<String, String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        show_name = findViewById(R.id.show_name);
        show_bmi = findViewById(R.id.show_bmi);
        show_bmr = findViewById(R.id.show_bmr);

        btn_cancel2 = findViewById(R.id.btn_cancel2);
        btn_save = findViewById(R.id.btn_save);

        Intent it = this.getIntent();

        Bundle bundle = it.getExtras();
        String name = bundle.getString("name");
        String gender = bundle.getString("gender");
        int age = bundle.getInt("age");
        double height = bundle.getDouble("height");
        double weight = bundle.getDouble("weight");


        double bmi = weight/(height/100)/(height/100);
        double bmr;

        if(gender.equals("male")){
            bmr = 66+13.7*weight+5*height-6.8*age;
        }
        else{
            bmr = 655+9.6*weight+1.8*height-4.7*age;
        }

        hash.put("name", name);
        hash.put("gender", gender);
        hash.put("age", String.valueOf(age));
        hash.put("height", String.valueOf(height));
        hash.put("weight", String.valueOf(weight));
        hash.put("bmr", String.valueOf((int)bmr));


        show_name.setText(name);
        show_bmi.setText(String.valueOf((int)bmi));
        show_bmr.setText(String.valueOf((int)bmr));


        btn_cancel2.setOnClickListener(new View.OnClickListener() {
            @Override
            // 按鈕事件
            public void onClick(View view) {
                // 按下之後會執行的程式碼，可以直接寫也可以呼叫方法

                Intent it = new Intent();
                it.setClass(Result.this, Creating_Record.class);
                startActivity(it);
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            // 按鈕事件
            public void onClick(View view) {
                // 按下之後會執行的程式碼，可以直接寫也可以呼叫方法

                insertRecord(hash);

                Intent it = new Intent();
                it.setClass(Result.this, MainActivity.class);
                startActivity(it);
            }
        });

    }



    private synchronized void insertRecord(HashMap<String, String> map){
        Thread thread = new Thread(new Runnable() {
            HashMap<String, String> _map;

            @Override
            public void run() {

                String path = "http://10.0.2.2/bmr/insert.php";
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
            //conn.setUseCaches(false);


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

            Log.d("Save Record", "result: " + result.toString());


            conn.disconnect();
        } catch (IOException e) {
            Log.v("Save Record", "Record saved failed");
            e.printStackTrace();
        }







    }
}