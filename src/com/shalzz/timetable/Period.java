package com.shalzz.timetable;

public class Period {

	// private variables;
	private int id;
	private String name;
	private String teacher;
	private String room;
	private int start;
	private int end;
	private String day;


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
	
	public String getDay() {
		return day;
	}
	
	public void setId(int id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setRoom(String room) {
		this.room = room;
	}

	public void setTeacher(String teacher) {
		this.teacher = teacher;
	}

	public void setTime(int start, int end) {
		this.start = start;
		this.end = end;
	}
	
	public void setDay(String day ) {
		this.day = day;
	}
}
