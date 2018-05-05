package sarah.nci.ie.reminder;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import sarah.nci.ie.reminder.actionbar.Action_Gallery;
import sarah.nci.ie.reminder.actionbar.Action_Photo;
import sarah.nci.ie.reminder.db_Firebase.Device;
import sarah.nci.ie.reminder.db_Firebase.DeviceListAdapter;
import sarah.nci.ie.reminder.listItem_Dialog.D_00_MainDialog;

/*
 * Listview retrieved from firebase.
 * Reference: https://www.youtube.com/watch?v=jEmq1B1gveM
 * Actionbar reference: https://www.journaldev.com/9357/android-actionbar-example-tutorial
 * Update deviceName onLongItemClick reference: https://www.youtube.com/watch?v=2bYWf0z8_8s&index=4&list=PLk7v1Z2rk4hj6SDHf_YybDeVhUT9MXaj1
 */
public class Activity_Main extends AppCompatActivity {
    DeviceListAdapter a;
    //Firebase listview
    DatabaseReference databaseDevices;

    ListView listViewDevices;
    //Define a new list to store fetched device data.
    List<Device> devices;
    //List to store each device's deviceId
    List<String> eachDevice_ID;

    //String checkTheid;
    private static final String TAG = "MainAc";

    //Device Item
    public static final String DEVICE_NAME = "deviceName";
    public static final String DEVICE_ID = "deviceId";

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
        databaseDevices = FirebaseDatabase.getInstance().getReference("Device");
        listViewDevices = (ListView) findViewById(R.id.listView);
        //Define a new list to store fetched device data.
        devices = new ArrayList<>();
        //Define a list to store every device's id
        eachDevice_ID = new ArrayList<>();
        //Create a new adapter
        a = new DeviceListAdapter(Activity_Main.this, devices);
        listViewDevices.setAdapter(a);



        //OnItemClick - Open the Main Dialog
        listViewDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                //Fetch the device from the defined list
                Device device = devices.get(position);

                //Create a new intent to open the main dialog
                Intent intent = new Intent();
                intent.setClass(Activity_Main.this, D_00_MainDialog.class);
                //Put extra the name & id of the device
                intent.putExtra(DEVICE_ID, device.getDeviceId());
                intent.putExtra(DEVICE_NAME, device.getNickname());

                intent.putExtra(Intent_Constants.INTENT_CA_DATA, devices.get(position).toString());
                intent.putExtra(Intent_Constants.INTENT_ITEM_POSITION, position);
                startActivityForResult(intent, Intent_Constants.INTENT_REQUEST_CODE_TWO);
            }
        });

        //On longclick listerner
        listViewDevices.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                //Get the particular device
                Device device = devices.get(position);
                //Get the original id and nickname of the device
                showUpdateDidalog(device.getDeviceId(), device.getNickname());
                //Return true to prevent opening the main dialog
                return true;
            }
        });

        // Listen to 'Device': Initialize the listView and deviceIdList
        // Update them only if new device is added through scanning QRcode
        databaseDevices.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {

                Log.i(TAG, "Striing?: "+ dataSnapshot.getValue(Device.class));
                Log.i(TAG, "Another?: "+ dataSnapshot.getValue());

                //Fetch the listView.
                Device device = dataSnapshot.getValue(Device.class);
                devices.add(device);

                //Fetch each ID of listItem.
                eachDevice_ID.add(dataSnapshot.getKey().toString());
                //Check if list correct
                for(int i=0; i < eachDevice_ID.size(); i++){
                    Log.i(TAG, "Size: "+eachDevice_ID.size()+". The ID of the device in 1st listerner: "+ eachDevice_ID.get(i));
                }
                //Notify
                a.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                //Real-time updating once changed name Reference: https://stackoverflow.com/questions/46690220/data-is-getting-added-into-list-instead-of-getting-updated-in-addchildeventliste
                Device newNameDevice = dataSnapshot.getValue(Device.class);

                //Grab current key; Find index in existing list, set Name.
                String key = dataSnapshot.getKey();
                int index = eachDevice_ID.indexOf(key);
                devices.set(index, newNameDevice);

                //Notify
                a.notifyDataSetChanged();

                /*----------------------------------Fetch CURRENT LOCATION data start------*/

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                //Remove
                Device deleteDevice = dataSnapshot.getValue(Device.class);

                //Notify
                a.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /*-----------------------------On create end-----------------------------*/


    @Override
    protected void onStart() {
        super.onStart();

        //NEW try - Listen to 'Device'
        databaseDevices.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            }

            @Override //When 'Device/address' changed
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                //Listen to 'Raw location' and fetch current device's location

                /*----------------------------------Fetch CURRENT LOCATION data start------------------------------*/

                Log.i(TAG, "Key before "+ dataSnapshot.getKey());

                //IF the safety_zone is defined:
                if (dataSnapshot.child("SafetyZone").exists()) {
                    Log.i(TAG, "Key after "+ dataSnapshot.getKey()); //Returns 1

                    //Grab each device's safetyLatitude and longitude
                    double safety_latitude = dataSnapshot.child("SafetyZone/s_latitude").getValue(Double.class);
                    double safety_longitude = dataSnapshot.child("SafetyZone/s_longitude").getValue(Double.class);
                    Log.i(TAG, "Safe_LOCATION: "+ safety_latitude+", "+safety_longitude); //Returns

                    //
                    double current_latitude = Double.parseDouble(dataSnapshot.child("latitude").getValue(String.class));
                    double current_longitude = Double.parseDouble(dataSnapshot.child("longitude").getValue(String.class));
                    Log.i(TAG, "DEVICE_LOCATION: "+ current_latitude+", "+current_longitude); //Returns

                    //Compute the latest distance
                    float[] new_d = new float[3];
                    Location.distanceBetween(current_latitude, current_longitude,
                                            safety_latitude, safety_longitude, new_d);
                    Log.i(TAG, "DISTANCE: "+ new_d[0]);

                    //Push the distance data to the Firebase AGAIN.* //("Device/" +deviceId+ "/distance");
                    dataSnapshot.child("distance").getRef().setValue(new_d[0] + "m");

                    //If distance < 50m, trigger the alarm on the device (Raspberrypi)
                    //By Sending 'on' or 'off' to Firebase.
                    if(new_d[0] < 50){
                        dataSnapshot.child("extra").getRef().setValue("on");
                    }else{
                        dataSnapshot.child("extra").getRef().setValue("off");
                    }


                }
                /*----------------------------------Fetch CURRENT LOCATION data end------------------------------*/

                //Notify
                a.notifyDataSetChanged();
            }


            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

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

    //On scanning finished
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

    //Function - Update the device's nickName.
    private void showUpdateDidalog(final String deviceId, String deviceName){
        //
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        //
        final View dialogView = inflater.inflate(R.layout.activity_main_update, null);
        dialogBuilder.setView(dialogView);

        final EditText etUpdateName = (EditText)dialogView.findViewById(R.id.etUpdateName);
        final Button btnUpdateName = (Button)dialogView.findViewById(R.id.btnUpdateName);
        final Button btnDeleteDevice = (Button)dialogView.findViewById(R.id.btnDeleteDevice);

        //Set the title of the dialog (include the original name of the device)
        dialogBuilder.setTitle("Updating Device "+deviceId);
        //Create & show the dialog
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        //On Update Button click
        btnUpdateName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Grab the new entered name
                String name = etUpdateName.getText().toString().trim();

                //Check if name is empty
                if(TextUtils.isEmpty(name)){
                    etUpdateName.setError("Please enter a name");
                    return;
                }else{
                    //Call the update method
                    updateDeviceName(deviceId, name);
                    //Close the dialog once completed.
                    alertDialog.dismiss();
                }
            }
        });

        //On Delete Button click
        btnDeleteDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Call the delete method
                deleteDevice(deviceId);
                //Close the dialog once completed.
                alertDialog.dismiss();
            }
        });


    }

    //Functon - Delete the device.
    private void deleteDevice(String deviceId) {
        //Point the databaseReference to the 'nickname' in the Device's Json.
        DatabaseReference dbReference = FirebaseDatabase.getInstance().getReference("Device/"+deviceId);
        //Update the nickname to new value
        dbReference.removeValue();
        //Confimation message
        Toast.makeText(this, "Device deleted", Toast.LENGTH_LONG).show();

    }

    //Function - Update the nickname.
    private boolean updateDeviceName(String deviceId, String deviceName){
        //Point the databaseReference to the 'nickname' in the Device's Json.
        DatabaseReference dbReference = FirebaseDatabase.getInstance().getReference("Device/"+deviceId+"/nickname");
        //Update the nickname to new value
        dbReference.setValue(deviceName);

        return true;
    }

    //Function - Grab the latest la
//    private double grabLatestSafety_La(String id){
//        //Grab the latest safety_zone's latitude for later usage.
//        databaseSafetyLocations = FirebaseDatabase.getInstance().getReference("Device/" +id+ "/SafetyZone/s_latitude");
//        databaseSafetyLocations.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                //Check if safetyZone exists.
//                //If no, a 0.0 is passed.
//                if(dataSnapshot.exists()){
//                    llla = dataSnapshot.getValue(double.class);
//                }
//            }
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//            }
//        });
//        return llla;
//    }

    //Function - Grab the latest lo
//    private double grabLatestSafety_Lo(String id){
//        //Grab the latest safety_zone's longtitude for later usage.
//        databaseSafetyLocations = FirebaseDatabase.getInstance().getReference("Device/" +id+ "/SafetyZone/s_longitude");
//        databaseSafetyLocations.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                //Check if safetyZone exists.
//                //If no, a 0.0 is passed.
//                if(dataSnapshot.exists()){
//                    lllo = dataSnapshot.getValue(double.class);
//                }
//            }
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//        return lllo;
//    }



    //Function - Update the distance
//    private boolean updateLatestDistance(String deviceId, double safety_la, double safety_lo){
//
//        //Compute the latest distance between device's current location & selected safetyzone location.
//        //To provide real-time distance changing
//        //Firebase - Push latest distance.
//        DatabaseReference dbLatest_distance = FirebaseDatabase.getInstance().getReference("Device/" +deviceId+ "/distance");
//        DatabaseReference dbLatest_extra = FirebaseDatabase.getInstance().getReference("Device/" +deviceId+ "/extra");
//
//        //Check if safetyZone defined
//        if(safety_la!=0.0){
//            float[] new_d = new float[3];
//            //Compute the distance AGAIN.*
//            Location.distanceBetween( Double.parseDouble(latitude), Double.parseDouble(longtitude), safety_la, safety_lo, new_d);
//
//            //Push the distance data to the Firebase AGAIN.*
//            dbLatest_distance.setValue(new_d[0]+"m");
//
//            //If distance < 50m, trigger the alarm on the device (Raspberrypi)
//            //By Sending 'on' or 'off' to Firebase.
//            if(new_d[0] < 50){
//                dbLatest_extra.setValue("on");
//            }else{
//                dbLatest_extra.setValue("off");
//            }
//        }
//
//        return true;
//    }

}