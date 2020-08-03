package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ImageFragment extends Fragment {
    final int ImageRequestCode = 1;
    final Intent[] intent = new Intent[1];

    ImageView imageview;

    static ImageFragment newInstance(){
        ImageFragment imageFragment = new ImageFragment();
        return imageFragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //fragment(due to its dynamic nature) can not use finViewById directly
        //Button button = findViewById(R.id.clickBtn);
        View view = inflater.inflate(R.layout.show_pic, container, false);


        Button clkButton = view.findViewById(R.id.clickBtn);
        imageview = view.findViewById(R.id.imageView2);

        //if click button, select photo from album
        clkButton.setOnClickListener(new View.OnClickListener(){
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

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //the result code we want to respond to
        if(requestCode == ImageRequestCode){
            Log.d("Activity Result Debug", "album activity return success");
            //see if request is successful (app didn't crash)
            if(resultCode == Activity.RESULT_OK){
                //get our image from the album activity
                Uri uri = data.getData();
                Log.d("Activity Result Debug", ""+uri);

                //get image uri as a bitmap
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //show image
                if(uri != null){
                    imageview.setImageBitmap(bitmap);
                    imageview.setScaleType(ImageView.ScaleType.FIT_CENTER);
                }

                //Log.d("Base64", base64_img);

                //send to classifier api
                MyNetwork.RetrofitRequest request = MyNetwork.RetrofitRequest.getInstance();
                request.sendPicture(bitmap);
            }
        }
    }
}
