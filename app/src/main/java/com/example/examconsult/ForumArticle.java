package com.example.examconsult;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.spec.ECField;
import java.util.ArrayList;

public class ForumArticle extends AppCompatActivity {

    RecyclerView recyclerView;
    AdapterComments adapter;
    private final ArrayList<Comments> comments = new ArrayList<>();
    Cursor cursor;
    SQLiteDatabase db;
    DBHelper dbHelper;
    EditText edit_comment;
    TextView textDescription;
    TextView textTitle;
    Button send;
    int user_id;
    int forum_id;
    int forum_author_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_forum_article);
            edit_comment = findViewById(R.id.edit_comment);
            recyclerView = findViewById(R.id.recycle_view);
            textDescription = findViewById(R.id.text_description);
            textTitle = findViewById(R.id.text_title);
            DBHelper dbHelper = new DBHelper(this);
            db = dbHelper.getWritableDatabase();

            Bundle arguments = getIntent().getExtras();
            user_id = arguments.getInt("id_user");
            forum_id = arguments.getInt("forum");
            forum_author_id = arguments.getInt("forum_author_id");
            String descr = dbHelper.getDescrById(forum_id);
            textDescription.setText(descr);
            String title = dbHelper.getTitleById(forum_id);
            textTitle.setText(title);
            db.close();
            getData(user_id);
        }
        catch (Exception e)
        {
            Toast.makeText(this, "Something Wrong", Toast.LENGTH_SHORT).show();
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
    public void getData(int user_id){
        try {
            dbHelper = new DBHelper(this);
            db = dbHelper.getWritableDatabase();
            comments.clear();
            cursor = db.rawQuery("select * from comments where forum_id = "+forum_id,null);
            if(cursor.moveToFirst()){
                do{
                    Comments comment = new Comments();
                    comment.setId(cursor.getInt(0));
                    comment.setComment(cursor.getString(1));
                    comment.setDate(cursor.getString(2));
                    comment.setAuthor_id(String.valueOf(cursor.getInt(3)));
                    comment.setForum_id(forum_id);
                    comments.add(comment);
                }while (cursor.moveToNext());
            }else{
                Toast.makeText(this, "Comment Table is Empty", Toast.LENGTH_SHORT).show();
            }
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter = new AdapterComments(comments,ForumArticle.this,user_id,forum_author_id);
            recyclerView.setAdapter(adapter);
            cursor.close();
            db.close();
        }
        catch (Exception e)
        {
            Toast.makeText(this, "Something wrong", Toast.LENGTH_SHORT).show();
        }

    }

    public void OnClick(View view) {
        try {
            if (edit_comment.length() > 4){
                dbHelper.addComment(edit_comment.getText().toString(), user_id, forum_id);
                getData(user_id);
            }
            else
            {
                Toast.makeText(this, "Comment to short", Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e)
        {
            Toast.makeText(this, "Something wrong", Toast.LENGTH_SHORT).show();
        }

    }
}
