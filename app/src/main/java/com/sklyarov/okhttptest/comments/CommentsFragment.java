package com.sklyarov.okhttptest.comments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.sklyarov.okhttptest.ApiUtilities;
import com.sklyarov.okhttptest.AuthFragment;
import com.sklyarov.okhttptest.R;
import com.sklyarov.okhttptest.model.Album;
import com.sklyarov.okhttptest.model.CommentToSend;

import java.net.UnknownHostException;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;

import static com.sklyarov.okhttptest.model.ServerCodes.SERVER_INTERNAL_ERROR;

public class CommentsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    public static final String ALBUM_KEY = "ALBUM_KEY";

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecycler;
    private View mErrorView;
    private EditText mEditText;
    private Button mEnterButton;
    private TextView mErrorViewText;

    private Album mAlbum;
    private String currentUser;
    private boolean isFirstRun = true;

    private CommentsAdapter mCommentsAdapter;

    public static CommentsFragment newInstance(Album albumId, String currentUser) {
        Bundle args = new Bundle();

        args.putSerializable(ALBUM_KEY, albumId);
        args.putString(AuthFragment.CURRENT_USER_KEY, currentUser);

        CommentsFragment fragment = new CommentsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fr_recycler_comments, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mRecycler = view.findViewById(R.id.recycler_messages);
        mEnterButton = view.findViewById(R.id.btn_chatbox_send);
        mEditText = view.findViewById(R.id.ed_chatbox);

        mSwipeRefreshLayout = view.findViewById(R.id.refresher_messages);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mErrorView = view.findViewById(R.id.error_view);
        mErrorViewText = mErrorView.findViewById(R.id.tv_error_text);

        mEnterButton.setOnClickListener(buttonView -> sendCommentsWithCheck());

        mEditText.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (keyEvent != null && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                mEditText.clearFocus();
                sendCommentsWithCheck();
                return true;
            }

            return false;
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            mAlbum = (Album) args.getSerializable(ALBUM_KEY);
            getActivity().setTitle(mAlbum.getName());
            currentUser = args.getString(AuthFragment.CURRENT_USER_KEY);
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecycler.setLayoutManager(layoutManager);

        mCommentsAdapter = new CommentsAdapter(currentUser);
        mRecycler.setAdapter(mCommentsAdapter);

        onRefresh();
    }

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.post(() -> getComments(mAlbum.getId()));
    }

    @SuppressLint("CheckResult")
    private void getComments(int albumId) {

        ApiUtilities.getApiService()
                .getAlbumComments(albumId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> mSwipeRefreshLayout.setRefreshing(true))
                .doFinally(() -> {
                    mSwipeRefreshLayout.setRefreshing(false);
                    isFirstRun = false;
                })
                .subscribe(comments -> {

                            if (comments.size() == 0) {
                                mRecycler.setVisibility(View.GONE);
                                mErrorViewText.setText(R.string.comments_no_comments);
                                mErrorView.setVisibility(View.VISIBLE);
                            } else {
                                mRecycler.setVisibility(View.VISIBLE);
                                mErrorView.setVisibility(View.GONE);

                                if (comments.size() == mCommentsAdapter.getItemCount()) {
                                    if (!isFirstRun) showMessage(R.string.comments_no_new);
                                } else {
                                    mCommentsAdapter.addData(comments, true);
                                    if (!isFirstRun) showMessage(R.string.comments_updated);
                                }
                            }
                        }
                        , throwable -> {
                            mRecycler.setVisibility(View.GONE);
                            mErrorViewText.setText(R.string.comments_error);
                            mErrorView.setVisibility(View.VISIBLE);
                        });

    }

    @SuppressLint("CheckResult")
    private void sendComment(String text, int albumId) {

        ApiUtilities.getApiService()
                .sendComment(new CommentToSend(text, albumId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                            showMessage(R.string.registration_success);
                            onRefresh();
                        }
                        , throwable -> {
                            if (throwable instanceof HttpException) {
                                HttpException httpException = ((HttpException) throwable);
                                int responseCode = httpException.code();

                                if (responseCode == SERVER_INTERNAL_ERROR) {
                                    showMessage(R.string.response_code_500);
                                } else {
                                    showMessage(R.string.registration_error);
                                }
                            } else if (throwable instanceof UnknownHostException) {
                                showMessage(R.string.host_error);
                            } else {
                                showMessage(R.string.error_text);
                            }
                        });
    }

    private void sendCommentsWithCheck() {

        String messageText = mEditText.getText().toString();

        if (messageText.isEmpty()) {
            showMessage(R.string.comments_empty_text_to_send);
            return;
        }

        sendComment(messageText, mAlbum.getId());
    }

    private void showMessage(@StringRes int string) {
        Toast.makeText(getActivity(), string, Toast.LENGTH_LONG).show();
    }
}
