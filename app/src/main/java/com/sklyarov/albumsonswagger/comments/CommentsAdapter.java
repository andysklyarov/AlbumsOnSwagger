package com.sklyarov.albumsonswagger.comments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sklyarov.albumsonswagger.R;
import com.sklyarov.albumsonswagger.model.Comment;

import java.util.ArrayList;
import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentHolder> {

    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    @NonNull
    private final List<Comment> messages = new ArrayList<>();
    private String currentUser;

    public CommentsAdapter(String currentUser) {
        this.currentUser = currentUser;
    }

    @Override
    public int getItemViewType(int position) {
        Comment message = messages.get(position);
        String sender = message.getAuthor();

        if (currentUser.equals(sender)) {
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    @NonNull
    @Override
    public CommentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = null;

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_sent, parent, false);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_received, parent, false);
        }

        return new CommentHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentHolder holder, int position) {
        Comment message = messages.get(position);
        holder.bind(message);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void addData(List<Comment> data, boolean isRefreshed) {
        if (isRefreshed) {
            messages.clear();
        }

        data.sort((comment1, comment2) -> comment2.getId() - comment1.getId());

        messages.addAll(data);
        notifyDataSetChanged();
    }
}
