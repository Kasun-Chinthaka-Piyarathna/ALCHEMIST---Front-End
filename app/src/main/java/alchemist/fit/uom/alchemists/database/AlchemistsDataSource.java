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
    private String userDetailsUserIdKey = "user_id";
    private String userDetailsNameKey = "name";
    private String userDetailsNearestCityKey = "nearest_city";
    private String userDetailsMobileKey = "mobile";
    private String userDetailsEmailKey = "email";
    private String userDetailsPasswordKey = "password";
    private String userDetailsProfileImageUrlKey = "profile_url";
    private String userDetailsCoverImageUrlKey = "cover_photo_url";
    private String userDetailsContextKey = "context";
    private String userDetailsDynamicKey = "dynamic";
    private String userDetailsBirthdayKey = "birthday";
    private String userDetailsGenderKey = "gender";
    private String notExistMessage = "NO EXIST";

    private String behaviourDetailsTable = "behaviour_details";
    private String behaviourDetailsEmailKey = "email";
    private String behaviourDetailsNewsFeedTimeKey = "newsfeed_time";
    private String behaviourDetailsNotificationTimeKey = "notification_time";
    private String behaviourDetailsProfileTimeKey = "profile_time";
    private String behaviourDetailsPhotoSelectionCountKey = "photo_selection_count";
    private String behaviourDetailsVideoSelectionCountKey = "video_selection_count";
    private String behaviourDetailsCurrentContextKey = "current_context";
    private String behaviourDetailsContextIdentificationKey = "context_identification";
    private String behaviourDetailsUserHistoryIdentificationKey = "user_history_identification";
    private String behaviourDetailsUserBehaviourIdentificationKey = "user_behaviour_identification";
    private String behaviourDetailsMuteFreqKey = "muteFreq";
    private String behaviourDetailsUnmuteFreqKey = "unmuteFreq";
    private String behaviourDetailsSimulationKey = "simulation";

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
    public void insertDataUserDetails(String userId, String name, String nearest_city, String mobile,
                                      String email, String password, String userProfileImageUrl,
                                      String cover_photo_url, String context, String dynamic,
                                      String birthday, String gender) {
        ContentValues newValues = new ContentValues();
        newValues.put(userDetailsUserIdKey, userId);
        newValues.put(userDetailsNameKey, name);
        newValues.put(userDetailsNearestCityKey, nearest_city);
        newValues.put(userDetailsMobileKey, mobile);
        newValues.put(userDetailsEmailKey, email);
        newValues.put(userDetailsPasswordKey, password);
        newValues.put(userDetailsProfileImageUrlKey, userProfileImageUrl);
        newValues.put(userDetailsCoverImageUrlKey, cover_photo_url);
        newValues.put(userDetailsContextKey, context);
        newValues.put(userDetailsDynamicKey, dynamic);
        newValues.put(userDetailsBirthdayKey, birthday);
        newValues.put(userDetailsGenderKey, gender);
        sqLiteDatabase.insertOrThrow(userDetailsTable, null, newValues);
    }

    public void insertDataBehaviourDetails(String newsfeed_time,
                                           String notification_time,
                                           String profile_time,
                                           String photo_selection_count,
                                           String video_selection_count, String current_context,
                                           String email, String context_identification,
                                           String user_history_identification,
                                           String user_behaviour_identification,
                                           String muteFreq, String unmuteFreq,
                                           String simulation) {
        ContentValues newValues = new ContentValues();
        newValues.put(behaviourDetailsEmailKey, email);
        newValues.put(behaviourDetailsNewsFeedTimeKey, newsfeed_time);
        newValues.put(behaviourDetailsNotificationTimeKey, notification_time);
        newValues.put(behaviourDetailsProfileTimeKey, profile_time);
        newValues.put(behaviourDetailsPhotoSelectionCountKey, photo_selection_count);
        newValues.put(behaviourDetailsVideoSelectionCountKey, video_selection_count);
        newValues.put(behaviourDetailsCurrentContextKey, current_context);
        newValues.put(behaviourDetailsContextIdentificationKey, context_identification);
        newValues.put(behaviourDetailsUserHistoryIdentificationKey, user_history_identification);
        newValues.put(behaviourDetailsUserBehaviourIdentificationKey, user_behaviour_identification);
        newValues.put(behaviourDetailsMuteFreqKey, muteFreq);
        newValues.put(behaviourDetailsUnmuteFreqKey, unmuteFreq);
        newValues.put(behaviourDetailsSimulationKey,simulation);
        sqLiteDatabase.insertOrThrow(behaviourDetailsTable, null, newValues);
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
        String[] arr = new String[12];
        cursor.moveToFirst();
        String name = cursor.getString(cursor.getColumnIndex(userDetailsNameKey));
        String nearest_city = cursor.getString(cursor.getColumnIndex(userDetailsNearestCityKey));
        String mobile = cursor.getString(cursor.getColumnIndex(userDetailsMobileKey));
        String email = cursor.getString(cursor.getColumnIndex(userDetailsEmailKey));
        String password = cursor.getString(cursor.getColumnIndex(userDetailsPasswordKey));
        String profile_image = cursor.getString(cursor.getColumnIndex(userDetailsProfileImageUrlKey));
        String cover_image = cursor.getString(cursor.getColumnIndex(userDetailsCoverImageUrlKey));
        String context = cursor.getString(cursor.getColumnIndex(userDetailsContextKey));
        String dynamic_status = cursor.getString(cursor.getColumnIndex(userDetailsDynamicKey));
        String birthday = cursor.getString(cursor.getColumnIndex(userDetailsBirthdayKey));
        String gender = cursor.getString(cursor.getColumnIndex(userDetailsGenderKey));

        arr[0] = name;
        arr[1] = nearest_city;
        arr[2] = mobile;
        arr[3] = email;
        arr[4] = password;
        arr[5] = profile_image;
        arr[6] = cover_image;
        arr[7] = context;
        arr[8] = dynamic_status;
        arr[9] = birthday;
        arr[10] = gender;
        cursor.close();
        return arr;
    }


    public String[] getAllDataFromBehaviourDetails(String userEmail) {
        Cursor cursor = sqLiteDatabase.query(behaviourDetailsTable, null, " email=?",
                new String[]{userEmail}, null, null, null);
        String[] response = new String[2];
        response[0] = notExistMessage;
        if (cursor.getCount() < 1) {
            cursor.close();
            return response;
        }
        String[] arr = new String[14];
        cursor.moveToFirst();
        String newsfeed_time = cursor.getString(cursor.getColumnIndex(behaviourDetailsNewsFeedTimeKey));
        String notification_time = cursor.getString(cursor.getColumnIndex(behaviourDetailsNotificationTimeKey));
        String profile_time = cursor.getString(cursor.getColumnIndex(behaviourDetailsProfileTimeKey));
        String select_photo_count = cursor.getString(cursor.getColumnIndex(behaviourDetailsPhotoSelectionCountKey));
        String select_video_coount = cursor.getString(cursor.getColumnIndex(behaviourDetailsVideoSelectionCountKey));
        String current_context = cursor.getString(cursor.getColumnIndex(behaviourDetailsCurrentContextKey));
        String context_identification = cursor.getString(cursor.getColumnIndex(behaviourDetailsContextIdentificationKey));
        String user_history_identification = cursor.getString(cursor.getColumnIndex(behaviourDetailsUserHistoryIdentificationKey));
        String user_behaviour_identification = cursor.getString(cursor.getColumnIndex(behaviourDetailsUserBehaviourIdentificationKey));
        String muteFreq = cursor.getString(cursor.getColumnIndex(behaviourDetailsMuteFreqKey));
        String unmuteFreq = cursor.getString(cursor.getColumnIndex(behaviourDetailsUnmuteFreqKey));
        String simulation = cursor.getString(cursor.getColumnIndex(behaviourDetailsSimulationKey));

        arr[0] = newsfeed_time;
        arr[1] = notification_time;
        arr[2] = profile_time;
        arr[3] = select_photo_count;
        arr[4] = select_video_coount;
        arr[5] = current_context;
        arr[6] = context_identification;
        arr[7] = user_history_identification;
        arr[8] = user_behaviour_identification;
        arr[9] = muteFreq;
        arr[10] = unmuteFreq;
        arr[11] = simulation;
        cursor.close();
        return arr;
    }

    //--------------------------------------------------------------
    //-------------------------UpdateQuery--------------------------
    //DailyChallenge Table
    public void updateUserDetails(String name, String nearest_city, String mobile, String email,
                                  String password, String profile_image,
                                  String cover_image, String context, String dynamic,
                                  String dob, String gender) {
        ContentValues updatedValues = new ContentValues();
        if (name != null) {
            updatedValues.put(userDetailsNameKey, name);
        } else if (nearest_city != null) {
            updatedValues.put(userDetailsNearestCityKey, nearest_city);
        } else if (mobile != null) {
            updatedValues.put(userDetailsMobileKey, mobile);
        } else if (profile_image != null) {
            updatedValues.put(userDetailsProfileImageUrlKey, profile_image);
        } else if (cover_image != null) {
            updatedValues.put(userDetailsCoverImageUrlKey, cover_image);
        } else if (password != null) {
            updatedValues.put(userDetailsPasswordKey, password);
        } else if (context != null) {
            updatedValues.put(userDetailsContextKey, context);
        } else if (dynamic != null) {
            updatedValues.put(userDetailsDynamicKey, dynamic);
        } else if (dob != null) {
            updatedValues.put(userDetailsBirthdayKey, dob);
        } else if (gender != null) {
            updatedValues.put(userDetailsGenderKey, gender);
        }
        String where = "email = ?";
        sqLiteDatabase.update(userDetailsTable, updatedValues, where, new String[]{email});
    }


    public void updateDataBehaviourDetails(String email, String newsfeed_time, String notification_time,
                                           String profile_time,
                                           String photo_selection_count,
                                           String video_selection_count, String current_context,
                                           String context_identification,
                                           String user_history_identification,
                                           String user_behaviour_identification,
                                           String muteFreq, String unmuteFreq,
                                           String simulation
    ) {

        ContentValues updatedValues = new ContentValues();
        if (newsfeed_time != null) {
            updatedValues.put(behaviourDetailsNewsFeedTimeKey, newsfeed_time);
        } else if (notification_time != null) {
            updatedValues.put(behaviourDetailsNotificationTimeKey, notification_time);
        } else if (profile_time != null) {
            updatedValues.put(behaviourDetailsProfileTimeKey, profile_time);
        } else if (photo_selection_count != null) {
            updatedValues.put(behaviourDetailsPhotoSelectionCountKey, photo_selection_count);
        } else if (video_selection_count != null) {
            updatedValues.put(behaviourDetailsVideoSelectionCountKey, video_selection_count);
        } else if (current_context != null) {
            updatedValues.put(behaviourDetailsCurrentContextKey, current_context);
        } else if (context_identification != null) {
            updatedValues.put(behaviourDetailsContextIdentificationKey, context_identification);
        } else if (user_history_identification != null) {
            updatedValues.put(behaviourDetailsUserHistoryIdentificationKey, user_history_identification);
        } else if (user_behaviour_identification != null) {
            updatedValues.put(behaviourDetailsUserBehaviourIdentificationKey, user_behaviour_identification);
        } else if (muteFreq != null) {
            updatedValues.put(behaviourDetailsMuteFreqKey, muteFreq);
        } else if (unmuteFreq != null) {
            updatedValues.put(behaviourDetailsUnmuteFreqKey, unmuteFreq);
        }else if (simulation != null) {
            updatedValues.put(behaviourDetailsSimulationKey, simulation);
        }
        String where = "email = ?";
        sqLiteDatabase.update(behaviourDetailsTable, updatedValues, where, new String[]{email});
    }


    //--------------------------------------------------------------
    //-------------------------DeleteQuery--------------------------
    public void deleteTableUserDetails() {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS 'user_details'; ");
        sqLiteDatabase.execSQL(AlchemistsOpenDbHelper.DATABASE_USER_DETAILS_TABLE_CREATE);
    }
}
