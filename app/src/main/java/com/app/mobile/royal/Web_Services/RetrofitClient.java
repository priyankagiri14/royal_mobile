package com.app.mobile.royal.Web_Services;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import static com.app.mobile.royal.Web_Services.IBaseURL.BASE_URL;


public class RetrofitClient {
    private static Retrofit retrofit = null;
    public static Retrofit getClient()
    {   if(retrofit== null)
    {

        final OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        // added query param of deviceType
        Interceptor clientInterceptor = chain -> {
            Request request = chain.request();
            HttpUrl url = request.url().newBuilder().addQueryParameter("deviceType", "MOBILE").
                    addQueryParameter("appName","ROYAL_MOBILE").build();
            request = request.newBuilder().url(url).build();
            return chain.proceed(request);
        };

        httpClient .addNetworkInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        ).addInterceptor(clientInterceptor)
            .build();
        OkHttpClient client = httpClient.build();
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(client)
                .build();
    }
        return retrofit; //return RetroFit
    }
}
