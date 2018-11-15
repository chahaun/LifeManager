package com.example.hanmi.lifemanager.DB;

import android.provider.BaseColumns;

public class MemoContract_child {

    private MemoContract_child() {


    }

    public static class MemoEntry implements BaseColumns {

        //BaseColumns 유니크 한 아이디를 제공해준다. Cursor어댑터와 연동하기 좋다.
        // 테이블  정보를 담는다.
        public static final String TABLE_NAME = "child_list";
        public static final String COLUMN_NAME_DATA_GROUP = "group_data";
        public static final String COLUMN_NAME_DATA_GROUP_INDEX = "group_data_indx";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_CONTENTS = "contents";

        public static final String COLUMN_NAME_YEAR = "year";
        public static final String COLUMN_NAME_MONTH = "month";
        public static final String COLUMN_NAME_DAY = "day";
        public static final String COLUMN_NAME_HOUR = "hour";
        public static final String COLUMN_NAME_MINUTE = "minute";

        public static final String COLUMN_NAME_WL = "work_and_life";
        public static final String COLUMN_NAME_SCOPE = "scope";
        public static final String COLUMN_NAME_PROBABILITY = "probability";

        public static final String COLUMN_NAME_SUCCESS_COUNT = "success";
        public static final String COLUMN_NAME_FAIL_COUNT = "fail";

        public static final String COLUMN_NAME_CONTINUE = "continue";

    }
}

