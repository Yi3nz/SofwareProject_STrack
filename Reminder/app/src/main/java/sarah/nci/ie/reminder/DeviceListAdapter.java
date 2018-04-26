package sarah.nci.ie.reminder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by User on 11/20/2017.
 */

public class DeviceListAdapter extends ArrayAdapter<Device>{

    private static final  String TAG = "DeviceListAdapter";
    private Context mContext;
    int mResource;

    public DeviceListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Device> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //Get the Device information
        String name = getItem(position).getName();
        String address = getItem(position).getAddress();
        String distance = getItem(position).getDistance();

        //Create the Device object
        Device device = new Device(name, address, distance);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView tvSubject = (TextView)convertView.findViewById(R.id.tvName);
        TextView tvCATitle = (TextView)convertView.findViewById(R.id.tvAddress);
        TextView tvDate = (TextView)convertView.findViewById(R.id.tvDistance);

        tvSubject.setText(name);
        tvCATitle.setText(address);
        tvDate.setText(distance);

        return convertView;
    }
}
