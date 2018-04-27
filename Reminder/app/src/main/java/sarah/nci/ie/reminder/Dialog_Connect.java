package sarah.nci.ie.reminder;

//Reference AndroidPubSubWebSocket_Example: https://github.com/awslabs/aws-sdk-android-samples/tree/master/AndroidPubSubWebSocket

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

public class Dialog_Connect extends AppCompatActivity {

    //Define Firebase
    FirebaseDatabase database;
    DatabaseReference myRef;
    //

    static final String LOG_TAG = Dialog_Connect.class.getCanonicalName();

    // IOT Endpoint
    private static final String CUSTOMER_SPECIFIC_ENDPOINT = "ansingrsn5txz.iot.us-west-2.amazonaws.com";
    // Unauthenticated cognito pool ID
    private static final String COGNITO_POOL_ID = "us-west-2:cfde6d61-be8c-4330-ad6b-60df256ce3b3";
    // Used region of AWS IoT
    private static final Regions MY_REGION = Regions.US_WEST_2;

    EditText txtSubscribe, txtTopic, txtMessage;
    TextView tvLastMessage, tvClientId, tvStatus;
    Button btnConnect, btnSubscribe, btnPublish, btnDisconnect;

    AWSIotMqttManager mqttManager;
    String clientId;

    CognitoCachingCredentialsProvider credentialsProvider;

    //Extract the specefic keys from the json object.
    String message, latitude, longtitude, utc_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_connect);

        txtMessage = (EditText) findViewById(R.id.txtMessage);

        tvLastMessage = (TextView) findViewById(R.id.tvLastMessage);
        tvClientId = (TextView) findViewById(R.id.tvClientId);
        tvStatus = (TextView) findViewById(R.id.tvStatus);

        btnSubscribe = (Button) findViewById(R.id.btnSubscribe);
        btnSubscribe.setOnClickListener(subscribeClick);

        btnDisconnect = (Button) findViewById(R.id.btnDisconnect);
        btnDisconnect.setOnClickListener(disconnectClick);

        // MQTT client IDs are required to be unique per AWS IoT account.
        // This UUID is "practically unique" but does not _guarantee_
        // uniqueness.
        clientId = UUID.randomUUID().toString();
        tvClientId.setText(clientId);

        // Initialize the AWS Cognito credentials provider
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
                                            message = new String(data, "UTF-8");

                                            //Extract the specefic keys from the json object.
                                            try {
                                                JSONObject reader = new JSONObject(message);

                                                JSONObject gps_data  = reader.getJSONObject("gps_data");
                                                latitude = gps_data.getString("Latitude");
                                                longtitude = gps_data.getString("Longtitude");
                                                utc_time = gps_data.getString("UTC Time");

                                                //Send the RAW mqtt message to Firebase - Raw_Location
                                                database = FirebaseDatabase.getInstance();
                                                myRef = database.getReference("Raw_Location");
                                                myRef.setValue(message);
                                                //Send the queried json message to Firebase - Location
                                                myRef = database.getReference("Location/Latitude");
                                                myRef.setValue(latitude);
                                                myRef = database.getReference("Location/Longtitude");
                                                myRef.setValue(longtitude);
                                                myRef = database.getReference("Location/Utc time");
                                                myRef.setValue(utc_time);
//                                                //Send the extracted message to Firebase - Device/LB7ujxfEps5uYAfmmaH
                                                myRef = database.getReference("Device/1/address");
                                                myRef.setValue(latitude+ ", "+longtitude);

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }


                                            tvLastMessage.setText(latitude + " " + longtitude + " " + utc_time);

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

