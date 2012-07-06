package com.everyuse.android.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.text.format.DateFormat;
import android.view.View;
import android.widget.TextView;

public class OtherHelper {
	private final static String DATE_FORMAT_RAILS = "yyyy-MM-dd'T'HH:mm:ss'Z'";
	private final static String DATE_FORMAT_SIMPLE = "E, dd/MMM/yyyy HH:mm";
	
	public static boolean isSeparatorView(View view) {
		return (view instanceof TextView);
	}

	public static Date parseDate(String date_string) {
		SimpleDateFormat formatter;
		Date date;

		formatter = new SimpleDateFormat(DATE_FORMAT_RAILS);
		try {
			date = (Date) formatter.parse(date_string);
		} catch (ParseException e) {
			e.printStackTrace();
			date = new Date(0);
		}
		
		return date;
		// 2012-05-11T16:02:08Z
	}
	
	public static String encodeDate(Date date) {
		SimpleDateFormat formatter;
		formatter = new SimpleDateFormat(DATE_FORMAT_RAILS);
		
		return formatter.format(date);
	}
	
	public static String encodeDateSimple(Date date) {
		SimpleDateFormat formatter;
		formatter = new SimpleDateFormat(DATE_FORMAT_SIMPLE);
		
		return formatter.format(date);
	}
}
