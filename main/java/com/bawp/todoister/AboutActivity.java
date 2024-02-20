package com.bawp.todoister;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bawp.todoister.util.CusDialog;

import java.io.ByteArrayOutputStream;

public class AboutActivity extends AppCompatActivity implements CusDialog.ExampleDialogListener{
    ImageView img;
    TextView name;
    TextView phone;
    ImageButton edit;
    ImageButton pic;

    SharedPreferences sp;
    SharedPreferences spPic;
    SharedPreferences.Editor er;
    SharedPreferences.Editor erPic;
    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        edit = findViewById(R.id.editAboutButton);
        name = findViewById(R.id.name_about);
        phone = findViewById(R.id.phone_about);
        pic = findViewById(R.id.takePicAbout);
        img  = findViewById(R.id.imageViewAbout);
        sp = getSharedPreferences("myPrefs",MODE_PRIVATE);
        spPic = getSharedPreferences("myPrefsPic",MODE_PRIVATE);
        name.setText(sp.getString("name",""));
        phone.setText(sp.getString("phone",""));
        String img_data = spPic.getString("image_data","");
        if( !img_data.equalsIgnoreCase("") ){
            byte[] b = Base64.decode(img_data, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
            img.setImageBitmap(bitmap);
        }


        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
            }
        });
        pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pici = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(pici,1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == 1 && resultCode == RESULT_OK){
            Bitmap profilePic = (Bitmap) data.getExtras().get("data");
            img.setImageBitmap(profilePic);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            profilePic.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] b = baos.toByteArray();
            String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
            erPic = spPic.edit();
            erPic.putString("image_data",encodedImage);
            erPic.commit();
        }


        super.onActivityResult(requestCode, resultCode, data);
    }

    public void openDialog() {
        CusDialog cusDialog = new CusDialog();
        cusDialog.show(getSupportFragmentManager(), "example dialog");
    }

    @Override
    public void applyTexts(String username, String phoneT) {
        name.setText(username);
        phone.setText(phoneT);
        er = sp.edit();
        er.putString("name",username);
        er.putString("phone",phoneT);
        er.commit();
    }
}