package com.example.hanmi.lifemanager.Activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.hanmi.lifemanager.R;


public class intro_activity extends Activity {
    private ProgressBar progressBar;    //프로그래스바 변수선언

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro_activity);
        TextView intro1 = (TextView) findViewById(R.id.intro1);
        TextView intro2 = (TextView) findViewById(R.id.intro2);
        Typeface face = Typeface.createFromAsset(getAssets(), "ssang.ttf");
        intro1.setTypeface(face); //외부폰트 적용
        intro2.setTypeface(face);

        progressBar = new ProgressBar(this); //프로그래스바 객체생성
        // progressBar.setBackgroundColor(0xFF5500); //배경색
        progressBar.getIndeterminateDrawable().setColorFilter(Color.rgb(0, 82, 99), PorterDuff.Mode.MULTIPLY);
        progressBar.setX(dp2px(150)); //프로그래스바 x위치
        progressBar.setY(dp2px(250)); //프로그래스바 y위치
        progressBar.setForegroundGravity(Gravity.CENTER);

        //프로그래스바를 xml파일로 안만들고 자바로 만들기때문에 이렇게 생성
        //this.addContentView(progressBar, new ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT));

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(intro_activity.this, Login.class);
                startActivity(intent);
                finish();
            }
        }, 2000);
    }

    public int dp2px(int dp) {
        DisplayMetrics displayMetrics = getApplicationContext().getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    public int px2dp(int px) {
        DisplayMetrics displayMetrics = getApplicationContext().getResources().getDisplayMetrics();
        int dp = Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return dp;
    }
}
