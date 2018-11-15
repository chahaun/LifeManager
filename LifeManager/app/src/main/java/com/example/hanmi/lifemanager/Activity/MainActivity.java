package com.example.hanmi.lifemanager.Activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.transition.Scene;
import android.view.Surface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.hanmi.lifemanager.Fragment.Calendar;
import com.example.hanmi.lifemanager.Fragment.HangOut;
import com.example.hanmi.lifemanager.Fragment.Schedule;
import com.example.hanmi.lifemanager.R;

public class MainActivity extends AppCompatActivity {

    private static int PAGE_NUMBER = 3;
    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initWindowOption(); //초기 전체화면으로 보이도록 하는 시스템 옵션메소드
        setContentView(R.layout.activity_main);

        TestPagerAdapter mTestPagerAdapter = new TestPagerAdapter(getSupportFragmentManager());
        ViewPager mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPager.setAdapter((mTestPagerAdapter));

        TabLayout mTab = (TabLayout) findViewById(R.id.tabs);
        mTab.setupWithViewPager(mViewPager);
    }

    private void initWindowOption() {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE); //타이틀 바 제거
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY); // 네비게이션 바, 상단바 숨김
    }

    public class TestPagerAdapter extends FragmentPagerAdapter {


        public TestPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "일정";
                case 1:
                    return "행아웃";
                case 2:
                    return "달력";
                default:
                    return null;
            }
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return Schedule.newInstance();
                case 1:
                    return HangOut.newInstance();
                case 2:
                    return Calendar.newInstance();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return PAGE_NUMBER;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

}
