/*
 * Copyright (C) 2018 Ferdinand Mütsch
 */

package com.github.n1try.popularmovies.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {
    public static Date parseDate(String dateString, String formatString) {
        SimpleDateFormat parser = new SimpleDateFormat(formatString);
        try {
            return parser.parse(dateString);
        } catch (ParseException e) {
            return null;
        }
    }

    public static String formatDate(Date date, String formatString) {
        SimpleDateFormat parser = new SimpleDateFormat(formatString);
        return parser.format(date);
    }
}
