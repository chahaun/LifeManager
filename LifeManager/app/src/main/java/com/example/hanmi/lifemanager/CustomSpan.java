package com.example.hanmi.lifemanager;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.prolificinteractive.materialcalendarview.spans.DotSpan;

// 날짜에 Dot을 그려주는 클래스.
class CustomSpan extends DotSpan{
    private int color;
    private int xOffset;
    private float radius = 10;

    CustomSpan(int color, int xOffset){
        this.color =color;
        this.xOffset = xOffset;
    }

    @Override
    public void drawBackground(Canvas canvas, Paint paint, int left, int right, int top, int baseline, int bottom, CharSequence charSequence, int start, int end, int lineNum) {
        int oldColor = paint.getColor();
        if(color!=0){
            paint.setColor(color);
        }
        int x = ((left+right)/2);
        canvas.drawCircle(x+xOffset, bottom+radius, radius, paint);
        paint.setColor(oldColor);
    }
}
