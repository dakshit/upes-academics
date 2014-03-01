package com.shalzz.timetable;

import java.util.ArrayList;
import java.util.List;

public class Day {

	List<Period> PeriodsList = new ArrayList<Period>();
	
	Day() {
	}
	
	public void addPeriod(Period newPeriod){
		PeriodsList.add(newPeriod);
	}
	
	public List<Period> getAllPeriods(){
		return PeriodsList;
	}
	
	public Period getPeriod(int x){
		return PeriodsList.get(x);
	}
}
