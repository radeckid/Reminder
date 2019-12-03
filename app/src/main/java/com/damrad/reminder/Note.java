package com.damrad.reminder;

import android.icu.util.Calendar;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class Note implements Serializable {

    private static final long serialVersionUID = 1L;

    private final int uniqueID;

    private String title;

    private String body;

    private String month;

    private String day;

    private String time;

    private int dayOfMonthNr;

    private int monthNr;

    private int yearNr;

    private int minuteNr;

    private int hourOfDayNr;

    Note(String title, String body, int uniqueID, Calendar calendar) {
        this.title = title;
        this.body = body;
        this.time = String.format(Locale.getDefault(), "%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
        this.uniqueID = uniqueID;

        String[] dateArr = new SimpleDateFormat("MMM d", Locale.getDefault()).format(calendar.getTime()).split(" ");
        this.month = dateArr[0].substring(0, 1).toUpperCase() + dateArr[0].substring(1);
        this.day = dateArr[1];

        dayOfMonthNr = calendar.get(Calendar.DAY_OF_MONTH);
        monthNr = calendar.get(Calendar.MONTH);
        yearNr = calendar.get(Calendar.YEAR);
        minuteNr = calendar.get(Calendar.MINUTE);
        hourOfDayNr = calendar.get(Calendar.HOUR_OF_DAY);
    }

    Calendar getCalendar() {
        Calendar calendar = Calendar.getInstance(Locale.getDefault());

        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonthNr);
        calendar.set(Calendar.MONTH, monthNr);
        calendar.set(Calendar.YEAR, yearNr);

        calendar.set(Calendar.MINUTE, minuteNr);
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDayNr);

        return calendar;
    }

    String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    int getUniqueID() {
        return uniqueID;
    }

}
