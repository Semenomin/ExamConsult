package com.example.examconsult;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

public class add_forum extends AppCompatActivity {

    EditText title, desc;
    String date;
    int user_id;

    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_forum);
        title = findViewById(R.id.title_edit);
        desc = findViewById(R.id.desc_edit);
        Bundle arguments = getIntent().getExtras();
        user_id = arguments.getInt("id_user");
        date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    public void click(View view) {
        DBHelper help = new DBHelper(this);
        if(!this.title.getText().toString().isEmpty()) {
            String desc = this.desc.getText().toString();
            String title = this.title.getText().toString();
            help.newForum(desc,100,date,title,user_id);
            Intent intent = new Intent(this, MainActivity.class);
            this.startActivity(intent);
        }
        else Toast.makeText(this,"Title is empty",Toast.LENGTH_SHORT).show();
    }
}
