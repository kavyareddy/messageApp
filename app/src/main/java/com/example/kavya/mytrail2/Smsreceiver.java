package com.example.kavya.mytrail2;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.Toast;

public class Smsreceiver extends BroadcastReceiver {

    private static final int NOTIFY_ME_ID=1337;
    static NotificationManager notificationmgr;
    static Notification note;
    String notQuestion = "true";//should not be static
    static String option1Txt="";
    static String option2Txt="";
    static String option3Txt="";
    static String option4Txt="";

    MessagesDatabase msgDB;
   //MessageDBHelper dbhelper;

    public Smsreceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving

        msgDB = new MessagesDatabase(context);

        notificationmgr= (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        note = new Notification(R.drawable.n_icon,"Android Example Status message!", System.currentTimeMillis());

        // This pending intent will open after notification click
        PendingIntent notifyIntent= PendingIntent.getActivity(context, 0,
                new Intent(context, ChatMain.class),0);



        // an Intent broadcast.
        Bundle bundle = intent.getExtras();
        SmsMessage[] msgs = null;
        String msg = "";
        String str = "";
        if (bundle != null) {
            //---retrieve the SMS message received---
            Object[] pdus = (Object[]) bundle.get("pdus");
            msgs = new SmsMessage[pdus.length];
            for (int i = 0; i < msgs.length; i++) {
                msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                str += "SMS from " + msgs[i].getOriginatingAddress();
                str += " :";
                str += msgs[i].getMessageBody().toString();
                str += "\n";
                if(msgs[i].getOriginatingAddress().equals("+15125457273")){
                    note.setLatestEventInfo(context, "Tailored Health Messaging",msgs[i].getMessageBody().toString(), notifyIntent);
                    notificationmgr.notify(NOTIFY_ME_ID, note);
                    ChatMain.data.add(msgs[i].getMessageBody().toString());
//                    ChatMain.msgsent = false;
//                    ChatMain.msgStatus.add(ChatMain.msgsent);
                    //ChatMain.adp.notifyDataSetChanged();

                    String sms = msgs[i].getMessageBody().toString();

                    String msgTypeSent = "false";
//                    ContentValues values = new ContentValues();
//                    values.put(MessageDBHelper.MESSAGE_BODY, sms);
//                    values.put(MessageDBHelper.MESSAGE_TYPE, ChatMain.msgTypeSent);
//                    dbHelper.insert(values);
//                    ChatMain.myCursor = ChatMain.mydb.rawQuery("SELECT * From messagesTable1",null);
//                    ChatMain.myCrsAdp.changeCursor(ChatMain.myCursor);


                    //conditions to be checked.
//                    if(sms.contains("Y or N")){
//                        option1Txt = "Y";
//                        option2Txt = "N";
//                        option3Txt = "";
//                        option4Txt = "";
//                        notQuestion = "false";
//                    }else if(sms.contains("GO or NOGO")){
//                        option1Txt = "GO";
//                        option2Txt = "NOGO";
//                        option3Txt = "";
//                        option4Txt = "";
//                        notQuestion = "false";
//                    }else if(sms.contains("MALE or FEMALE")) {
//                        option1Txt = "MALE";
//                        option2Txt = "FEMALE";
//                        option3Txt = "OTHERS";
//                        option4Txt = "";
//                        notQuestion = "false";
//                    }else if(sms.contains("GO or EXIT")){
//                        option1Txt = "GO";
//                        option2Txt = "EXIT";
//                        option3Txt = "";
//                        option4Txt = "";
//                        notQuestion = "false";
//                    }else if(sms.contains("Text a number")){
//                        notQuestion = "number";
//                    }else{
//                        notQuestion = "true";
//                    }



                    String smsOriginal = sms;
                    sms = sms.toUpperCase();
                    if(sms.matches("^\\s*[A-Za-z0-9,;'\"\\s]+[.?!]*\\s*[A-Za-z0-9,;'\"\\s]+TEXT \\w+ OR \\w+$")){

                        String[] options = sms.split(" ");
                        int len = options.length;
                        option1Txt = options[len-3];
                        option2Txt = options[len-1];
                        option3Txt = "";
                        option4Txt = "";
                        notQuestion = "false";
                    }else if(sms.matches("^\\s*[A-Za-z0-9,;'\"\\s]+[.?!]*\\s*[A-Za-z0-9,;'\"\\s]+TEXT \\w+ OR \\w+ OR \\w+$")){
                        System.out.println("matched");
                        String[] options = sms.split(" ");
                        int len = options.length;
                        option1Txt = options[len-5];
                        option2Txt = options[len-3];
                        option3Txt = options[len-1];
                        option4Txt = "";
                        notQuestion = "false";
                    }else if(sms.matches("^\\s*[A-Za-z0-9,;'\"\\s]+[.?!]*\\s*[A-Za-z0-9,;'\"\\s]+TEXT \\w+ OR \\w+ OR \\w+ OR \\w+$")){
                        System.out.println("matched");
                        String[] options = sms.split(" ");
                        int len = options.length;
                        option1Txt = options[len-7];
                        option2Txt = options[len-5];
                        option3Txt = options[len-3];
                        option4Txt = options[len-1];
                        notQuestion = "false";
                    }else if(sms.matches("^\\s*[A-Za-z0-9,;'\"\\s]+[.?!]*\\s*[A-Za-z0-9,;'\"\\s]+TEXT [A-Za-z0-9,;'\"\\s]+NUMBER$")) {
                        notQuestion = "number";
                    }else{
                        notQuestion = "true";
                    }


                    //ChatMain.msgFormat.add(notQuestion);

                    msgDB.insert(smsOriginal,msgTypeSent, notQuestion, option1Txt,option2Txt,option3Txt,option4Txt);
                    Cursor myCursor = msgDB.getAll();
                    ChatMain.myCrsAdp.changeCursor(myCursor);

                }

            }

            //---display the new SMS message---

            //Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
        }
    }
}
