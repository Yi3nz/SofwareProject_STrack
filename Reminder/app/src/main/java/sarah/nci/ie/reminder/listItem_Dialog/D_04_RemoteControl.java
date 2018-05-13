package sarah.nci.ie.reminder.listItem_Dialog;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

import sarah.nci.ie.reminder.Activity_Main;
import sarah.nci.ie.reminder.R;

/*
 * On particular button clicked, send 'on'/'off' message to Firebase.
 * To be retrieve later by the RaspberryPi to trigger its sensors status.
 */
public class D_04_RemoteControl extends AppCompatActivity {

    //Define the xml elements
    Switch swLight, swBuzzer;
    SeekBar sbLight, sbBuzzer;

    //Retrieve the intent
    String deviceId, deviceName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.d_04_remote_control);

        //Define the xml's variables
        swLight = (Switch)findViewById(R.id.swLight);
        swBuzzer = (Switch)findViewById(R.id.swBuzzer);
//        sbLight = (SeekBar)findViewById(R.id.sbLight);
//        sbBuzzer = (SeekBar)findViewById(R.id.sbBuzzer);

        //Get the intent from the D_00_MainDialogActivity
        Intent intent = getIntent();
        //Retrieve the particular device's id & name
        deviceId = intent.getStringExtra(D_00_MainDialog.DEVICE_ID);
        deviceName = intent.getStringExtra(D_00_MainDialog.DEVICE_NAME);

    }

    //Function - Switch LED
    public void triggerLED(View view){
        //Define the database reference
        DatabaseReference dbLED = FirebaseDatabase.getInstance().getReference("Device/" +deviceId+ "/manualLED");
        DatabaseReference piLED = FirebaseDatabase.getInstance().getReference("Pi/manualLED");

        //Check if led is enabled. If so send value 'on' to corresponding dweet content.
        if(swLight.isChecked()){
            dbLED.setValue("on");
            piLED.setValue("on");
            Toast.makeText(getApplicationContext(), deviceName + "'s LED on", Toast.LENGTH_SHORT).show();
        }else{ //Send 'off' if disabled.
            dbLED.setValue("off");
            piLED.setValue("off");
            Toast.makeText(getApplicationContext(), deviceName + "'s LED off", Toast.LENGTH_SHORT).show();
        }
    }

    //Function - Switch BUZZER
    public void triggerBuzzer(View view){
        //Define the database reference
        DatabaseReference dbBUZZER = FirebaseDatabase.getInstance().getReference("Device/" +deviceId+ "/manualBUZZER");
        DatabaseReference piLED = FirebaseDatabase.getInstance().getReference("Pi/manualBUZZER");

        //Check if led is enabled. If so send value 'on' to corresponding dweet content.
        if(swBuzzer.isChecked()){
            dbBUZZER.setValue("on");
            piLED.setValue("on");
            Toast.makeText(getApplicationContext(), deviceName + "'s BUZZER on", Toast.LENGTH_SHORT).show();
        }else{ //Send 'off' if disabled.
            dbBUZZER.setValue("off");
            piLED.setValue("off");
            Toast.makeText(getApplicationContext(), deviceName + "'s BUZZER off", Toast.LENGTH_SHORT).show();
        }
    }

}
