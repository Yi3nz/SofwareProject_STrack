package sarah.nci.ie.reminder.listItem_Dialog;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import sarah.nci.ie.reminder.R;

/*
 * Display device's (RaspberryPi) current location on a map.
 * Reference: https://developers.google.com/maps/documentation/android-api/marker
 *
 * 1. On Map ready, set a maker on the map.
 * 2. OnCreate:
 *             Fetch the latest raw_location from Firebase.
 *             Update the marker's location based on the latest data fetched.
 */
public class D_02_DeviceCurrentLocation extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    MarkerOptions m;
    Marker myMarker;

    //Retrieve the intent
    String deviceId, deviceName;

    //Firebase CurrentLocation
    DatabaseReference databaseLocations;
    String value = null;
    double latitude, longitude;
    String utc_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.d_02_device_current_location);

        //Get the intent from the D_00_MainDialogActivity
        Intent intent = getIntent();
        //Retrieve the particular device's id & name
        deviceId = intent.getStringExtra(D_00_MainDialog.DEVICE_ID);
        deviceName = intent.getStringExtra(D_00_MainDialog.DEVICE_NAME);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        /*----------------------------------Fetch CURRENT LOCATION data start------------------------------*/
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseLocations = database.getReference("Raw_Location");

        databaseLocations.addValueEventListener(new ValueEventListener() {
            @Override //On data change, do...
            public void onDataChange(DataSnapshot dataSnapshot) {
                value = dataSnapshot.getValue(String.class);

                try {//Extract the specefic keys from the json object.
                    JSONObject reader = new JSONObject(value);

                    JSONObject gps_data  = reader.getJSONObject("gps_data");
                    latitude = gps_data.getDouble("Latitude");
                    longitude = gps_data.getDouble("Longtitude");
                    utc_time = gps_data.getString("UTC Time");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //This line keeps goinggggggggg
                Toast.makeText(D_02_DeviceCurrentLocation.this, "Updated: " + latitude + ", " + longitude, Toast.LENGTH_LONG).show();

                //Update the marker's location
                LatLng device_updated_location = new LatLng(latitude, longitude);
                myMarker.setPosition(device_updated_location);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(device_updated_location));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        /*----------------------------------Fetch CURRENT LOCATION data end------------------------------*/

    }

    /*-----------------------------On create end-----------------------------*/




    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng device_current_location = new LatLng(latitude, longitude);

        //Set maker option
        m = new MarkerOptions().position(device_current_location).title("Current location");
        myMarker = mMap.addMarker(m);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(device_current_location));

        mMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f));
    }
}
