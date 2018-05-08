package sarah.nci.ie.reminder.listItem_Dialog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import sarah.nci.ie.reminder.Activity_Main;
import sarah.nci.ie.reminder.R;

/**
 * Main pop-up dialog, occurred on list item click.
 *
 * Pop-up dialog Reference: https://www.youtube.com/watch?v=fn5OlqQuOCk - How to create a pop-up dialog
 * Place picker Reference: https://developers.google.com/places/android-api/
 *
 * Btn 1 - Start subscribing AWS mqtt && publish to Firebase.
 * Btn 2 - View device's (Raspberrypi) current location on map view.
 * Btn 3 - Define a safety zone (E.g. home, Jervis shopping center etc.) using Place picker.
 * Btn 4 - Remote control led & buzzer.
 * Btn 5 - Remove device.
 *
 * EXTRA: Compute the distance between device's (Raspberrypi) current location & defined safety zone.
 * On create: (Once listitem clicked)
 * 1. Fetch device's (RaspberryPi) current location from Firebase.
 * 2. Check if safety_zone already defined:
 *    - If yes: Grab the latest safety_zone's latitude for later usage.
 *              Grab the latest safety_zone's longitude for later usage.
 * 3. Compute the distance if safety_zone exists:
 *    - If yes: Compute and provide real-time distance changing.
 *    - If no:  Returns '-- m'. (-- meters)
 * 4. Trigger the device's (Raspberrypi) sensor if distance < 50 meters.
 *
 * EXTRA2: Compute the initial distance once safety-zone selected.
 * On btn 3's activity finished:
 * 1. Push the selected safety-zone's location to Firebase Device/specific_deviceId/SafetyZone.
 * 2. Seperate safety_zone location into latitude & longitude.
 * 3. Compute the distance between them.
 * 4. Push the latest distance to Device/distance. (Which update the initial list-item's distance.)
 */

public class D_00_MainDialog extends Activity {

    //Define the buttons
    Button btn01, btn02, btn03, btn04, btn05;

    //PlacePicker - for Dialog 03: Select Safetyzone
    int PLACE_PICKER_REQUEST = 3;

    //Retrieve the intent
    String deviceId, deviceName;
    public static final String DEVICE_NAME = "deviceName";
    public static final String DEVICE_ID = "deviceId";

    /*******************************On create START********************************/
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.d_00_main_dialog);

        //Sizing Dialog
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int)(width*.8),(int)(height*.6));

        //Get the intent from the Main activity
        Intent intent = getIntent();
        //Retrieve the particular device's id & name based on the particular clicked item
        deviceId = intent.getStringExtra(Activity_Main.DEVICE_ID);
        deviceName = intent.getStringExtra(Activity_Main.DEVICE_NAME);

        //Define the buttons on the dialog
        btn01 = (Button)findViewById(R.id.btnStartConnection);
        btn02 = (Button)findViewById(R.id.btnDeviceMapView);
        btn03 = (Button)findViewById(R.id.btnDefineSafetyZone);
        btn04 = (Button)findViewById(R.id.btnRemoteControls);
        btn05 = (Button)findViewById(R.id.btnRemoveDevice);

        //Btn01 - Start subscribing AWS mqtt && publish to Firebase.
        btn01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Create a new intent to open the 01_StartConnection
                Intent intent = new Intent();
                intent.setClass(D_00_MainDialog.this, D_01_StartConnection.class);
                //Pass the device's id & name
                intent.putExtra(DEVICE_ID, deviceId);
                intent.putExtra(DEVICE_NAME, deviceName);
                startActivity(intent);
            }
        });

        //Btn02 - View device's (Raspberrypi) current location on map view.
        btn02.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Create a new intent to open the 01_StartConnection
                Intent intent = new Intent();
                intent.setClass(D_00_MainDialog.this, D_02_DeviceCurrentLocation.class);
                //Pass the device's id & name
                intent.putExtra(DEVICE_ID, deviceId);
                intent.putExtra(DEVICE_NAME, deviceName);
                startActivity(intent);
            }
        });

        //Btn03 - Define a safety zone (E.g. home, Jervis shopping center etc.) using Place picker.
        btn03.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {// Construct an intent for the place picker

                    PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
                    Intent intent = intentBuilder.build(D_00_MainDialog.this);

                    // Start the intent by requesting a result identified by a request code.
                    startActivityForResult(intent, PLACE_PICKER_REQUEST);

                } catch (GooglePlayServicesRepairableException e) {
                } catch (GooglePlayServicesNotAvailableException e) {
                }
            }
        });

        //Btn04 - Remote control led & buzzer.
        btn04.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Create a new intent to open the 04_RemoteControl
                Intent intent = new Intent();
                intent.setClass(D_00_MainDialog.this, D_04_RemoteControl.class);
                //Put extra the name & id of the device
                intent.putExtra(DEVICE_ID, deviceId);
                intent.putExtra(DEVICE_NAME, deviceName);
                startActivity(intent);
            }
        });

        //Btn05 - Remove device.
        btn05.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(D_00_MainDialog.this, D_05_RemoveDevice.class));
            }
        });

    }

    /*-----------------------------On create end-----------------------------*/

    //Btn03 - Place Picker on activity result. (Defined the safety_zone)
    @Override
    protected void onActivityResult(int requestCode,int resultCode, Intent data) {

        if (requestCode == PLACE_PICKER_REQUEST && resultCode == Activity.RESULT_OK) {
            // The user has selected a place. Extract the name and address.
            final Place place = PlacePicker.getPlace(data, this);

            final CharSequence name = place.getName();
            final CharSequence address = place.getAddress();
            final LatLng geocoding = place.getLatLng(); //LatLng(double latitude, double longitude)

            //Seperate geocoding into latitude & longitude.
            double s_latitude = geocoding.latitude;
            double s_longitude = geocoding.longitude;

            //Firebase - Push selected safety zone.
            DatabaseReference dbInitial_SZ;

            //Push the selected place's data to Firebase Device/specific_deviceID/SafetyZone.
            dbInitial_SZ = FirebaseDatabase.getInstance().getReference("Device/" +deviceId+ "/SafetyZone/name");
            dbInitial_SZ.setValue(name);
            dbInitial_SZ = FirebaseDatabase.getInstance().getReference("Device/" +deviceId+ "/SafetyZone/address");
            dbInitial_SZ.setValue(address);
            dbInitial_SZ = FirebaseDatabase.getInstance().getReference("Device/" +deviceId+ "/SafetyZone/s_latitude");
            dbInitial_SZ.setValue(s_latitude);
            dbInitial_SZ = FirebaseDatabase.getInstance().getReference("Device/" +deviceId+ "/SafetyZone/s_longitude");
            dbInitial_SZ.setValue(s_longitude);

            //Toast for confirmation.
            Toast.makeText(this, "Added "+ address,Toast.LENGTH_LONG).show();

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }



}
