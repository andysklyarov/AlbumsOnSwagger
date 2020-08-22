package com.sklyarov.okhttptest.comments;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.sklyarov.okhttptest.R;
import com.sklyarov.okhttptest.model.CommentToReceive;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class CommentHolder extends RecyclerView.ViewHolder {

    private static final String INPUT_DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss+00:00";
    private static final String OUTPUT_DATE_TIME_PATTERN = "dd MMM yyyy";

    private TextView messageText;
    private TextView timeText;
    private TextView nameText;

    CommentHolder(View itemView) {
        super(itemView);

        messageText = itemView.findViewById(R.id.text_message_body);
        timeText = itemView.findViewById(R.id.text_message_time);
        nameText = itemView.findViewById(R.id.text_message_name);
    }

    void bind(CommentToReceive message) {

        String commentTimeString = message.getTimestamp();
        LocalDateTime commentDateTime = LocalDateTime.parse(commentTimeString, DateTimeFormatter.ofPattern(INPUT_DATE_TIME_PATTERN));

        LocalDateTime now = LocalDateTime.now(ZoneId.systemDefault());
        if (commentDateTime.toLocalDate().isEqual(now.toLocalDate())) {
            String time = OffsetDateTime.parse(commentTimeString).toLocalTime().toString();
            timeText.setText(time);
        } else {
            String date = commentDateTime.format(DateTimeFormatter.ofPattern(OUTPUT_DATE_TIME_PATTERN));
            timeText.setText(date);
        }

        messageText.setText(message.getText());
        nameText.setText(message.getAuthor());
    }
}
