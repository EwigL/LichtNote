package com.ewiglicht.lichtnote;

import android.annotation.SuppressLint;
import android.content.ContentValues;
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

public class EditActivity extends AppCompatActivity {

    DBService myDb;
    private EditText titleEditText;
    private EditText contentEditText;
    private TextView timeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.avtivity_edit);
        init();
        if(timeTextView.getText().length()==0)
            timeTextView.setText(getTime());

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setContentView(R.layout.activity_main);
            Intent intent = new Intent(EditActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }


    private void init() {

        myDb = new DBService(this);
        titleEditText = findViewById(R.id.et_title);
        contentEditText = findViewById(R.id.et_content);
        timeTextView = findViewById(R.id.edit_time);
        Button btnCancel = findViewById(R.id.btn_cancel);
        Button btnSave = findViewById(R.id.btn_save);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SQLiteDatabase db = myDb.getWritableDatabase();
                ContentValues values = new ContentValues();

                String title= titleEditText.getText().toString();
                String content=contentEditText.getText().toString();
                String time= timeTextView.getText().toString();

               if("".equals(titleEditText.getText().toString())){
                    Toast.makeText(EditActivity.this,"标题不能为空",Toast.LENGTH_LONG).show();
                    return;
                }
                if("".equals(contentEditText.getText().toString())) {
                    Toast.makeText(EditActivity.this,"内容不能为空",Toast.LENGTH_LONG).show();
                    return;
                }
                values.put(DBService.TITLE,title);
                values.put(DBService.CONTENT,content);
                values.put(DBService.TIME,time);
                db.insert(DBService.TABLE,null,values);
                Toast.makeText(EditActivity.this,"保存成功",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(EditActivity.this,MainActivity.class);
                startActivity(intent);
                db.close();
                finish();
            }
        });
    }

    private String getTime() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        return sdf.format(date);
    }
}
