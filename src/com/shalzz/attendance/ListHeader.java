package com.shalzz.attendance;

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
