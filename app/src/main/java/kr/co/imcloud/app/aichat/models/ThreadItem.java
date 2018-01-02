package kr.co.imcloud.app.aichat.models;

import com.crazyup.app.CrazyApplication;

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

    public String content;
//    public final String details;
    public boolean isSend = false;
    public String userId;
    private String time;
    public String type;
    public String contentType;
    public JSONObject dataObj;
    public String imagePath;
    public String[][] trtd ;
    public String[][] trtdColor;

    public ThreadItem(boolean isSend, String userId,String type, String content) {
        this.isSend = isSend;
        this.userId = userId;


        if(type !=null) {
            this.type = type;
        }
        if(content.contains("table")){
            if(content.contains("<br>")){
                int msgDivide = content.indexOf("<br>");
                String msg = content.substring(0,msgDivide);
                this.content= msg;
            }
//                    String a = "<table><tr><td bgcolor=#87CEFA>Category</td><td bgcolor=#FAFAD2>some text</td><td bgcolor=#FAFAD2>some text</td><td bgcolor=#FAFAD2>some text</td><td bgcolor=#FAFAD2>some text</td><td bgcolor=#FAFAD2>some text</td><td bgcolor=#FAFAD2>some text</td><td bgcolor=#FAFAD2>some text</td><td bgcolor=#FAFAD2>some text</td><td bgcolor=#FAFAD2>some text</td></tr><tr><td bgcolor=#B0E0E6>Sell-In Guide</td><td bgcolor=#DCDCDC>124537</td><td bgcolor=#DCDCDC>783531</td></tr><tr><td bgcolor=#B0E0E6>Demand</td></tr><tr><td bgcolor=#B0E0E6>Sell-Out</td></tr><tr><td bgcolor=#B0E0E6>Ch.Inventory</td></tr><tr><td bgcolor=#B0E0E6>WOS</td></tr>";
            getTableContent(content);
        }else{
            this.content = content;
        }

//        this.details = details;
        Date now = new Date();
//        Locale locale = Locale.getDefault();
//        LocaleList list = CrazyApplication.getGlobalResources().getConfiguration().getLocales();
        Locale locale = CrazyApplication.getGlobalResources().getConfiguration().locale;
        SimpleDateFormat format = new SimpleDateFormat("a h:mm", Locale.KOREA);
        this.time = format.format(now);
    }

    public ThreadItem(boolean isSend, String userId, String type,String content ,String contentType, JSONObject dataObj) {
        this.isSend = isSend;
        this.userId = userId;
        this.type = type;
        this.content = content;
        this.contentType = contentType;
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
        return content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void getImagePath(JSONObject dataObj){
        if (dataObj.has("img_path")) {
            try {
                imagePath = dataObj.getString("img_path");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void getTableContent(String htmlData){

        trtd = new String[6][9];
        trtdColor = new String[6][9];

        Document doc = Jsoup.parse(htmlData);
        Elements tables = doc.select("table");
        for (Element table : tables) {
            Elements trs = table.select("tr");

            for (int i = 0; i < 6; i++) {
                if(trs != null &&trs.size() >0 && trs.size() > i&& trs.get(i) != null ) {
                    Elements tds = trs.get(i).select("td");
                    for (int j = 0; j < 9; j++) {
                        if (tds != null && tds.size() > 0 && tds.size() > j && tds.get(j) != null ) {
                            trtd[i][j] = tds.get(j).text();
                            String rowItem = tds.get(j).toString();
                            if (rowItem.contains("bgcolor=")) {
                                int rowItemBgColorPoint = rowItem.indexOf("#");
                                trtdColor[i][j] = rowItem.substring(rowItemBgColorPoint, rowItemBgColorPoint + 7);
                            }
                        } else {
                            trtd[i][j] = "";
                        }
                    }
                }else{
                    for (int j = 0; j < 9; j++) {
                        trtd[i][j] = "";
                    }
                }
            }


            // trtd now contains the desired array for this table
        }
    }
}
