package sarah.nci.ie.reminder;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    //Custom ListView declarations
    private static final String TAG = "MainActivity";
    String caTitleText, caSubjectText, caDueDateText;

    ListView listView;
    ArrayList<CA> caList;
    CAListAdapter adp;
    int position;

    //Actionbar - Reference - https://www.journaldev.com/9357/android-actionbar-example-tutorial
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { switch(item.getItemId()) {
        case R.id.photo:
            Intent i = new Intent(this, Action_Photo.class);
            this.startActivity(i);
            return(true);
        case R.id.gallery:
            Intent i2 = new Intent();
            i2.setClass(MainActivity.this, Action_Gallery.class);
            this.startActivity(i2);
            return(true);
        case R.id.exit:
            finish();
            return(true);
    }
        return(super.onOptionsItemSelected(item));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Custom ListView
        Log.d(TAG, "onCreate: Started.");

        listView = (ListView) findViewById(R.id.listView);
        caList = new ArrayList<>();
        adp = new CAListAdapter(this, R.layout.activity_adapter_view, caList);
        listView.setAdapter(adp);

        //Demo output
        CA ca1 = new CA("Subject", "Demo - CA Title", "Due date");
        CA ca2 = new CA("Click me to edit", "Click '+' to add new", "xxx date");
        caList.add(ca1);
        caList.add(ca2);

        //OnItemClick - Go to EditActivity
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, EditActivity.class);
                intent.putExtra(Intent_Constants.INTENT_CA_DATA, caList.get(position).toString());
                intent.putExtra(Intent_Constants.INTENT_ITEM_POSITION, position);
                startActivityForResult(intent, Intent_Constants.INTENT_REQUEST_CODE_TWO);
            }
        });

        /*Read and display the saved file - Reference https://www.youtube.com/watch?v=duHKgfl21BU
        try{
            Scanner sc = new Scanner(openFileInput("Todo.txt"));
            while(sc.hasNextLine()){
                String data = sc.nextLine();
                arrayAdapter.add(data);
            }
            sc.close();
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }*/

    }

    /*Saving the added list - Reference https://www.youtube.com/watch?v=duHKgfl21BU
    @Override
    public void onBackPressed() {
        try{
            PrintWriter pw = new PrintWriter(openFileOutput("Todo.txt", Context.MODE_PRIVATE));
            for(String data : arrayListAssigment){
                pw.println(data);
            }
            pw.close();
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }
        finish();
    }*/

    //FloatButton click - Go to AddActivity
    public void addClick(View v){
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, AddActivity.class);
        startActivityForResult(intent, Intent_Constants.INTENT_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Intent_Constants.INTENT_REQUEST_CODE){
            //Get the string from AddActivity
            caTitleText = data.getStringExtra(Intent_Constants.INTENT_CA_FIELD);
            caSubjectText = data.getStringExtra(Intent_Constants.INTENT_SUBJECT_FIELD);
            caDueDateText = data.getStringExtra(Intent_Constants.INTENT_DUEDATE_FIELD);
            //Create new item in ArrayList<CA> on click
            CA newCA = new CA(caSubjectText, caTitleText, caDueDateText);
            caList.add(newCA);
            adp.notifyDataSetChanged();
        }
        else if(resultCode == Intent_Constants.INTENT_REQUEST_CODE_TWO){ //For single item click - after edit
            caTitleText = data.getStringExtra(Intent_Constants.INTENT_CHANGED_CA);

            position = data.getIntExtra(Intent_Constants.INTENT_ITEM_POSITION, -1);
            //Create new
            CA newChangedCA = new CA(caSubjectText, caTitleText, caDueDateText);
            caList.remove(position);
            caList.add(position, newChangedCA);
            adp.notifyDataSetChanged();
        }
    }
}