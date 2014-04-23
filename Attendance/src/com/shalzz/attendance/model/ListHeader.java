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

/**
 * Modal class for the ExpandableListView header
 * @author shalzz
 *
 */
public class ListHeader {

	// private variables;
	private String name;
	private String fatherName;
	private String course;
	private String section;
	private int SAPId;
	private String RollNo;

	// Empty constructor
	public ListHeader(){

	}
	
	public ListHeader( String name, String fatherName, String course, String section, int SAPId, String RollNo){
		this.name = name;
		this.fatherName = fatherName;
		this.course = course;
		this.section = section;
		this.SAPId = SAPId;
		this.RollNo = RollNo;
	}
	
	public String getName() {
		return name;
	}
	
	public String getFatherName() {
		return fatherName;
	}
	
	public String getCourse() {
		return course;
	}
	
	public String getSection() {
		return section;
	}
	
	public int getSAPId() {
		return SAPId;
	}
	
	public String getRollNo() {
		return RollNo;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setFatherName( String fatherName) {
		this.fatherName = fatherName;
	}
	
	public void setCourse(String course) {
		this.course = course;
	}
	
	public void setSection(String section) {
		this.section = section;
	}
	
	public void setSAPId(int SAPId) {
		this.SAPId = SAPId;
	}
	
	public void setRollNo(String RollNo) {
		this.RollNo = RollNo;
	}
}
