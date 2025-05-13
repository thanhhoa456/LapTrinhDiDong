package com.example.laixea1.api;

import com.example.laixea1.entity.Question;
import com.google.gson.GsonBuilder;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit = null;

    private static Retrofit getRetrofit() {
        if (retrofit == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .build();

            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(Question.class, new QuestionDeserializer());

            retrofit = new Retrofit.Builder()
                    .baseUrl("http://10.0.2.2:8080/api/")
                    .addConverterFactory(GsonConverterFactory.create(gsonBuilder.create()))
                    .client(client)
                    .build();
        }
        return retrofit;
    }

    public static <T> T createService(Class<T> serviceClass) {
        return getRetrofit().create(serviceClass);
    }

    // Optional: Keep a specific method for ApiService if needed elsewhere
    public static ApiService getApiService() {
        return createService(ApiService.class);
    }
}