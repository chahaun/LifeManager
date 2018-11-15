package com.example.hanmi.lifemanager.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper_child extends SQLiteOpenHelper {


    private int sYear, sMonth, sDay, sHour, sMinute;  // 선택한 날짜


    private static DBHelper_child sInstance; // 싱클톤 패턴을 위한것

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "ChildList.db";  //DB 파일 이름
    private static final String childListSQL =
            String.format("create table %s (%s integer primary key autoincrement, " +
                            "%s text, %s text, %s text,%s text," +
                            " %s integer , %s integer , %s integer, %s integer, %s integer"+
                            " , %s text, %s text , %s integer," +
                            "%s integer DEFAULT 0, %s integer DEFAULT 0," +
                            "%s integer DEFAULT 0);",
                    MemoContract_child.MemoEntry.TABLE_NAME,
                    MemoContract_child.MemoEntry._ID, //primary key

                    MemoContract_child.MemoEntry.COLUMN_NAME_DATA_GROUP,
                    MemoContract_child.MemoEntry.COLUMN_NAME_DATA_GROUP_INDEX,
                    MemoContract_child.MemoEntry.COLUMN_NAME_TITLE,
                    MemoContract_child.MemoEntry.COLUMN_NAME_CONTENTS,

                    MemoContract_child.MemoEntry.COLUMN_NAME_YEAR,
                    MemoContract_child.MemoEntry.COLUMN_NAME_MONTH,
                    MemoContract_child.MemoEntry.COLUMN_NAME_DAY,
                    MemoContract_child.MemoEntry.COLUMN_NAME_HOUR,
                    MemoContract_child.MemoEntry.COLUMN_NAME_MINUTE,

                    MemoContract_child.MemoEntry.COLUMN_NAME_WL,
                    MemoContract_child.MemoEntry.COLUMN_NAME_SCOPE,
                    MemoContract_child.MemoEntry.COLUMN_NAME_PROBABILITY,

                    MemoContract_child.MemoEntry.COLUMN_NAME_SUCCESS_COUNT,
                    MemoContract_child.MemoEntry.COLUMN_NAME_FAIL_COUNT,
                    MemoContract_child.MemoEntry.COLUMN_NAME_CONTINUE

            );

    private static final String deleteSQL =
            "DROP TABLE IF EXISTS " + MemoContract_child.MemoEntry.TABLE_NAME;


    /*********************************************************************/
    // 싱글톤 패턴을 위해서 사용하는것 이다.
    public static DBHelper_child getsInstance(Context context) {

        if (sInstance == null) {
            sInstance = new DBHelper_child(context);
        }

        return sInstance;
    }

    /****************생성자   Context ******************/

    public DBHelper_child(Context context) {
        super(context,DB_NAME,null,DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(childListSQL);



    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(deleteSQL);
        onCreate(db);
    }

}
