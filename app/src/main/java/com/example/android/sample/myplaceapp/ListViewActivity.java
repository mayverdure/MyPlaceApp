package com.example.android.sample.myplaceapp;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.example.android.sample.myplaceapp.location.Place;
import com.example.android.sample.myplaceapp.location.PlaceRepository;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.List;

public class ListViewActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    private SimpleCursorAdapter adapter;
    public final static String EXTRA_MYID = "com.example.android.sample.myplaceapp.MYID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);

        Toolbar toolbar = (Toolbar)findViewById(R.id.Toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();

        String[] from = {
                LogContract.Memos.COL_TITLE,
                LogContract.Memos.COL_UPDATED
        };

        int[] to = {
                android.R.id.text1,
                android.R.id.text2
        };

        adapter = new SimpleCursorAdapter(
                this,
                android.R.layout.simple_list_item_2,
                null,
                from,
                to,
                0
        );

        ListView myListView = (ListView) findViewById(R.id.myListView);
        myListView.setAdapter(adapter);
        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ListViewActivity.this, LogActivity.class);
                intent.putExtra(EXTRA_MYID, id);
                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add) {
            Intent intent = new Intent(this, LogActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                LogContract.Memos._ID,
                LogContract.Memos.COL_TITLE,
                LogContract.Memos.COL_UPDATED
        };
        return new CursorLoader(
                this,
                LogContentProvider.CONTENT_URI,
                projection,
                null,
                null,
                LogContract.Memos.COL_UPDATED + " DESC"
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

//    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
//        mCursor = data;
//        locationDataLatitude(data);
//    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }



/*
自分で付け足したもの
 */
//    // 地図を表示する視点を操作する
//    private void locationDataLatitude(@NonNull Cursor cursor) {
//
//        // カーソルから情報を集めて、Placeのリストにする
//        List<Place> places = new ArrayList<>();
//        while (cursor.moveToNext()) {
//            places.add(PlaceRepository.cursorToPlace(cursor));
//        }
//
//        int size = places.size();
//        Place place = places.get(size);
//        double latiTude = place.getLatitude();
//        TextView textView1 = (TextView)findViewById(R.id.latitudeText);
//        textView1.setText((int) latiTude);
//        double longiTude = place.getLongitude();
//        TextView textView2 = (TextView)findViewById(R.id.longitudeText);
//        textView2.setText((int) longiTude);
//
//    }


}
