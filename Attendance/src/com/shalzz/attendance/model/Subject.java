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
 * Modal class for subjects.
 * @author shalzz
 *
 */
public class Subject {

	// private variables;
	private int id;
	private String name;
	private Float classesHeld;
	private Float classesAttended;
	private String absentDates;
	private Float percentage;
	private String projectedPercentage;

	// Empty constructor
	public Subject(){

	}

	/**
	 * Constructor with all values.
	 * @param id
	 * @param name
	 * @param classesHeld
	 * @param classesAttended
	 * @param absentDates
	 * @param percentage
	 * @param projectedPercentage
	 */
	public Subject(int id, String name, Float classesHeld, Float classesAttended, String absentDates, Float percentage, String projectedPercentage){
		this.id = id;
		this.name = name;
		this.classesHeld = classesHeld;
		this.classesAttended = classesAttended;
		this.absentDates = absentDates;
		this.percentage = percentage;
		this.projectedPercentage = projectedPercentage;
	}

	// constructor
	public Subject(String name) {
		this.name = name;
	}

	// getting ID
	public int getID(){
		return this.id;
	}

	// setting id
	public void setID(int id){
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Float getClassesHeld() {
		return this.classesHeld;
	}

	public void setClassesHeld(Float classesHeld) {
		this.classesHeld = classesHeld;
	}

	public Float getClassesAttended() {
		return this.classesAttended;
	}

	public void setClassesAttended(Float classesAttended) {
		this.classesAttended = classesAttended;
	}

	public String getAbsentDates() {
		return this.absentDates;
	}

	public void setAbsentDates(String absentDates) {
		this.absentDates = absentDates;
	}

	public Float getPercentage() {
		return this.percentage;
	}

	public void setPercentage(Float percentage) {
		this.percentage = percentage;
	}

	public String getProjectedPercentage() {
		return this.projectedPercentage;
	}

	public void setProjectedPercentage(String projectedPercentage) {
		this.projectedPercentage = projectedPercentage;
	}
}
