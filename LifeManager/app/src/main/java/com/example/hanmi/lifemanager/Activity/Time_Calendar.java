package com.example.hanmi.lifemanager.Activity;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;
import com.example.hanmi.lifemanager.R;
import java.util.Calendar;

public class Time_Calendar extends AppCompatActivity {

    static final int TIME_DIALOG_ID = 1;  // time 다이어 로그라는 것을 알려주기위한 상수값
    // 캘린더 뷰, 시간예약 버튼 , 에딧 텍스트 년월,시간
    CalendarView cal;
    Button button_time, button_passCal;
    EditText time_ym, time_h;
    String saveStr;

    private int mYear, mMonth, mDay, mHour, mMinute;  // 현재 날짜
    private int sYear, sMonth, sDay, sHour, sMinute;  // 선택한 날짜

    public Time_Calendar() {

        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

    }


    private TimePickerDialog.OnTimeSetListener onTLis = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            sHour = hourOfDay;
            sMinute = minute;

            /*

            13시가 넘으면 오후

            00시 즉 밤 12시 는 오전 12시

            12시 즉 낮 12시는 오후 12시

            그 밖은 오전 시간

             */

            if (sMonth == mMonth && sDay == mDay) {
                if (mHour > sHour || mMinute > sMinute) {
                    Toast.makeText(getApplicationContext(), "시간이 이미 지나버린 날짜입니다 수정해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            if (sHour >= 13) {
                time_h.setText("오후" + (sHour - 12) + "시" + sMinute + "분");

            } else if (sHour == 00) {
                time_h.setText("오전" + (sHour + 12) + "시" + sMinute + "분");

            } else if (sHour == 12) {
                time_h.setText("오후" + (sHour) + "시" + sMinute + "분");

            } else
                time_h.setText("오전" + sHour + "시" + sMinute + "분");

        }
    };

    // showdialog 가 호출되면  이 함수가 호출 되어지고 id 에 따라 핸들링 되어진다.
    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == TIME_DIALOG_ID)
            return new TimePickerDialog(this, onTLis, mHour, mMinute, false);
        return super.onCreateDialog(id);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.time_calendar);

        // 각각 뷰를 액티비티에 올려준다.
        cal = findViewById(R.id.calendarView);
        button_time = findViewById(R.id.button_time);
        button_passCal = findViewById(R.id.passCal);

        time_ym = findViewById(R.id.time_ym);
        time_h = findViewById(R.id.time_h);

        // 캘린더가 선택되었을때 발생할 리스너를 무명 객체로 선언하여준다.
        cal.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                sYear = year;
                sMonth = month;
                sDay = dayOfMonth;

                if (mYear > sYear || mMonth > sMonth) {
                    Toast.makeText(getApplicationContext(), "기간이 이미 지나버린 날짜입니다 수정해주세요", Toast.LENGTH_SHORT).show();
                    return;
                } else if (mYear == sYear && mMonth == sMonth) {
                    if (mDay > sDay) {
                        Toast.makeText(getApplicationContext(), "기간이 이미 지나버린 날짜입니다 수정해주세요", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                time_ym.setText(sYear + "년" + (sMonth + 1) + "월" + sDay + "일"); // 이밴트 발생시, 날짜가 클릭 되었을 때
                // 년월 에딧텍스트의 문장이 바뀐다.
                //참고로 month는 0부터 반환하므로 +1을 해준다.



            }
        });

        // 시간예약 버튼이 클릭 되어지면 showdialog를 호출한다.
        button_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(time_ym.getText())){
                    Toast.makeText(getApplicationContext(),"날짜를 먼저 기입해주세요"
                            ,Toast.LENGTH_SHORT).show();
                    return;
                }
                showDialog(TIME_DIALOG_ID);
            }
        });

        /********************* 인텐트에 담아서 호출한곳으로 다시 보내준다.*****************************/
        button_passCal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(time_h.getText()) || TextUtils.isEmpty(time_ym.getText())) {

                    Toast.makeText(getApplicationContext(), "날짜 및 시간을 모두 입력해주세요",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                saveStr = time_ym.getText().toString() + time_h.getText().toString();
                sMonth = sMonth+1;

                Intent intent = new Intent();
                intent.putExtra("passCal", saveStr);

                intent.putExtra("Year", sYear);
                intent.putExtra("Month", sMonth);
                intent.putExtra("Day", sDay);
                intent.putExtra("Hour", sHour);
                intent.putExtra("Minute", sMinute);

                setResult(RESULT_OK, intent);
                finish();
            }
        });

    }

}
