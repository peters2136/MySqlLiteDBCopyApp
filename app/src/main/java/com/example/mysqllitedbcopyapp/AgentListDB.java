/*
Purpose:  To set-up initial SQLite database (i.e. copy on first run, manage db connection)
          and manage interactions with the database Agents table
Author:   Stuart Peters
Date:     May 2019
 */


package com.example.mysqllitedbcopyapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class AgentListDB implements Serializable {

    // database constants
    public static final String DB_NAME = "TravelExpertsSqlLite.db";
    public static final int    DB_VERSION = 1;

    // task table constants
    public static final String AGENT_TABLE = "Agents";

    public static final String AGENT_ID = "AgentId";
    public static final int    AGENT_ID_COL = 0;

    public static final String AGENT_AGT_FIRST_NAME = "AgtFirstName";
    public static final int    AGENT_AGT_FIRST_NAME_COL = 1;

    public static final String AGENT_AGT_MIDDLE_INITIAL = "AgtMiddleInitial";
    public static final int    AGENT_AGT_MIDDLE_INITIAL_COL = 2;

    public static final String AGENT_AGT_LAST_NAME = "AgtLastName";
    public static final int    AGENT_AGT_LAST_NAME_COL = 3;

    public static final String AGENT_AGT_BUS_PHONE = "AgtBusPhone";
    public static final int    AGENT_AGT_BUS_PHONE_COL = 4;

    public static final String AGENT_AGT_EMAIL = "AgtEmail";
    public static final int    AGENT_AGT_EMAIL_COL = 5;

    public static final String AGENT_AGT_POSITION = "AgtPosition";
    public static final int    AGENT_AGT_POSITION_COL = 6;

    public static final String AGENT_AGENCY_ID = "AgencyId";
    public static final int    AGENT_AGENCY_ID_COL = 7;

    private static class DBHelper extends SQLiteOpenHelper {

        //Path to the device folder with databases
        public static String DB_PATH;

        //Database file name
        //public static String DB_NAME;
        public SQLiteDatabase database;
        public final Context context;

        public SQLiteDatabase getDb() {
            return database;
        }

        public DBHelper(Context context, String databaseName) {
            super(context, databaseName, null, 1);
            this.context = context;
            //Write a full path to the databases of your application
            String packageName = context.getPackageName();
            DB_PATH = String.format("//data//data//%s//databases//", packageName);
            //DB_NAME = databaseName;
            openDataBase();
        }

        //This piece of code will create a database if it’s not yet created
        public void createDataBase() {
            boolean dbExist = checkDataBase();
            if (!dbExist) {
                this.getReadableDatabase();
                try {
                    copyDataBase();
                } catch (IOException e) {
                    Log.e(this.getClass().toString(), "Copying error");
                    throw new Error("Error copying database!");
                }
            } else {
                Log.i(this.getClass().toString(), "Database already exists");
            }
        }

        //Performing a database existence check
        private boolean checkDataBase() {
            SQLiteDatabase checkDb = null;
            try {
                String path = DB_PATH + DB_NAME;
                checkDb = SQLiteDatabase.openDatabase(path, null,
                        SQLiteDatabase.OPEN_READONLY);
            } catch (SQLException e) {
                Log.e(this.getClass().toString(), "Error while checking db");
            }
            //Android doesn’t like resource leaks, everything should
            // be closed
            if (checkDb != null) {
                checkDb.close();
            }
            return checkDb != null;
        }

        //Method for copying the database
        private void copyDataBase() throws IOException {
            //Open a stream for reading from our ready-made database
            //The stream source is located in the assets
            InputStream externalDbStream = context.getAssets().open(DB_NAME);

            //Path to the created empty database on your Android device
            String outFileName = DB_PATH + DB_NAME;

            //Now create a stream for writing the database byte by byte
            OutputStream localDbStream = new FileOutputStream(outFileName);

            //Copying the database
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = externalDbStream.read(buffer)) > 0) {
                localDbStream.write(buffer, 0, bytesRead);
            }
            //Don’t forget to close the streams
            localDbStream.close();
            externalDbStream.close();
        }

        public SQLiteDatabase openDataBase() throws SQLException {
            String path = DB_PATH + DB_NAME;
            if (database == null) {
                createDataBase();
                database = SQLiteDatabase.openDatabase(path, null,
                        SQLiteDatabase.OPEN_READWRITE);
            }
            return database;
        }

        @Override
        public synchronized void close() {
            if (database != null) {
                database.close();
            }
            super.close();
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }

    // database and database helper objects
    private SQLiteDatabase db;
    private DBHelper dbHelper;

    // constructor
    public AgentListDB(Context context) {
        dbHelper = new DBHelper(context, DB_NAME);
    }

    // private methods
    private void openReadableDB() {
        db = dbHelper.getReadableDatabase();
    }

    private void openWriteableDB() {
        db = dbHelper.getWritableDatabase();
    }

    private void closeDB() {
        if (db != null)
            db.close();
    }

    // public methods
    public ArrayList<Agent> getAgents() {

        ArrayList<Agent> agents = new ArrayList<Agent>();
        this.openReadableDB();
        String sql = "select * from Agents";
        Cursor cursor = db.rawQuery(sql, null);

        while (cursor.moveToNext()) {
            agents.add(getAgentFromCursor(cursor));
        }
        if (cursor != null)
            cursor.close();
        this.closeDB();

        return agents;
    }

    public Agent getAgent(int id) {
        String where = AGENT_ID + "= ?";
        String[] whereArgs = { Integer.toString(id) };

        this.openReadableDB();
        Cursor cursor = db.query(AGENT_TABLE,
                null, where, whereArgs, null, null, null);
        cursor.moveToFirst();
        Agent agent = getAgentFromCursor(cursor);
        if (cursor != null)
            cursor.close();
        this.closeDB();

        return agent;
    }
//
    private static Agent getAgentFromCursor(Cursor cursor) {
        if (cursor == null || cursor.getCount() == 0){
            return null;
        }
        else {
            try {
                Agent agent = new Agent(
                        cursor.getInt(AGENT_ID_COL),
                        cursor.getString(AGENT_AGT_FIRST_NAME_COL),
                        cursor.getString(AGENT_AGT_MIDDLE_INITIAL_COL),
                        cursor.getString(AGENT_AGT_LAST_NAME_COL),
                        cursor.getString(AGENT_AGT_BUS_PHONE_COL),
                        cursor.getString(AGENT_AGT_EMAIL_COL),
                        cursor.getString(AGENT_AGT_POSITION_COL),
                        cursor.getInt(AGENT_AGENCY_ID_COL)
                );
                return agent;
            }
            catch(Exception e) {
                return null;
            }
        }
    }

    public long insertAgent(Agent agent) {

        Log.i("step", "enter insert");
        ContentValues cv = new ContentValues();
        cv.put(AGENT_AGT_FIRST_NAME, agent.getAgtFirstName());
        cv.put(AGENT_AGT_MIDDLE_INITIAL, agent.getAgtMiddleInitial());
        cv.put(AGENT_AGT_LAST_NAME, agent.getAgtLastName());
        cv.put(AGENT_AGT_BUS_PHONE, agent.getAgtBusPhone());
        cv.put(AGENT_AGT_EMAIL, agent.getAgtEmail());
        cv.put(AGENT_AGT_POSITION, agent.getAgtPosition());
        cv.put(AGENT_AGENCY_ID, agent.getAgencyId());


        Log.i("step", "open db");
        this.openWriteableDB();

        Log.i("step", "execute update");
        long rowID = db.insert(AGENT_TABLE, null, cv);
        Log.i("step",String.valueOf(rowID));

        this.closeDB();

        return rowID;
    }

    public int updateAgent(Agent agent) {
        ContentValues cv = new ContentValues();
        cv.put(AGENT_AGT_FIRST_NAME, agent.getAgtFirstName());
        cv.put(AGENT_AGT_MIDDLE_INITIAL, agent.getAgtMiddleInitial());
        cv.put(AGENT_AGT_LAST_NAME, agent.getAgtLastName());
        cv.put(AGENT_AGT_BUS_PHONE, agent.getAgtBusPhone());
        cv.put(AGENT_AGT_EMAIL, agent.getAgtEmail());
        cv.put(AGENT_AGT_POSITION, agent.getAgtPosition());
        cv.put(AGENT_AGENCY_ID, agent.getAgencyId());

        String where = AGENT_ID + "= ?";
        String[] whereArgs = { String.valueOf(agent.getAgentId()) };

        this.openWriteableDB();
        int rowCount = db.update(AGENT_TABLE, cv, where, whereArgs);
        this.closeDB();

        return rowCount;
    }

    public int deleteAgent(int id) {
        String where = AGENT_ID + "= ?";
        String[] whereArgs = { String.valueOf(id) };

        this.openWriteableDB();
        int rowCount = db.delete(AGENT_TABLE, where, whereArgs);
        this.closeDB();

        return rowCount;
    }
}