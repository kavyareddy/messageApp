package com.example.kavya.mytrail2;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
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
    static ArrayList<Boolean> msgStatus = new ArrayList<Boolean>();
    //static ArrayAdapter adp;
    static ChatAdapter adp;
    TextView textSent;
    static boolean msgsent = true;
    PopupWindow popupWindow;
    TextView smstext;
    Button option1Dismiss;
    Button option2Dismiss;
    static boolean active = false;



    static MessageDBHelper msgDBHelper;
    static SQLiteDatabase mydb;
    static Cursor myCursor;
    static MyCursorAdapter myCrsAdp;
    static String msgType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_main);
        myLayout = new LinearLayout(this);


        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);//to stop auto popingup of keyboard


        LayoutInflater layoutInflater
                = (LayoutInflater)getBaseContext()
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = layoutInflater.inflate(R.layout.popupwindow, null);
        popupWindow = new PopupWindow(
                popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        smstext = (TextView)popupView.findViewById(R.id.sms);
        option1Dismiss = (Button)popupView.findViewById(R.id.option1);
        option2Dismiss = (Button)popupView.findViewById(R.id.option2);


        if (Smsreceiver.notificationmgr != null) {
            Smsreceiver.notificationmgr.cancel(1337);
        }
        btnSendSMS = (Button) findViewById(R.id.btnSend);
        //textSent = (TextView) findViewById(R.id.textSent);
        txtMessage = (EditText) findViewById(R.id.txt);
        lv = (ListView) findViewById(R.id.list);
        //adp = new ChatAdapter();
        //lv.setAdapter(adp);
        if(Smsreceiver.notQuestion) {
            txtMessage.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(txtMessage, InputMethodManager.SHOW_IMPLICIT);

        }else {
            txtMessage.setFocusableInTouchMode(false);
            final String sms;
            if (data.size() != 0) {
                sms = data.get(data.size()-1);
            } else
                sms = "";

            txtMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //myLayout.getBackground().setAlpha(220);
                    popup(sms);
                    Smsreceiver.notQuestion = true;
                    //myLayout.getBackground().setAlpha(0);
                }
            });

        }




        msgDBHelper = new MessageDBHelper(this);
        mydb = msgDBHelper.getReadableDatabase();
        myCursor = mydb.rawQuery("SELECT * From messagesTable",null);
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
        msgsent = true;
        msgStatus.add(msgsent);
        //adp.notifyDataSetChanged();

        //add required lines

        msgType = "true";
        ContentValues values = new ContentValues();
        values.put(MessageDBHelper.MESSAGE_BODY, message);
        values.put(MessageDBHelper.MESSAGE_TYPE, msgType);
        msgDBHelper.insert(values);
        myCursor = mydb.rawQuery("SELECT * From messagesTable",null);
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

     class ChatAdapter extends BaseAdapter {

        /* (non-Javadoc)
         * @see android.widget.Adapter#getCount()
         */
        @Override
        public int getCount() {
            return data.size();
        }

        /* (non-Javadoc)
         * @see android.widget.Adapter#getItem(int)
         */
        @Override
        public String getItem(int arg0) {
            return data.get(arg0);
        }

        /* (non-Javadoc)
         * @see android.widget.Adapter#getItemId(int)
         */
        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        /* (non-Javadoc)
         * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
         */
        @Override
        public View getView(int pos, View v, ViewGroup arg2) {
            String msg = getItem(pos);
            v = getLayoutInflater().inflate(R.layout.chatsend, null);
            LinearLayout ll = (LinearLayout)v.findViewById(R.id.v1);
            TextView lbl = (TextView) v.findViewById(R.id.textRcv2);
            ImageView iv = (ImageView) v.findViewById(R.id.rcvImageView);
            lbl.setText(msg);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.weight = 6.0f;
            if(msg.length()>=25){
                ViewGroup.LayoutParams pars = lbl.getLayoutParams();
                pars.width = 850;
                lbl.setLayoutParams(pars);
            }
            if (msgStatus.get(pos)) {
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
            return v;
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        active = true;

    }
    @Override //to avoid multiple instances of same activity when the adp change occurs
    protected void onDestroy() {
        super.onDestroy();
    }
    @Override
    public void onPause() {
        super.onPause();
        active = false;
    }

    public void popup(String sms){
        smstext.setText(sms);
        option1Dismiss.setText(Smsreceiver.option1Txt);
        option2Dismiss.setText(Smsreceiver.option2Txt);
        option1Dismiss.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                String msgToSend = option1Dismiss.getText().toString();
                sendSMS(txtPhoneNo, msgToSend);
                popupWindow.dismiss();
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
        popupWindow.showAtLocation(myLayout,50,100,1000);
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
        String message = cursor.getString(cursor.getColumnIndexOrThrow(MessageDBHelper.MESSAGE_BODY));
        String notQuestion = cursor.getString(cursor.getColumnIndexOrThrow(MessageDBHelper.MESSAGE_TYPE));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.weight = 6.0f;
        if(message.length()>=25){
            ViewGroup.LayoutParams pars = txtmsg.getLayoutParams();
            pars.width = 850;
            txtmsg.setLayoutParams(pars);
        }
        if (notQuestion.equals("true")) {
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
