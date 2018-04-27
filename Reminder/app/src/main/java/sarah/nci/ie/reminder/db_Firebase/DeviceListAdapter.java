package sarah.nci.ie.reminder.db_Firebase;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import sarah.nci.ie.reminder.R;
import sarah.nci.ie.reminder.db_Firebase.Device;

/**
 * List Adapter
 * Reference https://www.youtube.com/watch?v=jEmq1B1gveM.
 * Link to activity_device_adapter
 */

public class DeviceListAdapter extends ArrayAdapter<Device>{

    //Firebase - retrieve data
    private Activity context;
    private List<Device> deviceList;

    //Initialize the variables
    public DeviceListAdapter(@NonNull Activity context, List<Device> deviceList) {
        super(context, R.layout.activity_device_adapter, deviceList);
        this.context = context;
        this.deviceList = deviceList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        //Create a layout inflater object
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.activity_device_adapter, null, true);

        //Link to the textviews in the xml.
        TextView tvAddress = (TextView)listViewItem.findViewById(R.id.tvAddress);
        TextView tvDistance = (TextView)listViewItem.findViewById(R.id.tvDistance);
        TextView tvExtra = (TextView)listViewItem.findViewById(R.id.tvExtra);
        TextView tvNickname = (TextView)listViewItem.findViewById(R.id.tvNickname);

        //Get the device on position
        Device device = deviceList.get(position);

        //Set the value of the textview.
        tvNickname.setText(device.getNickname());
        tvAddress.setText(device.getAddress());
        tvDistance.setText(device.getDistance());
        tvExtra.setText(device.getExtra());

        return listViewItem;
    }
}
