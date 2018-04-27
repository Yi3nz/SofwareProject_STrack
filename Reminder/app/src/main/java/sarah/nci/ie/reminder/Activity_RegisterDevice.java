package sarah.nci.ie.reminder;

/*
 * Register a new device and store it in Firebase.
 * Reference: https://www.youtube.com/watch?v=EM2x33g4syY
 * 1. Define the Firebase references
 * 2. On 'save' button clicked, store the data to Firebase.
 */
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import sarah.nci.ie.reminder.db_Firebase.Device;

public class Activity_RegisterDevice extends AppCompatActivity {

    //Define Firebase
    DatabaseReference dbDevice;
    EditText etDeviceName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        //Define Firebase
        dbDevice = FirebaseDatabase.getInstance().getReference("Device");
        etDeviceName = (EditText)findViewById(R.id.etDeviceName);

    }


    //SaveButton - SaveAll
    public void saveAllClick(View v){
        //Get the text from the text field (Pass to Activity_Main)
        String name = etDeviceName.getText().toString().trim();

        //Grab the entered device name
        Device device = new Device("Testing", name, "Current location",
                                    "???m", "extra");
        String id = dbDevice.push().getKey();
        dbDevice.child(id).setValue(device);
        Toast.makeText(this, "New device "+name+" added", Toast.LENGTH_LONG).show();
        finish();

    }

}
