package com.example.hanmi.lifemanager;

import android.graphics.Color;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.ArrayList;

/**
 * Created by sec on 2018-05-23.
 */

public class CalendarDot implements DayViewDecorator {

    private ArrayList<Integer> signup_date;
    private int count, flag;

    public CalendarDot(ArrayList<Integer> signup_date, int count, int flag) {
        this.signup_date=signup_date;
        this.count=count;
        this.flag=flag;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return day.getDay()==signup_date.get(count+2) && (day.getMonth()+1)==signup_date.get(count+1) && day.getYear()==signup_date.get(count);
    }

    @Override
    public void decorate(DayViewFacade view) {
        // Work와 Life에 따라 위치가 다름. Life는 왼쪽 빨간색(0), Work는 오른쪽 초록색(1)
        switch (flag){
            case 0:
                view.addSpan(new CustomSpan(Color.RED, -13));
                break;
            case 1:
                view.addSpan(new CustomSpan(Color.GREEN, 13));
                break;
        }
    }
}

