/*  
 *    Copyright (C) 2013 - 2014 Shaleen Jain <shaleen.jain95@gmail.com>
 *
 *	  This file is part of UPES Academics.
 *
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 **/    

package com.shalzz.attendance;

import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.shalzz.attendance.model.Day;
import com.shalzz.attendance.model.ListFooter;
import com.shalzz.attendance.model.ListHeader;
import com.shalzz.attendance.model.Period;
import com.shalzz.attendance.model.Subject;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class DataAssembler {

	private static String mTag = "Data Assembler";
	
	/**
	 * Extracts Attendance details from the HTML code.
	 * @param response HTML page string
	 */
	public static void  parseAttendance(String response,Context mContext) {

		ArrayList<Float> claHeld = new ArrayList<Float>();
		ArrayList<Float> claAttended = new ArrayList<Float>();
		ArrayList<String> abDates = new ArrayList<String>();
		ArrayList<String> projPer = new ArrayList<String>();
		ArrayList<String> subjectName = new ArrayList<String>();
		ArrayList<Float> percentage = new ArrayList<Float>();

		Log.i(mTag, "Parsing response...");
		Document doc = Jsoup.parse(response);

		Elements tddata = doc.select("td");

		if(doc.getElementsByTag("title").size()==0 || doc.getElementsByTag("title").get(0).text().equals("UPES - Home"))
		{
			// TODO: relogin
			String msg ="It seems your session has expired.\nPlease Login again.";
			if(!mContext.getClass().getName().equals("com.shalzz.attendance.wrapper.MyVolley"))
				Crouton.makeText((Activity) mContext, msg, Style.ALERT).show();
			Log.e(mTag,"Login Session Expired");
		}
		else if (tddata != null && tddata.size() > 0)
		{
			int i=0;
			ListHeader header = new ListHeader();
			for(Element element : tddata)
			{
				if(i==5)					
					header.setName(element.text());	
				else if(i==8)					
					header.setFatherName(element.text());
				else if(i==11)
					header.setCourse(element.text());
				else if(i==14)					
					header.setSection(element.text());
				else if(i==17)					
					header.setRollNo(element.text());
				else if(i==20)					
					header.setSAPId(Integer.parseInt(element.text()));
				else if(i>29)
				{
					// for subjects
					if ((i - 30) % 7 == 0) {
						subjectName.add(element.text());
					}
					// for Classes Held
					else if ((i - 31) % 7 == 0) {
						claHeld.add(Float.parseFloat(element.text()));
					}
					// for Classes attended
					else if ((i - 32) % 7 == 0) {
						claAttended.add(Float.parseFloat(element.text()));
					}
					// for Dates Absent
					else if ((i - 33) % 7 == 0) {
						abDates.add(element.text());
					}
					// for attendance percentage
					else if ((i - 34) % 7 == 0) {
						percentage.add(Float.parseFloat(element.text()));
					}
					// for projected percentage
					else if ((i - 35) % 7 == 0) {
						projPer.add(element.text());
					}
				}
				++i;
			}

			Elements total = doc.select("th");
			ListFooter footer = new ListFooter();
			footer.setAttended(Float.parseFloat(total.get(10).text()));
			footer.setHeld(Float.parseFloat(total.get(9).text()));
			footer.setPercentage(Float.parseFloat(total.get(12).text()));
			DatabaseHandler db = new DatabaseHandler(mContext);
			db.addOrUpdateListFooter(footer);
			db.addOrUpdateListHeader(header);

			Log.i(mTag, "Response parsing complete.");

			for(i=0;i<claHeld.size();i++)
			{
				Subject subject = new Subject(i+1, 
						subjectName.get(i),
						claHeld.get(i),
						claAttended.get(i),
						abDates.get(i),
						percentage.get(i),
						projPer.get(i));
				db.addOrUpdateSubject(subject);
			}
			db.close();
		}
	}

	public static void parseTimeTable(String response,Context mContext) {

		Document doc = Jsoup.parse(response);
		Elements thdata = doc.select("th");

		ArrayList<String> time = new ArrayList<String>();
		ArrayList<String> mon = new ArrayList<String>();
		ArrayList<String> tue = new ArrayList<String>();
		ArrayList<String> wed = new ArrayList<String>();
		ArrayList<String> thur = new ArrayList<String>();
		ArrayList<String> fri = new ArrayList<String>();
		ArrayList<String> sat = new ArrayList<String>();
		String dayNames[] = {"mon","tue","wed","thur","fri","sat"};
		ArrayList<ArrayList<String>> days = new ArrayList<ArrayList<String>>();
		//List<ArrayList<String>> days = Arrays.asList(mon,tue,wed,thur,fri,sat);
		days.add(mon);
		days.add(tue);
		days.add(wed);
		days.add(thur);
		days.add(fri);
		days.add(sat);

		if(doc.getElementsByTag("title").size()==0 || doc.getElementsByTag("title").get(0).text().equals("UPES - Home"))
		{
			String msg ="It seems your session has expired.\nPlease Login again.";
			if(!mContext.getClass().getName().equals("com.shalzz.attendance.wrapper.MyVolley"))
				Crouton.makeText((Activity) mContext, msg, Style.ALERT).show();
			Log.e(mTag,"Login Session Expired");
		}
		else if (thdata != null && thdata.size() > 0)
		{
			int i=0;
			for(Element element : thdata)
			{
				if(i>8)
				{
					// get time
					if ((i - 9) % 7 == 0) {
						time.add(element.text());
					}
					// periods on mon
					if ((i - 10) % 7 == 0) {
						mon.add(element.text());
					}
					// periods on tue
					if ((i - 11) % 7 == 0) {
						tue.add(element.text());
					}
					// periods on wed
					if ((i - 12) % 7 == 0) {
						wed.add(element.text());
					}
					// periods on thur
					if ((i - 13) % 7 == 0) {
						thur.add(element.text());
					}
					// periods on fri
					if ((i - 14) % 7 == 0) {
						fri.add(element.text());
					}
					// periods on sat
					if ((i - 15) % 7 == 0) {
						sat.add(element.text());
					}
				}
				++i;
			}
			for(int j=0;j<days.size();j++)
			{
				Day day = new Day();
				for(i=0;i<time.size();i++)
				{
					Period period = new Period();
					period.setDay(dayNames[j]);
					period.setTime(time.get(i));
					period.setName(days.get(j).get(i));
					day.addPeriod(period);
				}
				DatabaseHandler db = new DatabaseHandler(mContext);
				db.addOrUpdateDay(day);
			}
		}
	}
}
