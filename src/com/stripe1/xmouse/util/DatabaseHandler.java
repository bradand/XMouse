package com.stripe1.xmouse.util;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
 
public class DatabaseHandler extends SQLiteOpenHelper {
 
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 2;
    public SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
    // Database Name
    private static final String DATABASE_NAME = "contactsManager";
 
    // Contacts table name
    public static final String HOST_TABLE_NAME = "contacts";
    public static final String SCRIPT_TABLE_NAME = "scripts";
    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_TIME = "time";
    //private static final String KEY_NAME = "name";
    //private static final String KEY_PH_NO = "phone_number";
 
    private ArrayList<ArrayList<String>> hostVarList;
    private ArrayList<ArrayList<String>> scriptVarList;
    
    public DatabaseHandler(Context context,ArrayList<ArrayList<String>> hostVars,ArrayList<ArrayList<String>> scriptVars) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        hostVarList=hostVars; 
        scriptVarList=scriptVars;
    }
 
    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
    	Log.d("DatabaseHandler", "onCreate");
    	dateFormat.setLenient(false);
    	
    	//setup contacts table
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + HOST_TABLE_NAME + "(" + 
        		KEY_ID + " INTEGER PRIMARY KEY," +
        		KEY_TIME + " DATETIME";
        for(ArrayList<String> var : hostVarList){
        	CREATE_CONTACTS_TABLE += "," + var.get(0) + " " + var.get(1);
        }
        CREATE_CONTACTS_TABLE += " )";
        db.execSQL(CREATE_CONTACTS_TABLE);
        
        
        //setup scripts table
        String CREATE_SCRIPTS_TABLE = "CREATE TABLE " + SCRIPT_TABLE_NAME + "(" + 
        		KEY_ID + " INTEGER PRIMARY KEY," +
        		KEY_TIME + " DATETIME";
        for(ArrayList<String> var : scriptVarList){
        	CREATE_SCRIPTS_TABLE += "," + var.get(0) + " " + var.get(1);
        }
        CREATE_SCRIPTS_TABLE += " )";
        db.execSQL(CREATE_SCRIPTS_TABLE);
        
    }
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion){
    	Log.d("DatabaseHandler", "onDowngrade");
    	// Drop older tables if existing
    	db.execSQL("DROP TABLE IF EXISTS " + SCRIPT_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + HOST_TABLE_NAME);
        // Create tables again
        onCreate(db);
    }
    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    	Log.d("DatabaseHandler", "onUpgrade");
    	
    	// Drop older tables if existing
    	db.execSQL("DROP TABLE IF EXISTS " + SCRIPT_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + HOST_TABLE_NAME);
        // Create tables again
        onCreate(db);
        
    }
 
    /**
     * ArrayList<ArrayList<String>> items
     * item=items.get(i)
     * item.get(0)= key
     * item.get(1)= value
     */
    public void addRow(String TABLE_NAME,ArrayList<ArrayList<String>> items) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        ContentValues values = new ContentValues();
        Calendar date1 = Calendar.getInstance();
        values.put(KEY_TIME,dateFormat.format(date1.getTime())); 
        
        for(int i=0;i<items.size();i++){
        	ArrayList<String> item = items.get(i);
	        values.put(item.get(0), item.get(1)); 
	        
        }
        // Inserting Row
        db.insert(TABLE_NAME, null, values);
        db.close(); // Closing database connection
    }
    
    public String[] getLastRow(String TABLE_NAME,String[] columnKeys) {
    	
        SQLiteDatabase db = this.getReadableDatabase();
       
        Cursor cursor = db.query(	TABLE_NAME, //String table
        							columnKeys, //new String[] { KEY_ID, KEY_NAME, KEY_PH_NO }, //String[] columns
        							null,//KEY_ID + "=?", //String selection
        							null,//new String[] { String.valueOf(id) }, //String[] selectionArgs
        							null, //String groupBy
        							null, //String having
        							KEY_ID + " DESC",//null, //String orderBy
        							"1"//null //String limit
        						);
        
        
        if (cursor.moveToFirst()) {
            do {
            
            	for(int i=0;i<columnKeys.length;i++){
    	        	
    	        	columnKeys[i] = cursor.getString(i);
    	        }
            } while (cursor.moveToNext());
        }else{
        	return new String[columnKeys.length]; //return empty array
        }
        
        db.close();
        
        return columnKeys;
    }
    public ArrayList<ArrayList<String>> listAll(String TABLE_NAME,String[] strings) {
		// TODO Auto-generated method stub
    	SQLiteDatabase db = this.getReadableDatabase();
        
        Cursor cursor = db.query(	TABLE_NAME, //String table
        							strings, //new String[] { KEY_ID, KEY_NAME, KEY_PH_NO }, //String[] columns
        							null,//KEY_TIME + ">=? AND " + KEY_TIME + "<=?", //String selection
        							null,//new String[] { timeLow, timeHigh }, //String[] selectionArgs
        							null, //String groupBy
        							null, //String having
        							KEY_ID + " DESC",//null, //String orderBy
        							null //String limit
        						);
    	
    	ArrayList<ArrayList<String>> results = new ArrayList<ArrayList<String>>();
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                
            	ArrayList<String> temp = new ArrayList<String>();
            	for(int i=0;i<strings.length;i++){
            		temp.add(cursor.getString(i));
                	
                }
            	
            	results.add(temp);
            	
            	
            } while (cursor.moveToNext());
        }
        db.close();
        // return contact list
        return results;
	}
    // Getting All Contacts
    public ArrayList<ArrayList<String>> listWithTimeRange(String TABLE_NAME,String[] columnKeys,String timeLow,String timeHigh) {
        
    	//List<Contact> contactList = new ArrayList<Contact>();
        // Select All Query
        //String selectQuery = "SELECT  * FROM " + TABLE_NAME;
 
        //SQLiteDatabase db = this.getWritableDatabase();
        //Cursor cursor = db.rawQuery(selectQuery, null);
    	
    	
    	SQLiteDatabase db = this.getReadableDatabase();
        
        Cursor cursor = db.query(	TABLE_NAME, //String table
        							columnKeys, //new String[] { KEY_ID, KEY_NAME, KEY_PH_NO }, //String[] columns
        							KEY_TIME + ">=? AND " + KEY_TIME + "<=?", //String selection
        							new String[] { timeLow, timeHigh }, //String[] selectionArgs
        							null, //String groupBy
        							null, //String having
        							KEY_ID + " DESC",//null, //String orderBy
        							null //String limit
        						);
    	
    	ArrayList<ArrayList<String>> results = new ArrayList<ArrayList<String>>();
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                
            	ArrayList<String> temp = new ArrayList<String>();
            	for(int i=0;i<columnKeys.length;i++){
            		temp.add(cursor.getString(i));
                	
                }
            	
            	results.add(temp);
            	
            	
            } while (cursor.moveToNext());
        }
        db.close();
        // return contact list
        return results;
    }
 
    // Updating single contact
    public int updateRow(String TABLE_NAME,String[] columnKeys, String[] columnValues,String id) {
        SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
        
        for(int i=0;i<columnKeys.length;i++){
        	values.put(columnKeys[i], columnValues[i]);
        }
        // updating row
        return db.update(	TABLE_NAME,
        					values,
        					KEY_ID + " = ?",
        					new String[] { id }
        		);
        
    }
 
    // Deleting single contact
    public int deleteRow(String TABLE_NAME,int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.delete(	TABLE_NAME,
        			KEY_ID + " = ?",
        			new String[] { String.valueOf(id) });
        db.close();
        return rowsAffected;
    }
 
 
    // Getting contacts Count
    public int getTableCount(String TABLE_NAME) {
        String countQuery = "SELECT  "+ KEY_ID +" FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        
 
        // return count
        return cursor.getCount();
    }

    public ArrayList<ArrayList<String>> getRowWithId(String TABLE_NAME,String[] columnKeys, String id) {
		SQLiteDatabase db = this.getReadableDatabase();
        
        Cursor cursor = db.query(	TABLE_NAME, //String table
        							columnKeys, //new String[] { KEY_ID, KEY_NAME, KEY_PH_NO }, //String[] columns
        							KEY_ID + "=?", //String selection
        							new String[] { id }, //String[] selectionArgs
        							null, //String groupBy
        							null, //String having
        							KEY_ID + " DESC",//null, //String orderBy
        							null //String limit
        						);
    	
    	ArrayList<ArrayList<String>> results = new ArrayList<ArrayList<String>>();
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                
            	ArrayList<String> temp = new ArrayList<String>();
            	for(int i=0;i<columnKeys.length;i++){
            		temp.add(cursor.getString(i));
                	
                }
            	
            	results.add(temp);
            	
            	
            } while (cursor.moveToNext());
        }
        db.close();
        // return contact list
        return results;
		
	}
 
}