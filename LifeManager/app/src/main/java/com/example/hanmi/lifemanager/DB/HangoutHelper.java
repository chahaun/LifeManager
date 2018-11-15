package com.example.hanmi.lifemanager.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class HangoutHelper extends SQLiteOpenHelper {
    private  static HangoutHelper hangoutInstance;
    private static int DB_VERSION=1;
    public static final String DB_NAME = "Hangout.db";

    // 테이블 생성 SQL CREATE TABLE
    private static final String SQL_CREATE_ENTRIES =
            String.format("CREATE TABLE %s(%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s TEXT, %s INTEGER, %s INTEGER, %s INTEGER)",
                    HangoutContract.HangoutEntry.TABLE_NAME,
                    HangoutContract.HangoutEntry._ID,
                    HangoutContract.HangoutEntry.COLUMN_HANGOUT_NAME,
                    HangoutContract.HangoutEntry.COLUMN_HANGOUT_MSG,
                    HangoutContract.HangoutEntry.COLUMN_HANGOUT_YEAR,
                    HangoutContract.HangoutEntry.COLUMN_HANGOUT_MONTH,
                    HangoutContract.HangoutEntry.COLUMN_HANGOUT_DAY);

    // 테이블 삭제 SQL DROP TABLE
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS "+ HangoutContract.HangoutEntry.TABLE_NAME;

    // 행아웃을 미리 통신이 완료 된 상태로 가정하기 위해 데이터를 미리 넣어둔다.
    private static final String insertSQL1 =
            "INSERT INTO " + HangoutContract.HangoutEntry.TABLE_NAME + " VALUES (null, '차해운', '모바일' ,2018, 5, 28);";
    private static final String insertSQL2 =
            "INSERT INTO " + HangoutContract.HangoutEntry.TABLE_NAME + " VALUES (null, '신제우', '멀티미디어' ,2018, 5, 29);";
    private static final String insertSQL3 =
            "INSERT INTO " + HangoutContract.HangoutEntry.TABLE_NAME + " VALUES (null, '최민철', '프로그래밍' ,2018, 5, 29);";
    private static final String insertSQL4 =
            "INSERT INTO " + HangoutContract.HangoutEntry.TABLE_NAME + " VALUES (null, '유재석', '무' ,2018, 6, 1);";
    private static final String insertSQL5 =
            "INSERT INTO " + HangoutContract.HangoutEntry.TABLE_NAME + " VALUES (null, '박명수', '한' ,2018, 6, 1);";
    private static final String insertSQL6 =
            "INSERT INTO " + HangoutContract.HangoutEntry.TABLE_NAME + " VALUES (null, '정준하', '도' ,2018, 6, 5);";
    private static final String insertSQL7 =
            "INSERT INTO " + HangoutContract.HangoutEntry.TABLE_NAME + " VALUES (null, '양세형', '전' ,2018, 6, 5);";

    public static HangoutHelper getsInstance(Context context) {
        if (hangoutInstance == null) {
            hangoutInstance = new HangoutHelper(context);
        }
        return hangoutInstance;
    }

    // 내장 메모리일 때 /data/data/패키지이름/database/에 저장됨
    // 확인방법 Tools-> android-> android device monitor
    public HangoutHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
        db.execSQL(insertSQL1);
        db.execSQL(insertSQL2);
        db.execSQL(insertSQL3);
        db.execSQL(insertSQL4);
        db.execSQL(insertSQL5);
        db.execSQL(insertSQL6);
        db.execSQL(insertSQL7);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}