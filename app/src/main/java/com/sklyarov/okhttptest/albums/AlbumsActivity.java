package com.sklyarov.okhttptest.albums;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.sklyarov.okhttptest.AuthFragment;
import com.sklyarov.okhttptest.ProfileActivity;
import com.sklyarov.okhttptest.R;
import com.sklyarov.okhttptest.SingleFragmentActivity;
import com.sklyarov.okhttptest.model.User;

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
