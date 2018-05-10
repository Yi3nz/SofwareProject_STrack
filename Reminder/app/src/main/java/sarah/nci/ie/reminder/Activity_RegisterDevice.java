package sarah.nci.ie.reminder;

/*
 * Register a new device and store it into Firebase - Reference: https://www.youtube.com/watch?v=EM2x33g4syY
 *
 * 1. Retrieve the QRcodeScanningResult through intent's extra.
 * 2. Retrieve the entered nickname.
 * 3. Generate a unique key (id).
 * 4. Generate a new Device by assigning associated attribute values.
 * 5. On 'register' button clicked, store it to Firebase under 'Device'.
 */

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import sarah.nci.ie.reminder.db_Firebase.Device;
import sarah.nci.ie.reminder.listItem_Dialog.D_00_MainDialog;

public class Activity_RegisterDevice extends AppCompatActivity {

    //Define Firebase
    DatabaseReference dbDevice;

    //Define xml's reference
    EditText etDeviceName;

    //Retrieve the intent
    String qrCodeResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Define Firebase
        dbDevice = FirebaseDatabase.getInstance().getReference("Device");

        //Define xml's reference
        etDeviceName = (EditText)findViewById(R.id.etDeviceName);

        //Get the intent from the Avtivity_Main
        Intent intent = getIntent();
        //Retrieve the intent result
        qrCodeResult = intent.getStringExtra(Activity_Main.QRCODE_CONTENT);
    }


    //On register button clicked
    public void registerClick(View v){
        //Get the text from the text field (Pass to Activity_Main)
        String name = etDeviceName.getText().toString().trim();

        //Check if the name is empty
        if(!TextUtils.isEmpty(name)){
            //Generate a unique string, retrieve the string and store in the String id.
            String deviceId = dbDevice.push().getKey();

            //Grab the entered device name
            Device device = new Device(deviceId, name, qrCodeResult, "Current address", "...m", "extra");

            //Store this device into the specific 'id' in the Firebase
            dbDevice.child(deviceId).setValue(device);

            //Confirm message
            Toast.makeText(this, "New device "+name+" registered.", Toast.LENGTH_LONG).show();
            finish();

        }else{
            Toast.makeText(this, "Please enter a valid name.", Toast.LENGTH_LONG).show();
        }

    }

}
