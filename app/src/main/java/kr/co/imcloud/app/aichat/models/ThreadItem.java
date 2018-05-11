package kr.co.imcloud.app.aichat.models;

import com.crazyup.app.CrazyApplication;
import com.crazyup.utils.AppConfig;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.media.CamcorderProfile.get;

/**
 * Created by jeongmin on 17. 3. 17.
 */

public class ThreadItem {

    public String message;
//    public final String details;
    public boolean isSend = false;
    public String userId;
    private String time;
    public String type;
    public String msgData;
    public JSONObject dataObj;
    public String imagePath;

    public ThreadItem(boolean isSend, String userId,String type, String message,String msgData) {
        this.isSend = isSend;
        this.userId = userId;

        if(type !=null) {
            this.type = type;
        }
        this.message = message;
        this.msgData = msgData;

//        this.details = details;
        Date now = new Date();
//        Locale locale = Locale.getDefault();
//        LocaleList list = CrazyApplication.getGlobalResources().getConfiguration().getLocales();
        Locale locale = CrazyApplication.getGlobalResources().getConfiguration().locale;
        SimpleDateFormat format = new SimpleDateFormat("a h:mm", Locale.KOREA);
        this.time = format.format(now);
    }

    public ThreadItem(boolean isSend, String userId, String type,String message ,String msgData, JSONObject dataObj) {
        this.isSend = isSend;
        this.userId = userId;
        this.type = type;
        this.message = message;
        this.msgData = msgData;
        this.dataObj = dataObj;
//        this.details = details;

        getImagePath(dataObj);

        Date now = new Date();
//        Locale locale = Locale.getDefault();
//        LocaleList list = CrazyApplication.getGlobalResources().getConfiguration().getLocales();
        Locale locale = CrazyApplication.getGlobalResources().getConfiguration().locale;
        SimpleDateFormat format = new SimpleDateFormat("a h:mm", Locale.KOREA);
        this.time = format.format(now);
    }

    @Override
    public String toString() {
        return message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void getImagePath(JSONObject dataObj){
        if (dataObj.has("IMAGE")) {
            try {
                imagePath = AppConfig.DEFAULT_APIERVER_URL+dataObj.getString("IMAGE");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
