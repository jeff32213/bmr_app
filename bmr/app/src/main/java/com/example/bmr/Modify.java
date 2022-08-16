package com.example.bmr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class Modify extends AppCompatActivity {


    EditText mod_name;
    RadioGroup mod_gender;
    RadioButton gender;
    EditText mod_age;
    EditText mod_height;
    EditText mod_weight;

    Button btn_mod_cancel;
    Button btn_modify;

    String ori_name;

    HashMap<String, String> hash= new HashMap<String, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify);

        mod_name = findViewById(R.id.mod_name);
        mod_gender = findViewById(R.id.mod_gender);
        mod_age = findViewById(R.id.mod_age);
        mod_height = findViewById(R.id.mod_height);
        mod_weight = findViewById(R.id.mod_weight);

        btn_mod_cancel= findViewById(R.id.btn_mod_cancel);
        btn_modify = findViewById(R.id.btn_modify);

        Intent it = this.getIntent();
        Bundle bundle = it.getExtras();
        ori_name = bundle.getString("name");

        btn_mod_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            // 按鈕事件
            public void onClick(View view) {
                // 按下之後會執行的程式碼，可以直接寫也可以呼叫方法

                Intent it = new Intent();
                it.setClass(Modify.this, MainActivity.class);
                startActivity(it);
            }
        });

        btn_modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int age = Integer.parseInt(mod_age.getText().toString());
                double height = Double.parseDouble(mod_height.getText().toString());
                double weight = Double.parseDouble(mod_weight.getText().toString());
                double bmr;

                int gender_id = mod_gender.getCheckedRadioButtonId();
                gender = findViewById(gender_id);

                if(gender.getText().toString().equals("male")){
                    bmr = 66+13.7*weight+5*height-6.8*age;
                }
                else{
                    bmr = 655+9.6*weight+1.8*height-4.7*age;
                }


                hash.put("ori_name", ori_name);
                hash.put("name", mod_name.getText().toString());
                hash.put("gender", gender.getText().toString());
                hash.put("age", mod_age.getText().toString());
                hash.put("height", mod_height.getText().toString());
                hash.put("weight", mod_weight.getText().toString());
                hash.put("bmr", String.valueOf((int)bmr));

                modify(hash);

                Intent it = new Intent();
                it.setClass(Modify.this, MainActivity.class);
                startActivity(it);
            }
        });


    }

    private synchronized void modify(HashMap<String, String> map){
        Thread thread = new Thread(new Runnable() {
            HashMap<String, String> _map;

            @Override
            public void run() {

                String path = "http://10.0.2.2/bmr/modify.php";
                executeHttpPost(path, _map);

            }

            public Runnable init(HashMap<String, String> map){
                _map = map;
                return this;
            }
        }.init(map));
        thread.start();

        while(thread.isAlive()){

        }

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



            conn.disconnect();
        } catch (IOException e) {

            e.printStackTrace();
        }



    }
}