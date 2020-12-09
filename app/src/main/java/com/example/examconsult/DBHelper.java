package com.example.examconsult;

import android.annotation.SuppressLint;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class DBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
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
    private static final String KEY_CATEGORY = "category";


    private static final String TABLE_NAME_COMMENTS = "comments";
    private static final String KEY_ID_COMMENTS = "id";
    private static final String KEY_COMMENT_ID_COMMENT = "comment";
    private static final String KEY_CREATED_AT_COMMENTS = "created_at";
    private static final String KEY_AUTHOR_ID_COMMENTS = "author_id";
    private static final String KEY_FORUM_ID_COMMENTS = "forum_id";

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
                        " "+KEY_CATEGORY+" TEXT NOT NULL,"+
                        " FOREIGN KEY("+KEY_AUTHOR_ID_FORUM+") REFERENCES users("+KEY_ID_USER+")"+
                        " )");

        db.execSQL("CREATE TABLE IF NOT EXISTS "+TABLE_NAME_COMMENTS+" (" +
                        " "+KEY_ID_COMMENTS+" INTEGER PRIMARY KEY AUTOINCREMENT," +
                        " "+KEY_COMMENT_ID_COMMENT+" TEXT UNIQUE NOT NULL," +
                        " "+KEY_CREATED_AT_COMMENTS+" TEXT NOT NULL," +
                        " "+KEY_AUTHOR_ID_COMMENTS+" TEXT NOT NULL," +
                        " "+KEY_FORUM_ID_COMMENTS+" TEXT NOT NULL," +
                        " FOREIGN KEY("+KEY_AUTHOR_ID_COMMENTS+") REFERENCES users("+KEY_ID_USER+")," +
                        " FOREIGN KEY("+KEY_FORUM_ID_COMMENTS+") REFERENCES forum("+KEY_ID_FORUM+")" +
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
        User user = new User();
        try {
            if(login.length() >=4){
                if(password.length() >=4){
                    SQLiteDatabase database = getWritableDatabase();
                    String pass;
                    if(readFile(ctx)==null){
                        byte[] sa = getSalt();
                        pass = new String(getHash(sa,password));
                        user.salt = sa;
                    }
                    else{
                        user = SerealizationManager.readSerializable(ctx,"user");
                        pass = new String(getHash(user.salt,password));
                    }
                    user.login = login;
                    user.password = password;
                    SerealizationManager.saveSerializable(ctx,user,"user");
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(KEY_LOGIN_USER, login);
                    contentValues.put(KEY_PASSWORD_USER, pass);
                    contentValues.put(KEY_ISADMIN_USER,isAdmin);
                    if (database.insert(TABLE_NAME_USER, null, contentValues) == -1)
                    {
                        Toast.makeText(ctx, "Registration error, please enter valid login and pass", Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        Toast.makeText(ctx, "Registration success", Toast.LENGTH_LONG).show();
                    }
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
        if(query.moveToFirst() && query.getCount() != 0){
            Log.d("LOG","something");
            int id = query.getInt(0);
            String pass = query.getString(2);
            User user = SerealizationManager.readSerializable(ctx,"user");
            if(user.salt != null){
                String pass_input = new String(getHash(user.salt,password));
                if(pass.equals(pass_input)){
                    Intent intent = new Intent(ctx, MainActivity.class);
                    intent.putExtra("id_user",id);
                    ctx.startActivity(intent);
                }
                else Toast.makeText(ctx, "Incorrect password", Toast.LENGTH_LONG).show();
            }
            else Toast.makeText(ctx, "No Registered Users", Toast.LENGTH_LONG).show();
        }
        else Toast.makeText(ctx, "Incorrect login", Toast.LENGTH_LONG).show();
    }

    String getDescrById(int id){
        SQLiteDatabase db = getReadableDatabase();
        Cursor query = db.rawQuery("select description from "+TABLE_NAME_FORUM+" where "+KEY_ID_FORUM+" like "+id,null);
        if(query.moveToFirst() && query.getCount() != 0){
            return query.getString(0);
        }
        else return "NO";
    }

    String getTitleById(int id){
        SQLiteDatabase db = getReadableDatabase();
        Cursor query = db.rawQuery("select title from "+TABLE_NAME_FORUM+" where "+KEY_ID_FORUM+" like "+id,null);
        if(query.moveToFirst() && query.getCount() != 0){
            return query.getString(0);
        }
        else return "NO TITLE";
    }

    String getCategoryById(int id){
        SQLiteDatabase db = getReadableDatabase();
        Cursor query = db.rawQuery("select category from "+TABLE_NAME_FORUM+" where "+KEY_ID_FORUM+" like "+id,null);
        if(query.moveToFirst() && query.getCount() != 0){
            return query.getString(0);
        }
        else return "NO TITLE";
    }

    @SuppressLint("SimpleDateFormat")
    void addComment(String comment, int id_author, int title_id){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("comment", comment);
        cv.put("forum_id", title_id);
        cv.put("created_at", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        cv.put("author_id", id_author);
        db.insert(DBHelper.TABLE_NAME_COMMENTS,null,cv);
        cv.clear();
    }

    String getAuthor(int id){
        SQLiteDatabase db = getReadableDatabase();
        Cursor query = db.rawQuery("select * from "+TABLE_NAME_USER+" where "+KEY_ID_USER+" like "+id,null);
        if(query.moveToFirst() && query.getCount() != 0){
            return query.getString(1);
        }
        else return "Anonim";
    }

    List<Forum> getForums(){
        List<Forum> forums = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor query =  db.rawQuery("select * from "+TABLE_NAME_FORUM,null);
        if(query.moveToFirst()){
            do{
                Forum f = new Forum();
                f.setId(query.getInt(0));
                f.setCreated_at(query.getString(1));
                f.setAuthor_id(query.getInt(2));
                f.setTitle(query.getString(3));
                f.setDesc(query.getString(4));
                f.setCategory(query.getString(5));
                forums.add(f);
            }
            while(query.moveToNext());
        }
        return forums;
    }

    void newForum(String desc,int comm_id,String date, String title, int author_id,String category){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_DESCRIPTION_FORUM, desc);
        contentValues.put(KEY_CREATED_AT_FORUM, date);
        contentValues.put(KEY_TITLE_FORUM, title);
        contentValues.put(KEY_AUTHOR_ID_FORUM, author_id);
        contentValues.put(KEY_CATEGORY,category);
        db.insert(TABLE_NAME_FORUM, null, contentValues);
    }

    void changeForum(String newDesc,String newTitle,String forum_id){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_DESCRIPTION_FORUM,newDesc);
        contentValues.put(KEY_TITLE_FORUM,newTitle);
        db.update(TABLE_NAME_FORUM,contentValues,KEY_ID_FORUM+" = ?",new String[]{forum_id});
    }

    void deleteForum(String forum_id){
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NAME_FORUM,KEY_ID_FORUM+" = ?",new String[]{forum_id});
    }

    void deleteComment(int comment_id){
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NAME_COMMENTS,KEY_ID_COMMENTS+" = ?",new String[]{String.valueOf(comment_id)});
    }

}
