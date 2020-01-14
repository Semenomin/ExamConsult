package com.example.examconsult;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

public class AuthActivity extends AppCompatActivity {

    EditText login_edit;
    EditText pass_edit;
    Button sign;
    Button register;
    DBHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        helper = new DBHelper(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
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
        }
    }


}
