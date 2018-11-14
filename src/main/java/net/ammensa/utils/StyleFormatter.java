package net.ammensa.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class StyleFormatter {

    public static String formatDate(long timeInMillisec, int dateFormat) {
        Calendar calendar = createCalendar(timeInMillisec);
        DateFormat df = DateFormat.getDateInstance(dateFormat, Locale.ITALY);
        df.setCalendar(calendar);
        return df.format(calendar.getTime());
    }

    public static String formatDate(long timeInMillisec, String datePattern, Locale locale) {
        Calendar calendar = createCalendar(timeInMillisec);
        SimpleDateFormat sdf = new SimpleDateFormat(datePattern, locale);
        sdf.setTimeZone(TimeZone.getTimeZone("Europe/Rome"));
        return sdf.format(calendar.getTimeInMillis());
    }

    public static String formatDate(long timeInMillisec, String datePattern) {
        Calendar calendar = createCalendar(timeInMillisec);
        SimpleDateFormat sdf = new SimpleDateFormat(datePattern, Locale.ITALY);
        sdf.setTimeZone(TimeZone.getTimeZone("Europe/Rome"));
        return sdf.format(calendar.getTimeInMillis());
    }

    private static Calendar createCalendar(long timeInMillisec) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Rome"), Locale.ITALY);
        calendar.setTimeInMillis(timeInMillisec);
        return calendar;
    }
}
