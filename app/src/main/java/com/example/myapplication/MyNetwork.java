package com.example.myapplication;

import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.ByteArrayOutputStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

public class MyNetwork {

    public static class Picture {
        @Expose
        @SerializedName("img")
        private String img;

        public Picture(String s) {
            this.img = s;
        }

    }

    public interface ResnetApi {
        @POST("classifier/predict")
        Call<Picture> postPicture(@Body Picture picture);
    }

    public static class RetrofitRequest {
        //when accessing database, use singleton (single instance)
        private static RetrofitRequest retrofitRequest = new RetrofitRequest();
        private ResnetApi resnetApi;

        private RetrofitRequest() {
            String myUrl = "http://10.0.2.2:8000/";
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(myUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            resnetApi = retrofit.create(ResnetApi.class);
        }

        public static RetrofitRequest getInstance() {
            return retrofitRequest;
        }

        public void sendPicture(Bitmap bitmap) {
            String base64encoded = encodeImage(bitmap);
            //create object to post
            Picture picture = new Picture(base64encoded);

            Call<Picture> call = resnetApi.postPicture(picture);

            //async call
            call.enqueue(new Callback<Picture>() {
                @Override
                public void onResponse(@NonNull Call<Picture> call,@NonNull Response<Picture> response) {
                    Log.d("Retrofit","Request Successful");
                    Log.d("Retrofit","result = " + response.toString());
                }

                @Override
                public void onFailure(@NonNull Call<Picture> call,@NonNull Throwable t) {
                    Log.d("Retrofit","Request Failed\n" + t);
                }
            });

        }
    }


    private static String encodeImage(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();

        Log.d("Retrofit", "Encode sucess");

        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }
}
