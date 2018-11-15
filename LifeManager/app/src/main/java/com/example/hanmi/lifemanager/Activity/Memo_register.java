package com.example.hanmi.lifemanager.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.example.hanmi.lifemanager.R;

public class Memo_register extends AppCompatActivity {
    static final int GET_STRING = 1;  // Time_calendar 액티비티로부터 오는 스트링이 맞는가 구별하기위한것

    RelativeLayout relativeLayout;
    Button date_button;
    Button result_button;
    EditText title,contents;
    RadioGroup work_and_life;
    RadioGroup scope;
    String saveTitle;
    String saveContents;
    String saveDate;

    int saveYear;
    int saveMonth;
    int saveDay;
    int saveHour;
    int saveMinute;

    String saveWL;
    String saveScope;

    InputMethodManager editManager;
    // 에딧텍스트의 외부를 입력시 키보드를 내리기위한 매니저

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_register);

        relativeLayout= findViewById(R.id.register_layout);
        title = findViewById(R.id.title);
        contents = findViewById(R.id.contents);
        date_button = findViewById(R.id.date);
        result_button = findViewById(R.id.result_button);
        work_and_life = (RadioGroup) findViewById(R.id.work_and_life);
        scope = (RadioGroup) findViewById(R.id.scope);
        editManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);


        //메인액티비티로 결과값 보내준다
        result_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 메모사항 기재시 전부 입력하지 않을 경우 다음으로 넘어가면 안된다.
                if (TextUtils.isEmpty(title.getText()) ||TextUtils.isEmpty(contents.getText())
                        || date_button.getText().
                        toString().equals("시간설정하기") ||
                        (work_and_life.getCheckedRadioButtonId() != R.id.radio_work &&
                                work_and_life.getCheckedRadioButtonId() != R.id.radio_life) || (
                        scope.getCheckedRadioButtonId() != R.id.radio_all &&
                                scope.getCheckedRadioButtonId() != R.id.radio_friend &&
                                scope.getCheckedRadioButtonId() != R.id.radio_noOpen)
                        ) {
                    Toast.makeText(getApplicationContext(), "모든 선택사항을 입력및 클릭해주세요"
                            , Toast.LENGTH_SHORT).show();
                    hide_Key_Board();
                    return;
                }
                hide_Key_Board();


                Intent memo_intent = new Intent();

                saveTitle = title.getText().toString();
                saveContents = contents.getText().toString();

                int id1 = work_and_life.getCheckedRadioButtonId();
                RadioButton rb1 = (RadioButton) findViewById(id1);
                saveWL = rb1.getText().toString();

                int id2 = scope.getCheckedRadioButtonId();
                RadioButton rb2 = (RadioButton) findViewById(id2);
                saveScope = rb2.getText().toString();

                memo_intent.putExtra("title", saveTitle);
                memo_intent.putExtra("contents", saveContents);

                memo_intent.putExtra("year", saveYear);
                memo_intent.putExtra("month", saveMonth);
                memo_intent.putExtra("day", saveDay);
                memo_intent.putExtra("hour", saveHour);
                memo_intent.putExtra("minute", saveMinute);

                memo_intent.putExtra("work_and_life", saveWL);
                memo_intent.putExtra("scope", saveScope);

                setResult(RESULT_OK, memo_intent);
                finish();
            }
        });

        /********************* 키보드 없애 주기 ************************/
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v!=null)
                    hide_Key_Board();
            }
        });


        date_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hide_Key_Board();
                Intent intent = new Intent(Memo_register.this, Time_Calendar.class);
                startActivityForResult(intent, GET_STRING);
            }
        });


        work_and_life.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                hide_Key_Board();
            }
        });
        scope.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                hide_Key_Board();
            }
        });
        /********************* 키보드 없애 주기 ************************/


    }


    //Time_Calendar으로부터 결과를 받아온다.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GET_STRING) {  // 리퀘스트 코드확인후
            if (resultCode == RESULT_OK) { // 리졸트 코드가 맞는가 확인역시 한다.
                date_button.setText(data.getStringExtra("passCal")); // 시간설정 버튼에 선택한 시간을 표시한다.
                saveYear = data.getIntExtra("Year",2018);
                saveMonth = data.getIntExtra("Month",11);
                saveDay = data.getIntExtra("Day",2);
                saveHour = data.getIntExtra("Hour",6);
                saveMinute = data.getIntExtra("Minute",00);

            }
        }
    }

    public void hide_Key_Board(){

        editManager.hideSoftInputFromWindow(contents.getWindowToken(),0);
    }

}
