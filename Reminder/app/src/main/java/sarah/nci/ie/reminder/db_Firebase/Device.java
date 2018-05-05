package sarah.nci.ie.reminder.db_Firebase;

/**
 * Define the device's attributes.
 * Each device'll contains a deviceId, nickname, address, distance, extra;
 */

public class Device {
    private String deviceId, nickname, qrCode, address, latitude, longitude, distance, extra;

    public Device(){

    }

    public Device(String deviceId, String nickname, String qrCode, String address, String latitude, String longitude, String distance, String extra) {
        this.deviceId = deviceId;
        this.nickname = nickname;
        this.qrCode = qrCode;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
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

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
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
