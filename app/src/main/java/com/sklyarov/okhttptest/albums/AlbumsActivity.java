package com.sklyarov.okhttptest.albums;

import androidx.fragment.app.Fragment;

import com.sklyarov.okhttptest.SingleFragmentActivity;

public class AlbumsActivity extends SingleFragmentActivity {
    @Override
    protected Fragment getFragment() {
       return AlbumsFragment.newInstance();
    }
}
