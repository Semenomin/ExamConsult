package com.example.examconsult;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    List<Forum> forums = new ArrayList<>();
    int user_id;
    RecyclerView recyclerView;
    RecycleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle arguments = getIntent().getExtras();
        user_id = arguments.getInt("id_user");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setInitialData();
        recyclerView = findViewById(R.id.list);
        adapter = new RecycleAdapter(this, forums,user_id);
        recyclerView.setAdapter(adapter);
    }

    private void setInitialData() {
        DBHelper helper = new DBHelper(this);
        forums = helper.getForums();
    }


    public void click(View view) {
        Intent intent = new Intent(this,add_forum.class);
        intent.putExtra("id_user",user_id);
        this.startActivity(intent);
    }
}
