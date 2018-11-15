package com.example.hanmi.lifemanager.DB;

import android.provider.BaseColumns;

// 계약 클래스 ,,  인스턴스화는 아니고 상수를 정의해서 가져다쓰는 둉도로 사용한다.
public final class MemoContract_parent {
    private MemoContract_parent(){


    }

    public static class MemoEntry implements BaseColumns{

        //BaseColumns 유니크 한 아이디를 제공해준다. Cursor어댑터와 연동하기 좋다.
        // 테이블  정보를 담는다.
        public static final String TABLE_NAME = "parent_list";
        public static final String COLUMN_NAME_DATA_GROUP = "group_data";
        public static final String COLUMN_NAME_INDEX = "group_index";


    }
}
