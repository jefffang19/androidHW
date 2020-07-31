package com.example.myapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.IOException;
import java.net.URI;

public class MainActivity extends AppCompatActivity {

    final int ImageRequestCode = 1;
    final Intent[] intent = new Intent[1];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); //R(created by android) gets data from res folder

        Button button = findViewById(R.id.clickBtn);
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                intent[0] = new Intent(Intent.ACTION_GET_CONTENT);
                //we want to pick image
                intent[0].setType("image/*");
                //pick multiple images
                //intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);

                //if need to get result from activity, use startActivityForResult
                //startActivity(intent);
                startActivityForResult(intent[0], ImageRequestCode);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //the result code we want to respond to
        if(requestCode == ImageRequestCode){
            Log.d("Activity Result Debug", "album activity return success");
            //see if request is successful (app didn't crash)
            if(resultCode == RESULT_OK){
                //get our image from the album activity
                Uri uri = data.getData();
                Log.d("Activity Result Debug", ""+uri);

                //get image uri as a bitmap
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if(uri != null){
                    setContentView(R.layout.show_pic);
                    ImageView imageView = findViewById(R.id.imageView2);
                    imageView.setImageBitmap(bitmap);
                    imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                }
            }
        }
    }
}