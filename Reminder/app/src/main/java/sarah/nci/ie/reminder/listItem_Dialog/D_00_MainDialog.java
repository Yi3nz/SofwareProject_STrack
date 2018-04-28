package sarah.nci.ie.reminder.listItem_Dialog;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

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
 * 1. Fetch device's (RaspberryPi) current location from Firebase.
 * 2. Seperate safety_zone location into latitude & longitude.
 * 3. Compute the distance between them.
 * 4. Push the latest distance to Device/distance. (Which update the initial list-item's distance.)
 */

public class D_00_MainDialog extends Activity {

    //Define the buttons
    Button btn01, btn02, btn03, btn04, btn05;

    //PlacePicker
    int PLACE_PICKER_REQUEST = 1;

    //Firebase - Push selected safety zone.
    DatabaseReference dbDevice;

    //Firebase - Fetch device's CurrentLocation.
    DatabaseReference databaseLocations;
    String value = null;
    String latitude, longtitude, utc_time;

    //Define the distance
    float[] distance = new float[3];
    double s_latitude;
    double s_longitude;

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
                startActivity(new Intent(D_00_MainDialog.this, D_01_StartConnection.class));
            }
        });

        //Btn02 - View device's (Raspberrypi) current location on map view.
        btn02.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(D_00_MainDialog.this, D_02_DeviceCurrentLocation.class));
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
                startActivity(new Intent(D_00_MainDialog.this, D_04_RemoteControl.class));
            }
        });

        //Btn05 - Remove device.
        btn05.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(D_00_MainDialog.this, D_05_RemoveDevice.class));
            }
        });

        /*----------------------------------Fetch LOCATION data start------------------------------*/
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseLocations = database.getReference("Raw_Location");

        databaseLocations.addValueEventListener(new ValueEventListener() {
            @Override //On data change, do...
            public void onDataChange(DataSnapshot dataSnapshot) {
                value = dataSnapshot.getValue(String.class);

                try {//Extract the specefic keys from the json object.
                    JSONObject reader = new JSONObject(value);

                    JSONObject gps_data  = reader.getJSONObject("gps_data");
                    latitude = gps_data.getString("Latitude");
                    longtitude = gps_data.getString("Longtitude");
                    utc_time = gps_data.getString("UTC Time");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //Compute the distance
                Location.distanceBetween( Double.parseDouble(latitude), Double.parseDouble(longtitude),
                        s_latitude, s_longitude, distance);

                //Push the distance data to the Firebase
                dbDevice = FirebaseDatabase.getInstance().getReference("Device/1/distance");
                dbDevice.setValue(distance[0]+"m");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        /*----------------------------------Fetch LOCATION data end------------------------------*/

    }

    /*-----------------------------On create end-----------------------------*/

    //Btn03 - Place Picker on activity result
    @Override
    protected void onActivityResult(int requestCode,int resultCode, Intent data) {

        if (requestCode == PLACE_PICKER_REQUEST && resultCode == Activity.RESULT_OK) {
            // The user has selected a place. Extract the name and address.
            final Place place = PlacePicker.getPlace(data, this);

            final CharSequence name = place.getName();
            final CharSequence address = place.getAddress();
            final LatLng geocoding = place.getLatLng(); //LatLng(double latitude, double longitude)

            //Seperate geocoding into latitude & longitude.
            s_latitude = geocoding.latitude;
            s_longitude = geocoding.longitude;

            //Push the selected place's data to Firebase Device/1/SafetyZone.
            dbDevice = FirebaseDatabase.getInstance().getReference("Device/1/SafetyZone/name");
            dbDevice.setValue(name);
            dbDevice = FirebaseDatabase.getInstance().getReference("Device/1/SafetyZone/address");
            dbDevice.setValue(address);
            dbDevice = FirebaseDatabase.getInstance().getReference("Device/1/SafetyZone/s_latitude");
            dbDevice.setValue(s_latitude);
            dbDevice = FirebaseDatabase.getInstance().getReference("Device/1/SafetyZone/s_longitude");
            dbDevice.setValue(s_longitude);

            //Toast for confirmation.
            Toast.makeText(this, "Added "+ address,Toast.LENGTH_LONG).show();

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}
