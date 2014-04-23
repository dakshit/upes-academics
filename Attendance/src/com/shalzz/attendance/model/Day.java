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

import java.util.ArrayList;
import java.util.List;

public class Day {

	List<Period> PeriodsList = new ArrayList<Period>();
	
	public Day() {
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
