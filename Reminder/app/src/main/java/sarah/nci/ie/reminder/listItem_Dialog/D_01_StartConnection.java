package sarah.nci.ie.reminder.listItem_Dialog;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttClientStatusCallback;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttManager;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttNewMessageCallback;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttQos;
import com.amazonaws.regions.Regions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

import sarah.nci.ie.reminder.R;

/* Connect & Subscribe to AWS MQTT topic:
 * Reference AndroidPubSubWebSocket_Example: https://github.com/awslabs/aws-sdk-android-samples/tree/master/AndroidPubSubWebSocket
 *
 * 1. Connect automatically to the AWS MQTT topic 'pi/observations/DeviceID' on page created.
 * 2. Define button01 - Subscribe to the topic.
 *      * Retrieve the message from the topic.
 *      * Seperated the message which comes from a JSON format.
 *      * Push the data to Firebase 'Device/specificID'.
 * 3. Define button02 - Disconnect to AWS.
 */
public class D_01_StartConnection extends AppCompatActivity {

    //Define Firebase
    DatabaseReference myRef;

    //Retrieve the intent
    String deviceId, deviceName;

    //Define the LOG
    static final String LOG_TAG = D_01_StartConnection.class.getCanonicalName();
    //IOT Endpoint
    private static final String CUSTOMER_SPECIFIC_ENDPOINT = "ansingrsn5txz.iot.us-west-2.amazonaws.com";
    //Unauthenticated cognito pool ID
    private static final String COGNITO_POOL_ID = "us-west-2:cfde6d61-be8c-4330-ad6b-60df256ce3b3";
    //Used region of AWS IoT
    private static final Regions MY_REGION = Regions.US_WEST_2;

    //Define the xml elements
    TextView tvLastMessage, tvClientId, tvStatus;
    Button btnSubscribe, btnDisconnect;

    AWSIotMqttManager mqttManager;
    String clientId;

    CognitoCachingCredentialsProvider credentialsProvider;

    //Extract the specefic keys from the json object.
    String mqttMessage, latitude, longtitude, utc_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.d_01_start_connection);

        //Get the intent from the D_00_MainDialogActivity
        Intent intent = getIntent();
        //Retrieve the particular device's id & name
        deviceId = intent.getStringExtra(D_00_MainDialog.DEVICE_ID);
        deviceName = intent.getStringExtra(D_00_MainDialog.DEVICE_NAME);

        //Define the xml's elements
        tvLastMessage = (TextView) findViewById(R.id.tvLastMessage);
        tvClientId = (TextView) findViewById(R.id.tvClientId);
        tvStatus = (TextView) findViewById(R.id.tvStatus);
        btnSubscribe = (Button) findViewById(R.id.btnSubscribe);
        btnSubscribe.setOnClickListener(subscribeClick);
        btnDisconnect = (Button) findViewById(R.id.btnDisconnect);
        btnDisconnect.setOnClickListener(disconnectClick);

        //Generate a MQTT client ID
        clientId = UUID.randomUUID().toString();
        tvClientId.setText(clientId);

        //Initialize the AWS Cognito credentials provider
        credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(), // context
                COGNITO_POOL_ID, // Identity Pool ID
                MY_REGION // Region
        );

        // MQTT Client
        mqttManager = new AWSIotMqttManager(clientId, CUSTOMER_SPECIFIC_ENDPOINT);

        // The following block uses a Cognito credentials provider for authentication with AWS IoT.
        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    }
                });
            }
        }).start();

        //On page created, connect directly.
        Log.d(LOG_TAG, "clientId = " + clientId);

        try {
            mqttManager.connect(credentialsProvider, new AWSIotMqttClientStatusCallback() {
                @Override
                public void onStatusChanged(final AWSIotMqttClientStatus status,
                                            final Throwable throwable) {
                    Log.d(LOG_TAG, "Status = " + String.valueOf(status));

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (status == AWSIotMqttClientStatus.Connecting) {
                                tvStatus.setText("Connecting...");

                            } else if (status == AWSIotMqttClientStatus.Connected) {
                                tvStatus.setText("Connected");

                            } else if (status == AWSIotMqttClientStatus.Reconnecting) {
                                if (throwable != null) {
                                    Log.e(LOG_TAG, "Connection error.", throwable);
                                }
                                tvStatus.setText("Reconnecting");
                            } else if (status == AWSIotMqttClientStatus.ConnectionLost) {
                                if (throwable != null) {
                                    Log.e(LOG_TAG, "Connection error.", throwable);
                                    throwable.printStackTrace();
                                }
                                tvStatus.setText("Disconnected");
                            } else {
                                tvStatus.setText("Disconnected");

                            }
                        }
                    });
                }
            });
        } catch (final Exception e) {
            Log.e(LOG_TAG, "Connection error.", e);
            tvStatus.setText("Error! " + e.getMessage());
        }
    }

    //On Subscribe click
    View.OnClickListener subscribeClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            //Subscribe to the specific MQTT topic
            final String topic = "pi/observations/DeviceID";

            try {
                mqttManager.subscribeToTopic(topic, AWSIotMqttQos.QOS0,
                        new AWSIotMqttNewMessageCallback() {
                            @Override
                            public void onMessageArrived(final String topic, final byte[] data) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            mqttMessage = new String(data, "UTF-8");

                                            //Extract the specefic keys from the json object retrieved from MQTT.
                                            try {
                                                JSONObject reader = new JSONObject(mqttMessage);
                                                JSONObject gps_data  = reader.getJSONObject("gps_data");
                                                latitude = gps_data.getString("Latitude");
                                                longtitude = gps_data.getString("Longtitude");
                                                utc_time = gps_data.getString("UTC Time");

                                                //Update the particular device's current_location
                                                myRef = FirebaseDatabase.getInstance().getReference("Device/" +deviceId+ "/Current location/Latitude");
                                                myRef.setValue(latitude);
                                                myRef = FirebaseDatabase.getInstance().getReference("Device/" +deviceId+ "/Current location/Longitude");
                                                myRef.setValue(longtitude);
                                                myRef = FirebaseDatabase.getInstance().getReference("Device/" +deviceId+ "/Current location/Local Time");
                                                myRef.setValue(utc_time);
                                                //Update the particular device's address
                                                myRef = FirebaseDatabase.getInstance().getReference("Device/" +deviceId+ "/address");
                                                myRef.setValue(latitude+ ", " +longtitude);

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                            tvLastMessage.setText("Updated: " +latitude+ " " +longtitude+ " " +utc_time);

                                        } catch (UnsupportedEncodingException e) {
                                            Log.e(LOG_TAG, "Message encoding error.", e);
                                        }
                                    }
                                });
                            }
                        });
            } catch (Exception e) {
                Log.e(LOG_TAG, "Subscription error.", e);
            }
        }
    };

    //On Disconnect click, disconnect.
    View.OnClickListener disconnectClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                mqttManager.disconnect();
            } catch (Exception e) {
                Log.e(LOG_TAG, "Disconnect error.", e);
            }
        }
    };
}

