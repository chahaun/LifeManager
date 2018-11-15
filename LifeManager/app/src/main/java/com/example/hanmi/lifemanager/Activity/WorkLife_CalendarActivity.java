package com.example.hanmi.lifemanager.Activity;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hanmi.lifemanager.CalendarDot;
import com.example.hanmi.lifemanager.DB.DBHelper_child;
import com.example.hanmi.lifemanager.DB.MemoContract_child;
import com.example.hanmi.lifemanager.DB.UserContract;
import com.example.hanmi.lifemanager.DB.UserHelper;
import com.example.hanmi.lifemanager.R;
import com.example.hanmi.lifemanager.VO.ChildListData;
import com.example.hanmi.lifemanager.VO.ParentListData;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.util.ArrayList;
import java.util.Calendar;

public class WorkLife_CalendarActivity extends AppCompatActivity {
    private Cursor cursor;

    private ArrayList<Integer> signup_life_date;      // life 기록된 일정.
    private ArrayList<Integer> signup_work_date;      // work 기록된 일정.
    private ArrayList<Integer> signup_schedule_date; // life+work 기록된 일정.
    private ArrayList<String> signup_schedule_title; //life+work 기록된 일정 제목.

    String[] titles;
    private int position = 0;
    public int year, month, day, hour, minute;
    private int mYear, mMonth, mDay, mHour, mMinute;

    public WorkLife_CalendarActivity(){
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH)+1;
        mDay = c.get(Calendar.DAY_OF_MONTH);
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        // 등록된 날짜 추가.
        signup_life_date = new ArrayList<Integer>();
        signup_work_date = new ArrayList<Integer>();
        signup_schedule_date = new ArrayList<Integer>();
        signup_schedule_title = new ArrayList<String>();

        // 모든 데이터 받아옴.
        Cursor cursor = getDBChildCursor();

        // DB에서 모든 일정의 이름, 연,월,일만 받아옴.
        while(cursor.moveToNext()){
            if(cursor.getString(cursor.getColumnIndexOrThrow(MemoContract_child.MemoEntry.COLUMN_NAME_WL)).equals("Work")){
                signup_work_date.add(cursor.getInt(cursor.getColumnIndexOrThrow(MemoContract_child.MemoEntry.COLUMN_NAME_YEAR)));
                signup_work_date.add(cursor.getInt(cursor.getColumnIndexOrThrow(MemoContract_child.MemoEntry.COLUMN_NAME_MONTH)));
                signup_work_date.add(cursor.getInt(cursor.getColumnIndexOrThrow(MemoContract_child.MemoEntry.COLUMN_NAME_DAY)));
            }else{
                signup_life_date.add(cursor.getInt(cursor.getColumnIndexOrThrow(MemoContract_child.MemoEntry.COLUMN_NAME_YEAR)));
                signup_life_date.add(cursor.getInt(cursor.getColumnIndexOrThrow(MemoContract_child.MemoEntry.COLUMN_NAME_MONTH)));
                signup_life_date.add(cursor.getInt(cursor.getColumnIndexOrThrow(MemoContract_child.MemoEntry.COLUMN_NAME_DAY)));
            }
            signup_schedule_date.add(cursor.getInt(cursor.getColumnIndexOrThrow(MemoContract_child.MemoEntry.COLUMN_NAME_YEAR)));
            signup_schedule_date.add(cursor.getInt(cursor.getColumnIndexOrThrow(MemoContract_child.MemoEntry.COLUMN_NAME_MONTH)));
            signup_schedule_date.add(cursor.getInt(cursor.getColumnIndexOrThrow(MemoContract_child.MemoEntry.COLUMN_NAME_DAY)));

            signup_schedule_title.add(cursor.getString(cursor.getColumnIndexOrThrow(MemoContract_child.MemoEntry.COLUMN_NAME_TITLE)));
        }

        titles = new String[signup_schedule_date.size()/3];

        // 동적으로 일정을 추가 한 만큼 리스트가 늘어남.
        for(int i =0;i<signup_schedule_title.size();i++){
            titles[i]=signup_schedule_title.get(i);
        }
    }

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.worklife_calendar_activity);

        final MaterialCalendarView calendarView= (MaterialCalendarView)findViewById(R.id.calendar_view);
        final CustomList adapter = new CustomList(WorkLife_CalendarActivity.this);
        final ListView list = (ListView)findViewById(R.id.life_CalendarList);
        adapter.notifyDataSetChanged();
        list.setAdapter(adapter);

        calendarView.setShowOtherDates(MaterialCalendarView.SHOW_OTHER_MONTHS);          // 범위 밖의 다른 달의 날짜도 표시.
        calendarView.setSelectionColor(Color.parseColor("#66CC66"));           // 선택된 날짜 background 색상 값 변경.

        // 날짜의 색깔 속성 변경
        calendarView.addDecorator(new DayViewDecorator() {
            @Override

            // 일요일인 날짜만 추출
            public boolean shouldDecorate(CalendarDay day) {
                return day.getCalendar().get(Calendar.DAY_OF_WEEK)== Calendar.SUNDAY;
            }

            // 일요일인 날짜의 색깔 속성 변경
            @Override
            public void decorate(DayViewFacade view) {
                view.addSpan(new ForegroundColorSpan(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent)));
            }
        });

        // 날짜의 색깔 속성 변경
        calendarView.addDecorator(new DayViewDecorator() {
            @Override

            // 토요일인 날짜만 추출
            public boolean shouldDecorate(CalendarDay day) {
                return   day.getCalendar().get(Calendar.DAY_OF_WEEK)== Calendar.SATURDAY;
            }

            // 토요일인 날짜의 색깔 속성 변경
            @Override
            public void decorate(DayViewFacade view) {
                view.addSpan(new ForegroundColorSpan(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark)));
            }
        });

        // 날짜의 색깔 속성 변경
        calendarView.addDecorator(new DayViewDecorator() {
            @Override

            // 오늘인 날짜만 추출
            public boolean shouldDecorate(CalendarDay day) {

                return day.getDay()==mDay && (day.getMonth()+1)==mMonth && day.getYear()==mYear;
            }

            // 오늘인 날짜의 색깔 속성 변경
            @Override
            public void decorate(DayViewFacade view) {
                view.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle));
            }
        });

        // 등록된 life 일정 Dot으로 표시.
        for(int i =0;i<signup_life_date.size()/3;i++){
            calendarView.addDecorator(new CalendarDot(signup_life_date, i*3, 0));
        }

        //등록된 life 일정 Dot으로 표시.
        for(int i =0;i<signup_work_date.size()/3;i++){
            calendarView.addDecorator(new CalendarDot(signup_work_date, i*3, 1));
        }

        // 리스트뷰 클릭 이벤트
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            // 클릭 시 해당 일정에 관한 리스틀 포커싱됨.
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                list.setSelection(position); // 특정 position으로 이동.
                Toast.makeText(WorkLife_CalendarActivity.this, ""+position+"", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public class CustomList extends ArrayAdapter<String> {
        private final Activity context;

        public CustomList(Activity context){
            super(context, R.layout.calendar_list_item, titles);
            this.context=context;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            View rowView = inflater.inflate(R.layout.calendar_list_item, null, true);
            TextView tv1 = (TextView)rowView.findViewById(R.id.textview_1);
            TextView tv2 = (TextView)rowView.findViewById(R.id.textview_2);
            tv1.setText(" "+titles[position]);
            tv2.setText("기간 "+ signup_schedule_date.get(position*3)+"-"+signup_schedule_date.get((position*3)+1)+"-"+signup_schedule_date.get((position*3)+2));
            return rowView;
        }
    }

    private Cursor getDBChildCursor() {
        DBHelper_child dbHelper_child = DBHelper_child.getsInstance(this);

        // 데이터베이스를 가져옴. 연, 월, 일 순으로 정렬.
        return dbHelper_child.getReadableDatabase().rawQuery("SELECT * FROM "+
                MemoContract_child.MemoEntry.TABLE_NAME+" ORDER BY "+
                MemoContract_child.MemoEntry.COLUMN_NAME_YEAR + " ASC,"+
                MemoContract_child.MemoEntry.COLUMN_NAME_MONTH + " ASC,"+
                MemoContract_child.MemoEntry.COLUMN_NAME_DAY+" ASC", null);

    }
}
