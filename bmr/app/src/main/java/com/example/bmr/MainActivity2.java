package com.example.bmr;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.view.View;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.*;
import java.util.ArrayList;

import org.json.*;

public class MainActivity2 extends AppCompatActivity {

    TextView textView;
    Button button;
    String result;
    ArrayList<String> output = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        button = findViewById(R.id.button2);
        textView = findViewById(R.id.textView3);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            // 按鈕事件
            public void onClick(View view) {
                // 按下之後會執行的程式碼，可以直接寫也可以呼叫方法
                Thread thread = new Thread(mutiThread);
                thread.start(); // 開始執行


                for(int i = 0; i < output.size(); i++) {
                    System.out.println(output.get(i));
                }

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
                        output.add(name);
                        String gender = jobt.getString("gender");
                        output.add(gender);
                        String age = jobt.getString("age");
                        output.add(age);
                        String height = jobt.getString("height");
                        output.add(height);
                        String weight = jobt.getString("weight");
                        output.add(weight);
                        String bmr = jobt.getString("bmr");
                        output.add(bmr);
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
                    textView.setText(result); // 更改顯示文字
                }
            });
        }
    };


}