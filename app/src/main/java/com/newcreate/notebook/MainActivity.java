package com.newcreate.notebook;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    static SQLiteDatabase mydatabase;
    Cursor cursor;
    ListView listView;
    static ArrayAdapter arrayAdapter;
    static ArrayList<String> notes_list;
    static ArrayList<Integer> notes_id;
    static int count;
    static TextView countTextView;
    static int max_id=0;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.deleteAll)
        {
            new AlertDialog.Builder(MainActivity.this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Delete Note")
                    .setMessage("Data of notes can not to be retrieved")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int j)
                        {

                            notes_id.clear();
                            notes_list.clear();
                            count = 0;
                            mydatabase.execSQL("DELETE FROM notes_table");
                            arrayAdapter.notifyDataSetChanged();
                            countTextView.setText(Integer.toString(count));
                        }
                    })
                    .setNegativeButton("No",null)
                    .show();
        }

        return true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);
        notes_list = new ArrayList<>();
        notes_id = new ArrayList<>();

        count = 0;
        countTextView = findViewById(R.id.countTextView);

        toolbar = findViewById(R.id.topToolbar);
        setSupportActionBar(toolbar);

        mydatabase = this.openOrCreateDatabase("Notes_database",MODE_PRIVATE,null);

        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS notes_table (id INT NOT NULL PRIMARY KEY , data VARCHAR)");

        checkMaxId();

        cursor = mydatabase.rawQuery("SELECT * FROM notes_table",null);
        cursor.moveToFirst();

        while(cursor != null && !cursor.isAfterLast())
        {
            if(cursor.getString(1).equals(""))
            {
                mydatabase.execSQL("DELETE  FROM notes_table WHERE id = "+ Integer.toString (cursor.getInt(0)));
                cursor.moveToNext();
                continue;
            }
            count++;
            notes_list.add(cursor.getString(1));
            notes_id.add(cursor.getInt(0));
            cursor.moveToNext();
        }

        max_id = count+1;
        countTextView.setText(String.valueOf(count));

        arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,notes_list);
        listView.setAdapter(arrayAdapter);

        //  click on list view


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                Intent intent = new Intent(MainActivity.this,editorActivity.class);
                intent.putExtra("noteId",i);
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {

                new AlertDialog.Builder(MainActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Delete Note")
                        .setMessage("Data of notes can not to be retrieved")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int j)
                            {

                                String qry = "DELETE FROM notes_table WHERE id = "+ Integer.toString(notes_id.get(i));
                                mydatabase.execSQL(qry);
                                notes_list.remove(i);
                                notes_id.remove(i);
                                count--;
                                arrayAdapter.notifyDataSetChanged();
                                countTextView.setText(Integer.toString(count));
                            }
                        })
                        .setNegativeButton("No",null)
                        .show();

                return true;
            }
        });
    }


    public void addNote(View v)
    {
        checkMaxId();
        notes_id.add(max_id+1);
        notes_list.add("");
        mydatabase.execSQL("insert into notes_table values("+(max_id+1)+",'')");
        Intent intent = new Intent(this,editorActivity.class);
        intent.putExtra("noteId",notes_id.size()-1);
        count++;
        countTextView.setText(Integer.toString(count));
        startActivity(intent);
    }

    public void checkMaxId()
    {
        cursor = mydatabase.rawQuery("select max(id) from notes_table",null);
        cursor.moveToNext();
        max_id = cursor.getInt(0);
    }
}
