package sarah.nci.ie.reminder.db_Firebase;

/**
 * Created by User on 11/20/2017.
 */

public class Device {
    private String name, nickname, address, distance, extra;

    public Device(){

    }

    public Device(String name, String nickname, String address, String distance, String extra) {
        this.name = name;
        this.nickname = nickname;
        this.address = address;
        this.distance = distance;
        this.extra = extra;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
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

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }
}
