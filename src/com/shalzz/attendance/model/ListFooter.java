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
 * Modal class for the ExpandableListView footer.
 * @author shalzz
 *
 */
public class ListFooter {

	// private variables;
	private int serialNo;
	private Float classesHeld;
	private Float classesAttend;
	private Float percentage;

	// Empty constructor
	public ListFooter(){

	}

	public ListFooter(int serialNo, Float classesHeld, Float classesAttend, float percentage){
		this.serialNo = serialNo;
		this.classesHeld = classesHeld;
		this.classesAttend = classesAttend;
		this.percentage = percentage;
	}
	
	public int getSNo() {
		return serialNo;
	}
	
	public Float getHeld() {
		return classesHeld;
	}
	
	public Float getAttended() {
		return classesAttend;
	}
	
	public Float getPercentage() {
		return percentage;
	}
	
	public void setSNo(int serialNo) {
		this.serialNo = serialNo;
	}
	
	public void setHeld(Float classesHeld) {
		this.classesHeld = classesHeld;
	}
	
	public void setAttended(Float classesAttend) {
		this.classesAttend = classesAttend;
	}
	
	public void setPercentage(Float percentage) {
		this.percentage = percentage;
	}
}
