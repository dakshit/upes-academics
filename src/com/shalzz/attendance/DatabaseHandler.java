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

package com.shalzz.attendance;

import java.util.ArrayList;
import java.util.List;

import com.shalzz.attendance.model.Day;
import com.shalzz.attendance.model.ListFooter;
import com.shalzz.attendance.model.ListHeader;
import com.shalzz.attendance.model.Period;
import com.shalzz.attendance.model.Subject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Helper Class for SQLite database
 * @author shalzz
 *
 */
public class DatabaseHandler extends SQLiteOpenHelper {

	/**
	 * Database Version
	 */
	private static final int DATABASE_VERSION = 2;

	/**
	 * Database Name
	 */
	private static final String DATABASE_NAME = "attendanceManager";

	/**
	 *  Attendance table name
	 */
	private static final String TABLE_ATTENDENCE = "Attendance";

	/**
	 *  Attendance table name
	 */
	private static final String TABLE_TIMETABLE = "TimeTable";

	/**
	 * ListHeader table name
	 */
	private static final String TABLE_HEADER = "ListHeader";

	/**
	 * ListFooter table name
	 */
	private static final String TABLE_FOOTER = "ListFooter";

	/**
	 * Attendance Table Columns names
	 */
	private static final String KEY_ID = "id";
	private static final String KEY_NAME = "Subject_Name";
	private static final String KEY_CLASSES_HELD = "No_Classes_Held";
	private static final String KEY_CLASSES_ATTENDED = "No_Classes_Attended";
	private static final String KEY_DAYS_ABSENT = "Days_Absent";
	private static final String KEY_PERCENTAGE = "Percentage";
	private static final String KEY_PROJECTED_PERCENTAGE = "Projected_Percentage";

	/**
	 *  TimeTable Table Column names
	 */
	private static final String KEY_DAY = "Day";
	private static final String KEY_PERIOD_1 = "`8:00 - 8:30`";
	private static final String KEY_PERIOD_2 = "`8:30 - 9:00`";
	private static final String KEY_PERIOD_3 = "`9:00 - 9:30`";
	private static final String KEY_PERIOD_4 = "`9:30 - 10:00`";
	private static final String KEY_PERIOD_5 = "`10:00 - 10:30`";
	private static final String KEY_PERIOD_6 = "`10:30 - 11:00`";
	private static final String KEY_PERIOD_7 = "`11:00 - 11:30`";
	private static final String KEY_PERIOD_8 = "`11:30 - 12:00`";
	private static final String KEY_PERIOD_9 = "`12:00 - 12:30`";
	private static final String KEY_PERIOD_10 = "`12:30 - 13:00`";
	private static final String KEY_PERIOD_11 = "`13:00 - 13:30`";
	private static final String KEY_PERIOD_12 = "`13:30 - 14:00`";
	private static final String KEY_PERIOD_13 = "`14:00 - 14:30`";
	private static final String KEY_PERIOD_14 = "`14:30 - 15:00`";
	private static final String KEY_PERIOD_15 = "`15:00 - 15:30`";
	private static final String KEY_PERIOD_16 = "`15:30 - 16:00`";
	private static final String KEY_PERIOD_17 = "`16:00 - 16:30`";
	private static final String KEY_PERIOD_18 = "`16:30 - 17:00`";
	private static final String KEY_PERIOD_19 = "`17:00 - 17:30`";
	private static final String KEY_PERIOD_20 = "`17:30 - 18:00`";
	private static final String KEY_PERIOD_21 = "`18:00 - 18:30`";

	/**
	 * ListHeader Table Columns names
	 */
	private static final String KEY_STU_NAME = "Student_Name";
	private static final String KEY_FATHER_NAME = "Fathers_Name";
	private static final String KEY_COURSE = "Course_Name";
	private static final String KEY_SECTION = "Section";
	private static final String KEY_ROLLNO = "Rollno";
	private static final String KEY_SAPID = "SAPId";

	/**
	 * ListFooter Table Column names
	 */
	private static final String KEY_SNO = "SNo";
	private static final String KEY_TOTAL_HELD = "Classes_held";
	private static final String KEY_TOTAL_ATTEND = "Classes_attend";
	private static final String KEY_TOTAL_PERCANTAGE = "Percentage";

	/**
	 * Attendance CREATE TABLE SQL query.
	 */
	private static final String CREATE_ATTENDENCE_TABLE = "CREATE TABLE " + TABLE_ATTENDENCE + " ( "
			+ KEY_ID + " INTEGER PRIMARY KEY, " + KEY_NAME + " TEXT, " 
			+ KEY_CLASSES_HELD + " REAL, " + KEY_CLASSES_ATTENDED + " REAL, " 
			+ KEY_DAYS_ABSENT + " TEXT, " + KEY_PERCENTAGE + " REAL, " 
			+ KEY_PROJECTED_PERCENTAGE + "  TEXT " + ");";

	private static final String CREATE_TIME_TABLE = "CREATE TABLE " + TABLE_TIMETABLE + " ( "
			+ KEY_DAY + " TEXT PRIMARY KEY, " + KEY_PERIOD_1 + " TEXT, " + KEY_PERIOD_2 + " TEXT, "
			+ KEY_PERIOD_3 + " TEXT, " + KEY_PERIOD_4 + " TEXT, " + KEY_PERIOD_5 + " TEXT, " 
			+ KEY_PERIOD_6 + " TEXT, " + KEY_PERIOD_7 + " TEXT, " + KEY_PERIOD_8 + " TEXT, "
			+ KEY_PERIOD_9 + " TEXT, " + KEY_PERIOD_10 + " TEXT, " + KEY_PERIOD_11 + " TEXT, "
			+ KEY_PERIOD_12 + " TEXT, " + KEY_PERIOD_13 + " TEXT, " + KEY_PERIOD_14 + " TEXT, "
			+ KEY_PERIOD_15 + " TEXT, " + KEY_PERIOD_16 + " TEXT, " + KEY_PERIOD_17 + " TEXT, "
			+ KEY_PERIOD_18 + " TEXT, " + KEY_PERIOD_19 + " TEXT, " + KEY_PERIOD_20 + " TEXT, "
			+ KEY_PERIOD_21 + " TEXT " + ");";

	/**
	 * ListHeader CREATE TABLE SQL query.
	 */
	private static final String CREATE_HEADER_TABLE = "CREATE TABLE " + TABLE_HEADER + " ( "
			+ KEY_STU_NAME + " TEXT, " + KEY_FATHER_NAME + " TEXT, " 
			+ KEY_COURSE + " TEXT, " + KEY_SECTION + " TEXT, " 
			+ KEY_ROLLNO + " TEXT, " + KEY_SAPID + "  INTEGER PRIMARY KEY " + ");";

	/**
	 * ListFooter CREATE TABLE SQL query.
	 */
	private static final String CREATE_FOOTER_TABLE = "CREATE TABLE " + TABLE_FOOTER + " ( "
			+ KEY_SNO + " INTEGER PRIMARY KEY, " + KEY_TOTAL_HELD + " REAL, " 
			+ KEY_TOTAL_ATTEND + " REAL, " + KEY_TOTAL_PERCANTAGE + "  REAL " + ");";

	/**
	 * Constructor.
	 */
	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	/**
	 * Create Table.
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_ATTENDENCE_TABLE);
		db.execSQL(CREATE_HEADER_TABLE);
		db.execSQL(CREATE_FOOTER_TABLE);
		db.execSQL(CREATE_TIME_TABLE);
	}

	/**
	 * Drop the table if it exist and create a new table.
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_ATTENDENCE);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_HEADER);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_FOOTER);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_TIMETABLE);

		// Create tables again
		onCreate(db);
	}

	/**
	 * Add new Subject
	 * @param subject
	 */
	public void addSubject(Subject subject) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_ID, subject.getID());
		values.put(KEY_NAME, subject.getName());
		values.put(KEY_CLASSES_HELD, subject.getClassesHeld());
		values.put(KEY_CLASSES_ATTENDED, subject.getClassesAttended());
		values.put(KEY_DAYS_ABSENT, subject.getAbsentDates());
		values.put(KEY_PERCENTAGE, subject.getPercentage());
		values.put(KEY_PROJECTED_PERCENTAGE, subject.getProjectedPercentage());

		// Inserting Row
		db.insert(TABLE_ATTENDENCE, null, values);
		db.close(); // Closing database connection
	}

	/**
	 * Adds a new Subject if it doesn't exists otherwise updates it.
	 * @param subject
	 */
	public void addOrUpdateSubject(Subject subject) {
		SQLiteDatabase db = this.getWritableDatabase();

		Cursor cursor = db.query(TABLE_ATTENDENCE, new String[] { KEY_ID}, KEY_ID + "=?",
				new String[] { String.valueOf(subject.getID()) }, null, null, null, null);
		if (cursor.getCount() == 0)
		{
			addSubject(subject);
		}
		else
		{
			updateSubject(subject);
		}
		db.close(); // Closing database connection
	}

	/**
	 * Get a single Subject
	 * @param id
	 * @return subject
	 */
	public Subject getSubject(int id) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_ATTENDENCE, new String[] { KEY_ID,KEY_NAME, KEY_CLASSES_HELD,
				KEY_CLASSES_ATTENDED, KEY_DAYS_ABSENT, KEY_PERCENTAGE, KEY_PROJECTED_PERCENTAGE }, KEY_ID + "=?",
				new String[] { String.valueOf(id) }, null, null, null, null);
		if (cursor != null)
			cursor.moveToFirst();

		Subject subject = new Subject();
		subject.setID(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)));
		subject.setName(cursor.getString(cursor.getColumnIndexOrThrow(KEY_NAME)));
		subject.setClassesHeld(cursor.getFloat(cursor.getColumnIndexOrThrow(KEY_CLASSES_HELD)));
		subject.setClassesAttended(cursor.getFloat(cursor.getColumnIndexOrThrow(KEY_CLASSES_ATTENDED)));
		subject.setAbsentDates(cursor.getString(cursor.getColumnIndexOrThrow(KEY_DAYS_ABSENT)));
		subject.setPercentage(cursor.getFloat(cursor.getColumnIndexOrThrow(KEY_PERCENTAGE)));
		subject.setProjectedPercentage(cursor.getString(cursor.getColumnIndexOrThrow(KEY_PROJECTED_PERCENTAGE)));


		db.close();
		cursor.close();

		return subject;
	}

	/**
	 * Get All Subjects
	 * @return subjectList
	 */
	public List<Subject> getAllSubjects() {
		List<Subject> subjectList = new ArrayList<Subject>();
		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_ATTENDENCE + ";";

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {

				Subject subject = new Subject();
				subject.setID(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)));
				subject.setName(cursor.getString(cursor.getColumnIndexOrThrow(KEY_NAME)));
				subject.setClassesHeld(cursor.getFloat(cursor.getColumnIndexOrThrow(KEY_CLASSES_HELD)));
				subject.setClassesAttended(cursor.getFloat(cursor.getColumnIndexOrThrow(KEY_CLASSES_ATTENDED)));
				subject.setAbsentDates(cursor.getString(cursor.getColumnIndexOrThrow(KEY_DAYS_ABSENT)));
				subject.setPercentage(cursor.getFloat(cursor.getColumnIndexOrThrow(KEY_PERCENTAGE)));
				subject.setProjectedPercentage(cursor.getString(cursor.getColumnIndexOrThrow(KEY_PROJECTED_PERCENTAGE)));

				// Adding contact to list
				subjectList.add(subject);
			} while (cursor.moveToNext());
		}

		db.close();
		cursor.close();

		return subjectList;
	}

	/**
	 * Get All Subjects ordered alphabetically.
	 * @return subjectList
	 */
	public List<Subject> getAllOrderedSubjects() {
		List<Subject> subjectList = new ArrayList<Subject>();
		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_ATTENDENCE + " ORDER BY " + KEY_NAME + ";";

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {

				Subject subject = new Subject();
				subject.setID(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)));
				subject.setName(cursor.getString(cursor.getColumnIndexOrThrow(KEY_NAME)));
				subject.setClassesHeld(cursor.getFloat(cursor.getColumnIndexOrThrow(KEY_CLASSES_HELD)));
				subject.setClassesAttended(cursor.getFloat(cursor.getColumnIndexOrThrow(KEY_CLASSES_ATTENDED)));
				subject.setAbsentDates(cursor.getString(cursor.getColumnIndexOrThrow(KEY_DAYS_ABSENT)));
				subject.setPercentage(cursor.getFloat(cursor.getColumnIndexOrThrow(KEY_PERCENTAGE)));
				subject.setProjectedPercentage(cursor.getString(cursor.getColumnIndexOrThrow(KEY_PROJECTED_PERCENTAGE)));

				// Adding contact to list
				subjectList.add(subject);
			} while (cursor.moveToNext());
		}

		db.close();
		cursor.close();

		return subjectList;
	}

	/**
	 * Get All Subjects matching the wildcard.
	 * @return subjectList
	 */
	public List<Subject> getAllSubjectsLike(String wildcard) {
		List<Subject> subjectList = new ArrayList<Subject>();
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_ATTENDENCE, new String[] { KEY_ID,KEY_NAME, KEY_CLASSES_HELD,
				KEY_CLASSES_ATTENDED, KEY_DAYS_ABSENT, KEY_PERCENTAGE, KEY_PROJECTED_PERCENTAGE }, KEY_NAME +" LIKE '%" + wildcard + "%'" ,
				null, null, null, KEY_NAME, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				Subject subject = new Subject();
				subject.setID(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)));
				subject.setName(cursor.getString(cursor.getColumnIndexOrThrow(KEY_NAME)));
				subject.setClassesHeld(cursor.getFloat(cursor.getColumnIndexOrThrow(KEY_CLASSES_HELD)));
				subject.setClassesAttended(cursor.getFloat(cursor.getColumnIndexOrThrow(KEY_CLASSES_ATTENDED)));
				subject.setAbsentDates(cursor.getString(cursor.getColumnIndexOrThrow(KEY_DAYS_ABSENT)));
				subject.setPercentage(cursor.getFloat(cursor.getColumnIndexOrThrow(KEY_PERCENTAGE)));
				subject.setProjectedPercentage(cursor.getString(cursor.getColumnIndexOrThrow(KEY_PROJECTED_PERCENTAGE)));

				// Adding contact to list
				subjectList.add(subject);
			} while (cursor.moveToNext());
		}

		db.close();
		cursor.close();

		return subjectList;
	}

	/**
	 * Get All Subjects Names
	 * @return subjectList
	 */
	public List<String> getAllSubjectNames() {
		List<String> subjectNamesList = new ArrayList<String>();
		// Select All Query
		String selectQuery = "SELECT " + KEY_NAME + " FROM " + TABLE_ATTENDENCE;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				subjectNamesList.add(cursor.getString(0));
			} while (cursor.moveToNext());
		}
		db.close();
		cursor.close();

		return subjectNamesList;
	}

	/**
	 * Update a single Subject
	 * @param subject
	 * @return
	 */
	public int updateSubject(Subject subject) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_NAME, subject.getName());
		values.put(KEY_CLASSES_HELD, subject.getClassesHeld());
		values.put(KEY_CLASSES_ATTENDED, subject.getClassesAttended());
		values.put(KEY_DAYS_ABSENT, subject.getAbsentDates());
		values.put(KEY_PERCENTAGE, subject.getPercentage());
		values.put(KEY_PROJECTED_PERCENTAGE, subject.getPercentage());

		// updating row
		int rows_affected = db.update(TABLE_ATTENDENCE, values, KEY_ID + " = ?",
				new String[] { String.valueOf(subject.getID()) });
		db.close();

		return rows_affected;
	}

	/**
	 * Deleting a single Subject
	 * @param contact
	 */
	public void deleteSubject(Subject subject) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_ATTENDENCE, KEY_ID + " = ?",
				new String[] { String.valueOf(subject.getID()) });
		db.close();
	}

	/**
	 * Check if the attendance data is in database.
	 * */
	public int getRowCount() {
		String countQuery = "SELECT  * FROM " + TABLE_ATTENDENCE;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		int rowCount = cursor.getCount();
		db.close();
		cursor.close();

		return rowCount;
	}
	
	public int getRowCountofTimeTable() {
		String countQuery = "SELECT  * FROM " + TABLE_TIMETABLE;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		int rowCount = cursor.getCount();
		db.close();
		cursor.close();

		return rowCount;
	}

	/**
	 * Delete all tables and create them again
	 * */
	public void resetTables(){
		SQLiteDatabase db = this.getWritableDatabase();
		// Delete All Rows
		db.delete(TABLE_ATTENDENCE, "1", null);
		db.delete(TABLE_HEADER, "1", null);
		db.delete(TABLE_FOOTER, "1", null);
		db.delete(TABLE_TIMETABLE, "1", null);
		db.close();
	}

	public void addListHeader(ListHeader header) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_STU_NAME, header.getName());
		values.put(KEY_FATHER_NAME, header.getFatherName());
		values.put(KEY_COURSE, header.getCourse());
		values.put(KEY_SECTION,header.getSection());
		values.put(KEY_SAPID, header.getSAPId());
		values.put(KEY_ROLLNO, header.getRollNo());

		// Inserting Row
		db.insert(TABLE_HEADER, null, values);
		db.close(); // Closing database connection
	}

	public void addOrUpdateListHeader(ListHeader header) {
		SQLiteDatabase db = this.getWritableDatabase();

		Cursor cursor = db.query(TABLE_HEADER, new String[] { KEY_SAPID}, KEY_SAPID + "=?",
				new String[] { String.valueOf(header.getSAPId()) }, null, null, null, null);
		if (cursor.getCount() == 0) {
			addListHeader(header);
		}
		else {
			updateListHeader(header);
		}
		db.close();
	}

	public ListHeader getListHeader() {
		SQLiteDatabase db = this.getReadableDatabase();

		String selectQuery = "SELECT  * FROM " + TABLE_HEADER + ";";
		Cursor cursor = db.rawQuery(selectQuery, null);

		if (cursor != null)
			cursor.moveToFirst();

		ListHeader header = new ListHeader();
		header.setName(cursor.getString(cursor.getColumnIndexOrThrow(KEY_STU_NAME)));
		header.setFatherName(cursor.getString(cursor.getColumnIndexOrThrow(KEY_FATHER_NAME)));
		header.setCourse(cursor.getString(cursor.getColumnIndexOrThrow(KEY_COURSE)));
		header.setSection(cursor.getString(cursor.getColumnIndexOrThrow(KEY_SECTION)));
		header.setSAPId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_SAPID)));
		header.setRollNo(cursor.getString(cursor.getColumnIndexOrThrow(KEY_ROLLNO)));

		db.close();
		cursor.close();

		return header;
	}

	public int updateListHeader(ListHeader header) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_STU_NAME, header.getName());
		values.put(KEY_FATHER_NAME, header.getFatherName());
		values.put(KEY_COURSE, header.getCourse());
		values.put(KEY_SECTION,header.getSection());
		values.put(KEY_ROLLNO, header.getRollNo());

		// updating row
		int rows_affected = db.update(TABLE_HEADER, values, KEY_SAPID + " = ?",
				new String[] { String.valueOf(header.getSAPId()) });
		db.close();

		return rows_affected;
	}

	public ListFooter getListFooter() {
		SQLiteDatabase db = this.getReadableDatabase();

		String selectQuery = "SELECT  * FROM " + TABLE_FOOTER + ";";
		Cursor cursor = db.rawQuery(selectQuery, null);

		if (cursor != null)
			cursor.moveToFirst();

		ListFooter footer = new ListFooter();
		footer.setSNo(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_SNO)));
		footer.setHeld(cursor.getFloat(cursor.getColumnIndexOrThrow(KEY_TOTAL_HELD)));
		footer.setAttended(cursor.getFloat(cursor.getColumnIndexOrThrow(KEY_TOTAL_ATTEND)));
		footer.setPercentage(cursor.getFloat(cursor.getColumnIndexOrThrow(KEY_TOTAL_PERCANTAGE)));

		db.close();
		cursor.close();

		return footer;
	}

	public void addOrUpdateListFooter(ListFooter footer) {
		SQLiteDatabase db = this.getWritableDatabase();

		Cursor cursor = db.query(TABLE_FOOTER, new String[] { KEY_SNO}, KEY_SNO + "=?",
				new String[] { String.valueOf(footer.getSNo()) }, null, null, null, null);
		if (cursor.getCount() == 0) {
			addListFooter(footer);
		}
		else {
			updateListFooter(footer);
		}
		db.close(); // Closing database connection
	}

	public void addListFooter(ListFooter footer) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_SNO, footer.getSNo());
		values.put(KEY_TOTAL_HELD, footer.getHeld());
		values.put(KEY_TOTAL_ATTEND, footer.getAttended());
		values.put(KEY_TOTAL_PERCANTAGE,footer.getPercentage());

		// Inserting Row
		db.insert(TABLE_FOOTER, null, values);
		db.close(); // Closing database connection
	}

	public int updateListFooter(ListFooter footer) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_TOTAL_HELD, footer.getHeld());
		values.put(KEY_TOTAL_ATTEND, footer.getAttended());
		values.put(KEY_TOTAL_PERCANTAGE,footer.getPercentage());

		// updating row
		int rows_affected = db.update(TABLE_FOOTER, values, KEY_SNO + " = ?",
				new String[] { String.valueOf(footer.getSNo() )} );
		db.close();

		return rows_affected;
	}

	public void addOrUpdateDay(Day day) {
		SQLiteDatabase db = this.getWritableDatabase();

		Cursor cursor = db.query(TABLE_TIMETABLE, new String[] { KEY_DAY}, KEY_DAY + "=?",
				new String[] { String.valueOf(day.getPeriod(0).getDay()) }, null, null, null, null);
		if (cursor.getCount() == 0) {
			addDay(day);
		}
		else {
			updateDay(day);
		}
		db.close(); // Closing database connection
	}

	public void addDay(Day day) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_DAY, day.getPeriod(0).getDay());
		List<Period> periods = day.getAllPeriods();

		for(Period period : periods)
		{
			values.put(period.getTime(), period.getName());
		}

		// Inserting Row
		db.insert(TABLE_TIMETABLE, null, values);
		db.close(); // Closing database connection
	}

	public int updateDay(Day day) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_DAY, day.getPeriod(0).getDay());
		List<Period> periods = day.getAllPeriods();

		for(Period period : periods)
		{
			values.put(period.getTime(), period.getName());
		}

		// updating row
		int rows_affected = db.update(TABLE_TIMETABLE, values, KEY_DAY + " = ?",
				new String[] { String.valueOf(day.getPeriod(0).getDay())} );
		db.close(); // Closing database connection

		return rows_affected;
	}

	public Day getDay(String dayName) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_TIMETABLE, null, KEY_DAY + "=?",
				new String[] { String.valueOf(dayName) }, null, null, null, null);

		if (cursor != null)
			cursor.moveToFirst();


		Day day = new Day();
		for(int i=0;i<21;i++)
		{
			if(!cursor.getColumnName(i).equals(KEY_DAY))
			{
				Period period = new Period();
				period.setDay(dayName);
				period.setName(cursor.getString(i));
				period.setTime(cursor.getColumnName(i));
				day.addPeriod(period);
			}
		}
		db.close();
		cursor.close();

		return day;
	}
}