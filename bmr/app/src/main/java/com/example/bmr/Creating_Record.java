package com.example.bmr;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class Creating_Record extends AppCompatActivity {

    Button btn_cancel;
    Button btn_send;

    EditText input_name;
    RadioGroup input_gender;
    RadioButton gender;
    EditText input_age;
    EditText input_height;
    EditText input_weight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creating_record);

        btn_cancel = findViewById(R.id.btn_mod_cancel);
        btn_send = findViewById(R.id.btn_modify);

        input_name = findViewById(R.id.mod_name);
        input_gender = findViewById(R.id.mod_gender);
        input_age = findViewById(R.id.mod_age);
        input_height = findViewById(R.id.mod_height);
        input_weight = findViewById(R.id.mod_weight);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            // 按鈕事件
            public void onClick(View view) {
                // 按下之後會執行的程式碼，可以直接寫也可以呼叫方法

                Intent it = new Intent();
                it.setClass(Creating_Record.this, MainActivity.class);
                startActivity(it);
            }
        });


        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            // 按鈕事件
            public void onClick(View view) {
                // 按下之後會執行的程式碼，可以直接寫也可以呼叫方法

                int gender_id = input_gender.getCheckedRadioButtonId();
                gender = findViewById(gender_id);

                Bundle bundle = new Bundle();
                bundle.putString("name", input_name.getText().toString());
                bundle.putString("gender", gender.getText().toString());
                bundle.putInt("age", Integer.parseInt(input_age.getText().toString()));
                bundle.putDouble("height", Double.parseDouble(input_height.getText().toString()));
                bundle.putDouble("weight", Double.parseDouble(input_weight.getText().toString()));


                Intent it = new Intent();
                it.putExtras(bundle);
                it.setClass(Creating_Record.this, Result.class);
                startActivity(it);

            }
        });



    }
}