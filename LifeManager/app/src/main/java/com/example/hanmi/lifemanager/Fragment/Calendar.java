package com.example.hanmi.lifemanager.Fragment;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;

import com.example.hanmi.lifemanager.Activity.WorkLife_CalendarActivity;
import com.example.hanmi.lifemanager.Adapter.ParentAdapter_Calendar;
import com.example.hanmi.lifemanager.DB.DBHelper_child;
import com.example.hanmi.lifemanager.DB.MemoContract_child;
import com.example.hanmi.lifemanager.R;
import com.example.hanmi.lifemanager.VO.ChildListData;
import com.example.hanmi.lifemanager.VO.ParentListData;

import java.util.ArrayList;

public class Calendar extends Fragment {

    private Cursor cursor;
    private ArrayList<ParentListData> parentListData;
    private Button Bt_prev_CalendarView;
    private ExpandableListView parentListView;

    // 수정한부분
    private ArrayList<Integer> success_count_array;
    private ArrayList<Integer> fail_count_array;
    private ArrayList<Integer> continue_count_array;


    // 수정한 부분 생성과 동시에 확률값을 구함.(여기서 확률값 계산하여 db에 저장함.)
    public Calendar() {

    }

    // 수정한 부분 18.06.02
    public ParentAdapter_Calendar adapter;

    /* 실시간 갱신을 위한 run함수 */     // 수정한 부분  18.06.02
    private Runnable updateUI = new Runnable() {

        public void run() {
            Calendar.this.adapter.notifyDataSetChanged();
        }
    };

    public static Calendar newInstance() {
        Bundle args = new Bundle();
        Calendar fragment = new Calendar();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View Calendar_View = inflater.inflate(R.layout.fragment_calendar, container, false);
        initWindowOption();
        parentListView = (ExpandableListView) Calendar_View.findViewById(R.id.parentListView);
        Bt_prev_CalendarView = (Button) Calendar_View.findViewById(R.id.prev_CalendarView);
        registerForContextMenu(parentListView);  // 컨텍스트메뉴 리스트에 등록.

        Display newDisplay = getActivity().getWindowManager().getDefaultDisplay();
        int width = newDisplay.getWidth();
        parentListView.setIndicatorBounds(width - 200, width);

        // =========================== 확률 값 업데이트 부분=========================================
        int i = 0;
        int Calculated_Probability;
        success_count_array = new ArrayList<Integer>();
        fail_count_array = new ArrayList<Integer>();
        continue_count_array = new ArrayList<Integer>();

        DBHelper_child dbHelper_child_first = DBHelper_child.getsInstance(getActivity());
        SQLiteDatabase db_child_first = dbHelper_child_first.getWritableDatabase();

        // 오늘과 완료된 일정만 가져옴
        cursor = db_child_first.rawQuery("SELECT * FROM " +
                MemoContract_child.MemoEntry.TABLE_NAME + " WHERE " + MemoContract_child.MemoEntry.COLUMN_NAME_DATA_GROUP_INDEX + "= 1"
                + " OR " + MemoContract_child.MemoEntry.COLUMN_NAME_DATA_GROUP_INDEX + "= 4", null);

        // success, fail, continue 값 받아옴.
        while (cursor.moveToNext()) {
            success_count_array.add(cursor.getInt(cursor.getColumnIndexOrThrow(MemoContract_child.MemoEntry.COLUMN_NAME_SUCCESS_COUNT)));
            fail_count_array.add(cursor.getInt(cursor.getColumnIndexOrThrow(MemoContract_child.MemoEntry.COLUMN_NAME_FAIL_COUNT)));
            continue_count_array.add(cursor.getInt(cursor.getColumnIndexOrThrow(MemoContract_child.MemoEntry.COLUMN_NAME_CONTINUE)));

            // 확률값을 계산함. 확인차 성공 값과 실패 값 덧셈하여 표시. 여기서 작업 이루어짐
            if ((success_count_array.get(i) + fail_count_array.get(i)) == 0) {
                Calculated_Probability = 0;
            } else {
                Calculated_Probability = (int) (((double) success_count_array.get(i) / (double) (success_count_array.get(i) + fail_count_array.get(i))) * 100);
            }

            // 해당 id의 튜플의 확률값을 계산하여 확률값에 update함.
            db_child_first.execSQL("UPDATE " + MemoContract_child.MemoEntry.TABLE_NAME + " SET " +
                    MemoContract_child.MemoEntry.COLUMN_NAME_PROBABILITY + "= " + Calculated_Probability + " WHERE " + MemoContract_child.MemoEntry._ID + " = " +
                    cursor.getInt(cursor.getColumnIndexOrThrow(MemoContract_child.MemoEntry._ID)));
            i++;
        }
        cursor.close();
        db_child_first.close();

        // ============================================================================================

        // arrayList
        parentListData = new ArrayList<>();

        ParentListData parentListData_work = new ParentListData();
        ParentListData parentListData_life = new ParentListData();
        parentListData_work.setName("Work 관리");
        parentListData_life.setName("Life 관리");

        parentListData.add(parentListData_work);
        parentListData.add(parentListData_life);

        DBHelper_child dbHelper_child = DBHelper_child.getsInstance(getActivity());
        SQLiteDatabase db_child = dbHelper_child.getWritableDatabase();

        // 오늘과 완료된 일정만 가져옴
        cursor = db_child.rawQuery("SELECT * FROM " +
                MemoContract_child.MemoEntry.TABLE_NAME + " WHERE " + MemoContract_child.MemoEntry.COLUMN_NAME_DATA_GROUP_INDEX + "= 1"
                + " OR " + MemoContract_child.MemoEntry.COLUMN_NAME_DATA_GROUP_INDEX + "= 4", null);

        // 일정이름과 확률만 얻어옴.
        while (cursor.moveToNext()) {
            ChildListData childListData = new ChildListData();

            childListData.setId(cursor.getLong(cursor.getColumnIndexOrThrow
                    (MemoContract_child.MemoEntry._ID)));

            childListData.setTitle(cursor.getString(cursor.getColumnIndexOrThrow
                    (MemoContract_child.MemoEntry.COLUMN_NAME_TITLE)));

            childListData.setProbability(cursor.getInt(cursor.getColumnIndexOrThrow
                    (MemoContract_child.MemoEntry.COLUMN_NAME_PROBABILITY)));

            // work, life에 따라 나뉘어서 들어감
            if (cursor.getString(cursor.getColumnIndexOrThrow(MemoContract_child.MemoEntry.COLUMN_NAME_WL)).equals("Work")) {
                parentListData.get(0).childListData.add(childListData);
            } else {
                parentListData.get(1).childListData.add(childListData);
            }
        }
        db_child.close();
        cursor.close();

        // 버튼 이벤트(액티비티 이동)
        Bt_prev_CalendarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), WorkLife_CalendarActivity.class);
                startActivity(intent);
            }
        });

        // 어댑터 등록
        adapter = new ParentAdapter_Calendar(getActivity(), parentListData);
        parentListView.setAdapter(adapter);

        return Calendar_View;
    }

    private void initWindowOption() {
        getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY); // 네비게이션 바, 상단바 숨김
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        ExpandableListView.ExpandableListContextMenuInfo info =
                (ExpandableListView.ExpandableListContextMenuInfo) menuInfo;
        int type = ExpandableListView.getPackedPositionType(info.packedPosition);
        int groupPosition = ExpandableListView.getPackedPositionGroup(info.packedPosition);
        int childPosition = ExpandableListView.getPackedPositionChild(info.packedPosition);

        if (type == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
        } else if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
            menu.add(1, 1, 1, "메모삭제");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        ExpandableListView.ExpandableListContextMenuInfo info =
                (ExpandableListView.ExpandableListContextMenuInfo) item.getMenuInfo();
        int type = ExpandableListView.getPackedPositionType(info.packedPosition);
        int groupPosition = ExpandableListView.getPackedPositionGroup(info.packedPosition);
        int childPosition = ExpandableListView.getPackedPositionChild(info.packedPosition);

        if (type == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
        } else if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
            switch (item.getItemId()) {
                case 1:
                    deleteMemo(groupPosition, childPosition);
                default:
                    return super.onContextItemSelected(item);
            }
        }
        return super.onContextItemSelected(item);
    }

    // 메모삭제 함수
    public void deleteMemo(int group, int child) {

        // 삭제할 childlistdata에서 id값을 가져옴.
        long cId = parentListData.get(group).childListData.get(child).getId();

        parentListData.get(group).childListData.remove(child); // ArrayList 먼저 삭제.

        DBHelper_child dbHelper_child = DBHelper_child.getsInstance(getActivity());
        SQLiteDatabase db_child = dbHelper_child.getWritableDatabase();

        // db의 id와 일치하는 행을 삭제.
        db_child.delete(MemoContract_child.MemoEntry.TABLE_NAME,
                MemoContract_child.MemoEntry._ID + " = " + cId, null);

        db_child.close();
        dbHelper_child.close();
        getActivity().runOnUiThread(updateUI); //실시간 UI갱신
    }
}