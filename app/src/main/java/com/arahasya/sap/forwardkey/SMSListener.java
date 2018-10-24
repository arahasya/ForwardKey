package com.arahasya.sap.forwardkey;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SMSListener extends BroadcastReceiver {


    String[] keywords;
    int[] contact_count;
    int count;
    String[] number_list, number;

    Pattern p;
    String regex;
    Matcher m;

    String messageBody;
    String SENT = "SMS_SENT";

    PendingIntent sentPI;

    @Override
    public void onReceive(Context context, Intent intent) {

        sentPI = PendingIntent.getBroadcast(context, 0, new Intent(SENT), 0);
        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction())) {
            for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                messageBody = smsMessage.getMessageBody();
            }

            SQLiteAdapter sqLiteAdapter = new SQLiteAdapter(context);

            ArrayList<RecordModel> recordList;
            recordList = sqLiteAdapter.getAllRecords(sqLiteAdapter);

            count = recordList.size();

            keywords = new String[count];
            contact_count = new int[count];

            number_list = new String[count];

            for (int i = 0; i < count; i++) {

                keywords[i] = recordList.get(i).getKeyword();
                regex = "\\b" + keywords[i] + "\\b";
                p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
                m = p.matcher(messageBody);

                // if(m.find() || keywords[i].equals("Forward=all+")){
                number_list[i] = recordList.get(i).getContactNumbers();
                contact_count[i] = recordList.get(i).getCount();
                number = new String[contact_count[i]];
                number = number_list[i].split(",");


                if (keywords[i].equals("Forward=all+")) {

                    sendSMS(i);

                } else {

                    if (m.find()) {
                        Log.i("Found", "true");
                        sendSMS(i);
                    }

                }

            }

        }

        int result = getResultCode();
        Log.i("Result", "ok" + result);
    }

    void sendSMS(int i) {
        for (int j = 0; j < contact_count[i]; j++) {
            //   Log.i("Number"," num"+number[j]);
            SmsManager.getDefault().sendTextMessage(number[j], null, "[ForwardKey]" + messageBody, sentPI, null);
        }
    }


}