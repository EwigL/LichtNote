package com.ewiglicht.lichtnote;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class ShowActivity extends AppCompatActivity {

    private EditText showContent;
    private EditText showTitle;
    private Values value;
    DBService myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
        init();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setContentView(R.layout.activity_main);
            Intent intent = new Intent(ShowActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    public void init() {
        myDb = new DBService(this);
        Button btnCancel = findViewById(R.id.show_cancel);
        Button btnSave = findViewById(R.id.show_save);
        TextView showTime = findViewById(R.id.show_time);
        showTitle = findViewById(R.id.show_title);
        showContent = findViewById(R.id.show_content);

        Intent intent = this.getIntent();
        if (intent != null) {
            value = new Values();

            value.setTime(intent.getStringExtra(DBService.TIME));
            value.setTitle(intent.getStringExtra(DBService.TITLE));
            value.setContent(intent.getStringExtra(DBService.CONTENT));
            value.setId(Integer.valueOf(Objects.requireNonNull(intent.getStringExtra(DBService.ID))));

            showTime.setText(value.getTime());
            showTitle.setText(value.getTitle());
            showContent.setText(value.getContent());
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase db = myDb.getWritableDatabase();
                ContentValues values = new ContentValues();
                String content = showContent.getText().toString();
                String title = showTitle.getText().toString();

                values.put(DBService.TIME, getTime());
                values.put(DBService.TITLE, title);
                values.put(DBService.CONTENT, content);

                db.update(DBService.TABLE, values, DBService.ID + "=?", new String[]{value.getId().toString()});
                Toast.makeText(ShowActivity.this, "修改成功", Toast.LENGTH_LONG).show();
                db.close();
                Intent intent = new Intent(ShowActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String content = showContent.getText().toString();
                final String title = showTitle.getText().toString();
                new AlertDialog.Builder(ShowActivity.this)
                        .setTitle("提示框")
                        .setMessage("是否保存当前内容?")
                        .setPositiveButton("yes",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        SQLiteDatabase db = myDb.getWritableDatabase();
                                        ContentValues values = new ContentValues();
                                        values.put(DBService.TIME, getTime());
                                        values.put(DBService.TITLE, title);
                                        values.put(DBService.CONTENT, content);
                                        db.update(DBService.TABLE, values, DBService.ID + "=?", new String[]{value.getId().toString()});
                                        Toast.makeText(ShowActivity.this, "修改成功", Toast.LENGTH_LONG).show();
                                        db.close();
                                        finish();
                                        Intent intent = new Intent(ShowActivity.this, MainActivity.class);
                                        startActivity(intent);
                                    }
                                })
                        .setNegativeButton("no",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(ShowActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }).show();
            }
        });

    }

    String getTime() {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //获取当前时间
        Date date = new Date(System.currentTimeMillis());
        return simpleDateFormat.format(date);
    }
}
