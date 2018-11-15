package com.example.hanmi.lifemanager.DB;

import android.provider.BaseColumns;

public class HangoutContract {
    private HangoutContract(){

    }
    public static class HangoutEntry implements BaseColumns {
        public static final String TABLE_NAME = "hangout_table";
        public static final String COLUMN_HANGOUT_NAME = "hangout_name";
        public static final String COLUMN_HANGOUT_MSG = "hangout_msg";
        public static final String COLUMN_HANGOUT_YEAR = "hangout_year";
        public static final String COLUMN_HANGOUT_MONTH = "hangout_month";
        public static final String COLUMN_HANGOUT_DAY = "hangout_day";
    }
}