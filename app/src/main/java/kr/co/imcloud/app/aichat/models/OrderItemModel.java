package kr.co.imcloud.app.aichat.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.gson.internal.LinkedTreeMap;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

/**
 * Created by jeongmin on 17. 3. 22.
 */

public class OrderItemModel extends BaseModel {

    private static final String TAG = "OrderItemModel";

    private Bitmap bitmap;

    public OrderItemModel() {

    }

    public OrderItemModel(LinkedTreeMap<String, Object> data) {
        for( Map.Entry<String, Object> elem : data.entrySet() ){
            Object value = elem.getValue();
            String key = elem.getKey();
            this.setValue(key, value);
        }
    }

    public String getName() {
        return this.getString("name");
    }

    public String getProductType() {
        return this.getString("product_type");
    }

    public String getDough() {
        return this.getString("dough");
    }

    public String getSize() {
        String ret = this.getString("size");
        if (ret == null) {
            return null;
        }
        if (ret.length() == 0) {
            return null;
        }
        return ret;
    }


    public int getAmount() {
        return this.getInt("amount");
    }

    public String getAmount(String form) {
        return String.format(form, this.getAmount());
    }


    public int getProductCount() {
        return this.getInt("product_count");
    }

    public String getImgUrl() {
//        return AppConfig.DEFAUT_APIERVER_URL + this.getString("img_url");
        return this.getString("img_url");
    }

    public String getId() {
        return this.getString("id");
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public boolean handleLoadImage() {
        this.bitmap = this.getImageBitmap(this.getImgUrl());
        if (this.bitmap != null) {
            return true;
        }
        return false;
    }

    private Bitmap getImageBitmap(String url) {
        Bitmap bm = null;
        try {
            URL aURL = new URL(url);
            URLConnection conn = aURL.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            bm = BitmapFactory.decodeStream(bis);
            bis.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Error getting bitmap", e);
        }
        return bm;
    }

    public String getDetailString() {
        String ret = this.getName();
        String dough = this.getDough();
//        if (dough != null && dough.length() > 0) {
//            ret += " " + dough;// + " 도우";
//        }

        String size = this.getSize();

        if (size != null && (size.equals("L") || size.equals("M"))) {
            ret += "(" + size + ")";
        }
        ret += " : " + getPrice(this.getAmount()) + " X " + this.getProductCount() + " = " + getPrice(getSumAmount());
        return ret;
    }

    private String getPrice(int value) {
        return String.format("%,d", value);
    }

    public int getSumAmount() {
        return this.getAmount() * this.getProductCount();
    }
}
