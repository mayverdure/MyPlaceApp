package com.example.android.sample.myplaceapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by 30032758 on 2018/03/27.
 */

public class LogOpenHelper extends SQLiteOpenHelper {


    public static final String DB_NAME = "myapp.db";
    public static final int DB_VERSION = 1;

    public static final String CREATE_TABLE =
            "create table memos (" +
                    "_id integer primary key autoincrement, " +
                    "title text," +
                    "body text," +
                    "latitude double" +
                    "longitude double" +
                    "created datetime default current_timestamp, " +
                    "updated datetime default current_timestamp)";
    public static final String INIT_TABLE =
            "insert into memos (title, body, latitude, longitude) values " +
                    "('t1', 'b1', '0.0', '0.0'), " +
                    "('t2', 'b2', '0.0', '0.0'), " +
                    "('t3', 'b3', '0.0', '0.0')";
    public static final String DROP_TABLE =
            "drop table if exists " + LogContract.Memos.TABLE_NAME;

    //コンストラクターを作成
    public LogOpenHelper(Context c) {
        super(c, DB_NAME, null, DB_VERSION);
    }

    // テーブルを作成するメソッドの定義
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
        db.execSQL(INIT_TABLE);
    }


    // データベースをアップグレードするメソッドの定義
    @Override
    public void onUpgrade( SQLiteDatabase db, int oldVersion, int newVersion) {
        // 古いバージョンのテーブルが存在する場合はこれを削除
        db.execSQL(DROP_TABLE);
        onCreate(db);
    }

}
