package com.sklyarov.albumsonswagger.comments;

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

import com.sklyarov.albumsonswagger.ApiUtilities;
import com.sklyarov.albumsonswagger.AuthFragment;
import com.sklyarov.albumsonswagger.R;
import com.sklyarov.albumsonswagger.db.DbUtils;
import com.sklyarov.albumsonswagger.db.MusicDao;
import com.sklyarov.albumsonswagger.model.Album;
import com.sklyarov.albumsonswagger.model.Comment;

import java.net.UnknownHostException;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;

import static com.sklyarov.albumsonswagger.model.ServerCodes.SERVER_INTERNAL_ERROR;

public class CommentsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    public static final String ALBUM_KEY = "ALBUM_KEY";

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recycler;
    private View errorView;
    private EditText editText;
    private Button enterButton;
    private TextView errorViewText;

    private Album mAlbum;
    private String currentUser;
    private boolean isFirstRun = true;

    private CommentsAdapter mCommentsAdapter;

    private MusicDao musicDao;
    private boolean isNoConnection = false;


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
        recycler = view.findViewById(R.id.recycler_messages);
        enterButton = view.findViewById(R.id.btn_chatbox_send);
        editText = view.findViewById(R.id.ed_chatbox);

        swipeRefreshLayout = view.findViewById(R.id.refresher_messages);
        swipeRefreshLayout.setOnRefreshListener(this);

        errorView = view.findViewById(R.id.error_view);
        errorViewText = errorView.findViewById(R.id.tv_error_text);

        enterButton.setOnClickListener(buttonView -> sendCommentsWithCheck());

        editText.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (keyEvent != null && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                editText.clearFocus();
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
        recycler.setLayoutManager(layoutManager);

        mCommentsAdapter = new CommentsAdapter(currentUser);
        recycler.setAdapter(mCommentsAdapter);

        musicDao = DbUtils.getDatabase().getMusicDao();

        onRefresh();
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.post(() -> getComments(mAlbum.getId()));
    }

    @SuppressLint("CheckResult")
    private void getComments(int albumId) {

        ApiUtilities.getApiService()
                .getAlbumComments(albumId)
                .subscribeOn(Schedulers.io())
                .doOnSuccess(comments -> {
                    musicDao.insertComments(comments);
                    isNoConnection = false;
                })
                .onErrorReturn(throwable -> {
                    List<Comment> comments = null;

                    if (ApiUtilities.NETWORK_EXCEPTIONS.contains(throwable.getClass())) {
                        comments = musicDao.getCommentsByAlbumId(albumId);

                        if (comments.size() == 0)
                            comments = null;
                    }

                    isFirstRun = true;
                    isNoConnection = true;

                    return comments;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> swipeRefreshLayout.setRefreshing(true))
                .doFinally(() -> {
                    swipeRefreshLayout.setRefreshing(false);
                    isFirstRun = false;
                })
                .subscribe(comments -> {
                            if (comments.size() == 0) {
                                recycler.setVisibility(View.GONE);
                                errorViewText.setText(R.string.comments_no_comments);
                                errorView.setVisibility(View.VISIBLE);
                            } else {
                                recycler.setVisibility(View.VISIBLE);
                                errorView.setVisibility(View.GONE);

                                if (comments.size() == mCommentsAdapter.getItemCount()) {
                                    if (!isFirstRun) showMessage(R.string.comments_no_new);
                                } else {
                                    mCommentsAdapter.addData(comments, true);
                                    if (!isFirstRun) showMessage(R.string.comments_updated);
                                }

                                if (isNoConnection) showMessage(R.string.host_error_no_connection);
                            }
                        }
                        , throwable -> {
                            recycler.setVisibility(View.GONE);
                            errorViewText.setText(R.string.comments_error);
                            errorView.setVisibility(View.VISIBLE);
                        });

    }

    @SuppressLint("CheckResult")
    private void sendComment(String text, int albumId) {

        ApiUtilities.getApiService()
                .sendComment(new Comment(text, albumId))
                .subscribeOn(Schedulers.io())
                .doFinally(this::onRefresh)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                            showMessage(R.string.registration_success);
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
                                showMessage(R.string.host_error_no_connection);
                            } else {
                                showMessage(R.string.error_text);
                            }
                        });
    }

    private void sendCommentsWithCheck() {

        String messageText = editText.getText().toString();

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
