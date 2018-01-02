package kr.co.imcloud.app.aichat.models;

import com.google.gson.internal.LinkedTreeMap;

import java.util.Map;

/**
 * Created by jeongmin on 17. 3. 22.
 */

public class ChannelModel extends BaseModel {

    public ChannelModel() {
    }

    public ChannelModel(LinkedTreeMap<String, Object> item) {
        for( Map.Entry<String, Object> elem : item.entrySet() ){
            this.put(elem.getKey(), elem.getValue());
        }
    }

    public String getId() {
        return this.getString("id");
    }
    public String getName() {
        return this.getString("name");
    }
    public String getDesc() {
        return this.getString("desc");
    }
}
