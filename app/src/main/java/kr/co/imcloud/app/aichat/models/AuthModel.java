package kr.co.imcloud.app.aichat.models;

/**
 * Created by jeongmin on 17. 3. 22.
 */

public class AuthModel extends BaseModel {

    public String getToken() {
        return this.getString("token");
    }
}
