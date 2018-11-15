package com.example.hanmi.lifemanager.DB;

import android.provider.BaseColumns;

/**
 * Created by sec on 2018-05-28.
 */

public final class UserContract {
    private UserContract(){

    }
    public static class UserEntry implements BaseColumns{
        public static final String TABLE_NAME = "user_table";
        public static final String COLUMN_USER_ID = "user_id";
        public static final String COLUMN_USER_PW = "user_pw";
        public static final String COLUMN_USER_NAME = "user_name";
        public static final String COLUMN_USER_SEX = "user_sex";
        public static final String COLUMN_USER_YEAR = "user_year";
        public static final String COLUMN_USER_MONTH = "user_month";
        public static final String COLUMN_USER_DAY = "user_day";
        public static final String COLUMN_USER_PHONE_NUM = "user_phone_num";
    }
}
