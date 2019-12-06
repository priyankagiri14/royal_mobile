package com.app.mobile.royal.Web_Services;

import com.app.mobile.royal.Web_Services.Utils.Pref;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import static com.app.mobile.royal.Web_Services.IBaseURL.BASE_URL_GET;


public class RetrofitToken {
    public static String token;
    private static Retrofit retrofit = null;
    public static Retrofit getClient()
    {   if(retrofit== null)
    {

        final OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        // added query param of deviceType
        Interceptor clientInterceptor = chain -> {
            Request request = chain.request();
            HttpUrl url = request.url().newBuilder().addQueryParameter("deviceType", "MOBILE").build();
            request = request.newBuilder().url(url).build();
            return chain.proceed(request);
        };
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Interceptor.Chain chain) throws IOException {
                Request original = chain.request();
                Request request = original.newBuilder()
                        .header("Authorization", Pref.getUserToken(MyApp.getContext()))
                        .method(original.method(), original.body())
                        .build();

                return chain.proceed(request);
            }
        })
                //here we adding Interceptor for full level logging
                .addNetworkInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .addInterceptor(clientInterceptor)
                .build();


        OkHttpClient client = httpClient.build();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL_GET)
                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(client)
                .build();

        token=Pref.getUserToken(MyApp.getContext());


    }
        return retrofit;


    }
}
