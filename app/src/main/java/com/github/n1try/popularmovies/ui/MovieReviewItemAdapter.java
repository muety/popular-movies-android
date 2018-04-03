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
import com.github.n1try.popularmovies.utils.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MovieReviewItemAdapter extends ArrayAdapter<MovieReview> {
    private int maxReviewContentLength;
    private Map<Integer, Boolean> toggled;

    public MovieReviewItemAdapter(@NonNull Context context, @NonNull List<MovieReview> reviews) {
        super(context, 0, reviews);
        maxReviewContentLength = getContext().getResources().getInteger(R.integer.details_max_review_content_length);
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull final ViewGroup parent) {
        boolean isNew = convertView == null;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_review, parent, false);
        }

        final MovieReview review = getItem(position);

        final View finalConvertView = convertView;
        final TextView reviewAuthorTv = convertView.findViewById(R.id.details_review_author_tv);
        final TextView reviewContentTv = convertView.findViewById(R.id.details_review_content_tv);

        reviewAuthorTv.setText(review.getAuthor());
        reviewContentTv.setText(Utils.ellipsizeText(review.getContent(), maxReviewContentLength));
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (review.getContent().length() < maxReviewContentLength) return;
                if (reviewContentTv.getText().length() <= maxReviewContentLength) {
                    reviewContentTv.setText(review.getContent());
                } else if (reviewContentTv.getText().length() > maxReviewContentLength) {
                    reviewContentTv.setText(Utils.ellipsizeText(review.getContent(), maxReviewContentLength));
                }

                Map convertViews = new HashMap();
                convertViews.put(position, finalConvertView);
            }
        });

        return convertView;
    }
}
