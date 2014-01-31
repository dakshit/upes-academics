package com.shalzz.attendance;

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

	// Attendance table name
	private static final String TABLE_ATTENDENCE = "Attendance";

	// ListHeader table name
	private static final String TABLE_HEADER = "ListHeader";

	// ListFooter table name
	private static final String TABLE_FOOTER = "ListFooter";

	// Attendance Table Columns names
	private static final String KEY_ID = "id";
	private static final String KEY_NAME = "Subject_Name";
	private static final String KEY_CLASSES_HELD = "No_Classes_Held";
	private static final String KEY_CLASSES_ATTENDED = "No_Classes_Attended";
	private static final String KEY_DAYS_ABSENT = "Days_Absent";
	private static final String KEY_PERCENTAGE = "Percentage";
	private static final String KEY_PROJECTED_PERCENTAGE = "Projected_Percentage";

	// ListHeader Table Columns names
	private static final String KEY_STU_NAME = "Student_Name";
	private static final String KEY_FATHER_NAME = "Fathers_Name";
	private static final String KEY_COURSE = "Course_Name";
	private static final String KEY_SECTION = "Section";
	private static final String KEY_ROLLNO = "Rollno";
	private static final String KEY_SAPID = "SAPId";

	// ListFooter Table Column names
	private static final String KEY_SNO = "SNo";
	private static final String KEY_TOTAL_HELD = "Classes_held";
	private static final String KEY_TOTAL_ATTEND = "Classes_attend";
	private static final String KEY_TOTAL_PERCANTAGE = "Percentage";

	private static final String CREATE_ATTENDENCE_TABLE = "CREATE TABLE " + TABLE_ATTENDENCE + " ( "
			+ KEY_ID + " INTEGER PRIMARY KEY, " + KEY_NAME + " TEXT, " 
			+ KEY_CLASSES_HELD + " REAL, " + KEY_CLASSES_ATTENDED + " REAL, " 
			+ KEY_DAYS_ABSENT + " TEXT, " + KEY_PERCENTAGE + " REAL, " 
			+ KEY_PROJECTED_PERCENTAGE + "  TEXT " + ");";

	private static final String CREATE_HEADER_TABLE = "CREATE TABLE " + TABLE_HEADER + " ( "
			+ KEY_STU_NAME + " TEXT, " + KEY_FATHER_NAME + " TEXT, " 
			+ KEY_COURSE + " TEXT, " + KEY_SECTION + " TEXT, " 
			+ KEY_ROLLNO + " TEXT, " + KEY_SAPID + "  INTEGER PRIMARY KEY " + ");";

	private static final String CREATE_FOOTER_TABLE = "CREATE TABLE " + TABLE_FOOTER + " ( "
			+ KEY_SNO + " INTEGER PRIMARY KEY, " + KEY_TOTAL_HELD + " REAL, " 
			+ KEY_TOTAL_ATTEND + " REAL, " + KEY_TOTAL_PERCANTAGE + "  REAL " + ");";

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

		ContentValues values = new ContentValues();
		values.put(KEY_NAME, subject.getName());
		values.put(KEY_CLASSES_HELD, subject.getClassesHeld());
		values.put(KEY_CLASSES_ATTENDED, subject.getClassesAttended());
		values.put(KEY_DAYS_ABSENT, subject.getAbsentDates());
		values.put(KEY_PERCENTAGE, subject.getPercentage());
		values.put(KEY_PROJECTED_PERCENTAGE, subject.getProjectedPercentage());

		Cursor cursor = db.query(TABLE_ATTENDENCE, new String[] { KEY_ID}, KEY_ID + "=?",
				new String[] { String.valueOf(subject.getID()) }, null, null, null, null);
		if (cursor.getCount() == 0)
		{
			values.put(KEY_ID, subject.getID());
			db.insert(TABLE_ATTENDENCE, null, values);
		}
		else
		{
			db.update(TABLE_ATTENDENCE, values, KEY_ID + " = ?",
					new String[] { String.valueOf(subject.getID()) });
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
		db.delete(TABLE_HEADER, "1", null);
		db.delete(TABLE_FOOTER, "1", null);
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
		db.close(); // Closing database connection
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
}