package kr.co.imcloud.app.aichat.models;

/**
 * Created by jeongmin on 17. 3. 22.
 */

public class LastAccessModel extends BaseModel {

    public LastAccessModel() {
    }

    public boolean isFirstAccess() {
        return this.getBoolean("first_access", true);
    }

    public String get_last_time() {
        if (this.isFirstAccess()) {
            return null;
        }
        return this.getString("last_time");
    }
}
