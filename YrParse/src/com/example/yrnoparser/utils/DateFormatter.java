package com.example.yrnoparser.utils;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

public class DateFormatter {

    public static String get24HourString(DateTime dateTime) {
        DateTimeFormatter formatter =
                new DateTimeFormatterBuilder().appendPattern("HH:mm").toFormatter();
        return formatter.print(dateTime);
    }

    public static String get24HourDateString(DateTime dateTime) {
        DateTimeFormatter formatter =
                new DateTimeFormatterBuilder().appendPattern("dd/MM/YY HH:mm").toFormatter();
        return formatter.print(dateTime);
    }
}
