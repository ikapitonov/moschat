package ru.chat.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class Time {
    public static final DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss");

    public static String getFormatDate(LocalDateTime localDateTime) {
        return localDateTime.format(format);
    }

    public static String getNowDate() {
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        return dateFormat.format(date);
    }
}
