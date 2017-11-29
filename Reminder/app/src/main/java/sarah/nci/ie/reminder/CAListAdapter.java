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
import java.util.List;

/**
 * Created by User on 11/20/2017.
 */

public class CAListAdapter extends ArrayAdapter<CA>{

    private static final  String TAG = "CAListAdapter";
    private Context mContext;
    int mResource;

    public CAListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<CA> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //Get the CA information
        String subject = getItem(position).getSubject();
        String CATitle = getItem(position).getCATitle();
        String date = getItem(position).getDate();

        //Create the CA object
        CA ca = new CA(subject, CATitle, date);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView tvSubject = (TextView)convertView.findViewById(R.id.tvSubject);
        TextView tvCATitle = (TextView)convertView.findViewById(R.id.tvCATitle);
        TextView tvDate = (TextView)convertView.findViewById(R.id.tvDate);

        tvSubject.setText(subject);
        tvCATitle.setText(CATitle);
        tvDate.setText(date);

        return convertView;
    }
}
