package com.example.foush.foushvision;

import com.google.gson.JsonObject;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by foush on 13/02/18.
 */

public interface RetrofitClient {
    @Multipart
    @POST("upload_file.php")
    Call<JsonObject> uploadImage(@Part MultipartBody.Part file);
}
