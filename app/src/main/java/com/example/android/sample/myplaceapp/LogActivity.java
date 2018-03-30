package com.example.android.sample.myplaceapp;

import android.*;
import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.sample.myplaceapp.location.Place;
import com.example.android.sample.myplaceapp.location.PlaceRepository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LogActivity extends AppCompatActivity {
    private Uri _imageUri;

    private long memoId;

    private EditText titleText;
    private EditText bodyText;
    private TextView updatedText;
    private TextView latitudeText;
    private TextView longitudeText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        titleText = (EditText) findViewById(R.id.titleText);
        bodyText = (EditText) findViewById(R.id.bodyText);
        updatedText = (TextView) findViewById(R.id.updatedText);
        latitudeText = (TextView) findViewById(R.id.latitudeText);
        longitudeText = (TextView) findViewById(R.id.longitudeText);

        Toolbar toolbar = (Toolbar)findViewById(R.id.Toolbar);
        setSupportActionBar(toolbar);

        Intent intent = this.getIntent();
        Intent intent2 = getIntent();
        //もし取得できなかったらidに0を入れる
        memoId = intent.getLongExtra(ListViewActivity.EXTRA_MYID, 0L);

                    /*
         * 表示
         */
        Spinner spinner1 = (Spinner) findViewById(R.id.spinner1);
        // ArrayAdapter を、string-array とデフォルトのレイアウトを引数にして生成
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.type_array, android.R.layout.simple_spinner_item);
        // 選択肢が表示された時に使用するレイアウトを指定
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // スピナーにアダプターを設定
        spinner1.setAdapter(adapter);

        /*
         * イベントリスナー
         */
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // スピナー要素の文字列を取得
                String selectedItemString = (String) parent.getItemAtPosition(position);

                // 選択した要素で TextView を書き換え
//                    TextView helloWorldTextView = (TextView) findViewById(R.id.helloWorldTextView1);
//                    helloWorldTextView.setText(selectedItemString);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(LogActivity.this, "onNothingSelected が呼ばれた", Toast.LENGTH_SHORT).show();
            }
        });

        if (memoId == 0) {
            // new memo
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("New memo");
            }
            updatedText.setText("Updated: -------");
        } else {
            // show memo
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Edit memo");
            }

            Uri uri = ContentUris.withAppendedId(
                    LogContentProvider.CONTENT_URI,
                    memoId
            );
            String[] projection = {
                    LogContract.Memos.COL_TITLE,
                    LogContract.Memos.COL_BODY,
                    LogContract.Memos.COL_UPDATED,
                    LogContract.Memos.COL_LATITUDE,
                    LogContract.Memos.COL_LONGITUDE
            };
            Cursor c = getContentResolver().query(
                    uri,
                    projection,
                    LogContract.Memos._ID + " = ?",
                    new String[]{Long.toString(memoId)},
                    null
            );
            c.moveToFirst();
            titleText.setText(
                    c.getString(c.getColumnIndex(LogContract.Memos.COL_TITLE))
            );
            bodyText.setText(
                    c.getString(c.getColumnIndex(LogContract.Memos.COL_BODY))
            );
            latitudeText.setText(
                    "Updated: " +
                            c.getString(c.getColumnIndex(LogContract.Memos.COL_LATITUDE))
            );
            longitudeText.setText(
                    "Updated: " +
                            c.getString(c.getColumnIndex(LogContract.Memos.COL_LONGITUDE))
            );
            updatedText.setText(
                    "Updated: " +
                            c.getString(c.getColumnIndex(LogContract.Memos.COL_UPDATED))
            );
            c.close();


        }

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem deleteItem = menu.findItem(R.id.action_delete);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_form, menu);
        return true;
    }

    public void onCameraImageClick(View view) {
        //ストレージへのパーミッションチェックコード(if文)
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            ActivityCompat.requestPermissions(this, permissions, 2000);
            return;
        }
        //日時の取得
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");  // （1）
        Date now = new Date(System.currentTimeMillis());  // （1）
        String nowStr = dateFormat.format(now);  // （1）

        //保存画像の名前
        String fileName = "UseCameraActivityPhoto_" + nowStr + ".jpg";  // （1）

        //ContentResolverに渡す2引数を生成
        ContentValues values = new ContentValues();  // （2）

        //第1引数はデータの格納先を表すUriオブジェクト。画像ファイルを格納するストレージを表すUriオブジェクトは、定数が用意されている
        // それが「MediaStore.Images.Media.EXTERNAL_CONTENT_URI」
        //画像のファイル名を指定している。その際のキーは「MediaStore.Images.Media.TITLE」
        values.put(MediaStore.Images.Media.TITLE, fileName);  // （3）

        //JPEG画像を生成
        //キーは「MediaStore.Images.Media.MIME_TYPE」で、値は「image/jpeg」
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");  // （4）

        //ContentResolverオブジェクトがUriを生成
        ContentResolver resolver = getContentResolver();  // （5）
        _imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);  // （6）

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);  // （1）

        //IntentオブジェクトのExtra情報としてUriオブジェクトを渡す
        intent.putExtra(MediaStore.EXTRA_OUTPUT, _imageUri);  // （8）
        startActivityForResult(intent, 200);  // （9）
    }

//    //Uriを使う前のクリックイベント
//    public void onCameraImageClick(View view) {
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);  // （1）
//        startActivityForResult(intent, 200);  // （2）
//    }

    /*
    Uriで画像を大きく表示
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 200 && resultCode == RESULT_OK) {
            ImageView ivCamera = (ImageView) findViewById(R.id.ivCamera);  // （2）
            ivCamera.setImageURI(_imageUri);  // （3）
        }
    }

//    画像を保存せず、そのまま持ってきて表示・すごく小さい画像しかできない。
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if(requestCode == 200 && resultCode == RESULT_OK) {  // （1）
//            Bitmap bitmap =  data.getParcelableExtra("data");  // （2）
//            ImageView ivCamera = (ImageView) findViewById(R.id.ivCamera);  // （3)
//ImageViewのsetImageURI()メソッドにUriオブジェクトを渡すだけでその画像が表示される
//            ivCamera.setImageBitmap(bitmap);  // （3）
//        }
//    }


    private void deleteMemo() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Memo")
                .setMessage("Are you sure?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface daialog, int which) {
                        Uri uri = ContentUris.withAppendedId(
                                LogContentProvider.CONTENT_URI,
                                memoId
                        );
                        getContentResolver().delete(
                                uri,
                                LogContract.Memos._ID + " =?",
                                new String[]{Long.toString(memoId)}
                        );
                        finish();
                    }
                })
                .show();

    }

    private void saveMemo() {
        String title = titleText.getText().toString().trim();
        String body = bodyText.getText().toString().trim();
//        String latitude = latitudeText.getText().toString().trim();
//        String longitude = longitudeText.getText().toString().trim();
        Place latestPlace = PlaceRepository.getLastestPlaceInDay(this, new Date().getTime());
        double latitude = latestPlace.getLatitude();
        double longitude = latestPlace.getLongitude();

        String updated =
                new SimpleDateFormat("yyyy-MM-dd kk:mm:ss", Locale.US)
                        .format(new Date());
        if (title.isEmpty()) {
            Toast.makeText(
                    LogActivity.this,
                    "Please enter title",
                    Toast.LENGTH_LONG
            ).show();
        } else {
            ContentValues values = new ContentValues();
            values.put(LogContract.Memos.COL_TITLE, title);
            values.put(LogContract.Memos.COL_BODY, body);
            values.put(LogContract.Memos.COL_UPDATED, updated);
            values.put(LogContract.Memos.COL_LATITUDE, latitude);
            values.put(LogContract.Memos.COL_LONGITUDE, longitude);
            if (memoId == 0L) {
                // new memo
                getContentResolver().insert(
                        LogContentProvider.CONTENT_URI,
                        values
                );
            } else {
                //updated
                Uri uri = ContentUris.withAppendedId(
                        LogContentProvider.CONTENT_URI,
                        memoId
                );
                getContentResolver().update(
                        uri,
                        values,
                        LogContract.Memos._ID + " = ?",
                        new String[]{Long.toString(memoId)}
                );
            }
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                deleteMemo();
                break;
            case R.id.action_save:
                saveMemo();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onSaveButtonClicked(View view) {
        saveMemo();
    }
}
