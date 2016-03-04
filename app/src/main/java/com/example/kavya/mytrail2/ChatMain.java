package com.example.kavya.mytrail2;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;


public class ChatMain extends ActionBarActivity {

    Button btnSendSMS;
    String txtPhoneNo = "+19795879728";
    EditText txtMessage;
    ListView lv;
    LinearLayout myLayout;
    static ArrayList<String> data = new ArrayList<String>();
    //static ArrayList<Boolean> msgStatus = new ArrayList<Boolean>();
    static ArrayList<Boolean> msgFormat = new ArrayList<Boolean>();
    //static ArrayAdapter adp;
    //static ChatAdapter adp;
//    TextView textSent;
//    static boolean msgsent = true;
    PopupWindow popupWindow;
    TextView smstext;
    RadioButton option1Dismiss;
    RadioButton option2Dismiss;
    Button option3Dismiss;
    Button option4Dismiss;
    Button option5Dismiss;
    Button close;
    static boolean active = false;



    //static MessagesDBHelper msgDBHelper;
    static MessagesDatabase msgdb;
    static Cursor myCursor;
    static MyCursorAdapter myCrsAdp;
    static String msgTypeSent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_main);
        myLayout = new LinearLayout(this);

        msgdb = new MessagesDatabase(this);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);//to stop auto popingup of keyboard


        LayoutInflater layoutInflater
                = (LayoutInflater) getBaseContext()
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = layoutInflater.inflate(R.layout.popupwindow, null);
        popupWindow = new PopupWindow(
                popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        smstext = (TextView) popupView.findViewById(R.id.sms);
        option1Dismiss = (RadioButton) popupView.findViewById(R.id.option1);
        option2Dismiss = (RadioButton) popupView.findViewById(R.id.option2);
        option3Dismiss = (RadioButton) popupView.findViewById(R.id.option3);
        option4Dismiss = (RadioButton) popupView.findViewById(R.id.option4);
        close = (Button) popupView.findViewById(R.id.btnClose);

        if (Smsreceiver.notificationmgr != null) {
            Smsreceiver.notificationmgr.cancel(1337);
        }
        btnSendSMS = (Button) findViewById(R.id.btnSend);
        //textSent = (TextView) findViewById(R.id.textSent);
        txtMessage = (EditText) findViewById(R.id.txt);
        lv = (ListView) findViewById(R.id.list);
        //adp = new ChatAdapter();
        //lv.setAdapter(adp);

        //get msgType from db in desending order of _id
        Cursor c = msgdb.getLast();
        String msgType;
//        boolean type = true;
//        if(msgFormat.size()!=0)
//             type = msgFormat.get(msgFormat.size()-1);
//        if(type){
//        if(cursor != null && cursor.moveToFirst()) {
//            id = cursor.getInt(0);
//        }
        final String sms;
        final String option1;
        final String option2;
        if (c != null && c.getCount() > 0) {
            if (c.moveToFirst()) {
                msgType = c.getString(c.getColumnIndexOrThrow(msgdb.C_MSGTYPE));
                sms = c.getString(c.getColumnIndexOrThrow(msgdb.C_MSGS));
                option1 = c.getString(c.getColumnIndexOrThrow(msgdb.C_OPTION1));
                option2 = c.getString(c.getColumnIndexOrThrow(msgdb.C_OPTION2));
            } else {
                msgType = "true";
                sms = "";
                option1 = "";
                option2 = "";
            }

        } else {
            msgType = "true";
            sms = "";
            option1 = "";
            option2 = "";
        }
        if (msgType.equals("true")) {
            txtMessage.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(txtMessage, InputMethodManager.SHOW_IMPLICIT);

        } else if(msgType.equals("number")) {
            txtMessage.setRawInputType(Configuration.KEYBOARD_QWERTY);
        }else{
                txtMessage.setFocusableInTouchMode(false);

//            if (data.size() != 0) {
//                sms = data.get(data.size()-1);
//            } else
//                sms = "";

                txtMessage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //myLayout.getBackground().setAlpha(220);
                        popup(sms, option1,option2);
                        //myLayout.getBackground().setAlpha(0);
                    }
                });


        }




//        msgDBHelper = new MessageDBHelper(this);
//        mydb = msgDBHelper.getReadableDatabase();

        //myCursor = mydb.rawQuery("SELECT * From messagesTable1",null);

        myCursor = msgdb.getAll();
        myCrsAdp = new MyCursorAdapter(this,myCursor);
        lv.setAdapter(myCrsAdp);


        //textSent.setVisibility(View.INVISIBLE); giving error..
    }

    public void Send(View v) {
        //txtPhoneNo = "+19795879728";
        String message = txtMessage.getText().toString();
        if (txtPhoneNo.length() > 0 && message.length() > 0)
            sendSMS(txtPhoneNo, message);
        else
            Toast.makeText(getBaseContext(),
                    "Please enter both phone number and message.",
                    Toast.LENGTH_SHORT).show();
    }

    public void sendSMS(String phoneNumber, String message) {
        PendingIntent pi = PendingIntent.getActivity(this, 0,
                new Intent(this, ChatMain.class), 0);
//        SmsManager sms = SmsManager.getDefault();
//        sms.sendTextMessage(phoneNumber, null, message, pi, null);

//        String SENT = "SMS_SENT";
//        String DELIVERED = "SMS_DELIVERED";
//
//        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,
//                new Intent(SENT), 0);
//
//        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
//                new Intent(DELIVERED), 0);
//        //---when the SMS has been sent---
//        registerReceiver(new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context arg0, Intent arg1) {
//                switch (getResultCode()) {
//                    case Activity.RESULT_OK:
//                        Toast.makeText(getBaseContext(), "SMS sent",
//                                Toast.LENGTH_SHORT).show();
//                        break;
//                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
//                        Toast.makeText(getBaseContext(), "Generic failure",
//                                Toast.LENGTH_SHORT).show();
//                        break;
//                    case SmsManager.RESULT_ERROR_NO_SERVICE:
//                        Toast.makeText(getBaseContext(), "No service",
//                                Toast.LENGTH_SHORT).show();
//                        break;
//                    case SmsManager.RESULT_ERROR_NULL_PDU:
//                        Toast.makeText(getBaseContext(), "Null PDU",
//                                Toast.LENGTH_SHORT).show();
//                        break;
//                    case SmsManager.RESULT_ERROR_RADIO_OFF:
//                        Toast.makeText(getBaseContext(), "Radio off",
//                                Toast.LENGTH_SHORT).show();
//                        break;
//                }
//            }
//        }, new IntentFilter(SENT));
        //---when the SMS has been delivered---
//        registerReceiver(new BroadcastReceiver(){
//            @Override
//            public void onReceive(Context arg0, Intent arg1) {
//                switch (getResultCode())
//                {
//                    case Activity.RESULT_OK:
//                        Toast.makeText(getBaseContext(), "SMS delivered",
//                                Toast.LENGTH_SHORT).show();
//                        break;
//                    case Activity.RESULT_CANCELED:
//                        Toast.makeText(getBaseContext(), "SMS not delivered",
//                                Toast.LENGTH_SHORT).show();
//                        break;
//                }
//            }
//        }, new IntentFilter(DELIVERED));
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, pi, null);
        //sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
        //ll.setBackgroundResource(R.drawable.chat_bubble_green);
        data.add(message);
        //msgsent = true;
        //msgStatus.add(msgsent);
        msgFormat.add(true);
        //adp.notifyDataSetChanged();

        //add required lines

        msgTypeSent = "true";//sent or rcvd
//        ContentValues values = new ContentValues();
//        values.put(MessageDBHelper.MESSAGE_BODY, message);
//        values.put(MessageDBHelper.MESSAGE_TYPE, msgTypeSent);
//        msgDBHelper.insert(values);
//        myCursor = mydb.rawQuery("SELECT * From messagesTable",null);
//        myCrsAdp.changeCursor(myCursor);

        msgdb.insert(message,msgTypeSent, "true", "","");
        myCursor = msgdb.getAll();
        myCrsAdp.changeCursor(myCursor);
        txtMessage.setText("");
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
//
//     class ChatAdapter extends BaseAdapter {
//
//        /* (non-Javadoc)
//         * @see android.widget.Adapter#getCount()
//         */
//        @Override
//        public int getCount() {
//            return data.size();
//        }
//
//        /* (non-Javadoc)
//         * @see android.widget.Adapter#getItem(int)
//         */
//        @Override
//        public String getItem(int arg0) {
//            return data.get(arg0);
//        }
//
//        /* (non-Javadoc)
//         * @see android.widget.Adapter#getItemId(int)
//         */
//        @Override
//        public long getItemId(int arg0) {
//            return arg0;
//        }
//
//        /* (non-Javadoc)
//         * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
//         */
//        @Override
//        public View getView(int pos, View v, ViewGroup arg2) {
//            String msg = getItem(pos);
//            v = getLayoutInflater().inflate(R.layout.chatsend, null);
//            LinearLayout ll = (LinearLayout)v.findViewById(R.id.v1);
//            TextView lbl = (TextView) v.findViewById(R.id.textRcv2);
//            ImageView iv = (ImageView) v.findViewById(R.id.rcvImageView);
//            lbl.setText(msg);
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//            params.weight = 6.0f;
//            if(msg.length()>=25){
//                ViewGroup.LayoutParams pars = lbl.getLayoutParams();
//                pars.width = 850;
//                lbl.setLayoutParams(pars);
//            }
//            if (msgStatus.get(pos)) {
//                ll.setBackgroundResource(R.drawable.rectsend_bgd);
//                ll.setPadding(20, 20, 20, 20);
//                params.gravity = Gravity.RIGHT;
//
//                ll.setLayoutParams(params);
//
//            }
//            else {
//                ll.setBackgroundResource(R.drawable.rectrcv_bgd);
//                ll.setPadding(20, 20, 20, 20);
//                ll.setHorizontalGravity(Gravity.CENTER_HORIZONTAL);
//                params.gravity = Gravity.LEFT;
//                ll.setLayoutParams(params);
//
//            }
//            return v;
//        }
//
//    }

    @Override
    public void onResume() {
        super.onResume();
        active = true;

    }
    @Override //to avoid multiple instances of same activity when the adp change occurs
    protected void onDestroy() {
        popupWindow.dismiss();
        super.onDestroy();
    }
    @Override
    public void onPause() {
        super.onPause();
        active = false;
    }

    public void popup(String sms, String opt1, String opt2){
        smstext.setText(sms);
//        option1Dismiss.setText(Smsreceiver.option1Txt);
//        option2Dismiss.setText(Smsreceiver.option2Txt);
        option1Dismiss.setText(opt1);
        option2Dismiss.setText(opt2);
        option3Dismiss.setVisibility(View.GONE);
        option4Dismiss.setVisibility(View.GONE);
        option1Dismiss.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                String msgToSend = option1Dismiss.getText().toString();
                popupWindow.dismiss();
                sendSMS(txtPhoneNo, msgToSend);

            }});
        option2Dismiss.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                String msgToSend = option2Dismiss.getText().toString();
                sendSMS(txtPhoneNo, msgToSend);
                popupWindow.dismiss();
            }});
//        if(popupWindow.isShowing()){
//            popupWindow.dismiss();
//        }
         //myLayout.getBackground().setAlpha(100);
        close.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                popupWindow.dismiss();
            }});
        popupWindow.showAtLocation(myLayout,Gravity.CENTER,0,0);
        myLayout.setAlpha(0.5F);
//        popupWindow.setBackgroundDrawable(new ColorDrawable(
//                android.graphics.Color.TRANSPARENT));
        //@android:color/darker_gray
    }

}
class MyCursorAdapter extends CursorAdapter{

    public MyCursorAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.chatsend,parent,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView txtmsg = (TextView)view.findViewById(R.id.textRcv2);
        LinearLayout ll = (LinearLayout)view.findViewById(R.id.v1);
//        String message = cursor.getString(cursor.getColumnIndexOrThrow(MessageDBHelper.MESSAGE_BODY));
//        String msgSend = cursor.getString(cursor.getColumnIndexOrThrow(MessageDBHelper.MESSAGE_TYPE));
        String message = cursor.getString(cursor.getColumnIndexOrThrow(ChatMain.msgdb.C_MSGS));
        String msgSend = cursor.getString(cursor.getColumnIndexOrThrow(ChatMain.msgdb.C_STATUS));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.weight = 6.0f;
        if(message.length()>=25){
            ViewGroup.LayoutParams pars = txtmsg.getLayoutParams();
            pars.width = 850;
            txtmsg.setLayoutParams(pars);
        }
        if (msgSend.equals("true")) {
            ll.setBackgroundResource(R.drawable.rectsend_bgd);
            ll.setPadding(20, 20, 20, 20);
            params.gravity = Gravity.RIGHT;

            ll.setLayoutParams(params);
        }
        else {
            ll.setBackgroundResource(R.drawable.rectrcv_bgd);
            ll.setPadding(20, 20, 20, 20);
            ll.setHorizontalGravity(Gravity.CENTER_HORIZONTAL);
            params.gravity = Gravity.LEFT;
            ll.setLayoutParams(params);
        }

        txtmsg.setText(message);

    }
}
