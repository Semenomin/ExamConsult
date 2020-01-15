package com.example.examconsult;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

public class AuthActivity extends AppCompatActivity {

    private FileOutputStream fos;
    private FileInputStream fis;
    String FILE = "backgrownd";
    EditText login_edit;
    EditText pass_edit;
    Button sign;
    Button register;
    DBHelper helper;
    private ImageView userAva;
    private final int PICK_IMAGE = 2;
    private Uri fileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        helper = new DBHelper(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        try {
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

    public void OnClick(View view) throws NoSuchAlgorithmException {
        login_edit = findViewById(R.id.login_editText);
        pass_edit = findViewById(R.id.pass_editText);
        sign = findViewById(R.id.signIn_btn);
        register = findViewById(R.id.register_btn);
        String login = login_edit.getText().toString();
        String password = pass_edit.getText().toString();

        switch (view.getId()) {
            case R.id.register_btn:
                if (!login_edit.getText().toString().isEmpty() && !pass_edit.getText().toString().isEmpty()) {
                    helper.addUser(login,password,1,this);
                } else
                    Toast.makeText(getApplicationContext(), "Input data in fields", Toast.LENGTH_LONG).show();
                break;
            case R.id.signIn_btn:
                if (!login_edit.getText().toString().isEmpty() && !pass_edit.getText().toString().isEmpty()) {
                    helper.signIn(password,login,this);
                } else
                    Toast.makeText(getApplicationContext(), "Input data in fields", Toast.LENGTH_LONG).show();
                break;
            case R.id.change:
                userAva = findViewById(R.id.imageView);
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, PICK_IMAGE);
                break;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PICK_IMAGE:
                    fileUri = data.getData();
                    InputStream imageStream = null;
                    try {
                        imageStream = getContentResolver().openInputStream(fileUri);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    selectedImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();
                    try {
                        this.deleteFile(FILE);
                        fos = this.openFileOutput(FILE, MODE_PRIVATE);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    try {
                        fos.write(byteArray);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    userAva.setImageBitmap(selectedImage);
                    break;
            }
        }
    }
}
