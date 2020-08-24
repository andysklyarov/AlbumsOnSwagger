package com.sklyarov.okhttptest;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.sklyarov.okhttptest.model.Errors;
import com.sklyarov.okhttptest.model.User;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.HttpException;
import retrofit2.Response;

import static com.sklyarov.okhttptest.model.ServerCodes.SERVER_INTERNAL_ERROR;
import static com.sklyarov.okhttptest.model.ServerCodes.VALIDATION_FAILED;


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
        @SuppressLint("CheckResult")
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

                ApiUtilities.getApiService()
                        .registration(user)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(() -> {
                                    showMessage(R.string.registration_success);
                                    if (getFragmentManager() != null)
                                        getFragmentManager().popBackStack();
                                },
                                throwable -> {
                                    if (throwable instanceof HttpException) {
                                        HttpException httpException = ((HttpException) throwable);
                                        int responseCode = httpException.code();

                                        Response<?> response = httpException.response();
                                        ResponseBody responseBody = null;
                                        if (response != null) responseBody = response.errorBody();

                                        ErrorHandling(responseBody, responseCode);
                                    } else if (throwable instanceof UnknownHostException) {
                                        showMessage(R.string.host_error);
                                    } else {
                                        showMessage(R.string.error_text);
                                    }
                                }
                        );
            } else {
                showMessage(R.string.input_error);
            }
        }
    };

    private void ErrorHandling(ResponseBody responseBody, int responseCode) {
        switch (responseCode) {
            case VALIDATION_FAILED:
                if (responseBody != null) {
                    Errors.ErrorsBean errors = convertToError(responseBody).getErrors();

                    String emailMessage = convertToMessage(errors.getEmail());
                    if (emailMessage != null) {
                        mEmail.setError(emailMessage);
                        mEmail.setTextColor(Color.MAGENTA);
                        showMessage(emailMessage);
                    }

                    String nameMessage = convertToMessage(errors.getName());
                    if (nameMessage != null) {
                        mName.setError(nameMessage);
                        mName.setTextColor(Color.MAGENTA);
                        showMessage(nameMessage);
                    }

                    String passwordMessage = convertToMessage(errors.getPassword());
                    if (passwordMessage != null) {
                        mPassword.setError(passwordMessage);
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
    }

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
