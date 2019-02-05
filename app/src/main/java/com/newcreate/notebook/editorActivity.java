package com.newcreate.notebook;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import static com.newcreate.notebook.MainActivity.arrayAdapter;
import static com.newcreate.notebook.MainActivity.count;
import static com.newcreate.notebook.MainActivity.mydatabase;
import static com.newcreate.notebook.MainActivity.notes_list;

public class editorActivity extends AppCompatActivity {

    EditText editText;
    int noteId;
    boolean flag;
    String qry1;
    String previous;    // this will be use in cancel button



    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        String temp = (String) savedInstanceState.get("editText");
        editText.setText(temp);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("editText",editText.getText().toString());

    }



    @Override
    public void finish() {
        super.finish();

       if(flag == true)  return;

       if(previous.equals("") && !editText.getText().toString().equals(""))
         {
           notes_list.set(noteId,editText.getText().toString());
           arrayAdapter.notifyDataSetChanged();
          }

       else if(!previous.equals("") && editText.getText().toString().equals(""))
          {
              Toast.makeText(this,"empty node",Toast.LENGTH_SHORT).show();
              notes_list.set(noteId,previous);
              arrayAdapter.notifyDataSetChanged();
          }

       else if(!previous.equals("") && !editText.getText().toString().equals(""))
       {
           notes_list.set(noteId,editText.getText().toString());
           arrayAdapter.notifyDataSetChanged();
       }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor_1);

        flag = false;
        editText = findViewById(R.id.editText);
        qry1 = new String();

        Intent intent = getIntent();
        noteId = intent.getIntExtra("noteId", -1);

        editText.setText(MainActivity.notes_list.get(noteId));
        editText.setSelection(editText.getText().length());
        previous = editText.getText().toString();

    }

    public void cancelTextViewPressed(View v) {

        flag = true;
        if (previous.equals(""))
        {
            MainActivity.mydatabase.execSQL("DELETE  FROM notes_table WHERE id = " + Integer.toString((MainActivity.max_id) + 1));
            MainActivity.notes_id.remove(MainActivity.notes_id.size() - 1);
            MainActivity.notes_list.remove(MainActivity.notes_list.size() - 1);
            MainActivity.arrayAdapter.notifyDataSetChanged();
            MainActivity.countTextView.setText(Integer.toString(--count));
        }
        else
        {
            mydatabase.execSQL("UPDATE notes_table set data = '" + previous + "' WHERE id = " + MainActivity.notes_id.get(noteId));
            MainActivity.notes_list.set(noteId, previous);
            MainActivity.arrayAdapter.notifyDataSetChanged();
        }

        Toast.makeText(this, "Notes is not saved ", Toast.LENGTH_SHORT).show();
        finish();
    }

    public void saveTextViewPressed(View v) {

        flag = true;
        if (editText.getText().toString().equals("") && previous.equals(""))
            Toast.makeText(this, "Empty note can not to be created", Toast.LENGTH_SHORT).show();

        else if(editText.getText().toString().equals("") && !previous.equals(""))
            Toast.makeText(this, "Empty note can not to be  saved ", Toast.LENGTH_SHORT).show();

        else {
            mydatabase.execSQL("UPDATE notes_table set data = '" + editText.getText().toString() + "' WHERE id = " + MainActivity.notes_id.get(noteId));
            Toast.makeText(this, "Note saved successfully", Toast.LENGTH_SHORT).show();
            notes_list.set(noteId,editText.getText().toString());
            arrayAdapter.notifyDataSetChanged();
            finish();
        }

    }
}