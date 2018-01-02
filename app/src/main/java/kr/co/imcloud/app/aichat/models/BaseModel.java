package kr.co.imcloud.app.aichat.models;

import java.util.HashMap;

/**
 * Created by jeongmin on 17. 3. 22.
 */

public class BaseModel extends HashMap {

    public int getInt(String attr) {
        Object obj = this.get(attr);
        if (obj != null) {
            if (obj instanceof Double) {
                return ((Double) obj).intValue();
            }
            return (int) obj;
        }
        return 0;
    }

    public String getString(String attr) {
        Object obj = this.get(attr);
        if (obj != null) {
            return (String) obj;
        }
        return null;
    }

    public boolean getBoolean(String attr, boolean defaultValue) {
        Object obj = this.get(attr);
        if (obj != null) {
            return (boolean) obj;
        }
        return defaultValue;
    }

    public void setValue(String attr, Object obj) {
        this.put(attr, obj);
    }

    public void setString(String attr, String value) {
        this.setValue(attr, value);
    }

}
