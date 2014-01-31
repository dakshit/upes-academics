package com.shalzz.attendance;

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
