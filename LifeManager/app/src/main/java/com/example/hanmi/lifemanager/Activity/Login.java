package com.example.hanmi.lifemanager.Activity;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.hanmi.lifemanager.Activity.MainActivity;
import com.example.hanmi.lifemanager.DB.UserContract;
import com.example.hanmi.lifemanager.DB.UserHelper;
import com.example.hanmi.lifemanager.R;

public class Login extends AppCompatActivity {
    RelativeLayout relativeLayout;
    InputMethodManager editManager;
    EditText EmailInput, PasswordInput;
    CheckBox cb1;
    String Data_ID, Data_PW;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        relativeLayout = findViewById(R.id.loginLayout);
        editManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        Button bt_login = (Button)findViewById(R.id.loginButton);
        Button bt_signup = (Button)findViewById(R.id.signupButton);
        EmailInput = (EditText)findViewById(R.id.emailInput);
        PasswordInput = (EditText)findViewById(R.id.passwordInput);
        cb1 = (CheckBox)findViewById(R.id.checkBox);

        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor cursor = getUserCursor();
                String input_id = EmailInput.getText().toString();
                String input_pw = PasswordInput.getText().toString();
                int i=0; // 로그인 성공 유무 제어변수

                // db에 저장된 id와 pw를 순차탐색 비교하여 로그인 성공 및 실패
                while(cursor.moveToNext()){
                    Data_ID = cursor.getString(cursor.getColumnIndex("user_id"));
                    Data_PW = cursor.getString(cursor.getColumnIndex("user_pw"));

                    if(input_id.equals(Data_ID)&&input_pw.equals(Data_PW)){
                        i=1; // 성공할 경우 1로 저장.
                        Toast.makeText(Login.this, "로그인 성공!!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Login.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
                cursor.close();
                // 예외처리. while문에서 아무런 변화가없으면 데이터 찾지 못한것.
                if(i==0){
                    Toast.makeText(Login.this, "로그인 실패!!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        bt_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(Login.this, SignUp.class);
                startActivity(intent1);
            }
        });

        // 레이아웃을 누를 시 키보드 숨기기.
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v!=null)
                    hide_Key_Board();
            }
        });
    }

    private Cursor getUserCursor() {
        UserHelper dbHelper = UserHelper.getsInstance(this);
        // 데이터베이스를 가져옴. 모든 데이터를 받고싶으면 모두 다 null 값.
        return dbHelper.getReadableDatabase()
                .query(UserContract.UserEntry.TABLE_NAME,
                        null, null, null, null, null, UserContract.UserEntry._ID +
                                " DESC");
    }

    // EditText에 키보드 바로 띄우지 않기
    @Override
    protected void onResume() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        super.onResume();
    }

    public void hide_Key_Board(){
        // Edit_ID와 같은(EditText)의 뷰들도 포함.
        editManager.hideSoftInputFromWindow(EmailInput.getWindowToken(),0);
    }
}
