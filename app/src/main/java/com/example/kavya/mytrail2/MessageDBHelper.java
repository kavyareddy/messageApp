package com.example.kavya.mytrail2;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by kavya on 1/20/16.
 */
public class MessageDBHelper extends SQLiteOpenHelper {

    SQLiteDatabase msgDB;

    // Database Info
    public static final String DATABASE_NAME = "messagesDatabase";
    public static final int DATABASE_VERSION = 1;

    // Table Names
    public static final String TABLE_Messages = "messagesTable";

    // User Table Columns
    public static final String MESSAGE_ID = "_id";
    public static final String MESSAGE_BODY = "message";
    public static final String MESSAGE_TYPE = "notQuestion";

    public static MessageDBHelper sInstance;

    public static synchronized MessageDBHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new MessageDBHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    //constructor
    public MessageDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_MESSAGES_TABLE = "CREATE TABLE " + TABLE_Messages +
                "(" +
                MESSAGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MESSAGE_BODY + " TEXT," +
                MESSAGE_TYPE + " TEXT" +
                ")";
        db.execSQL(CREATE_MESSAGES_TABLE);
//        ContentValues values = new ContentValues();
//        values.put(MessageDBHelper.MESSAGE_BODY, "Welcome");
//        values.put(MessageDBHelper.MESSAGE_TYPE, "true");
//        insert(values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate them
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_Messages);
            onCreate(db);
        }
    }

    public void insert(ContentValues values){
        msgDB = getWritableDatabase();
        msgDB.beginTransaction();
        try{
            msgDB.insertWithOnConflict(TABLE_Messages,null,values,SQLiteDatabase.CONFLICT_IGNORE);
            msgDB.setTransactionSuccessful();
        }catch (Exception e){
            Log.d("DB","Error while inserting");
        }finally {
            msgDB.endTransaction();
        }
    }
}
