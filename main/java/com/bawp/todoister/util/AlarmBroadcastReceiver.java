package com.bawp.todoister.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.bawp.todoister.AlarmActivity;

public class AlarmBroadcastReceiver extends BroadcastReceiver {


    String title, desc, date, time;
    @Override
    public void onReceive(Context context, Intent intent) {
        title = intent.getStringExtra("TITLE");

        Intent i = new Intent(context, AlarmActivity.class);
        i.putExtra("TITLE", title);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }
}
