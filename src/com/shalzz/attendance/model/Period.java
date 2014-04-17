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

package com.shalzz.attendance.model;

public class Period {

	// private variables;
	private int id;
	private String name;
	private String teacher;
	private String room;
	private String time;
	private int start;
	private int end;
	private String day;

	public Period() {

	}

	Period (int _id, String id, String name, String room, String teacher, int start, int end, boolean isBreak, String day) {
		this.name = name;
		this.room = room;
		this.teacher = teacher;
		this.start = start;
		this.end = end;
		this.day = day;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getRoom() {
		return room;
	}

	public String getTeacher() {
		return teacher;
	}

	public int getStartTime() {
		return start;
	}

	public int getEndTime() {
		return end;
	}

	public String getTime() {
		return time;
	}

	public String getDay() {
		return day;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setName(String name) {
		if(name.equals("***"))
			name = "";
		this.name = name;
	}

	public void setRoom(String room) {
		this.room = room;
	}

	public void setTeacher(String teacher) {
		this.teacher = teacher;
	}

	public void setTime(String time) {
		this.time = "`" + time + "`";
	}

	public void setTime(int start, int end) {
		this.start = start;
		this.end = end;
	}

	public void setDay(String day ) {
		this.day = day;
	}
}
