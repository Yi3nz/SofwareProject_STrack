package sarah.nci.ie.reminder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Reference - https://www.youtube.com/watch?v=fn5OlqQuOCk - How to create a pop-up dialog
 */

public class Dialog_MainDialog extends Activity {

    //STrack
    Button btn01, btn02, btn03, btn04;

    ListView listViewSubject;
    ArrayList<String> arrayListSubject;
    ArrayAdapter<String> arrayAdapterSubject;
    String subjectText, selectedText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_maindialog);

        //Sizing Dialog
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int)(width*.8),(int)(height*.6));

        btn01 = (Button)findViewById(R.id.btnCurrentLocation);
        btn02 = (Button)findViewById(R.id.btnDefineSafetyZone);
        btn03 = (Button)findViewById(R.id.btnRemoteControls);
        btn04 = (Button)findViewById(R.id.btnRemoveDevice);

        //Current Location on map
        btn01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Dialog_MainDialog.this, Dialog_CurrentLocation.class));
            }
        });

        //Go to Dialog_RemoteControl.java
        btn03.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Dialog_MainDialog.this, Dialog_RemoteControl.class));
            }
        });

        btn04.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Dialog_MainDialog.this, Dialog_RemoveDevice.class));
            }
        });

    }
}
