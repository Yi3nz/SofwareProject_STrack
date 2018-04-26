package sarah.nci.ie.reminder;

/**
 * Created by User on 11/20/2017.
 */

public class Device {
    private String name, address, distance;

    public Device(String name, String address, String distance) {
        this.name = name;
        this.address = address;
        this.distance = distance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }
}
