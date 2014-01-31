package com.shalzz.attendance;

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
