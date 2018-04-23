package com.example.android.sample.myplaceapp;

import android.provider.BaseColumns;

/**
 * Created by 30032758 on 2018/03/27.
 */

public final class LogContract {

    public LogContract() {}

    public static abstract class Memos implements BaseColumns {
        public static final String TABLE_NAME = "memos";
        public static final String COL_TITLE = "title";
        public static final String COL_BODY = "body";
        // 緯度
        public static final String COL_LATITUDE = "latitude";
        // 経度
        public static final String COL_LONGITUDE = "longitude";
        public static final String COL_CREATE = "created";
        public static final String COL_UPDATED = "updated";
    }

}
