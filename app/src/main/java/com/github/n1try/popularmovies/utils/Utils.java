package com.github.n1try.popularmovies.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {
    public static Date parseDate(String dateString) {
        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return parser.parse(dateString);
        } catch (ParseException e) {
            return null;
        }
    }

    public static String dumpDate(Date date) {
        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd");
        return parser.format(date);
    }
}
