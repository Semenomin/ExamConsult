package com.example.examconsult;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

import static android.content.Context.MODE_PRIVATE;

public class DBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "exactest.db";

    private static final String TABLE_NAME_USER = "users";
    private static final String KEY_ID_USER = "id";
    private static final String KEY_LOGIN_USER = "login";
    private static final String KEY_PASSWORD_USER = "password";
    private static final String KEY_ISADMIN_USER = "is_admin";

    private static final String TABLE_NAME_FORUM = "forum";
    private static final String KEY_ID_FORUM = "id";
    private static final String KEY_CREATED_AT_FORUM = "created_at";
    private static final String KEY_AUTHOR_ID_FORUM= "author_id";
    private static final String KEY_TITLE_FORUM = "title";
    private static final String KEY_DESCRIPTION_FORUM = "description";
    private static final String KEY_COMMENT_ID_FORUM = "comment_id";

    private static final String TABLE_NAME_COMMENTS = "comments";
    private static final String KEY_ID_COMMENTS = "id";
    private static final String KEY_COMMENT_ID_COMMENT = "comment";
    private static final String KEY_CREATED_AT_COMMENTS = "created_at";
    private static final String KEY_AUTHOR_ID_COMMENTS = "author_id";

    private static final String FILE_NAME = "no_password";
    private FileOutputStream fos;
    private FileInputStream fis;

    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS "+TABLE_NAME_USER+" (" +
                        " "+KEY_ID_USER+" INTEGER PRIMARY KEY AUTOINCREMENT," +
                        " "+KEY_LOGIN_USER+" TEXT UNIQUE NOT NULL," +
                        " "+KEY_PASSWORD_USER+" TEXT NOT NULL," +
                        " "+KEY_ISADMIN_USER+" INTEGER" +
                        " )");

        db.execSQL("CREATE TABLE IF NOT EXISTS "+TABLE_NAME_FORUM+" ("+
                        " "+KEY_ID_FORUM+" INTEGER PRIMARY KEY AUTOINCREMENT,"+
                        " "+KEY_CREATED_AT_FORUM+" TEXT NOT NULL,"+
                        " "+KEY_AUTHOR_ID_FORUM+" TEXT NOT NULL,"+
                        " "+KEY_TITLE_FORUM+" TEXT UNIQUE NOT NULL,"+
                        " "+KEY_DESCRIPTION_FORUM+" TEXT NOT NULL,"+
                        " "+KEY_COMMENT_ID_FORUM+" INTEGER,"+
                        " FOREIGN KEY("+KEY_AUTHOR_ID_FORUM+") REFERENCES users("+KEY_ID_USER+")"+
                        " )");

        db.execSQL("CREATE TABLE IF NOT EXISTS "+TABLE_NAME_COMMENTS+" (" +
                        " "+KEY_ID_COMMENTS+" INTEGER PRIMARY KEY AUTOINCREMENT," +
                        " "+KEY_COMMENT_ID_COMMENT+" TEXT UNIQUE NOT NULL," +
                        " "+KEY_CREATED_AT_COMMENTS+" TEXT NOT NULL," +
                        " "+KEY_AUTHOR_ID_COMMENTS+" TEXT NOT NULL," +
                        " FOREIGN KEY("+KEY_AUTHOR_ID_COMMENTS+") REFERENCES users("+KEY_ID_USER+")," +
                        " FOREIGN KEY("+KEY_ID_COMMENTS+") REFERENCES forum("+KEY_ID_COMMENTS+")" +
                        " );");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table "+TABLE_NAME_USER);
        db.execSQL("drop table "+TABLE_NAME_COMMENTS);
        db.execSQL("drop table "+TABLE_NAME_FORUM);
        onCreate(db);
    }

    public void addUser(String login,String password,int isAdmin ,Context ctx) {
        try {
            if(login.length() >=4){
                if(password.length() >=4){
                    SQLiteDatabase database = getWritableDatabase();
                    String pass;
                    if(readFile(ctx)==null){
                        byte[] sa = getSalt();
                        pass = new String(getHash(sa,password));
                        writeFile(sa,ctx);
                    }
                    else{
                        byte[]salt = readFile(ctx);
                        pass = new String(getHash(salt,password));
                    }
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(KEY_LOGIN_USER, login);
                    contentValues.put(KEY_PASSWORD_USER, pass);
                    contentValues.put(KEY_ISADMIN_USER,isAdmin);
                    database.insert(TABLE_NAME_USER, null, contentValues);
                    Toast.makeText(ctx, "Registration success", Toast.LENGTH_LONG).show();
                }
                else Toast.makeText(ctx, "Password is less than 4 symbols", Toast.LENGTH_LONG).show();
            }
            else Toast.makeText(ctx, "Login is less than 4 symbols", Toast.LENGTH_LONG).show();
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
    }

    private byte[] getSalt() {
        SecureRandom random = new SecureRandom();
        byte[]salt = new byte[16];
        random.nextBytes(salt);
        return salt;
    }

    private byte[] getHash(byte[] salt, String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        md.update(salt);
        return md.digest(password.getBytes(StandardCharsets.UTF_8));
    }


    private void writeFile(byte[] salt, Context ctx) {
        try {
            ctx.deleteFile(FILE_NAME);
            fos = ctx.openFileOutput(FILE_NAME, MODE_PRIVATE);
            fos.write(salt);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private byte[] readFile(Context ctx) {
        try {
            fis = ctx.openFileInput(FILE_NAME);
            byte[] bytes = new byte[fis.available()];
            fis.read(bytes);
            return bytes;
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    void signIn(String password, String login,Context ctx) throws NoSuchAlgorithmException {
        SQLiteDatabase db = getReadableDatabase();
        Cursor query =  db.rawQuery("select * from "+TABLE_NAME_USER+" where "+KEY_LOGIN_USER+" LIKE \'"+login+"\';",null);
        Log.d("LOG","select * from "+TABLE_NAME_USER+" where "+KEY_LOGIN_USER+" = "+login+";");
        if(query.moveToFirst() && query.getCount() != 0){
            Log.d("LOG","something");
            int id = query.getInt(0);
            String pass = query.getString(2);
            byte[] salt = readFile(ctx);
            if(salt != null){
                String pass_input = new String(getHash(salt,password));
                if(pass.equals(pass_input)){
                    ctx.startActivity(new Intent(ctx, MainActivity.class));
                }
                else Toast.makeText(ctx, "Incorrect password", Toast.LENGTH_LONG).show();
            }
            else Toast.makeText(ctx, "No Registered Users", Toast.LENGTH_LONG).show();
        }
        else Toast.makeText(ctx, "Incorrect login", Toast.LENGTH_LONG).show();
    }

    void getAllUsers(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor query = db.rawQuery("SELECT * FROM users;", null);
        if(query.moveToFirst()){
            do{
                Log.d("LOG",query.getString(0));
                Log.d("LOG",query.getString(1));
                Log.d("LOG",query.getString(2));
                Log.d("LOG",query.getString(3));
            }
            while(query.moveToNext());
        }
    }
}
