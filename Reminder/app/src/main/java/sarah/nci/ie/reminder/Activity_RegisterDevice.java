package sarah.nci.ie.reminder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class Activity_RegisterDevice extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

    }
    //SaveButton - SaveAll
    public void saveAllClick(View v){
        //Get the tex from all fields (Pass to Activity_Main)
        String deviceNickname = ((EditText)findViewById(R.id.etDeviceName)).getText().toString();
        /*
        String caTitleText = ((EditText)findViewById(R.id.eTitle)).getText().toString();
        String caSubjectText = ((Button)findViewById(R.id.btnSubject)).getText().toString();
        String caDueDateText = date;*/

        if(deviceNickname.equals("")){
            //Check if the user is submitting empty CA title
        }else{
            Intent intent = new Intent();
            intent.putExtra(Intent_Constants.INTENT_DEVICE_FIELD, deviceNickname);
            /*
            intent.putExtra(Intent_Constants.INTENT_CA_FIELD, caTitleText);
            intent.putExtra(Intent_Constants.INTENT_SUBJECT_FIELD, caSubjectText);
            intent.putExtra(Intent_Constants.INTENT_DUEDATE_FIELD, caDueDateText);*/

            setResult(Intent_Constants.INTENT_RESULT_CODE, intent);
            finish();
        }
    }

    //TRYYYYY


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Intent_Subject.INT_RQC_SUBJECT_SELECT){

            /*
            //ubject picker - Open DialogList
            Button bSubject = (Button) findViewById(R.id.btnSubject);
            bSubject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(Activity_RegisterDevice.this, Dialog_MainDialog.class));
                }
            });

            //Update button's text
            Intent intent = getIntent();
            selectedText = intent.getStringExtra(Intent_Subject.SELECT_SUBJECT_DATA);
            bSubject.setText(selectedText);*/
        }
    }


}
