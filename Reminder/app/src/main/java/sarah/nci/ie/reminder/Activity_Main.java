package sarah.nci.ie.reminder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import sarah.nci.ie.reminder.db_Firebase.Device;
import sarah.nci.ie.reminder.db_Firebase.DeviceListAdapter;

/*
 * Listview retrieved from firebase.
 * Reference: https://www.youtube.com/watch?v=jEmq1B1gveM
 * Actionbar: https://www.journaldev.com/9357/android-actionbar-example-tutorial
 */
public class Activity_Main extends AppCompatActivity {

    //Firebase listview
    DatabaseReference databaseDevices;
    ListView listViewDevices;
    List<Device> deviceList;

    //Firebase CurrentLocation
    DatabaseReference databaseLocations;
    String value = null;
    String latitude, longtitude, utc_time;

    //Actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    //Actionbar - Continue
    @Override
    public boolean onOptionsItemSelected(MenuItem item) { switch(item.getItemId()) {
        case R.id.photo:
            Intent i = new Intent(this, Action_Photo.class);
            this.startActivity(i);
            return(true);
        case R.id.gallery:
            Intent i2 = new Intent();
            i2.setClass(Activity_Main.this, Action_Gallery.class);
            this.startActivity(i2);
            return(true);
        case R.id.exit:
            finish();
            return(true);
    }
        return(super.onOptionsItemSelected(item));
    }

    /*-----------------------------On create start-----------------------------*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Firebase - listView
        listViewDevices = (ListView) findViewById(R.id.listView);
        deviceList = new ArrayList<>();
        databaseDevices = FirebaseDatabase.getInstance().getReference("Device");

        //OnItemClick - Open the Dialog
        listViewDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent();
                intent.setClass(Activity_Main.this, Dialog_MainDialog.class);
                intent.putExtra(Intent_Constants.INTENT_CA_DATA, deviceList.get(position).toString());
                intent.putExtra(Intent_Constants.INTENT_ITEM_POSITION, position);
                startActivityForResult(intent, Intent_Constants.INTENT_REQUEST_CODE_TWO);
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
                //This line keeps goinggggggggg
                Toast.makeText(Activity_Main.this, latitude + ", " + longtitude, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        /*----------------------------------Fetch data end------------------------------*/

    }

    /*-----------------------------On create end-----------------------------*/

    //Firebase listview
    @Override
    protected void onStart() {
        super.onStart();

        databaseDevices.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) { //Read the value
                //Clear the list
                deviceList.clear();
                //Loop
                for(DataSnapshot deviceSnapshot: dataSnapshot.getChildren()){
                    Device device = deviceSnapshot.getValue(Device.class);
                    //Store
                    deviceList.add(device);
                }
                //Create a new adapter
                DeviceListAdapter a = new DeviceListAdapter(Activity_Main.this, deviceList);
                listViewDevices.setAdapter(a);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //FloatButton click - Go to scan QR code, and link to the add activity
    final Activity activity = this;
    public void addClick(View v){
        IntentIntegrator integrator = new IntentIntegrator(activity);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setPrompt("Scan");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(false);
        integrator.setBarcodeImageEnabled(false);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Goes to Scanning
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null){
            if(result.getContents()==null){
                Toast.makeText(this, "You cancelled the scanning", Toast.LENGTH_LONG).show();
            }
            else {//Once scan completed...
                Toast.makeText(this, "Success " + result.getContents(),Toast.LENGTH_LONG).show();

                //Forward to the add activity - activity_regirsterdevice
                startActivity(new Intent(this, Activity_RegisterDevice.class));
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }

    }

}