package com.example.hanmi.lifemanager.Fragment;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import static java.lang.Math.toIntExact;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.ContextMenu;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.hanmi.lifemanager.Activity.Memo_register;
import com.example.hanmi.lifemanager.Activity.Memo_register_Modify;
import com.example.hanmi.lifemanager.DB.DBHelper_child;
import com.example.hanmi.lifemanager.DB.DBHelper_parent;
import com.example.hanmi.lifemanager.DB.MemoContract_child;
import com.example.hanmi.lifemanager.DB.MemoContract_parent;
import com.example.hanmi.lifemanager.Adapter.ListAdapter;
import com.example.hanmi.lifemanager.R;
import com.example.hanmi.lifemanager.Receiver.scheduleReceiver;
import com.example.hanmi.lifemanager.VO.ChildListData;
import com.example.hanmi.lifemanager.VO.ParentListData;
import com.melnykov.fab.FloatingActionButton;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

public class Schedule extends Fragment {

    static final int MEMO_REGISTER = 1;
    static final int MEMO_REGISTER_MODIFY = 2;
    private ExpandableListView expandable_ListView;
    private Boolean isFabClose = false;
    private FloatingActionButton fab, fab1, fab2;
    private Animation fab_open, fab_close, rotate_forward, rotate_backward;
    private TextView fab1_title, fab2_title;
    int changeMemo_child_INDEX = -1;

    private String title, contents, work_and_life, scope;

    ListAdapter list_adapter;

    String intent_title;
    private long intent_id;
    private boolean test;

    AlarmManager am;
    PendingIntent sender;
    Calendar calendar;
    /* 실시간 갱신을 위한 run함수 */
    private Runnable updateUI = new Runnable() {
        public void run() {
            Schedule.this.list_adapter.notifyDataSetChanged();
        }
    };
    private Cursor cursor;

    private View scheduleView;
    private ArrayList<ParentListData> parent_list_data;
    private int intent_id_integer;
    private Intent sch_receiver_intent;
    private int groupMemo_child_index;

    public Schedule() {
        // Required empty public constructor
    }

    public static Schedule newInstance() {

        Bundle args = new Bundle();
        Schedule fragment = new Schedule();
        fragment.setArguments(args);
        return fragment;
    }

    // 갱신을 위해 최신 데이터를 가져오는 매서드
    private Cursor getMemoCusor() {
        DBHelper_parent dbHelperParent = DBHelper_parent.getsInstance(getActivity());
        return dbHelperParent.getWritableDatabase().query(MemoContract_child.MemoEntry.TABLE_NAME, null, null,
                null, null,
                null, MemoContract_child.MemoEntry._ID);
    }

    /****************************************onCreateVIew****************************************/

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        scheduleView = inflater.inflate(R.layout.fragment_schedule, container, false);

        expandable_ListView = scheduleView.findViewById(R.id.expandable_listview);
        registerForContextMenu(expandable_ListView); // 컨텍스트메뉴 리스트에 등록

        setDataBase(); // 데이터베이스 셋팅함수

        list_adapter = new ListAdapter(getActivity(), parent_list_data);
        expandable_ListView.setAdapter(list_adapter);

        Toast.makeText(getActivity(), "현재날짜 : " + getDate(), Toast.LENGTH_SHORT).show();
        initWindowOption();
        //오늘 - _id 값 1   groupPosition 0 부터 시작

        setFloatingButton(); // 플로팅 버튼함수

        /*********************** 중첩리스트뷰의 인디케이터를 오른쪽으로 배치 ***********************/
        Display newDisplay = getActivity().getWindowManager().getDefaultDisplay();
        int width = newDisplay.getWidth();
        expandable_ListView.setIndicatorBounds(width - 200, width);
        /*****************************************************************************************/

        return scheduleView;
    }

    /****************************************onCreateVIew  끝****************************************/

    private void initWindowOption() {
        getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY); // 네비게이션 바, 상단바 숨김
    }

    /*********************** 이곳은 플로팅버튼 구현구간 ***********************/
    private void setFloatingButton() {
        // Floating Action Button을 리스트 뷰에 적용
        fab = (FloatingActionButton) scheduleView.findViewById(R.id.fab); // 메인 플로팅버튼
        fab1 = (FloatingActionButton) scheduleView.findViewById(R.id.fab1); // 메인 바로위 버튼
        fab2 = (FloatingActionButton) scheduleView.findViewById(R.id.fab2); // 그 위 버튼
        fab1_title = (TextView) scheduleView.findViewById(R.id.fab1_title);
        fab2_title = (TextView) scheduleView.findViewById(R.id.fab2_title);
        fab_open = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_backward);

        fab.attachToListView(expandable_ListView); //리스트 뷰위에 플로팅버튼 추가

        fab.setOnClickListener(new View.OnClickListener() { //메인 플로팅버튼을 눌렀을 때 이벤트처리
            @Override
            public void onClick(View v) {
                animateFAB(); // 이 함수로 이동
                initWindowOption();
            }
        });
        fab1.setOnClickListener(new View.OnClickListener() { //일정추가 이벤트
            @Override
            public void onClick(View v) {
                Intent Memo_register_intent = new Intent(getActivity(), Memo_register.class);
                startActivityForResult(Memo_register_intent, MEMO_REGISTER);
                initWindowOption();
            }
        });

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog builder = new AlertDialog.Builder(getActivity())
                        .setTitle("만든 사람들")
                        .setMessage("황인성 신제우 최민철 차해운")
                        .setNegativeButton("닫기", null)
                        .create();
                // 다이얼로그 이외의 화면 터치해도 꺼짐
                builder.setCanceledOnTouchOutside(true);
                builder.show(); // 다이얼로그 실행
                initWindowOption();
            }
        });
    }

    /*********************** 이곳은 DB 셋팅 구간 ***********************/
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setDataBase() {
        /**부모리스트 DB에 저장하는곳***/
        DBHelper_parent dbHelperParent = DBHelper_parent.getsInstance(getActivity());
        SQLiteDatabase db_parent = dbHelperParent.getWritableDatabase();
        cursor = db_parent.query(MemoContract_parent.MemoEntry.TABLE_NAME, null, null,
                null, null,
                null, MemoContract_parent.MemoEntry._ID);

        parent_list_data = new ArrayList<>();

        //cursor.moveToFirst();
        while (cursor.moveToNext()) {
            //중첩리스트의 부모리스트 data변수
            ParentListData parentListData = new ParentListData();
            parentListData.setName(cursor.getString(cursor.getColumnIndexOrThrow
                    (MemoContract_parent.MemoEntry.COLUMN_NAME_DATA_GROUP)));

            parent_list_data.add(parentListData);
        }
        db_parent.close();
        cursor.close();

        /**자식일정리스트 DB에 저장하는곳***/
        DBHelper_child dbHelper_child = DBHelper_child.getsInstance(getActivity());
        SQLiteDatabase db_child = dbHelper_child.getWritableDatabase();

        cursor = db_child.query(MemoContract_child.MemoEntry.TABLE_NAME, null, null,
                null, null,
                null, MemoContract_child.MemoEntry._ID);

        am = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        sch_receiver_intent = new Intent(getActivity(), scheduleReceiver.class);

        calendar = Calendar.getInstance();

        while (cursor.moveToNext()) {
            ChildListData childListData = new ChildListData();
            childListData.setId(cursor.getLong(cursor.getColumnIndexOrThrow
                    (MemoContract_child.MemoEntry._ID)));
            childListData.setTitle(cursor.getString(cursor.getColumnIndexOrThrow
                    (MemoContract_child.MemoEntry.COLUMN_NAME_TITLE)));
            childListData.setContents(cursor.getString(cursor.getColumnIndexOrThrow
                    (MemoContract_child.MemoEntry.COLUMN_NAME_CONTENTS)));
            childListData.setYear(cursor.getInt(cursor.getColumnIndexOrThrow
                    (MemoContract_child.MemoEntry.COLUMN_NAME_YEAR)));
            childListData.setMonth(cursor.getInt(cursor.getColumnIndexOrThrow
                    (MemoContract_child.MemoEntry.COLUMN_NAME_MONTH)));
            childListData.setDay(cursor.getInt(cursor.getColumnIndexOrThrow
                    (MemoContract_child.MemoEntry.COLUMN_NAME_DAY)));
            childListData.setHour(cursor.getInt(cursor.getColumnIndexOrThrow
                    (MemoContract_child.MemoEntry.COLUMN_NAME_HOUR)));
            childListData.setMinute(cursor.getInt(cursor.getColumnIndexOrThrow
                    (MemoContract_child.MemoEntry.COLUMN_NAME_MINUTE)));
            childListData.setWork_and_life(cursor.getString(cursor.getColumnIndexOrThrow
                    (MemoContract_child.MemoEntry.COLUMN_NAME_WL)));
            childListData.setScope(cursor.getString(cursor.getColumnIndexOrThrow
                    (MemoContract_child.MemoEntry.COLUMN_NAME_SCOPE)));

            intent_title = childListData.getTitle();
            intent_id = childListData.getId();

            if (cursor.getString(cursor.getColumnIndexOrThrow
                    (MemoContract_child.MemoEntry.COLUMN_NAME_DATA_GROUP_INDEX)).equals("1")) {
                parent_list_data.get(0).childListData.add(childListData);
                intent_id_integer = toIntExact(childListData.getId());
                sch_receiver_intent.putExtra("getId", intent_id);
                sch_receiver_intent.putExtra("getTitle", intent_title);
                sender = PendingIntent.getBroadcast(getActivity(), intent_id_integer, sch_receiver_intent, FLAG_UPDATE_CURRENT);

                calendar.set(childListData.getYear(), childListData.getMonth() - 1, childListData.getDay(),
                        childListData.getHour(), childListData.getMinute() + 1);

                am.set(AlarmManager.RTC, calendar.getTimeInMillis(), sender); // 알람설정
                am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 60 * 1000, sender);

            } else if (cursor.getString(cursor.getColumnIndexOrThrow
                    (MemoContract_child.MemoEntry.COLUMN_NAME_DATA_GROUP_INDEX)).equals("2")) {
                parent_list_data.get(1).childListData.add(childListData);
                intent_id_integer = toIntExact(childListData.getId());
                sch_receiver_intent.putExtra("getId", intent_id);
                sch_receiver_intent.putExtra("getTitle", intent_title);
                sender = PendingIntent.getBroadcast(getActivity(), intent_id_integer, sch_receiver_intent, FLAG_UPDATE_CURRENT);

                calendar.set(childListData.getYear(), childListData.getMonth() - 1, childListData.getDay(),
                        childListData.getHour(), childListData.getMinute() + 1);

                am.set(AlarmManager.RTC, calendar.getTimeInMillis(), sender); // 알람설정
                am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 60 * 1000, sender);

            } else if (cursor.getString(cursor.getColumnIndexOrThrow
                    (MemoContract_child.MemoEntry.COLUMN_NAME_DATA_GROUP_INDEX)).equals("3")) {
                parent_list_data.get(2).childListData.add(childListData);
                intent_id_integer = toIntExact(childListData.getId());
                sch_receiver_intent.putExtra("getId", intent_id);
                sch_receiver_intent.putExtra("getTitle", intent_title);
                sender = PendingIntent.getBroadcast(getActivity(), intent_id_integer, sch_receiver_intent, FLAG_UPDATE_CURRENT);

                calendar.set(childListData.getYear(), childListData.getMonth() - 1, childListData.getDay(),
                        childListData.getHour(), childListData.getMinute() + 1);

                am.set(AlarmManager.RTC, calendar.getTimeInMillis(), sender); // 알람설정
                am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 60 * 1000, sender);

            }
        }
        db_child.close();
        cursor.close();
    }

    /**
     * 오늘날짜 얻어오기
     **/
    public static String getDate() {
        // 포맷형식을 yyyy-MM-dd로 정하고 오늘날짜를 dateFormat 변수에 저장한다.
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
        Date date = new Date(); //Date객체생성
        String strDate = dateFormat.format(date); // String형태로 변환한다.

        return strDate;
    }

    /**
     * 두 일정의 차이 가져오기
     **/
    public static String getProDay(String selectDate) {

        String result = ""; //결과 초기화지정
        String select = selectDate.substring(0, 10); //지정한 날짜를 10바이트만 짤라서 받아온다
        String today = getDate().substring(0, 10); //오늘 날짜를 10바이트 받아옴

        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd"); //이 형식으로 포맷
            Date choiceDate = formatter.parse(select); //고른날짜를 Date형식으로 변환
            Date todayDate = formatter.parse(today); //오늘날짜를 Date형식으로 변환

            // 일정차이를 시간,분,초를 곱한 값으로 나누면 하루 단위가 나옴
            long diff = choiceDate.getTime() - todayDate.getTime();
            long diffDays = diff / (24 * 60 * 60 * 1000);

            result = diffDays + "";  //두 날짜의 차이를 result에 저장

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    //Memo_register or Memo_register_modify 로부터 결과값 받아온다
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MEMO_REGISTER) {
            if (resultCode == getActivity().RESULT_OK) {

                int sYear, sMonth, sDay, sHour, sMinute;  // 선택한 날짜 시간은 24시간 기준

                title = data.getStringExtra("title");
                contents = data.getStringExtra("contents");

                sYear = data.getIntExtra("year", 2019);
                sMonth = data.getIntExtra("month", 1);
                sDay = data.getIntExtra("day", 1);
                sHour = data.getIntExtra("hour", 1);
                sMinute = data.getIntExtra("minute", 11);

                work_and_life = data.getStringExtra("work_and_life");
                scope = data.getStringExtra("scope");

                ChildListData ch = new ChildListData();

                ch.setTitle(title);
                ch.setContents(contents);
                ch.setYear(sYear);
                ch.setMonth(sMonth);
                ch.setDay(sDay);
                ch.setHour(sHour);
                ch.setMinute(sMinute);

                ch.setWork_and_life(work_and_life);
                ch.setScope(scope);

                String formatDate = sYear + "-" + sMonth + "-" + sDay + " " + sHour + ":" + sMinute;
                String day_DR = getProDay(formatDate); //두 일정날짜의 차이
                Toast.makeText(getActivity(), "날짜차이 : " + day_DR + "일", Toast.LENGTH_SHORT).show();
                // 여기까지가 객체에 넣기
                /****************** 알람 리시버 등록 **********************/

                am = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE); //알람매니저 객체얻어옴
                //AlarmReceive.class이클레스는 따로 만들꺼임 알람이 발동될때 동작하는 클레이스임
                sch_receiver_intent = new Intent(getActivity(), scheduleReceiver.class);
                intent_title = ch.getTitle();  // 등록알람의 타이틀을 얻어서 intent_title String형변수에 넣음
                sch_receiver_intent.putExtra("getTitle", intent_title);  // intent에 intent_title을 붙인다.

                calendar = Calendar.getInstance();
                //알람시간 calendar에 set해주기
                /****************** 알람 리시버 등록 **********************/
                //이제 DB에 넣기

                ContentValues contentValues = new ContentValues();
                contentValues.put(MemoContract_child.MemoEntry.COLUMN_NAME_TITLE, title);
                contentValues.put(MemoContract_child.MemoEntry.COLUMN_NAME_CONTENTS, contents);

                contentValues.put(MemoContract_child.MemoEntry.COLUMN_NAME_YEAR, sYear);
                contentValues.put(MemoContract_child.MemoEntry.COLUMN_NAME_MONTH, sMonth);
                contentValues.put(MemoContract_child.MemoEntry.COLUMN_NAME_DAY, sDay);
                contentValues.put(MemoContract_child.MemoEntry.COLUMN_NAME_HOUR, sHour);
                contentValues.put(MemoContract_child.MemoEntry.COLUMN_NAME_MINUTE, sMinute);

                contentValues.put(MemoContract_child.MemoEntry.COLUMN_NAME_WL, work_and_life);
                contentValues.put(MemoContract_child.MemoEntry.COLUMN_NAME_SCOPE, scope);

                if (day_DR.equals("0")) {
                    contentValues.put(MemoContract_child.MemoEntry.COLUMN_NAME_DATA_GROUP, "오늘");
                    contentValues.put(MemoContract_child.MemoEntry.COLUMN_NAME_DATA_GROUP_INDEX, "1");
                    // parent_list_data.get(0).childListData.add(ch);
                    // am.set(AlarmManager.RTC, calendar.getTimeInMillis(), sender); // 알람설정
                } else if (day_DR.equals("1")) {
                    contentValues.put(MemoContract_child.MemoEntry.COLUMN_NAME_DATA_GROUP, "내일");
                    contentValues.put(MemoContract_child.MemoEntry.COLUMN_NAME_DATA_GROUP_INDEX, "2");
                    // parent_list_data.get(1).childListData.add(ch);
                    // am.set(AlarmManager.RTC, calendar.getTimeInMillis(), sender);
                } else {
                    contentValues.put(MemoContract_child.MemoEntry.COLUMN_NAME_DATA_GROUP, "다가올");
                    contentValues.put(MemoContract_child.MemoEntry.COLUMN_NAME_DATA_GROUP_INDEX, "3");
                    //   parent_list_data.get(2).childListData.add(ch);
                    //  am.set(AlarmManager.RTC, calendar.getTimeInMillis(), sender);
                }

                SQLiteDatabase db = DBHelper_child.getsInstance(getActivity()).getWritableDatabase();

                long newRowId = db.insert(MemoContract_child.MemoEntry.TABLE_NAME, null, contentValues);

                if (newRowId == -1) {
                    Toast.makeText(getActivity(), "저장에 문제 발생", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "저장됨", Toast.LENGTH_SHORT).show();
                }


                SQLiteDatabase db1 = DBHelper_child.getsInstance(getActivity()).getReadableDatabase();

                Cursor cursor = db1.query(MemoContract_child.MemoEntry.TABLE_NAME, null, null,
                        null, null, null, MemoContract_child.MemoEntry._ID);

                while ((test = cursor.moveToNext())) {


                    if (title.equals(cursor.getString(cursor.
                            getColumnIndexOrThrow(MemoContract_child.MemoEntry.COLUMN_NAME_TITLE)))) {
                        if (day_DR.equals("0")) {
                            ch.setId(cursor.getLong(cursor.getColumnIndexOrThrow(MemoContract_child.MemoEntry._ID)));
                            parent_list_data.get(0).childListData.add(ch);
                            intent_id = ch.getId();
                            intent_id_integer = toIntExact(intent_id);

                            sch_receiver_intent.putExtra("getId", intent_id);
                            sender = PendingIntent.getBroadcast(getActivity(), intent_id_integer, sch_receiver_intent, FLAG_UPDATE_CURRENT); // sender을 구성한다.


                            calendar.set(ch.getYear(), ch.getMonth() - 1, ch.getDay(), ch.getHour(), ch.getMinute() + 1);// 현재시간 + 1분으로 설정 -> 1분이 하루
                            calendar.set(Calendar.SECOND, 0);

                            am.set(AlarmManager.RTC, calendar.getTimeInMillis(), sender); // 알람설정
                            am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 60 * 1000, sender);
                        } else if (day_DR.equals("1")) {
                            ch.setId(cursor.getLong(cursor.getColumnIndexOrThrow(MemoContract_child.MemoEntry._ID)));
                            parent_list_data.get(1).childListData.add(ch);
                            intent_id = ch.getId();

                            intent_id_integer = toIntExact(intent_id);

                            sch_receiver_intent.putExtra("getId", intent_id);
                            sender = PendingIntent.getBroadcast(getActivity(), intent_id_integer, sch_receiver_intent, FLAG_UPDATE_CURRENT); // sender을 구성한다.


                            calendar.set(ch.getYear(), ch.getMonth() - 1, ch.getDay(), ch.getHour(), ch.getMinute() + 1);// 현재시간 + 1분으로 설정 -> 1분이 하루
                            calendar.set(Calendar.SECOND, 0);

                            am.set(AlarmManager.RTC, calendar.getTimeInMillis(), sender); // 알람설정
                        } else {
                            ch.setId(cursor.getLong(cursor.getColumnIndexOrThrow(MemoContract_child.MemoEntry._ID)));
                            parent_list_data.get(2).childListData.add(ch);
                            intent_id = ch.getId();

                            intent_id_integer = toIntExact(intent_id);

                            sch_receiver_intent.putExtra("getId", intent_id);
                            sender = PendingIntent.getBroadcast(getActivity(), intent_id_integer, sch_receiver_intent, FLAG_UPDATE_CURRENT); // sender을 구성한다.


                            calendar.set(ch.getYear(), ch.getMonth() - 1, ch.getDay(), ch.getHour(), ch.getMinute() + 1);// 현재시간 + 1분으로 설정 -> 1분이 하루
                            calendar.set(Calendar.SECOND, 0);

                            am.set(AlarmManager.RTC, calendar.getTimeInMillis(), sender); // 알람설정
                        }

                    }
                }
                Toast.makeText(getActivity(), "스케줄 id:" + Long.toString(ch.getId()), Toast.LENGTH_SHORT).show();

                cursor.close();
                db.close();
                db1.close();

                getActivity().runOnUiThread(updateUI); //실시간 UI갱신


            }
        } else if (requestCode == MEMO_REGISTER_MODIFY) {
            if (resultCode == getActivity().RESULT_OK) {
                long sId = data.getLongExtra("id", -1);
                int sYear, sMonth, sDay, sHour, sMinute;  // 선택한 날짜 시간은 24시간 기준

                title = data.getStringExtra("title");
                contents = data.getStringExtra("contents");

                sYear = data.getIntExtra("year", 2019);
                sMonth = data.getIntExtra("month", 1);
                sDay = data.getIntExtra("day", 1);
                sHour = data.getIntExtra("hour", 1);
                sMinute = data.getIntExtra("minute", 11);

                work_and_life = data.getStringExtra("work_and_life");
                scope = data.getStringExtra("scope");

                ChildListData ch = new ChildListData();
                ch.setTitle(title);
                ch.setContents(contents);
                ch.setYear(sYear);
                ch.setMonth(sMonth);
                ch.setDay(sDay);
                ch.setHour(sHour);
                ch.setMinute(sMinute);

                ch.setWork_and_life(work_and_life);
                ch.setScope(scope);
                String formatDate = sYear + "-" + sMonth + "-" + sDay + " " + sHour + ":" + sMinute;
                String day_DR = getProDay(formatDate); //두 일정날짜의 차이
                Toast.makeText(getActivity(), "날짜차이 : " + day_DR + "일", Toast.LENGTH_SHORT).show();
                // 여기까지가 객체에 넣기
                am = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE); //알람매니저 객체얻어옴
                sch_receiver_intent = new Intent(getActivity(), scheduleReceiver.class);
                intent_title = ch.getTitle();  // 등록알람의 타이틀을 얻어서 intent_title String형변수에 넣음
                sch_receiver_intent.putExtra("getTitle", intent_title);  // intent에 intent_title을 붙인다.
                calendar = Calendar.getInstance();

                //이제 DB에 넣기

                ContentValues contentValues = new ContentValues();
                contentValues.put(MemoContract_child.MemoEntry.COLUMN_NAME_TITLE, title);
                contentValues.put(MemoContract_child.MemoEntry.COLUMN_NAME_CONTENTS, contents);

                contentValues.put(MemoContract_child.MemoEntry.COLUMN_NAME_YEAR, sYear);
                contentValues.put(MemoContract_child.MemoEntry.COLUMN_NAME_MONTH, sMonth);
                contentValues.put(MemoContract_child.MemoEntry.COLUMN_NAME_DAY, sDay);
                contentValues.put(MemoContract_child.MemoEntry.COLUMN_NAME_HOUR, sHour);
                contentValues.put(MemoContract_child.MemoEntry.COLUMN_NAME_MINUTE, sMinute);

                contentValues.put(MemoContract_child.MemoEntry.COLUMN_NAME_WL, work_and_life);
                contentValues.put(MemoContract_child.MemoEntry.COLUMN_NAME_SCOPE, scope);

                if (day_DR.equals("0")) {
                    contentValues.put(MemoContract_child.MemoEntry.COLUMN_NAME_DATA_GROUP, "오늘");
                    contentValues.put(MemoContract_child.MemoEntry.COLUMN_NAME_DATA_GROUP_INDEX, "1");
                    parent_list_data.get(0).childListData.add(ch);
                    parent_list_data.get(groupMemo_child_index).childListData.remove(changeMemo_child_INDEX);

                    intent_id_integer = toIntExact(sId);

                    sch_receiver_intent.putExtra("getId", intent_id);
                    sender = PendingIntent.getBroadcast(getActivity(), intent_id_integer, sch_receiver_intent, FLAG_UPDATE_CURRENT); // sender을 구성한다.
                    am.cancel(sender);

                    calendar.set(ch.getYear(), ch.getMonth() - 1, ch.getDay(), ch.getHour(), ch.getMinute());// 현재시간 + 1일으로 설정
                    calendar.set(Calendar.SECOND, 0);
                    am.set(AlarmManager.RTC, calendar.getTimeInMillis(), sender); // 알람설정
                    am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 60 * 1000, sender);

                } else if (day_DR.equals("1")) {
                    contentValues.put(MemoContract_child.MemoEntry.COLUMN_NAME_DATA_GROUP, "내일");
                    contentValues.put(MemoContract_child.MemoEntry.COLUMN_NAME_DATA_GROUP_INDEX, "2");
                    parent_list_data.get(1).childListData.add(ch);
                    parent_list_data.get(groupMemo_child_index).childListData.remove(changeMemo_child_INDEX);

                    intent_id_integer = toIntExact(sId);

                    sch_receiver_intent.putExtra("getId", intent_id);
                    sender = PendingIntent.getBroadcast(getActivity(), intent_id_integer, sch_receiver_intent, FLAG_UPDATE_CURRENT); // sender을 구성한다.
                    am.cancel(sender);

                    calendar.set(ch.getYear(), ch.getMonth() - 1, ch.getDay(), ch.getHour(), ch.getMinute() + 1);// 현재시간 + 1일으로 설정
                    calendar.set(Calendar.SECOND, 0);
                    am.set(AlarmManager.RTC, calendar.getTimeInMillis(), sender); // 알람설정
                    am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 60 * 1000, sender);
                } else {
                    contentValues.put(MemoContract_child.MemoEntry.COLUMN_NAME_DATA_GROUP, "다가올");
                    contentValues.put(MemoContract_child.MemoEntry.COLUMN_NAME_DATA_GROUP_INDEX, "3");
                    parent_list_data.get(2).childListData.add(ch);
                    parent_list_data.get(groupMemo_child_index).childListData.remove(changeMemo_child_INDEX);

                    intent_id_integer = toIntExact(sId);

                    sch_receiver_intent.putExtra("getId", intent_id);
                    sender = PendingIntent.getBroadcast(getActivity(), intent_id_integer, sch_receiver_intent, FLAG_UPDATE_CURRENT); // sender을 구성한다.
                    am.cancel(sender);

                    calendar.set(ch.getYear(), ch.getMonth() - 1, ch.getDay(), ch.getHour(), ch.getMinute() + 1);// 현재시간 + 1일으로 설정
                    calendar.set(Calendar.SECOND, 0);
                    am.set(AlarmManager.RTC, calendar.getTimeInMillis(), sender); // 알람설정
                    am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 60 * 1000, sender);
                }

                SQLiteDatabase db = DBHelper_child.getsInstance(getActivity()).getWritableDatabase();

                //수정되면 1개수정되면 1리턴(count수), 수정안되면 0 리턴
                int count = db.update(MemoContract_child.MemoEntry.TABLE_NAME, contentValues,
                        MemoContract_child.MemoEntry._ID + " = " + sId, null);
                if (count == 0) {
                    Toast.makeText(getActivity(), "수정하지 않음", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "수정됨", Toast.LENGTH_SHORT).show();
                }
                db.close();
                //getActivity().runOnUiThread(updateUI); //실시간 UI갱신

                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.detach(this).attach(this).commit();
            }
        }
    }

    /***************onActivityResult 끝**************************/

    // 메인 플로팅버튼의 이벤트 처리함수
    public void animateFAB() { //초기의 isFabOpen = false상태이다. 플로팅버튼을 누르면 else부터 시작
        if (isFabClose) {
            expandable_ListView.setBackgroundColor(Color.WHITE);
            expandable_ListView.setEnabled(true);

            fab.startAnimation(rotate_backward);
            fab1.startAnimation(fab_close);
            fab2.startAnimation(fab_close);
            fab1_title.startAnimation(fab_close);
            fab2_title.startAnimation(fab_close);
            fab1.setClickable(false);
            fab2.setClickable(false);
            isFabClose = false;
        } else {
            expandable_ListView.setBackgroundColor(Color.GRAY);
            expandable_ListView.setEnabled(false);

            fab.startAnimation(rotate_forward); //플로팅버튼 이미지를 45도 회전시킴.
            fab1.startAnimation(fab_open); //자식 플로팅버튼을 open
            fab2.startAnimation(fab_open);
            fab1_title.startAnimation(fab_open);
            fab2_title.startAnimation(fab_open);
            fab1.setClickable(true); //클릭 가능하도록 만든다.
            fab2.setClickable(true);
            isFabClose = true;
        }
    }


    /**************** 컨텍스트 메뉴 구현 ****************/
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        ExpandableListView.ExpandableListContextMenuInfo info =
                (ExpandableListView.ExpandableListContextMenuInfo) menuInfo; //메뉴인포 생성(메뉴정보)
        int type = ExpandableListView.getPackedPositionType(info.packedPosition); //타입변수-부모인지 자식인지
        int groupPosition = ExpandableListView.getPackedPositionGroup(info.packedPosition); //여기서는 안씀
        int childPosition = ExpandableListView.getPackedPositionChild(info.packedPosition); //여기서는 안씀

        if (type == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
            // 부모 메뉴를 길게누르면 아무 반응 안하도록 한다.
        } else if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) { //자식 메뉴를 길게 누를때
            menu.add(1, 1, 1, "메모 정보"); //두개의 메뉴가 보인다.
            menu.add(1, 2, 1, "메모 삭제");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        ExpandableListView.ExpandableListContextMenuInfo info =
                (ExpandableListView.ExpandableListContextMenuInfo) item.getMenuInfo(); //메뉴인포 얻어오기
        int type = ExpandableListView.getPackedPositionType(info.packedPosition); //타입변수
        int groupPosition = ExpandableListView.getPackedPositionGroup(info.packedPosition); //부모인덱스(포지션)
        int childPosition = ExpandableListView.getPackedPositionChild(info.packedPosition); //자식인덱스
        if (type == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
            //부모는 무시한다.
        } else if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) { //자식 메뉴가 선택됬을때
            switch (item.getItemId()) {
                case 1: //메모정보를 눌렀을 때
                    changeMemo(groupPosition, childPosition); //인자 2개 넘기고 정보함수로 넘어간다.
                    return true;
                case 2: //메모삭제를 눌렀을 때
                    deleteMemo(groupPosition, childPosition); //삭제함수로 넘어간다
                    return true;
                default:
                    return super.onContextItemSelected(item);
            }
        }
        return super.onContextItemSelected(item);
    }

    public void changeMemo(int group, int child) {
        Intent Memo_register_modify_intent = new Intent(getActivity(), Memo_register_Modify.class);
        groupMemo_child_index = group;
        changeMemo_child_INDEX = child;

        long cId = parent_list_data.get(group).childListData.get(child).getId();
        String cTitle = parent_list_data.get(group).childListData.get(child).getTitle();
        String cContents = parent_list_data.get(group).childListData.get(child).getContents();
        int cYear = parent_list_data.get(group).childListData.get(child).getYear();
        int cMonth = parent_list_data.get(group).childListData.get(child).getMonth();
        int cDay = parent_list_data.get(group).childListData.get(child).getDay();
        int cHour = parent_list_data.get(group).childListData.get(child).getHour();
        int cMinute = parent_list_data.get(group).childListData.get(child).getMinute();
        String cWL = parent_list_data.get(group).childListData.get(child).getWork_and_life();
        String cScope = parent_list_data.get(group).childListData.get(child).getScope();

        Memo_register_modify_intent.putExtra("cId", cId);
        Memo_register_modify_intent.putExtra("cTitle", cTitle);
        Memo_register_modify_intent.putExtra("cContents", cContents);
        Memo_register_modify_intent.putExtra("cYear", cYear);
        Memo_register_modify_intent.putExtra("cMonth", cMonth);
        Memo_register_modify_intent.putExtra("cDay", cDay);
        Memo_register_modify_intent.putExtra("cHour", cHour);
        Memo_register_modify_intent.putExtra("cMinute", cMinute);
        Memo_register_modify_intent.putExtra("cWL", cWL);
        Memo_register_modify_intent.putExtra("cScope", cScope);

        startActivityForResult(Memo_register_modify_intent, MEMO_REGISTER_MODIFY);

        getActivity().runOnUiThread(updateUI); //실시간 UI갱신
    }


    // 메모삭제 함수
    public void deleteMemo(int group, int child) {
        // 삭제할 자식의 _ID를 얻어온다. 그 후 cId 에 저장한다.
        long cId = parent_list_data.get(group).childListData.get(child).getId();

        parent_list_data.get(group).childListData.remove(child); // 일단 ArrayList에서 일정을 삭제한다.

        // DBHelper와 SQLiteDatebase 객체를 생성한다.
        DBHelper_child dbHelper_child = DBHelper_child.getsInstance(getActivity());
        SQLiteDatabase db_child = dbHelper_child.getWritableDatabase();

        // db_child(DB)에서 [얻어온 ID]와 [DB의 ID]가 일치하는 행을 삭제한다.
        db_child.delete(MemoContract_child.MemoEntry.TABLE_NAME,
                MemoContract_child.MemoEntry._ID + " = " + cId, null);

        // 그 후 DBHelper와 SQLiteDatebase를 닫아준다.
        db_child.close();
        dbHelper_child.close();
        getActivity().runOnUiThread(updateUI); //실시간 UI갱신
    }
}
