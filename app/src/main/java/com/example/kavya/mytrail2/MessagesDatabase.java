package com.example.kavya.mytrail2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by kavya on 1/31/16.
 */
public class MessagesDatabase {
    public static final String TAG = "MessagesDatabase";
    int DB_VERSION = 1;
    public final String DB_NAME = "messagesdb1.db";

    public final String TABLE_NAME = "messagestable1";
    public final String C_id = "_id";
    public final String C_MSGS = "MessageBody";
    public final String C_STATUS="MessageSent";//to know msg sent or rcved
    public final String C_MSGTYPE = "MessageType";//to whether a que or not
    public final String C_OPTION1 = "Option1";//to store options
    public final String C_OPTION2 = "Option2";
    static int i = 1;

    Context context;
    MessagesDBHelper dbHelper;
    SQLiteDatabase msgDB;
    public MessagesDatabase(Context context){
        this.context =context;
//        i++;
//        DB_VERSION = i;
        dbHelper = new MessagesDBHelper(context,DB_NAME,null,DB_VERSION);//once this is called the db is created and the onCreate method is called => creating tables
        //dbHelper.onCreate(msgDB);
    }
    public void insert(String msgBody, String msgStatus, String msgType, String option1, String option2){
        msgDB = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(C_MSGS,msgBody);
        values.put(C_STATUS, msgStatus);
        values.put(C_MSGTYPE, msgType);
        values.put(C_OPTION1, option1);
        values.put(C_OPTION2, option2);
        msgDB.insertWithOnConflict(TABLE_NAME,null,values,SQLiteDatabase.CONFLICT_IGNORE);//inserts only those are not there in db
        msgDB.close();
    }

    public Cursor getAll(){
        msgDB = dbHelper.getReadableDatabase();
        Cursor messagesFetched;
        messagesFetched = msgDB.rawQuery("SELECT * FROM "+ TABLE_NAME ,null);
        //msgDB.close();
        return messagesFetched;

    }
    public Cursor getLast(){
        msgDB = dbHelper.getReadableDatabase();
        Cursor lastMsg;
        lastMsg = msgDB.rawQuery("SELECT * FROM "+ TABLE_NAME +" ORDER BY "+C_id+" DESC LIMIT 1",null);
        //msgDB.close();
        return lastMsg;
    }

    class MessagesDBHelper extends SQLiteOpenHelper {
        public MessagesDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);

        }

        @Override
        public void onCreate(SQLiteDatabase db) {//this function is called only once in the life time of the database
//            String sql1 = String.format("DROP TABLE if exists %s;", TABLE_NAME);
//            db.execSQL(sql1);
            String sql = String.format("CREATE TABLE %s( %s INTEGER PRIMARY KEY AUTOINCREMENT, %s text, %s text, %s text, %s text, %s text);", TABLE_NAME,C_id, C_MSGS, C_STATUS, C_MSGTYPE, C_OPTION1, C_OPTION2);
            Log.d(TAG, "onCreate: " + sql);
            db.execSQL(sql);//eq to execute db query
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            //don't drop
            String sql = String.format("DROP TABLE if exists %s;", TABLE_NAME);
            db.execSQL(sql);
            Log.d(TAG, "onUpgrade");
            onCreate(db);
        }
    }

}
