package kr.co.imcloud.app.aichat.models;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by jeongmin on 17. 3. 17.
 */

public class ChatModel {
    private final List<ThreadItem> items = new ArrayList<ThreadItem>();


    public  void addItem(ThreadItem item) {
        items.add(item);
    }

    public List<ThreadItem> getItems() {
        return items;
    }

}
