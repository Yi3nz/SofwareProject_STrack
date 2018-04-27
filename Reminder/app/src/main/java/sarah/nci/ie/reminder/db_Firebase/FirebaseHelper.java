package sarah.nci.ie.reminder.db_Firebase;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

/**
 * Reference: https://www.youtube.com/watch?v=un0fZhUlaU4
 * 1. Save the data to Firebase
 * 2. Retrieve
 * 3. Return an arraylist
 */

public class FirebaseHelper {

    //Define the variables
    DatabaseReference db;
    Boolean saved;
    ArrayList<Device> devices = new ArrayList<>();

    //Pass the database reference
    public FirebaseHelper(DatabaseReference db) {
        this.db = db;
    }

    //Write if not null
    public Boolean save(Device device) {
        if(device == null){
            saved = false;
        }else{
            try{
                db.child("New_Device").push().setValue(device);
                saved = true;
            }catch(DatabaseException e){
                e.printStackTrace();
                saved = false;
            }
        }
        return saved;
    }

    //Implement fetch data and fill arraylist
    private void fetchData(DataSnapshot dataSnapshot){
        //Clear the arraylist.
        devices.clear();
        //Loop through the children of dataScapshot.
        for(DataSnapshot ds : dataSnapshot.getChildren()){
            Device device = ds.getValue(Device.class);
            devices.add(device);

        }
    }

    //Retrieve
    public ArrayList<Device> retrieve(){
        db.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                fetchData(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                fetchData(dataSnapshot);
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
        return devices;
    }
}
