package sarah.nci.ie.reminder.db_Firebase;

/**
 * Define the device's attributes.
 * Each device'll contains a deviceId, nickname, address, distance, extra;
 */

public class Device {
    private String deviceId, nickname, qrCode, address, distance, extra;

    public Device(){

    }

    public Device(String deviceId, String nickname, String qrCode, String address, String distance, String extra) {
        this.deviceId = deviceId;
        this.nickname = nickname;
        this.qrCode = qrCode;
        this.address = address;
        this.distance = distance;
        this.extra = extra;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
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
