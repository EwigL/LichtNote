package com.ewiglicht.lichtnote;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    DBService noteDB;
    private ListView notelist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        noteDB = new DBService(this);
        init();
    }

    public void init(){

        Button add = findViewById(R.id.btn_add);
        notelist = findViewById(R.id.notelist);
        List<Values> valuesList = new ArrayList<>();
        SQLiteDatabase db = noteDB.getReadableDatabase();

        Cursor cursor = db.query(DBService.TABLE,null,null,
        null,null,null,null);
        if(cursor.moveToFirst()){
            Values values;
            while (!cursor.isAfterLast()){

                values = new Values();

                values.setId(Integer.valueOf(cursor.getString(cursor.getColumnIndex(DBService.ID))));
                values.setTitle(cursor.getString(cursor.getColumnIndex(DBService.TITLE)));
                values.setContent(cursor.getString(cursor.getColumnIndex(DBService.CONTENT)));
                values.setTime(cursor.getString(cursor.getColumnIndex(DBService.TIME)));

                valuesList.add(values);
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();

        final MyBaseAdapter myBaseAdapter = new MyBaseAdapter(valuesList);
        notelist.setAdapter(myBaseAdapter);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                startActivity(intent);
                finish();
            }
        });

        notelist.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this,ShowActivity.class);
                Values values = (Values) notelist.getItemAtPosition(position);
                intent.putExtra(DBService.TITLE,values.getTitle().trim());
                intent.putExtra(DBService.CONTENT,values.getContent().trim());
                intent.putExtra(DBService.TIME,values.getTime().trim());
                intent.putExtra(DBService.ID,values.getId().toString().trim());
                startActivity(intent);
                finish();
            }
        });


        notelist.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final Values values = (Values) notelist.getItemAtPosition(position);
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("提示")
                        .setMessage("是否删除?")
                        .setPositiveButton("是",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        SQLiteDatabase db = noteDB.getWritableDatabase();
                                        db.delete(DBService.TABLE,DBService.ID+"=?",new String[]{String.valueOf(values.getId())});
                                        db.close();
                                        myBaseAdapter.removeItem(position);
                                        notelist.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                myBaseAdapter.notifyDataSetChanged();
                                            }
                                        });
                                    }
                                })
                        .setNegativeButton("否",null).show();
                return true;
            }
        });
    }

    class MyBaseAdapter extends BaseAdapter{

        private List<Values> valuesList;

        public MyBaseAdapter(List<Values> valuesList) {
            this.valuesList = valuesList;
        }

        @Override
        public int getCount() {
            if (valuesList != null && valuesList.size() > 0)
                return valuesList.size();
            else
                return 0;
        }

        @Override
        public Object getItem(int position) {
            if (valuesList != null && valuesList.size() > 0)
                return valuesList.get(position);
            else
                return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;

            if (convertView == null) {
                convertView = LayoutInflater.from(
                        getApplicationContext()).inflate(R.layout.note_item, parent,
                        false);
                viewHolder = new ViewHolder();
                viewHolder.title = convertView.findViewById(R.id.tv_title);
                viewHolder.content = convertView.findViewById(R.id.tv_content);
                viewHolder.time = convertView.findViewById(R.id.tv_time);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            String title = valuesList.get(position).getTitle();
            String content = valuesList.get(position).getContent();
            viewHolder.title.setText(title);
            viewHolder.content.setText(content);
            viewHolder.time.setText(valuesList.get(position).getTime());
            return convertView;
        }

        public void removeItem(int position){
            this.valuesList.remove(position);
        }
    }

    static class ViewHolder{
        TextView title;
        TextView content;
        TextView time;
    }
}


