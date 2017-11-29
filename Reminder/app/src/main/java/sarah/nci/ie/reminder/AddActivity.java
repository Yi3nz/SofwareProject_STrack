package sarah.nci.ie.reminder;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.Calendar;
import java.util.ResourceBundle;

public class AddActivity extends AppCompatActivity {

    //Subject picker
    String selectedText;
    String json;
    Button bSubject;
    //Date picker - Reference https://www.youtube.com/watch?v=hwe1abDO2Ag
    private static final String TAG = "AddActivity";
    private Button mDisplayDate;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        /*****Date picker - Reference https://www.youtube.com/watch?v=hwe1abDO2Ag*****/
        mDisplayDate = (Button) findViewById(R.id.btnDueDate);

        mDisplayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Create a calendar object
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        AddActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month +1;
                Log.d(TAG, "OnDateSet: mm/dd/yyyy: " + month + "/" + day + "/" + year);
                //Create a String date, and set textView to this date.
                date = day + "th " + month + " " + year;
                mDisplayDate.setText(date);
            }
        };

        /*****Subject picker - Open DialogList*****/
        bSubject = (Button) findViewById(R.id.btnSubject);
        bSubject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AddActivity.this, SubjectDialogActivity.class));
            }
        });

        //Obtain shared preference
        //SharedPreferences prefs = this.getSharedPreferences("sp", Context.MODE_PRIVATE); //https://stackoverflow.com/questions/3624280/how-to-use-sharedpreferences-in-android-to-store-fetch-and-edit-values
        //Get

        /*
        //Update button's text
        Intent intent = getIntent();
        selectedText = intent.getStringExtra(Int_Subject.SELECT_SUBJECT_DATA);
        bSubject.setText(selectedText);*/

    }

    //Refresh button
    public void refresh(View view){
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(AddActivity.this);
        //SharedPreferences mPrefs = getSharedPreferences("SubjectDialogActivity", MODE_PRIVATE);
        json = mPrefs.getString("MyObject", "");
        if (json != null) {
            Toast.makeText(getBaseContext(), json, Toast.LENGTH_LONG).show();
            bSubject.setText(json);
        }
    }

    //SaveButton - SaveAll
    public void saveAllClick(View v){
        //Get the tex from all fields (Pass to MainActivity)
        String caTitleText = ((EditText)findViewById(R.id.eTitle)).getText().toString();
        String caSubjectText = ((Button)findViewById(R.id.btnSubject)).getText().toString();
        String caDueDateText = date;

        if(caTitleText.equals("")){
            //Check if the user is submitting empty CA title
        }else{
            Intent intent = new Intent();
            intent.putExtra(Intent_Constants.INTENT_CA_FIELD, caTitleText);
            intent.putExtra(Intent_Constants.INTENT_SUBJECT_FIELD, caSubjectText);
            intent.putExtra(Intent_Constants.INTENT_DUEDATE_FIELD, caDueDateText);

            setResult(Intent_Constants.INTENT_RESULT_CODE, intent);
            finish();
        }
    }

    //TRYYYYY


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode ==Int_Subject.INT_RQC_SUBJECT_SELECT){

            /*
            //ubject picker - Open DialogList
            Button bSubject = (Button) findViewById(R.id.btnSubject);
            bSubject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(AddActivity.this, SubjectDialogActivity.class));
                }
            });

            //Update button's text
            Intent intent = getIntent();
            selectedText = intent.getStringExtra(Int_Subject.SELECT_SUBJECT_DATA);
            bSubject.setText(selectedText);*/
        }
    }


}
