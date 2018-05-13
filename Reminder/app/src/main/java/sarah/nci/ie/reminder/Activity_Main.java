package sarah.nci.ie.reminder;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.support.annotation.RequiresApi;
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
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;

import sarah.nci.ie.reminder.db_Firebase.Device;
import sarah.nci.ie.reminder.db_Firebase.DeviceListAdapter;
import sarah.nci.ie.reminder.listItem_Dialog.D_00_MainDialog;

/*
 * Listview retrieved from firebase reference: https://www.youtube.com/watch?v=jEmq1B1gveM
 * Actionbar reference: https://www.journaldev.com/9357/android-actionbar-example-tutorial
 * Update deviceName onLongItemClick reference: https://www.youtube.com/watch?v=2bYWf0z8_8s&index=4&list=PLk7v1Z2rk4hj6SDHf_YybDeVhUT9MXaj1
 * Real-time update the listView if child-change reference: https://stackoverflow.com/questions/46690220/data-is-getting-added-into-list-instead-of-getting-updated-in-addchildeventliste
 * Simple alert dialog reference: https://www.youtube.com/watch?v=va-nn1JAwj8
 *
 * Oncreate - ListView
 *      * On Item click - Open the D_00_MainDialog, while put intent extra the selected item's ID.
 *      * On Item longclick - showUpdateDialog.
 *      * Initialize the listView according to Firebase data.
 *
 * Onstart - Compute & update latest distance between device's (Raspberrypi) current location & defined safety zone.
 *      * Fetch device's (RaspberryPi) current location from Firebase.
 *      * Check if safety_zone already defined:
 *          - If yes: Grab the latest safety_zone's latitude for later usage.
 *                    Grab the latest safety_zone's longitude for later usage.
 *      * Compute and provide real-time distance changing.
 *      * Trigger the device's (Raspberrypi) sensor if distance < 50 meters.
 *
 * OnFloatButtonClick - Goes to scanning page
 *      * Brings back the scanning result once scanning end successfully.
 */

public class Activity_Main extends AppCompatActivity {

    //Define Firebase's database reference
    DatabaseReference databaseDevices;
    DatabaseReference databasePi;

    //Define a listView connected to Activity_main's listView
    ListView listViewDevices;
    //Define a adapter for the listView
    DeviceListAdapter adapter;

    //Define a new list to store fetched device data.
    List<Device> devices;
    //List to store each device's deviceId
    List<String> eachDevice_ID;

    double safety_latitude = 0.0;
    double safety_longitude = 0.0;

    //To store intent's extra for D_00_MainDialog
    public static final String DEVICE_NAME = "deviceName";
    public static final String DEVICE_ID = "deviceId";
    //To store intent's extra for Activity_RegisterDevice
    public static final String QRCODE_CONTENT = "qrcode";

    //Actionbar - onCreate
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //Actionbar - OnItemClicked
    @Override
    public boolean onOptionsItemSelected(MenuItem item) { switch(item.getItemId()) {
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

        //Define Firebase's database reference -Listen to 'Device/everyId'
        databaseDevices = FirebaseDatabase.getInstance().getReference("Device");
        //Define a listView connected to Activity_main's listView
        listViewDevices = (ListView) findViewById(R.id.listView);

        //Define a new list to store fetched device data.
        devices = new ArrayList<>();
        //Define a list to store every device's id
        eachDevice_ID = new ArrayList<>();

        //Define the adapter
        adapter = new DeviceListAdapter(Activity_Main.this, devices);
        listViewDevices.setAdapter(adapter);

        //On ListViewItemClick - Open the D_00_MainDialog
        listViewDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
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

                //Open the intent with animation
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(Activity_Main.this).toBundle());
            }
        });

        //On ListViewItemLongClick - open showUpdateDialog
        listViewDevices.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                //Get the particular device
                Device device = devices.get(position);
                //Get the original id and nickname of the device
                showUpdateDialog(device.getDeviceId(), device.getNickname());
                //Return true to prevent opening the main dialog
                return true;
            }
        });

        // Listen to 'Device': Initialize the listView and deviceIdList.
        // Update them only if new device is added through scanning QRcode.
        // Update the changes real-time on name update & device delete.
        databaseDevices.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                //Create the listView.
                Device device = dataSnapshot.getValue(Device.class);
                devices.add(device);

                //Fetch each ID of listItem.
                eachDevice_ID.add(dataSnapshot.getKey().toString());

                //Notify
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                //Real-time updating the list once changed name.
                Device newNameDevice = dataSnapshot.getValue(Device.class);

                //Grab current key; Find index in existing list, set Name.
                String key = dataSnapshot.getKey();
                int index = eachDevice_ID.indexOf(key);
                devices.set(index, newNameDevice);

                //Notify
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                //Real-time updating the list once deleted.
                //Grab current key; Find index in existing list, remove the value in the deviceList & idList.
                String key = dataSnapshot.getKey();
                int index = eachDevice_ID.indexOf(key);
                devices.remove(index);
                eachDevice_ID.remove(index);

                //Notify
                adapter.notifyDataSetChanged();
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


    //On start, real-time compute and update every device's latest distance
    @Override
    protected void onStart() {
        super.onStart();

        //Listen to 'Device/everyId'
        databaseDevices.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            }

            @Override //When 'Device/everyId/CurrentLocation' is updated...
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                //Compute and update the latest distance.
                computeLatestDistance(dataSnapshot);

                //Notify
                adapter.notifyDataSetChanged();
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
    public void addClick(View v){
        final Activity activity = this;
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
            else {
                //If scan successed, go to the registering page.
                Intent intent = new Intent();
                intent.setClass(this, Activity_RegisterDevice.class);
                //Put extra the retrieved QRcode's content
                intent.putExtra(QRCODE_CONTENT, result.getContents());
                //Start the register device activity
                startActivity(intent);
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }

    }

    //Function - Update the device's nickName / delete the device.
    private void showUpdateDialog(final String deviceId, String deviceName){
        //Define an alert dialog
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        //Define the view's xml
        final View dialogView = inflater.inflate(R.layout.activity_main_update, null);
        dialogBuilder.setView(dialogView);
        //Define the xml's objects
        final EditText etUpdateName = (EditText)dialogView.findViewById(R.id.etUpdateName);
        final Button btnUpdateName = (Button)dialogView.findViewById(R.id.btnUpdateName);
        final Button btnDeleteDevice = (Button)dialogView.findViewById(R.id.btnDeleteDevice);

        //Set the title of the dialog (include the original name of the device)
        dialogBuilder.setTitle("Updating Device '"+deviceName+"'.");
        //Create & show the dialog
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        //On Update Button click
        btnUpdateName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Grab the entered name
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

                //Initialize another alert dialog to confirm delete
                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Main.this);
                builder.setMessage("Confirm to delete?")
                        .setPositiveButton("Sure", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Call the delete method
                                deleteDevice(deviceId);
                            }
                        })
                        .setNegativeButton("Cancel", null);

                //Create & show the dialog
                AlertDialog alert = builder.create();
                alert.show();

                //Close the dialog once completed.
                alertDialog.dismiss();
            }
        });


    }

    //Function - Update the nickname. (Used in function - showUpdateDialog)
    private boolean updateDeviceName(String deviceId, String newName){
        //Point the databaseReference to the 'nickname' in the Device's Json.
        DatabaseReference dbReference = FirebaseDatabase.getInstance().getReference("Device/"+deviceId+"/nickname");
        //Update the nickname to new value
        dbReference.setValue(newName);
        //Confimation message
        Toast.makeText(this, "Device updated.", Toast.LENGTH_LONG).show();
        return true;
    }

    //Functon - Delete the device. (Used in function - showUpdateDialog)
    private void deleteDevice(String deviceId) {
        //Point the databaseReference to the 'nickname' in the Device's Json.
        DatabaseReference dbReference = FirebaseDatabase.getInstance().getReference("Device/"+deviceId);
        //Update the nickname to new value
        dbReference.removeValue();
        //Confimation message
        Toast.makeText(this, "Device deleted.", Toast.LENGTH_LONG).show();
    }

    //Function - Compute and update the latest distance.
    private void computeLatestDistance(DataSnapshot dataSnapshot){
        //Check if the safety_zone is defined already before processing:
        if (dataSnapshot.child("SafetyZone").exists()) {

            //Grab the current location
            double current_latitude = Double.parseDouble(dataSnapshot.child("Current location/Latitude").getValue(String.class));
            double current_longitude = Double.parseDouble(dataSnapshot.child("Current location/Longitude").getValue(String.class));

            //Check if the safety_zone's latitude & longitude is updated before processing:
            if (dataSnapshot.child("SafetyZone/s_latitude").exists()) {
                safety_latitude = dataSnapshot.child("SafetyZone/s_latitude").getValue(Double.class);
            }
            if(dataSnapshot.child("SafetyZone/s_longitude").exists()) {
                safety_longitude = dataSnapshot.child("SafetyZone/s_longitude").getValue(Double.class);
            }

            //Compute the latest distance
            float[] new_d = new float[3];
            Location.distanceBetween(current_latitude, current_longitude, safety_latitude, safety_longitude, new_d);

            //Push the distance data to the Firebase AGAIN.* //("Device/" +deviceId+ "/distance");
            dataSnapshot.child("distance").getRef().setValue(new_d[0] + "m");

            //Trigger the sensor's status based on latest distance.
            triggerSensorStatus(dataSnapshot, new_d[0]);

        }
    }

    //Function - Trigger the sensor's status based on latest distance. (Used in function - computeLatestDistance)
    private void triggerSensorStatus(DataSnapshot dataSnapshot, float latestDistance){
        //If distance < 50m, trigger sensor status to 'on'.
        if(latestDistance > 50){
            dataSnapshot.child("extra").getRef().setValue("on");
            databasePi = FirebaseDatabase.getInstance().getReference("Pi/distanceStatus");
            databasePi.setValue("on");

        }else{
            dataSnapshot.child("extra").getRef().setValue("off");
            databasePi = FirebaseDatabase.getInstance().getReference("Pi/distanceStatus");
            databasePi.setValue("off");
        }
    }
}