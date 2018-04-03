package com.github.n1try.popularmovies.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.github.n1try.popularmovies.R;
import com.github.n1try.popularmovies.model.MovieReview;

import java.util.List;

public class MovieReviewItemAdapter extends ArrayAdapter<MovieReview> {
    public MovieReviewItemAdapter(@NonNull Context context, @NonNull List<MovieReview> reviews) {
        super(context, 0, reviews);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_review, parent, false);
        }

        final MovieReview review = getItem(position);

        final View container = convertView.findViewById(R.id.item_container);
        final TextView reviewAuthorTv = convertView.findViewById(R.id.details_review_author_tv);
        final TextView reviewContentTv = convertView.findViewById(R.id.details_review_content_tv);

        container.setEnabled(false);
        reviewAuthorTv.setText(review.getAuthor());
        reviewContentTv.setText(review.getContent());

        return convertView;
    }
}
