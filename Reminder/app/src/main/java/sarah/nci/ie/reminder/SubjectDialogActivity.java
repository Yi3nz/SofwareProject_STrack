package sarah.nci.ie.reminder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Reference - https://www.youtube.com/watch?v=fn5OlqQuOCk - How to create a pop-up dialog
 */

public class SubjectDialogActivity extends Activity {

    ListView listViewSubject;
    ArrayList<String> arrayListSubject;
    ArrayAdapter<String> arrayAdapterSubject;
    String subjectText;

    String selectedText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog_subject);

        //Sizing Dialog
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int)(width*.8),(int)(height*.6));

        //Create a simpleList
        listViewSubject = (ListView)findViewById(R.id.listViewSubject);
        arrayListSubject = new ArrayList<>();
        arrayAdapterSubject = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayListSubject);
        listViewSubject.setAdapter(arrayAdapterSubject);

        //OnListItemClick - Back to AddActivity with the text of the Subject button updated - Reference https://www.youtube.com/watch?v=3QHgJnPPnqQ 19.00-26.xx
        listViewSubject.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                //Get the text from the selected item
                selectedText = listViewSubject.getItemAtPosition(position).toString(); //Reference - https://stackoverflow.com/questions/9208827/how-to-extract-the-text-from-the-selected-item-on-the-listview

                /*
                //Back to Add activity
                Intent intent = new Intent();
                intent.setClass(SubjectDialogActivity.this, AddActivity.class);
                //Pass the selected String
                intent.putExtra(Int_Subject.SELECT_SUBJECT_DATA, selectedText);
                startActivityForResult(intent, Int_Subject.INT_RQC_SUBJECT_SELECT);
                */

                //Save GSon
                SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(SubjectDialogActivity.this);//getPreferences(MODE_PRIVATE);
                SharedPreferences.Editor editor = mPrefs.edit();
                editor.putString("MyObject", selectedText);
                editor.commit();
                Toast.makeText(getBaseContext(), "Selected: " + selectedText + ".", Toast.LENGTH_LONG).show();
            }
        });

        //Save the added list - Reference https://www.youtube.com/watch?v=duHKgfl21BU
        try {
            Scanner sc = new Scanner(openFileInput("Subject.txt"));
            while(sc.hasNextLine()){
                String data = sc.nextLine();
                arrayAdapterSubject.add(data);
            }
            sc.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    //Save the added list - Reference https://www.youtube.com/watch?v=duHKgfl21BU
    @Override
    public void onBackPressed() {
        try{
            PrintWriter pw = new PrintWriter(openFileOutput("Subject.txt", Context.MODE_PRIVATE));
            for(String data : arrayListSubject){
                pw.println(data);
            }
            pw.close();
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }
        finish();
    }

    //On subject + button click, Go to AddSubjectActivity
    public void addSubjectClick(View v){
        Intent intent = new Intent();
        intent.setClass(SubjectDialogActivity.this, AddSubjectActivity.class);
        startActivityForResult(intent, Int_Subject.INT_RC_SUBJECT);
    }

    //Add submitted subject to list
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Int_Subject.INT_RC_SUBJECT){
            subjectText = data.getStringExtra(Int_Subject.TV_SUBJECT);
            arrayListSubject.add(subjectText);
            arrayAdapterSubject.notifyDataSetChanged();
        }
    }
}
