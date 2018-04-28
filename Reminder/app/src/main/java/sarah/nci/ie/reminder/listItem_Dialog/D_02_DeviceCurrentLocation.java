package sarah.nci.ie.reminder.listItem_Dialog;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import sarah.nci.ie.reminder.R;

public class D_02_DeviceCurrentLocation extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    String str;
    double dLat;
    double dLong;

    //Firebase CurrentLocation
    DatabaseReference databaseLocations;
    String value = null;
    Double latitude, longtitude;
    String utc_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.d_02_device_current_location);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //
        dLat = 53.352218333;
        dLong = -6.25159;

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
                    latitude = gps_data.getDouble("Latitude");
                    longtitude = gps_data.getDouble("Longtitude");
                    utc_time = gps_data.getString("UTC Time");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //This line keeps goinggggggggg
                Toast.makeText(D_02_DeviceCurrentLocation.this, "Updated: " + latitude + ", " + longtitude, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        /*----------------------------------Fetch data end------------------------------*/

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
        LatLng sydney = new LatLng(dLat, dLong);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        mMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f));
    }
}
