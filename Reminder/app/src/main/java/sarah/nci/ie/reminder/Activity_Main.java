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

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class Activity_Main extends AppCompatActivity {

    //Custom ListView declarations
    private static final String TAG = "Activity_Main";
    String deviceNickname;

    ListView listView;
    ArrayList<Device> deviceList;
    DeviceListAdapter adp;
    int position;

    //Actionbar - Reference - https://www.journaldev.com/9357/android-actionbar-example-tutorial
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Custom ListView
        Log.d(TAG, "onCreate: Started.");

        listView = (ListView) findViewById(R.id.listView);
        deviceList = new ArrayList<>();
        adp = new DeviceListAdapter(this, R.layout.activity_device_adapter, deviceList);
        listView.setAdapter(adp);

        //OnItemClick - Open the Dialog
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent();
                intent.setClass(Activity_Main.this, Dialog_MainDialog.class);
                intent.putExtra(Intent_Constants.INTENT_CA_DATA, deviceList.get(position).toString());
                intent.putExtra(Intent_Constants.INTENT_ITEM_POSITION, position);
                startActivityForResult(intent, Intent_Constants.INTENT_REQUEST_CODE_TWO);
            }
        });

        //Read and display the saved file - Reference https://www.youtube.com/watch?v=duHKgfl21BU
        try{
            Scanner sc = new Scanner(openFileInput("Device.txt"));

            while(sc.hasNextLine()){
                String data[] = sc.nextLine().split(" ");
                    if(data.equals("")){
                        break;
                    }
                    deviceList.add(new Device(data[0], data[0], data[0]));
            }
            sc.close();
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }

    }

    //Saving the added list - Reference https://www.youtube.com/watch?v=duHKgfl21BU
    @Override
    public void onBackPressed() {
        try{
            PrintWriter pw = new PrintWriter(openFileOutput("Device.txt", Context.MODE_PRIVATE));
            for(Device data : deviceList){ //Pass each item of the list deviceList to data
                pw.println(data);
                System.out.println("Added: "+data); //I/System.out: Added: sarah.nci.ie.reminder.Device@b4efc34
            }
            pw.close();
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }
        finish();
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

                //Forward to the add activity
                Intent intent = new Intent();
                intent.setClass(this, Activity_RegisterDevice.class);
                startActivityForResult(intent, Intent_Constants.INTENT_REQUEST_CODE);
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }

        //For the add activity - pass the entered Strings to the listView in the mainActivity.
        if(resultCode == Intent_Constants.INTENT_REQUEST_CODE){
            /*Get the string from Activity_RegisterDevice
            caTitleText = data.getStringExtra(Intent_Constants.INTENT_CA_FIELD);
            caSubjectText = data.getStringExtra(Intent_Constants.INTENT_SUBJECT_FIELD);
            caDueDateText = data.getStringExtra(Intent_Constants.INTENT_DUEDATE_FIELD);   */
            deviceNickname = data.getStringExtra(Intent_Constants.INTENT_DEVICE_FIELD);
            Device newDevice = new Device("At Pheonix Park", deviceNickname, "580m");
            deviceList.add(newDevice);
            adp.notifyDataSetChanged();
        }
    }
}