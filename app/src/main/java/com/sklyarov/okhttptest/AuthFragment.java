package com.sklyarov.okhttptest;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;

import com.sklyarov.okhttptest.albums.AlbumsActivity;
import com.sklyarov.okhttptest.model.User;

import java.net.UnknownHostException;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;

import static com.sklyarov.okhttptest.model.ServerCodes.SERVER_INTERNAL_ERROR;
import static com.sklyarov.okhttptest.model.ServerCodes.USER_NOT_AUTHORIZED;
import static com.sklyarov.okhttptest.model.ServerCodes.VALIDATION_FAILED;

public class AuthFragment extends Fragment {

    private AutoCompleteTextView mEmail;
    private EditText mPassword;
    private Button mEnter;
    private Button mRegister;

    public static AuthFragment newInstance() {
        return new AuthFragment();
    }

    private View.OnClickListener mOnEnterClickListener = new View.OnClickListener() {
        @SuppressLint("CheckResult")
        @Override
        public void onClick(View view) {
            if (isEmailValid() && isPasswordValid()) {

                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();

                ApiUtilities.getAuthApiService(email, password)
                        .getUser()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(user -> {
//                                    Intent startProfileIntent = new Intent(getActivity(), ProfileActivity.class);
//                                    startProfileIntent.putExtra(ProfileActivity.USER_KEY, user);
//                                    startActivity(startProfileIntent);

                                    startActivity(new Intent(getActivity(), AlbumsActivity.class));
                                    getActivity().finish();
                                },
                                throwable -> {
                                    if (throwable instanceof HttpException) {
                                        HttpException httpException = ((HttpException) throwable);
                                        int responseCode = httpException.code();
                                        ErrorHandling(responseCode);
                                    } else if (throwable instanceof UnknownHostException) {
                                        showMessage("Нет доступа к серверу. Проверьте соединение.");
                                    } else {
                                        showMessage("Неверный логин или пароль");
                                    }
                                });
            } else {
                showMessage(R.string.input_error);
            }
        }
    };

    private void ErrorHandling(int responseCode) {
        switch (responseCode) {
            case VALIDATION_FAILED:
                showMessage(R.string.response_code_400);
                break;
            case USER_NOT_AUTHORIZED:
                showMessage(R.string.response_code_401);
                break;
            case SERVER_INTERNAL_ERROR:
                showMessage(R.string.response_code_500);
                break;
        }
    }

    private View.OnClickListener mOnRegisterClickListener = view -> getFragmentManager()
            .beginTransaction()
            .replace(R.id.fragmentContainer, RegistrationFragment.newInstance())
            .addToBackStack(RegistrationFragment.class.getName())
            .commit();

    private View.OnFocusChangeListener mOnEmailFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            if (hasFocus) {
                mEmail.showDropDown();
            }
        }
    };

    private boolean isEmailValid() {
        return !TextUtils.isEmpty(mEmail.getText())
                && Patterns.EMAIL_ADDRESS.matcher(mEmail.getText()).matches();
    }

    private boolean isPasswordValid() {
        return !TextUtils.isEmpty(mPassword.getText());
    }

    private void showMessage(@StringRes int string) {
        Toast.makeText(getActivity(), string, Toast.LENGTH_LONG).show();
    }

    private void showMessage(String string) {
        Toast.makeText(getActivity(), string, Toast.LENGTH_LONG).show();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fr_auth, container, false);

        mEmail = v.findViewById(R.id.etEmail);
        mPassword = v.findViewById(R.id.etPassword);
        mEnter = v.findViewById(R.id.buttonEnter);
        mRegister = v.findViewById(R.id.buttonRegister);

        mEnter.setOnClickListener(mOnEnterClickListener);
        mRegister.setOnClickListener(mOnRegisterClickListener);
        mEmail.setOnFocusChangeListener(mOnEmailFocusChangeListener);

        return v;
    }
}
