package sarah.nci.ie.reminder;

/*
 * Register a new device and store it into Firebase - Reference: https://www.youtube.com/watch?v=EM2x33g4syY
 *
 * 1. Retrieve the QRcodeScanningResult through intent's extra.
 * 2. Check if the QRcodeScanningResult is a valid code.
 * 2. Retrieve the entered nickname.
 * 3. Generate a unique key (id).
 * 4. Generate a new Device by assigning associated attribute values.
 * 5. On 'register' button clicked, store it to Firebase under 'Device'.
 */

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import sarah.nci.ie.reminder.db_Firebase.Device;

public class Activity_RegisterDevice extends AppCompatActivity {

    private static final String TAG = "Registerrrr";

    //Retrieve the intent
    String qrCodeResult;

    //Define Firebase for device registering
    DatabaseReference dbDevice;
    //Define Firebase for QRCode checking
    DatabaseReference qrCodeRef;
    //Define list to store Main QRCode list from Firebase
    List<String> qrCodeList;
    List<String> registeredQrCodeList;

    //Define xml's reference
    EditText etDeviceName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Retrieve the intent result from the Activity_Main
        Intent intent = getIntent();
        qrCodeResult = intent.getStringExtra(Activity_Main.QRCODE_CONTENT);

        //Define Firebase for device registering
        dbDevice = FirebaseDatabase.getInstance().getReference("Device");
        //Define Firebase for QRCode checking
        qrCodeRef = FirebaseDatabase.getInstance().getReference("QRCode");
        //Define list to store Main QRCode list from Firebase
        qrCodeList = new ArrayList<>();
        registeredQrCodeList = new ArrayList<>();

        //Define xml's reference
        etDeviceName = (EditText)findViewById(R.id.etDeviceName);

        //Retrieve the Main_QRCode list
        qrCodeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot qr : dataSnapshot.getChildren()) {
                    //Add every qrcode to the list
                    qrCodeList.add(qr.getValue().toString());
                }
                //Check if the qrCode is valid
                checkQRCode(qrCodeResult);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    //Function - Check if the qrCode is valid
    private void checkQRCode(String qrCode){
        Boolean valid = false;

        //For every list item:
        for(int i=0; i<qrCodeList.size(); i++){
            Log.d(TAG, "Checking: '"+qrCode +"', check with '"+qrCodeList.get(i)+"'.");
            //Process to register if valid
            if(qrCode.equals(qrCodeList.get(i))){
                valid = true;
                break;
            }else if(!qrCode.equals(qrCodeList.get(i))){ //Stop if not valid.
                valid = false;
            }
        }

        if(valid==true){
            Toast.makeText(this, "Code valid.", Toast.LENGTH_LONG).show();
        }else if(valid==false){
            Toast.makeText(this, "Code not valid.", Toast.LENGTH_LONG).show();
            finish();
        }
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
