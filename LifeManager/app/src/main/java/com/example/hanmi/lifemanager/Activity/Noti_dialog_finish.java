package com.example.hanmi.lifemanager.Activity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;
import static java.lang.Math.toIntExact;
import com.example.hanmi.lifemanager.DB.DBHelper_child;
import com.example.hanmi.lifemanager.DB.MemoContract_child;
import com.example.hanmi.lifemanager.R;
import com.example.hanmi.lifemanager.Receiver.scheduleReceiver;

public class Noti_dialog_finish extends Activity implements View.OnClickListener {
    private Button success_button, fail_button;
    DBHelper_child dbHelper_child_update;
    SQLiteDatabase db_child_update;
    private long sId;
    int intID;
    private int suc_count, fail_count;
    private Intent intent_receive,intent_send_receiver;
    private Cursor cursor;
    private NotificationManager notificationManager;
    private AlarmManager am;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.noti_dialog);
        setContent();
        notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        am = (AlarmManager)getSystemService(ALARM_SERVICE);
        intent_receive = getIntent();
        intent_send_receiver = new Intent(this, scheduleReceiver.class);

        if (intent_receive == null)
            Toast.makeText(this, "인텐트를 아에 못받음", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "다이어로그에서 받은 id:"
                    + Long.toString(intent_receive.getLongExtra("id", -1)), Toast.LENGTH_SHORT).show();
        sId = intent_receive.getLongExtra("id", -1);
        intID = toIntExact(sId);

        PendingIntent sender = PendingIntent.getBroadcast(this,intID,intent_send_receiver,PendingIntent.FLAG_UPDATE_CURRENT);

        am.cancel(sender);
    }

    private void setContent() {
        success_button = (Button) findViewById(R.id.btnConfirm);
        fail_button = (Button) findViewById(R.id.btnFinish);

        success_button.setOnClickListener(this);
        fail_button.setOnClickListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void onClick(View v) {

        dbHelper_child_update = DBHelper_child.getsInstance(this);
        db_child_update = dbHelper_child_update.getWritableDatabase();
        ContentValues contentValues;

        cursor = db_child_update.rawQuery("SELECT *"
                + " FROM " + MemoContract_child.MemoEntry.TABLE_NAME + " ORDER BY " +
                "" + MemoContract_child.MemoEntry._ID, null);

        if (sId == -1)
            Toast.makeText(this, "다이어로그에서 잘못 받음", Toast.LENGTH_SHORT).show();

        switch (v.getId()) { // 다이얼로그를 눌렀으면
            case R.id.btnConfirm: // [네] 를 눌렀을때 이벤트
                contentValues = new ContentValues();

                while (cursor.moveToNext()) {
                    if (sId == cursor.getInt(cursor.getColumnIndexOrThrow(MemoContract_child.MemoEntry._ID))) {
                        suc_count = cursor.getInt(cursor.getColumnIndexOrThrow(MemoContract_child.MemoEntry.COLUMN_NAME_SUCCESS_COUNT));
                        suc_count++;
                    } else continue;
                }

                contentValues.put(MemoContract_child.MemoEntry.COLUMN_NAME_SUCCESS_COUNT, suc_count); // 성공변수 1증가
                contentValues.put(MemoContract_child.MemoEntry.COLUMN_NAME_DATA_GROUP_INDEX, 4);
                contentValues.put(MemoContract_child.MemoEntry.COLUMN_NAME_DATA_GROUP, "완료");
                db_child_update.update(MemoContract_child.MemoEntry.TABLE_NAME, contentValues,
                        MemoContract_child.MemoEntry._ID + " = " + sId, null);

                cursor.close();
                db_child_update.close();
                dbHelper_child_update.close();
                notificationManager.cancel(intID);
                this.finish(); // 다이얼로그창을 끈다(액티비티종료)
                this.finishAffinity(); // 앱을 종료한다
                break;
            case R.id.btnFinish:  // [아니요] 를 눌렀을때 이벤트
                contentValues = new ContentValues();

                while (cursor.moveToNext()) {
                    if (sId == cursor.getInt(cursor.getColumnIndexOrThrow(MemoContract_child.MemoEntry._ID))) {
                        fail_count = cursor.getInt(cursor.getColumnIndexOrThrow(MemoContract_child.MemoEntry.COLUMN_NAME_FAIL_COUNT));
                        fail_count++;
                    } else continue;
                }

                contentValues.put(MemoContract_child.MemoEntry.COLUMN_NAME_FAIL_COUNT, fail_count); // 성공변수 1증가
                contentValues.put(MemoContract_child.MemoEntry.COLUMN_NAME_DATA_GROUP_INDEX, 4);
                contentValues.put(MemoContract_child.MemoEntry.COLUMN_NAME_DATA_GROUP, "완료");
                db_child_update.update(MemoContract_child.MemoEntry.TABLE_NAME, contentValues,
                        MemoContract_child.MemoEntry._ID + " = " + sId, null);

                cursor.close();
                db_child_update.close();
                dbHelper_child_update.close();
                notificationManager.cancel(intID);
                this.finish(); // 다이얼로그창을 끈다(액티비티종료)
                this.finishAffinity(); // 앱을 종료한다
                break;
            default:
                break;
        }
    }

}
