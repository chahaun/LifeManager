package com.example.hanmi.lifemanager.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UserHelper extends SQLiteOpenHelper {
    private  static UserHelper userInstance;
    private static final int DB_VERSION=1;
    public static final String DB_NAME = "User.db";

    // 유지보수를 위해 SQL문을 미리 만들어줌.
    // 테이블 생성 SQL CREATE TABLE
    private static final String SQL_CREATE_ENTRIES =
            String.format("CREATE TABLE %s(%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s TEXT, %s TEXT, %s INTEGER, %s INTEGER, %s INTEGER, %s TEXT, %s TEXT)",
                    UserContract.UserEntry.TABLE_NAME,
                    UserContract.UserEntry._ID,
                    UserContract.UserEntry.COLUMN_USER_ID,
                    UserContract.UserEntry.COLUMN_USER_PW,
                    UserContract.UserEntry.COLUMN_USER_NAME,
                    UserContract.UserEntry.COLUMN_USER_YEAR,
                    UserContract.UserEntry.COLUMN_USER_MONTH,
                    UserContract.UserEntry.COLUMN_USER_DAY,
                    UserContract.UserEntry.COLUMN_USER_PHONE_NUM,
                    UserContract.UserEntry.COLUMN_USER_SEX);

    // 테이블 삭제 SQL DROP TABLE
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS"+ UserContract.UserEntry.TABLE_NAME;

    public static UserHelper getsInstance(Context context){
        if(userInstance==null){
            userInstance = new UserHelper(context);
        }
        return userInstance;
    }

    // 내장 메모리일 때 /data/data/패키지이름/database/에 저장됨
    // 확인방법 Tools-> android-> android device monitor
    public UserHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    // 테이블 업그레이드 될 때
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}
