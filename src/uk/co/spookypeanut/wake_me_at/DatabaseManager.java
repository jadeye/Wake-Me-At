package uk.co.spookypeanut.wake_me_at;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseManager
{
    private SQLiteDatabase db;
    public final String LOG_NAME = WakeMeAt.LOG_NAME;

    private final String DB_NAME = "WakeMeAtDB";
    private final int DB_VERSION = 2;

    private final String TABLE_NAME = "Locations";
    private final String TABLE_ROW_ID = "id";
    private final String TABLE_ROW_NICK = "table_row_nick";
    private final String TABLE_ROW_LAT = "table_row_lat";
    private final String TABLE_ROW_LONG = "table_row_long";
    private final String TABLE_ROW_PROV = "table_row_prov";
    private final String TABLE_ROW_RADIUS = "table_row_radius";

    Context context;


    public DatabaseManager(Context context) {
        this.context = context;
        CustomSQLiteOpenHelper helper = new CustomSQLiteOpenHelper(context);
        this.db = helper.getWritableDatabase();
    }

    public int getRowCount() {
        ArrayList <ArrayList <Object>> allData = getAllRowsAsArrays();
        return allData.size();
    }
    
    public long addRow(String rowNick, double rowLat, double rowLong,
                       String rowProv, float rowRadius) {
        // this is a key value pair holder used by android's SQLite functions
        ContentValues values = new ContentValues();
        values.put(TABLE_ROW_NICK, rowNick);
        values.put(TABLE_ROW_LAT, rowLat);
        values.put(TABLE_ROW_LONG, rowLong);
        values.put(TABLE_ROW_PROV, rowProv);
        values.put(TABLE_ROW_RADIUS, rowRadius);
        long rowId;
        try {
            rowId = db.insert(TABLE_NAME, null, values);
            return rowId;
        }
        catch(Exception e) {
            Log.e("DB ERROR", e.toString());
            e.printStackTrace();
        }
        return -1;
    }

    public void deleteRow(long rowID) {
        try {
            db.delete(TABLE_NAME, TABLE_ROW_ID + "=" + rowID, null);
        }
        catch (Exception e) {
            Log.e("DB ERROR", e.toString());
            e.printStackTrace();
        }
    }

    public void updateRow(long rowId, String rowNick, double rowLat, double rowLong,
                          String rowProv, float rowRadius) {
        // this is a key value pair holder used by android's SQLite functions
        ContentValues values = new ContentValues();
        values.put(TABLE_ROW_NICK, rowNick);
        values.put(TABLE_ROW_LAT, rowLat);
        values.put(TABLE_ROW_LONG, rowLong);
        values.put(TABLE_ROW_PROV, rowProv);
        values.put(TABLE_ROW_RADIUS, rowRadius);
        try {
            db.update(TABLE_NAME, values, TABLE_ROW_ID + "=" + rowId, null);
        }
        catch (Exception e) {
            Log.e("DB Error", e.toString());
            e.printStackTrace();
        }
    }

    public String getDatumS(long rowId, String column) {
        Cursor cursor;
        String returnValue = "";

        try {
            cursor = db.query (
                    TABLE_NAME,
                    new String[] {column},
                            TABLE_ROW_ID + "=" + rowId,
                            null, null, null, null, null
            );
            cursor.moveToFirst();
            returnValue = cursor.getString(0);
            cursor.close();
        }
        catch (SQLException e) 
        {
            Log.e("DB ERROR", e.toString());
            e.printStackTrace();
        }
        return returnValue;
    }

    public void setDatumS(long rowId, String column, String value) {
        ContentValues values = new ContentValues();
        values.put(column, value);
        try {
            db.update(TABLE_NAME, values, TABLE_ROW_ID + "=" + rowId, null);
        }
        catch (Exception e) {
            Log.e("DB Error", e.toString());
            e.printStackTrace();
        }
    }

    public void setDatumD(long rowId, String column, double value) {
        setDatumS(rowId, column, Double.toString(value));
    }

    public void setDatumF(long rowId, String column, float value) {
        setDatumS(rowId, column, Float.toString(value));
    }

    public String getNick(long rowId) {
        return getDatumS(rowId, TABLE_ROW_NICK);
    }

    public void setNick(long rowId, String nick) {
        setDatumS(rowId, TABLE_ROW_NICK, nick);
    }
    
    public double getLatitude(long rowId) {
        return Double.valueOf(getDatumS(rowId, TABLE_ROW_LAT));
    }

    public void setLatitude(long rowId, double latitude) {
        setDatumD(rowId, TABLE_ROW_LAT, latitude);
    }

    public double getLongitude(long rowId) {
        return Double.valueOf(getDatumS(rowId, TABLE_ROW_LONG));
    }

    public void setLongitude(long rowId, double longitude) {
        setDatumD(rowId, TABLE_ROW_LONG, longitude);
    }

    public String getProvider(long rowId) {
        return getDatumS(rowId, TABLE_ROW_PROV);
    }

    public void setProvider(long rowId, String provider) {
        setDatumS(rowId, TABLE_ROW_PROV, provider);
    }
    
    public float getRadius(long rowId) {
        return Float.valueOf(getDatumS(rowId, TABLE_ROW_RADIUS));
    }

    public void setRadius(long rowId, float radius) {
        setDatumF(rowId, TABLE_ROW_RADIUS, radius);
    }

    public ArrayList<Long> getIdsAsList() {
        ArrayList<Long> returnList = new ArrayList<Long>();
        Cursor cursor;

        try {
            cursor = db.query (
                    TABLE_NAME,
                    new String[] {TABLE_ROW_ID},
                    null, null, null,
                    null, null, null
            );
            cursor.moveToFirst();

            // if there is data available after the cursor's pointer, add
            // it to the ArrayList that will be returned by the method.
            if (!cursor.isAfterLast()) {
                do {
                    returnList.add(cursor.getLong(0));
                }
                while (cursor.moveToNext());
            }
            cursor.close();
        }
        catch (SQLException e) {
            Log.e("DB ERROR", e.toString());
            e.printStackTrace();
        }
        return returnList;
    }
    
    public ArrayList<Object> getRowAsArray(long rowId) {
        // create an array list to store data from the database row.
        // I would recommend creating a JavaBean compliant object 
        // to store this data instead.  That way you can ensure
        // data types are correct.
        ArrayList<Object> rowArray = new ArrayList<Object>();
        Cursor cursor;

        try {
            cursor = db.query (
                    TABLE_NAME,
                    new String[] { TABLE_ROW_ID, TABLE_ROW_NICK,
                            TABLE_ROW_LAT, TABLE_ROW_LONG,
                            TABLE_ROW_PROV, TABLE_ROW_RADIUS },
                            TABLE_ROW_ID + "=" + rowId,
                            null, null, null, null, null
            );
            cursor.moveToFirst();

            // if there is data available after the cursor's pointer, add
            // it to the ArrayList that will be returned by the method.
            if (!cursor.isAfterLast()) {
                do {
                    rowArray.add(cursor.getLong(0));
                    rowArray.add(cursor.getString(1));
                    rowArray.add(cursor.getDouble(2));
                    rowArray.add(cursor.getDouble(3));
                    rowArray.add(cursor.getString(4));
                    rowArray.add(cursor.getFloat(5));
                }
                while (cursor.moveToNext());
            }
            cursor.close();
        }
        catch (SQLException e) {
            Log.e("DB ERROR", e.toString());
            e.printStackTrace();
        }
        return rowArray;
    }

    public void logOutArray() {
        Log.d(LOG_NAME, "Start of array log");
        ArrayList<ArrayList<Object>> data = getAllRowsAsArrays();
        for (int position=0; position < data.size(); position++)
        { 
            ArrayList<Object> row = data.get(position);
            Log.d(LOG_NAME, row.get(0).toString() + ", " +
                            row.get(1).toString() + ", " +
                            row.get(2).toString() + ", " +
                            row.get(3).toString() + ", " +
                            row.get(4).toString() + ", " +
                            row.get(5).toString());
        }
        Log.d(LOG_NAME, "End of array log");
    }

    public ArrayList<ArrayList<Object>> getAllRowsAsArrays() {
        ArrayList<ArrayList<Object>> dataArrays = new ArrayList<ArrayList<Object>>();
        Cursor cursor;

        try {
            cursor = db.query(
                    TABLE_NAME,
                    new String[] { TABLE_ROW_ID, TABLE_ROW_NICK,
                            TABLE_ROW_LAT, TABLE_ROW_LONG,
                            TABLE_ROW_PROV, TABLE_ROW_RADIUS},
                            null, null, null, null, null
            );
            cursor.moveToFirst();

            if (!cursor.isAfterLast()) {
                do {
                    ArrayList<Object> dataList = new ArrayList<Object>();

                    dataList.add(cursor.getLong(0));
                    dataList.add(cursor.getString(1));
                    dataList.add(cursor.getDouble(2));
                    dataList.add(cursor.getDouble(3));
                    dataList.add(cursor.getString(4));
                    dataList.add(cursor.getFloat(5));

                    dataArrays.add(dataList);
                }
                while (cursor.moveToNext());
            }
            cursor.close();
        }
        catch (SQLException e) {
            Log.e("DB Error", e.toString());
            e.printStackTrace();
        }
        return dataArrays;
    }


    /**********************************************************************
     * THIS IS THE BEGINNING OF THE INTERNAL SQLiteOpenHelper SUBCLASS.
     * 
     * I MADE THIS CLASS INTERNAL SO I CAN COPY A SINGLE FILE TO NEW APPS 
     * AND MODIFYING IT - ACHIEVING DATABASE FUNCTIONALITY.  ALSO, THIS WAY 
     * I DO NOT HAVE TO SHARE CONSTANTS BETWEEN TWO FILES AND CAN
     * INSTEAD MAKE THEM PRIVATE AND/OR NON-STATIC.  HOWEVER, I THINK THE
     * INDUSTRY STANDARD IS TO KEEP THIS CLASS IN A SEPARATE FILE.
     *********************************************************************/

    private class CustomSQLiteOpenHelper extends SQLiteOpenHelper {
        public CustomSQLiteOpenHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String newTableQueryString = "create table " +
            TABLE_NAME +
            " (" +
            TABLE_ROW_ID + " integer primary key autoincrement not null," +
            TABLE_ROW_NICK + " TEXT," +
            TABLE_ROW_LAT + " DOUBLE," +
            TABLE_ROW_LONG + " DOUBLE," +
            TABLE_ROW_PROV + " TEXT," +
            TABLE_ROW_RADIUS + " FLOAT" +
            ");";
            db.execSQL(newTableQueryString);
        }


        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            String upgradeMessage = "Upgrading db v" + oldVersion + " to v" + newVersion;
            Log.d(LOG_NAME, upgradeMessage);
        }
    }
}