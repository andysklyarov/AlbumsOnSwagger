package com.sklyarov.albumsonswagger.albums;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.sklyarov.albumsonswagger.AuthFragment;
import com.sklyarov.albumsonswagger.SingleFragmentActivity;

public class AlbumsActivity extends SingleFragmentActivity {

    private String currentUser;

    @Override
    protected Fragment getFragment() {
        return AlbumsFragment.newInstance(currentUser);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Bundle bundle = getIntent().getExtras();
        currentUser = bundle.getString(AuthFragment.CURRENT_USER_KEY);

        super.onCreate(savedInstanceState);
    }
}
