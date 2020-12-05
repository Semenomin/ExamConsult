package com.example.examconsult;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class add_forum extends AppCompatActivity {

    EditText title, desc,category;
    String date;
    int user_id;
    boolean isEdit;
    int forum_id;

    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_forum);
        title = findViewById(R.id.title_edit);
        desc = findViewById(R.id.desc_edit);
        category = findViewById(R.id.category_edit);
        Bundle arguments = getIntent().getExtras();
        isEdit = arguments.getBoolean("isEdit");
        user_id = arguments.getInt("id_user");
        date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        if(isEdit){
            TextView te = findViewById(R.id.textView);
            te.setText("Change Forum");
            title.setText(arguments.getString("title"));
            desc.setText(arguments.getString("desc"));
            forum_id = arguments.getInt("id");
            category.setText(arguments.getString("category"));
        }
        try {
            ImageView userAva;
            String FILE = "backgrownd";
            FileInputStream fis;
            userAva = findViewById(R.id.imageView);
            fis = this.openFileInput(FILE);
            byte[] bytes;
            bytes = new byte[fis.available()];
            fis.read(bytes);
            if(bytes != null){
                ByteArrayInputStream is = new ByteArrayInputStream(bytes);
                final Bitmap selectedImage = BitmapFactory.decodeStream(is);
                userAva.setImageBitmap(selectedImage);
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void click(View view) {
        DBHelper help = new DBHelper(this);
        if(!this.title.getText().toString().isEmpty()) {
            if(!this.desc.getText().toString().isEmpty()) {
                if(isEdit){
                    String desc = this.desc.getText().toString();
                    String title = this.title.getText().toString();
                    help.changeForum(desc, title, String.valueOf(forum_id));
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.putExtra("id_user", user_id);
                    this.startActivity(intent);
                }else{
                    String desc = this.desc.getText().toString();
                    String title = this.title.getText().toString();
                    help.newForum(desc, 100, date, title, user_id,category.getText().toString());
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.putExtra("id_user", user_id);
                    this.startActivity(intent);
                }
            }
            else
            {
                Toast.makeText(this,"Descr is empty",Toast.LENGTH_SHORT).show();
            }
        }
        else Toast.makeText(this,"Title is empty",Toast.LENGTH_SHORT).show();
    }
}
