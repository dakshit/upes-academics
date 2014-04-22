package com.shalzz.attendance.wrapper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateHelper {
	
	private static String weekdays[] = {"sun","mon","tue","wed","thur","fri","sat"};
	private static DateFormat technicalDateFormat = new SimpleDateFormat("dd-MM-yyyy",Locale.US);
	
	public static String getWeekday(Date date) {
		Calendar today = Calendar.getInstance();
		today.setTime(date);
		int weekday = today.get(Calendar.DAY_OF_WEEK);
		return weekdays[weekday-1];
	}
	
	public static Date getToDay() {
		return new Date();
	}
	
	public static Date addDays(Date date, int numberOfDays) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DATE, numberOfDays);
		return c.getTime();
	}

	public static String formatToTechnicalFormat(Date date) {
		return technicalDateFormat.format(date);
	}
	
	public static String getNetworkRequestDate(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		if(c.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY)
			date = addDays(date,1);
		return technicalDateFormat.format(date);
	}
}
