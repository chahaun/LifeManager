package com.example.hanmi.lifemanager.Fragment;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.hanmi.lifemanager.DB.HangoutContract;
import com.example.hanmi.lifemanager.DB.HangoutHelper;
import com.example.hanmi.lifemanager.R;

import java.util.ArrayList;

public class HangOut extends Fragment {
    ListView list;
    // 행아웃 데이터베이스에서 레코드 수만큼 리스트의 아이템을 만들기 위한 ArrayList
    public ArrayList<String> itemCount = new ArrayList<>();

    // 사용자 프로필 사진 저장하는 배열
    Integer[] images = {
            R.drawable.takgu,
            R.drawable.cat,
            R.drawable.tanos,
            R.drawable.yoo,
            R.drawable.park,
            R.drawable.jung,
            R.drawable.yang
    };

    public int year, month, day, hour, minute;
    private int mYear, mMonth, mDay, mHour, mMinute;

    // 현재시간을 구하여 변수에저장.
    public HangOut() {
        final java.util.Calendar c = java.util.Calendar.getInstance();
        mYear = c.get(java.util.Calendar.YEAR);
        mMonth = c.get(java.util.Calendar.MONTH);
        mDay = c.get(java.util.Calendar.DAY_OF_MONTH);
        mHour = c.get(java.util.Calendar.HOUR_OF_DAY);
        mMinute = c.get(java.util.Calendar.MONTH);
    }

    public static HangOut newInstance() {
        Bundle args = new Bundle();

        HangOut fragment = new HangOut();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View HangOut_View = inflater.inflate(R.layout.fragment_hang_out, container, false);
        initWindowOption();
        CustomList adapter = new CustomList(getActivity());
        list = (ListView) HangOut_View.findViewById(R.id.list);
        list.setAdapter(adapter);
        // 행아웃 데이터베이스에서 레코드 수만큼 리스트의 아이템을 동적으로 만들 수 있게 한다.
        HangoutHelper hangoutHelper = HangoutHelper.getsInstance(getActivity());
        Cursor cursor = hangoutHelper.getReadableDatabase()
                .query(HangoutContract.HangoutEntry.TABLE_NAME,
                        null, null, null, null, null, HangoutContract.HangoutEntry._ID);
        while (cursor.moveToNext()) itemCount.add("");
        return HangOut_View;
    }

    private void initWindowOption() {
        getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY); // 네비게이션 바, 상단바 숨김
    }

    public class CustomList extends ArrayAdapter<String> {
        private final Activity context;

        public CustomList(Activity context) {
            super(context, R.layout.hanout_custom, itemCount);
            this.context = context;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View View, @NonNull ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            View rowView = inflater.inflate(R.layout.hanout_custom, null, true);
            ImageView sns_imageView = (ImageView) rowView.findViewById(R.id.sns_image);
            TextView sns_Name = (TextView) rowView.findViewById(R.id.sns_name);
            TextView sns_Msg = (TextView) rowView.findViewById(R.id.sns_message);
            TextView sns_Date = (TextView) rowView.findViewById(R.id.sns_date);
            // 데이터베이스에서 데이터를 가져와 리스트에 직접 뿌려준다.
            HangoutHelper hangoutHelper = HangoutHelper.getsInstance(getActivity());
            SQLiteDatabase db = HangoutHelper.getsInstance(getActivity()).getReadableDatabase();
            Cursor cursor = hangoutHelper.getReadableDatabase()
                    .query(HangoutContract.HangoutEntry.TABLE_NAME,
                            null, null, null, null, null, HangoutContract.HangoutEntry._ID);
            for (int i = 0; i < position + 1; i++)
                cursor.moveToNext();
            sns_Name.setText("Name: " + cursor.getString(cursor.getColumnIndexOrThrow(HangoutContract.HangoutEntry.COLUMN_HANGOUT_NAME)));
            sns_imageView.setImageResource(images[position]);
            sns_Msg.setText("Msg: " + cursor.getString(cursor.getColumnIndexOrThrow(HangoutContract.HangoutEntry.COLUMN_HANGOUT_MSG)));
            sns_Date.setText(cursor.getString(cursor.getColumnIndexOrThrow(HangoutContract.HangoutEntry.COLUMN_HANGOUT_YEAR))
                    + "."
                    + cursor.getString(cursor.getColumnIndexOrThrow(HangoutContract.HangoutEntry.COLUMN_HANGOUT_MONTH))
                    +
                    "."
                    + cursor.getString(cursor.getColumnIndexOrThrow(HangoutContract.HangoutEntry.COLUMN_HANGOUT_DAY))
                    + "");

            return rowView;
        }
    }

}