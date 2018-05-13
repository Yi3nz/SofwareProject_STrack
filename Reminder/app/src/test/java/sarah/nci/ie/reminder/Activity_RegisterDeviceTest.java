package sarah.nci.ie.reminder;

import android.util.Log;
import android.widget.Toast;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class Activity_RegisterDeviceTest {

    List<String> qrCodeList;

    @Test
    public void onCreate() {
        //Initiate qrCodeList with 3 codes.
        qrCodeList = new ArrayList<>();
        qrCodeList.add("STrackCode1");
        qrCodeList.add("STrackCode2");
        qrCodeList.add("STrackCode3");

        //Define a scanned result code and check validation.
        String qrCodeResult = "STrackCode3";
        checkQRCode(qrCodeResult);
    }

    //Function - Check if the qrCode is valid
    private void checkQRCode(String qrCode){

        //For every list item:
        for(int i=0; i<qrCodeList.size(); i++){
            //Process to register if valid
            if(qrCode.equals(qrCodeList.get(i))){
                System.out.println("Valid");
                break;
            }else if(!qrCode.equals(qrCodeList.get(i))){
                System.out.println("Not Valid");
            }
        }

    }

    @Test
    public void registerClick() {
    }
}


