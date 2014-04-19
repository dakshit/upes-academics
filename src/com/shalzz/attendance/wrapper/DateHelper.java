package com.shalzz.attendance.wrapper;

import java.util.Calendar;

public class DateHelper {
	
	private static String weekdays[] = {"mon","tue","wed","thur","fri","sat"};
	
	public static int getWeekday() {
		Calendar today = Calendar.getInstance();
		int weekday = today.get(Calendar.DAY_OF_WEEK);	
		if(weekday == Calendar.SUNDAY)
			weekday = Calendar.MONDAY;
		return weekday;
	}
	
	public static String getWeekdayName(int i) {
		i = i%6;
		return weekdays[i];
	}
}
