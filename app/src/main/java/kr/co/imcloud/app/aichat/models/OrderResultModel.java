package kr.co.imcloud.app.aichat.models;

import android.os.Message;

import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jeongmin on 17. 3. 22.
 */

public class OrderResultModel extends BaseModel {

    private static final String TAG = "OrderResultModel";

    private List<OrderItemModel> items = new ArrayList<>();


    public OrderResultModel() {
    }

    public int getCount() {
        return this.getInt("count");
    }

    public int getTotalAmount() {
        return this.getInt("total_amount");
    }

    public String getTotalAmount(String form) {
        return String.format(form, this.getTotalAmount());
    }


    public int getTotalDiscountedAmount() {
        return this.getInt("total_discounted_amount");
    }

    public String getTotalDiscountedAmount(String form) {
        return String.format(form, this.getTotalDiscountedAmount());
    }


    public int getDiscountRate() {
        Object value = this.get("discount_rate");
        if (value instanceof String) {
            return Integer.parseInt((String) value);
        }
        return this.getInt("discount_rate");
    }

    public String getDiscountRate(String form) {
        String dd = this.getDiscountRate() + "%";
        return String.format(form, dd);
    }


    public String getRequestMsg() {
        return this.getString("request_msg");
    }

    public String getRequestMsg(String form) {
        return String.format(form, this.getRequestMsg());
    }


    public String getAddressFull() {
        return this.getString("address_full");
    }

    public String getAddressFull(String form) {
        return String.format(form, this.getAddressFull());
    }


    public void buildItems() {
        items = new ArrayList<>();
        Object obj = this.get("items");
        if (obj != null) {
            List list = (List) obj;
            for (Object item: list) {
                OrderItemModel model = new OrderItemModel((LinkedTreeMap<String, Object>) item);
                items.add(model);
//                model.handleLoadImage();
            }
        }
    }

    public List<OrderItemModel> getItems() {
        return items;
//        List<OrderItemModel> ret = new ArrayList<>();
//        if (this.items.size() > 0) {
//            ret.add(this.items.get(0));
//            ret.add(this.items.get(1));
//            ret.add(this.items.get(2));
//            ret.add(this.items.get(3));
//        }
//
//        return ret;
    }


    public boolean handleLoadImage(Message msg) {
        String itemId = msg.getData().getString("itemId");
        OrderItemModel item = this.findItem(itemId);
        if (item == null) {
            return false;
        }
        return item.handleLoadImage();
    }

    private OrderItemModel findItem(String id) {
        if (this.items == null) {
            return null;
        }
        for (OrderItemModel model: this.items) {
            if (model.getId().equals(id)) {
                return model;
            }
        }
        return null;
    }

    public void setTestOrderItems(List<OrderItemModel> items) {
        this.items = items;
    }

    public void handleLoadItemsImage(Message msg) {
        for (OrderItemModel model: this.items) {
            model.handleLoadImage();
        }
    }
}
