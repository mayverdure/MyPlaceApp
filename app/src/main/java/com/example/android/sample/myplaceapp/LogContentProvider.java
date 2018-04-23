package com.example.android.sample.myplaceapp;

//Authority:com.example.android.sample.myplaceapp.LogContentProvider

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class LogContentProvider extends ContentProvider {

    public static final String AUTHORITY =
            "com.example.android.sample.myplaceapp.LogContentProvider";
    public static final Uri CONTENT_URI =
            Uri.parse("content://" + AUTHORITY + "/" + LogContract.Memos.TABLE_NAME);
    //文字列をUri型に変換

    // UriMatcher
    private static final int MEMOS = 1;
    private static final int MEMO_ITEM = 2;
    private static final UriMatcher uriMatcher;
    //クラスが呼ばれたときに１回だけ実行される静的初期化ブロック
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, LogContract.Memos.TABLE_NAME, MEMOS);
        uriMatcher.addURI(AUTHORITY, LogContract.Memos.TABLE_NAME+"/#", MEMO_ITEM);
    }

    private LogOpenHelper memoOpenHelper;

    public LogContentProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        if (uriMatcher.match(uri) != MEMO_ITEM) {
            throw new IllegalArgumentException("Invalid URI: " + uri);
        }
        SQLiteDatabase db = memoOpenHelper.getWritableDatabase();
        int deletedCount = db.delete(
                LogContract.Memos.TABLE_NAME,
                selection,
                selectionArgs
        );
        getContext().getContentResolver().notifyChange(uri, null);
        return deletedCount;
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (uriMatcher.match(uri) != MEMOS) {
            throw new IllegalArgumentException("Invalid URI: " + uri);
        }
        SQLiteDatabase db = memoOpenHelper.getWritableDatabase();
        long newId = db.insert(
                LogContract.Memos.TABLE_NAME,
                null,
                values
        );
        Uri newUri = ContentUris.withAppendedId(
                LogContentProvider.CONTENT_URI,
                newId
        );
        getContext().getContentResolver().notifyChange(newUri, null);
        return newUri;
    }

    @Override
    public boolean onCreate() {
        memoOpenHelper = new LogOpenHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri,
                        String[] projection,
                        String selection,
                        String[] selectionArgs,
                        String sortOrder
    ) {
        switch (uriMatcher.match(uri)) {
            case MEMOS:
            case MEMO_ITEM:
                break;
            default:
                throw new IllegalArgumentException("Invalid URI: " + uri);
        }
        SQLiteDatabase db = memoOpenHelper.getReadableDatabase();
        Cursor c = db.query(
                LogContract.Memos.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        if (uriMatcher.match(uri) != MEMO_ITEM) {
            throw new IllegalArgumentException("Invalid URI: " + uri);
        }
        SQLiteDatabase db = memoOpenHelper.getWritableDatabase();
        int updatedCount = db.update(
                LogContract.Memos.TABLE_NAME,
                values,
                selection,
                selectionArgs
        );
        getContext().getContentResolver().notifyChange(uri, null);
        return updatedCount;
    }
}
