package kr.co.imcloud.app.aichat.models;

import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jeongmin on 17. 3. 22.
 */

public class ChannelsModel extends BaseModel {

    private List<ChannelModel> items = new ArrayList<>();

    public int getCount() {
        return this.getInt("count");
    }

    public List<ChannelModel> getChannels() {
        Object obj = this.get("channel");
        List<LinkedTreeMap<String, Object>> list = (List<LinkedTreeMap<String, Object>>) obj;

        this.items = new ArrayList<>();
        for (LinkedTreeMap<String, Object> item: list) {
            this.items.add(new ChannelModel(item));
        }
        return this.items;
    }

    public ChannelModel getTestChannel() {
        for (ChannelModel model: this.items) {
            if (model.getName().equals("테스트")) {
                return model;
            }
        }
        return null;
    }
}
