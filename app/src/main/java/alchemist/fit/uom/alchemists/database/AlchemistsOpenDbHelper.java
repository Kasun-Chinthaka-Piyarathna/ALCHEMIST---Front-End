package alchemist.fit.uom.alchemists.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public class AlchemistsOpenDbHelper extends SQLiteOpenHelper implements BaseColumns {
    private static final String DATABASE_NAME = "alchemists";
    private static final int DATABASE_VERSION = 1;
    private static final String TAG = "AlchemistsOpenDbHelper";

    public AlchemistsOpenDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    //create user_details table
    public static final String DATABASE_USER_DETAILS_TABLE_CREATE = "create table " + "user_details" + "( "
            + _ID + " integer primary key autoincrement,"
            + "name  text,nearest_city text,mobile text,email text,password text)";


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_USER_DETAILS_TABLE_CREATE);
        Log.d(TAG, DATABASE_USER_DETAILS_TABLE_CREATE);
        //------------------------

    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS 'user_details'; ");
        Log.d(TAG, "DROP TABLE IF EXISTS 'user_details'; ");
        onCreate(db);
    }
}
