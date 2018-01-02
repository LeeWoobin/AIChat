package kr.co.imcloud.app.aichat.models;

import com.crazyup.utils.IpUtil;

/**
 * Created by jeongmin on 17. 3. 22.
 */

public class ConnectInfoModel extends BaseModel {

    public ConnectInfoModel() {
        this.setDevice();
        setIp();
    }

    private void setDevice() {
        this.setValue("device", 0);
    }

    private void setIp() {
        this.setValue("ip", IpUtil.getIPAddress(true));
    }

    public void setUserId(String userId) {
        this.setValue("id", userId);
    }

    public void setKey(String key) {
        this.setValue("key", key);
    }

    public String getKey() {
        return this.getString("key");
    }

    public void set_room_id(String key) {
        this.setValue("room_id", key);
    }

    public String get_room_id() {
        return this.getString("room_id");
    }


    public void setCustomerName(String customerName) {
        this.setString("customer_name", customerName);
    }

    public void setCustomerHpNo(String customerHpNo) {
        this.setString("customer_phone", customerHpNo);
    }

}
