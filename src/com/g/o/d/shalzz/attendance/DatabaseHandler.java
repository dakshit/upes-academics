package com.g.o.d.shalzz.attendance;

import java.util.ArrayList;
import java.util.List;

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

	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "attendanceManager";

	// Contacts table name
	private static final String TABLE_ATTENDENCE = "Attendance";

	// Contacts Table Columns names
	private static final String KEY_ID = "id";
	private static final String KEY_NAME = "Subject_Name";
	private static final String KEY_CLASSES_HELD = "No_Classes_Held";
	private static final String KEY_CLASSES_ATTENDED = "No_Classes_Attended";
	private static final String KEY_DAYS_ABSENT = "Days_Absent";
	private static final String KEY_PERCENTAGE = "Percentage";
	private static final String KEY_PROJECTED_PERCENTAGE = "Projected_Percentage";

	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	/**
	 * Create Table.
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_ATTENDENCE_TABLE = "CREATE TABLE " + TABLE_ATTENDENCE + " ( "
				+ KEY_ID + " INTEGER PRIMARY KEY, " + KEY_NAME + " TEXT, " 
				+ KEY_CLASSES_HELD + " REAL, " + KEY_CLASSES_ATTENDED + " REAL, " 
				+ KEY_DAYS_ABSENT + " TEXT, " + KEY_PERCENTAGE + " REAL, " 
				+ KEY_PROJECTED_PERCENTAGE + "  TEXT " + ")";
		System.out.println(CREATE_ATTENDENCE_TABLE);
		db.execSQL(CREATE_ATTENDENCE_TABLE);
	}

	/**
	 * Drop the table if it exist and create a new table.
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_ATTENDENCE);

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
		String selectQuery = "SELECT  * FROM " + TABLE_ATTENDENCE;

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

		// return row count
		return rowCount;
	}

	/**
	 * Delete all tables and create them again
	 * */
	public void resetTables(){
		SQLiteDatabase db = this.getWritableDatabase();
		// Delete All Rows
		db.delete(TABLE_ATTENDENCE, "1", null);
		db.close();
	}

}
