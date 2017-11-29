package sarah.nci.ie.reminder;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

/**
 * Created by User on 11/20/2017.
 */

public class AddSubjectActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_subject);
    }

    //SaveButton
    public void saveSubjectClick(View v){
        String textSubject = ((EditText)findViewById(R.id.eSubjectTitle)).getText().toString();
        if(textSubject.equals("")){

        }else{
            Intent intent = new Intent();
            intent.putExtra(Int_Subject.TV_SUBJECT, textSubject);
            setResult(Int_Subject.INTENT_RESULT_CODE_SUBJECT, intent);
            finish();
        }
    }
}
