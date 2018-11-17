package alchemist.fit.uom.alchemists.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class AlchemistsDataSource {
    private static final String TAG = "AlchemistsDataSource";
    private SQLiteDatabase sqLiteDatabase;
    private SQLiteOpenHelper sqLiteOpenHelper;
    private Context context;
    private String DataBaseOpenedMessage = "Database opened.";
    private String DataBaseClosedMessage = "Database closed.";
    private String userDetailsTable = "user_details";
    private String userDetailsNameKey = "name";
    private String userDetailsNearestCityKey = "nearest_city";
    private String userDetailsMobileKey = "mobile";
    private String userDetailsEmailKey = "email";
    private String userDetailsPasswordKey = "password";
    private String notExistMessage = "NO EXIST";

    public AlchemistsDataSource(Context context) {
        this.context = context;
        sqLiteOpenHelper = new AlchemistsOpenDbHelper(context);
    }

    public void open() {
        sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();
        Log.d(TAG, DataBaseOpenedMessage);
    }

    public void close() {
        sqLiteOpenHelper.close();
        Log.d(TAG, DataBaseClosedMessage);
    }

    //--------------------------InsertQuery-----------------------------
    public void insertDataUserDetails(String name, String nearest_city, String mobile, String email, String password) {
        ContentValues newValues = new ContentValues();
        newValues.put(userDetailsNameKey, name);
        newValues.put(userDetailsNearestCityKey, nearest_city);
        newValues.put(userDetailsMobileKey, mobile);
        newValues.put(userDetailsEmailKey, email);
        newValues.put(userDetailsPasswordKey, password);
        sqLiteDatabase.insertOrThrow(userDetailsTable, null, newValues);
    }

    //----------------------------------------------------------------
    //------------------------RetrieveQuery----------------------------
    public String[] getAllDataFromUserDetails(String userEmail) {
        Cursor cursor = sqLiteDatabase.query(userDetailsTable, null, " email=?",
                new String[]{userEmail}, null, null, null);
        String[] response = new String[2];
        response[0] = notExistMessage;
        if (cursor.getCount() < 1) {
            cursor.close();
            return response;
        }
        String[] arr = new String[7];
        cursor.moveToFirst();
        String name = cursor.getString(cursor.getColumnIndex(userDetailsNameKey));
        String nearest_city = cursor.getString(cursor.getColumnIndex(userDetailsNearestCityKey));
        String mobile = cursor.getString(cursor.getColumnIndex(userDetailsMobileKey));
        String email = cursor.getString(cursor.getColumnIndex(userDetailsEmailKey));
        String password = cursor.getString(cursor.getColumnIndex(userDetailsPasswordKey));

        arr[0] = name;
        arr[1] = nearest_city;
        arr[2] = mobile;
        arr[3] = email;
        arr[4] = password;
        cursor.close();
        return arr;
    }

    //--------------------------------------------------------------
    //-------------------------UpdateQuery--------------------------
    //DailyChallenge Table
    public void updateUserDetails(String name, String nearest_city, String mobile, String email, String password) {
        ContentValues updatedValues = new ContentValues();
        if (name != null) {
            updatedValues.put(userDetailsNameKey, name);
        }
        else if (nearest_city != null) {
            updatedValues.put(userDetailsNearestCityKey, nearest_city);
        }
        else if (mobile != null) {
            updatedValues.put(userDetailsMobileKey, mobile);
        }
        else if (email != null) {
            updatedValues.put(userDetailsEmailKey, email);
        }
        else {
            updatedValues.put(userDetailsPasswordKey, password);
        }
        String where = "email = ?";
        sqLiteDatabase.update(userDetailsTable, updatedValues, where, new String[]{email});
    }

    //--------------------------------------------------------------
    //-------------------------DeleteQuery--------------------------
    public void deleteTableUserDetails() {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS 'user_details'; ");
        sqLiteDatabase.execSQL(AlchemistsOpenDbHelper.DATABASE_USER_DETAILS_TABLE_CREATE);
    }
}
