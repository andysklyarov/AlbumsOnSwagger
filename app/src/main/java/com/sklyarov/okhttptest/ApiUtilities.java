package com.sklyarov.okhttptest;

import com.google.gson.Gson;
import com.sklyarov.okhttptest.model.converter.DataConverterFactory;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

import okhttp3.Authenticator;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiUtilities {

    public static final List<Class<?>> NETWORK_EXCEPTIONS = Arrays.asList(
            UnknownHostException.class,
            SocketTimeoutException.class,
            ConnectException.class
    );

    private static OkHttpClient okHttpClient;
    private static Retrofit retrofit;
    private static Gson gson;
    private static AcademyApi api;


    public static OkHttpClient getBasicAuthClient(final String email, final String password, boolean newInstance) {

        if (newInstance || okHttpClient == null) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();

            builder.authenticator((route, response) -> {
                String credentials = Credentials.basic(email, password);
                return response.request().newBuilder().header("Authorization", credentials).build();
            });

            builder.addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)); //todo avoid when realise

            okHttpClient = builder.build();
        }
        return okHttpClient;
    }

    public static Retrofit getRetrofit() {
        return getRetrofit("", "");
    }

    public static Retrofit getRetrofit(String email, String password) {

        if (gson == null)
            gson = new Gson();

        retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.SERVER_URL)
                //need for interceptors
                .client(getBasicAuthClient(email, password, true))
                .addConverterFactory(new DataConverterFactory())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        return retrofit;
    }

    public static AcademyApi getApiService() {
        if (api == null)
            api = getRetrofit().create(AcademyApi.class);

        return api;
    }

    public static AcademyApi getApiService(String email, String password) {
        api = getRetrofit(email, password).create(AcademyApi.class);
        return api;
    }
}
