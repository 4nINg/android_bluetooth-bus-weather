package com.example.j.total2.Common;

import android.location.Location;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Common {
    public  static final  String APP_ID = ""; // api key request
    public  static Location current_location = null;


    public static String converUnixToHour(long dt) {
        Date date = new Date(dt*1000L);
        SimpleDateFormat sdf =new SimpleDateFormat("HH:mm");
        String formatted = sdf.format(date);
        return formatted;
    }

    public static String convertUnixToDate(long dt) {
        Date date = new Date(dt*1000L);
        SimpleDateFormat sdf =new SimpleDateFormat("HH:mm dd EEE MM yyyy");
        String formatted = sdf.format(date);
        return formatted;
    }
}
