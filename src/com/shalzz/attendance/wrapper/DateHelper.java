package com.shalzz.attendance.wrapper;

import java.util.Calendar;
import java.util.Date;

public class DateHelper {
	
	private static String weekdays[] = {"mon","tue","wed","thur","fri","sat"};
	
	public static String getWeekday(Date date) {
		Calendar today = Calendar.getInstance();
		today.setTime(date);
		int weekday = today.get(Calendar.DAY_OF_WEEK);	
		if(weekday == Calendar.SUNDAY)
			weekday = Calendar.MONDAY;
		return weekdays[weekday-2]; // fix for Monday as first day of week
	}
	
	public static Date getToDay() {
		Calendar c = Calendar.getInstance();
		@SuppressWarnings("deprecation")
		Date date = new Date(c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH));
		return date;
	}
	
	public static Date addDays(Date date, int numberOfDays) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DATE, numberOfDays);
		return c.getTime();
	}
}
