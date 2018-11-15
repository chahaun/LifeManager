package com.example.hanmi.lifemanager.DB;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper_parent extends SQLiteOpenHelper {

    private static DBHelper_parent sInstance; // 싱클톤 패턴을 위한것

    private static final int DB_VERSION =1;
    private static final String DB_NAME = "ParentList.db";  //DB 파일 이름
    private static final String parentListSQL =
            String.format("create table %s (%s integer primary key autoincrement, " +
                            "%s text, %s integer);",
                    MemoContract_parent.MemoEntry.TABLE_NAME,
                    MemoContract_parent.MemoEntry._ID, //primary key
                    MemoContract_parent.MemoEntry.COLUMN_NAME_DATA_GROUP,
                    MemoContract_parent.MemoEntry.COLUMN_NAME_INDEX
            );

    private static final String deleteSQL =
            "DROP TABLE IF EXISTS " + MemoContract_parent.MemoEntry.TABLE_NAME;


    private static final String insertSQL1 =
            "INSERT INTO " + MemoContract_parent.MemoEntry.TABLE_NAME + " VALUES (NULL, '오늘' ,1);";
    private static final String insertSQL2 =
            "INSERT INTO " + MemoContract_parent.MemoEntry.TABLE_NAME + " VALUES (NULL, '내일' ,2);";
    private static final String insertSQL3 =
            "INSERT INTO " + MemoContract_parent.MemoEntry.TABLE_NAME + " VALUES (NULL, '다가올' ,3);";



    /*********************************************************************/
    // 싱글톤 패턴을 위해서 사용하는것 이다.
    public static DBHelper_parent getsInstance(Context context){

        if(sInstance == null){
            sInstance = new DBHelper_parent(context);
        }
        return sInstance;
    }

    /****************생성자   Context ******************/
    private DBHelper_parent(Context context)
    {
        super(context, DB_NAME,  null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(parentListSQL);

        db.execSQL(insertSQL1);
        db.execSQL(insertSQL2);
        db.execSQL(insertSQL3);



    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(deleteSQL);
        onCreate(db);
    }


}
