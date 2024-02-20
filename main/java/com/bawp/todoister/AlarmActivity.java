package com.bawp.todoister;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;



public class AlarmActivity extends AppCompatActivity {
    TextView taskTV;
    ImageView imageView;

    MediaPlayer mediaPlayer;



    long id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.notification);
        mediaPlayer.start();
        imageView = findViewById(R.id.imageView1);
        taskTV = findViewById(R.id.task_title_al_ac);
        if (getIntent().getExtras() != null) {
            taskTV.setText("Its Time!!!!  " +getIntent().getStringExtra("TITLE"));

        }

        Glide.with(getApplicationContext()).load(R.drawable.ala).into(imageView);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
    }
}