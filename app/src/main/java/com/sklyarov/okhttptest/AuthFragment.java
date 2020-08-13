package com.sklyarov.okhttptest;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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

import com.sklyarov.okhttptest.model.User;
import com.sklyarov.okhttptest.model.UserServerData;

import okhttp3.Credentials;
import retrofit2.Call;
import retrofit2.Response;

public class AuthFragment extends Fragment {
    private AutoCompleteTextView mEmail;
    private EditText mPassword;
    private Button mEnter;
    private Button mRegister;
    private SharedPreferencesHelper mSharedPreferencesHelper;

    private ArrayAdapter<String> mEmailedUsersAdapter;

    public static AuthFragment newInstance() {
        return new AuthFragment();
    }

    private View.OnClickListener mOnEnterClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (isEmailValid() && isPasswordValid()) {

                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();

                ApiUtilities.getApiService().getUser(Credentials.basic(email, password)).enqueue(
                        new retrofit2.Callback<UserServerData>() {
                            Handler mainHandler = new Handler(getActivity().getMainLooper());

                            @Override
                            public void onResponse(Call<UserServerData> call, Response<UserServerData> response) {
                                mainHandler.post(() -> {
                                    if (!response.isSuccessful()) {
                                        //todo добавить полноценную обработку ошибок по кодам ответа от сервера и телу запроса
                                        showMessage(R.string.auth_error);
                                    } else {
                                        User user = response.body().getData();

                                        Intent startProfileIntent = new Intent(getActivity(), ProfileActivity.class);
                                        startProfileIntent.putExtra(ProfileActivity.USER_KEY, user);
                                        startActivity(startProfileIntent);
                                        getActivity().finish();
                                    }
                                });
                            }

                            @Override
                            public void onFailure(Call<UserServerData> call, Throwable t) {
                                mainHandler.post(() -> showMessage(R.string.request_error));
                            }
                        }
                );
            } else {
                showMessage(R.string.input_error);
            }
        }
    };

    private View.OnClickListener mOnRegisterClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, RegistrationFragment.newInstance())
                    .addToBackStack(RegistrationFragment.class.getName())
                    .commit();
        }
    };

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fr_auth, container, false);

        mSharedPreferencesHelper = new SharedPreferencesHelper(getActivity());

        mEmail = v.findViewById(R.id.etEmail);
        mPassword = v.findViewById(R.id.etPassword);
        mEnter = v.findViewById(R.id.buttonEnter);
        mRegister = v.findViewById(R.id.buttonRegister);

        mEnter.setOnClickListener(mOnEnterClickListener);
        mRegister.setOnClickListener(mOnRegisterClickListener);
        mEmail.setOnFocusChangeListener(mOnEmailFocusChangeListener);

        mEmailedUsersAdapter = new ArrayAdapter<>(
                getActivity(),
                android.R.layout.simple_dropdown_item_1line,
                mSharedPreferencesHelper.getSuccessEmails()
        );
        mEmail.setAdapter(mEmailedUsersAdapter);
        return v;
    }
}
