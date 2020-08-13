package com.sklyarov.okhttptest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnRegistration = findViewById(R.id.btn_registration);
        btnRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                User user = new User("one@mail.com", "one1", "One1one11");

                Request request = new Request.Builder()
                        .url(BuildConfig.SERVER_URL.concat("registration/"))
                        .post(RequestBody.create(JSON, new Gson().toJson(user)))
                        .build();

                OkHttpClient client = new OkHttpClient();

                client.newCall(request).enqueue(new Callback() {

                    Handler handler = new Handler(getMainLooper());

                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "Error request", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (response.isSuccessful()) {
                                    Toast.makeText(MainActivity.this, "Registration OK", Toast.LENGTH_SHORT).show();

                                } else {


                                    Toast.makeText(MainActivity.this, "Registration failed" + response.message(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            }
        });

        Button btnAuthentication = findViewById(R.id.btn_authentication);
        btnAuthentication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                User user = new User("one@mail.com", "one1", "One1one11");

                final Request request = new Request.Builder()
                        .url(BuildConfig.SERVER_URL.concat("user/"))
                        .build();

                OkHttpClient client = ApiUtilities.getBasicAuthClient(
                        user.getEmail(),
                        user.getPassword(),
                        true);

                client.newCall(request).enqueue(new Callback() {

                    Handler handler = new Handler(getMainLooper());

                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "Error request", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (response.isSuccessful()) {
                                    try {
                                        Gson gson = new Gson();
                                        JsonObject json = gson.fromJson(response.body().string(), JsonObject.class);
                                        User user = gson.fromJson(json.get("data"), User.class);

                                        Toast.makeText(MainActivity.this, "Authorization OK \n" + user.toString(), Toast.LENGTH_SHORT).show();
                                    } catch (IOException e) {
                                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(MainActivity.this, "Authorization failed" + response.message(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            }
        });
    }
}