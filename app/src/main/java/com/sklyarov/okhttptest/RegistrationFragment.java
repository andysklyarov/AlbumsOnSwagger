package com.sklyarov.okhttptest;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.sklyarov.okhttptest.model.Errors;
import com.sklyarov.okhttptest.model.User;

import java.io.IOException;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

import static com.sklyarov.okhttptest.model.ServerCodes.*;


public class RegistrationFragment extends Fragment {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private EditText mEmail;
    private EditText mName;
    private EditText mPassword;
    private EditText mPasswordAgain;
    private Button mRegistration;

    public static RegistrationFragment newInstance() {
        return new RegistrationFragment();
    }

    private View.OnClickListener mOnRegistrationClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (isInputValid()) {

                Context context = getActivity();
                if (context != null) {
                    mEmail.setTextColor(ContextCompat.getColor(context, R.color.textColorPrimary));
                    mName.setTextColor(ContextCompat.getColor(context, R.color.textColorPrimary));
                    mPassword.setTextColor(ContextCompat.getColor(context, R.color.textColorPrimary));
                }


                User user = new User(
                        mEmail.getText().toString(),
                        mName.getText().toString(),
                        mPassword.getText().toString());

                ApiUtilities.getApiService().registration(user).enqueue(
                        new retrofit2.Callback<Void>() {
                            Handler mainHandler = new Handler(getActivity().getMainLooper());

                            @Override
                            public void onResponse(Call<Void> call, final Response<Void> response) {
                                mainHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (!response.isSuccessful()) {
                                            int responseCode = response.code();
                                            switch (responseCode) {
                                                case VALIDATION_FAILED:
                                                    ResponseBody responseBody = response.errorBody();
                                                    if (responseBody != null) {
                                                        Errors.ErrorsBean errors = convertToError(responseBody).getErrors();

                                                        String emailMessage = convertToMessage(errors.getEmail());
                                                        if (emailMessage != null) {
                                                            mEmail.setTextColor(Color.MAGENTA);
                                                            showMessage(emailMessage);
                                                        }

                                                        String nameMessage = convertToMessage(errors.getName());
                                                        if (nameMessage != null) {
                                                            mName.setTextColor(Color.MAGENTA);
                                                            showMessage(nameMessage);
                                                        }

                                                        String passwordMessage = convertToMessage(errors.getPassword());
                                                        if (passwordMessage != null) {
                                                            mPassword.setTextColor(Color.MAGENTA);
                                                            showMessage(passwordMessage);
                                                        }
                                                    } else {
                                                        showMessage(R.string.registration_error);
                                                        return;
                                                    }
                                                    break;
                                                case SERVER_INTERNAL_ERROR:
                                                    showMessage(R.string.response_code_500);
                                                    break;

                                                default:
                                                    showMessage(R.string.registration_error);
                                                    break;
                                            }
                                        } else {
                                            showMessage(R.string.registration_success);
                                            getFragmentManager().popBackStack();
                                        }
                                    }
                                });
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                mainHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        showMessage(R.string.request_error);
                                    }
                                });
                            }
                        }
                );
            } else {
                showMessage(R.string.input_error);
            }
        }
    };

    private String convertToMessage(List<String> emailErrors) {
        StringBuilder errorMessage = new StringBuilder();
        if (emailErrors != null && emailErrors.size() != 0) {
            for (String error : emailErrors) {
                errorMessage.append(error);
            }
            return errorMessage.toString();
        } else {
            return null;
        }
    }

    private Errors convertToError(ResponseBody errorBody) {
        Errors resultError = null;
        Gson gson = new Gson();
        try {
            resultError = gson.fromJson(errorBody.string(), Errors.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultError;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup
            container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fr_registration, container, false);

        mEmail = view.findViewById(R.id.etEmail);
        mName = view.findViewById(R.id.etName);
        mPassword = view.findViewById(R.id.etPassword);
        mPasswordAgain = view.findViewById(R.id.tvPasswordAgain);
        mRegistration = view.findViewById(R.id.btnRegistration);

        mRegistration.setOnClickListener(mOnRegistrationClickListener);

        return view;
    }

    private boolean isInputValid() {
        return isEmailValid(mEmail.getText().toString())
                && !TextUtils.isEmpty(mName.getText())
                && isPasswordsValid();
    }

    private boolean isEmailValid(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isPasswordsValid() {
        String password = mPassword.getText().toString();
        String passwordAgain = mPasswordAgain.getText().toString();

        return password.equals(passwordAgain)
                && !TextUtils.isEmpty(password)
                && !TextUtils.isEmpty(passwordAgain);
    }

    private void showMessage(@StringRes int string) {
        Toast.makeText(getActivity(), string, Toast.LENGTH_LONG).show();
    }

    private void showMessage(String string) {
        Toast.makeText(getActivity(), string, Toast.LENGTH_LONG).show();
    }

}
