package com.example.myapplication;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public class MyNetwork extends Fragment {

    static TextView resultText1;
    static TextView resultText2;
    static TextView resultText3;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.show_predict, container, false);
        resultText1 = view.findViewById(R.id.resultText1);
        resultText2 = view.findViewById(R.id.resultText2);
        resultText3 = view.findViewById(R.id.resultText3);

        return view;
    }

    static MyNetwork newInstance(){
        return new MyNetwork();
    }

    //build this according to the response json (use the same keys as the Json)
    public static class Picture {
        //it is important to have these get and set methods
        public String getPredict0() {
            return predict0;
        }

        public void setPredict0(String predict0) {
            this.predict0 = predict0;
        }

        public String getScore0() {
            return score0;
        }

        public void setScore0(String score0) {
            this.score0 = score0;
        }

        public String getPredict1() {
            return predict1;
        }

        public void setPredict1(String predict1) {
            this.predict1 = predict1;
        }

        public String getScore1() {
            return score1;
        }

        public void setScore1(String score1) {
            this.score1 = score1;
        }

        public String getPredict2() {
            return predict2;
        }

        public void setPredict2(String predict2) {
            this.predict2 = predict2;
        }

        public String getScore2() {
            return score2;
        }

        public void setScore2(String score2) {
            this.score2 = score2;
        }

        private String predict0, score0, predict1, score1, predict2, score2;

    }

    public interface ResnetApi {
        @FormUrlEncoded
        @POST("classifier/predict")
        Call<Picture> postPicture(@Field("img") String img);
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

        public HashMap<String, String> sendPicture(Bitmap bitmap) {
            HashMap<String, String> result = new HashMap<>();

            String base64encoded = encodeImage(bitmap);
            //create object to post
            //Picture picture = new Picture(base64encoded);

            Call<Picture> call = resnetApi.postPicture(base64encoded);

            //async call
            call.enqueue(new Callback<Picture>() {
                @Override
                public void onResponse(@NonNull Call<Picture> call,@NonNull Response<Picture> response) {
                    Log.d("Retrofit","Request Successful");
                    Log.d("Retrofit","result = " + response.body().predict0 + " " + response.body().score0);
                    resultText1.setText(String.format("%s=%s", response.body().predict0, response.body().score0));
                }

                @Override
                public void onFailure(@NonNull Call<Picture> call,@NonNull Throwable t) {
                    Log.d("Retrofit","Request Failed\n" + t);
                }
            });

            return result;
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
