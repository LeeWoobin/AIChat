package kr.co.imcloud.app.aichat.models;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;

/**
 * Created by jeongmin on 17. 3. 22.
 */

public class BannerModel extends BaseModel {

    public static final int TB_None = -1;
    public static final int TB_TextOnly = 0;
    public static final int TB_ImageOnly = 1;
    public static final int TB_All = 2;
    private static final String TAG = "BannerModel";

    private Bitmap bitmap;

    public BannerModel() {
    }

    public int get_type() {
        return this.getInt("type");
//        return TB_All;
    }

    public String get_text() {
        if (this.get_type() == TB_None) {
            return null;
        }
        String text = this.getString("text");
        Log.d(TAG, "get_text(), text="+text);
        if (this.get_type() == TB_ImageOnly) {
//            text = "<div><img src=\"" + this.get_img_url() + "\" alt=\"\" style=\"width: 100%; \"><br></div>";
            text = "<img src=\"" + this.get_img_url() + "\" alt=\"\" style=\"width: 100%; \">";
            Log.d(TAG, "get_text(), text="+text);
            String link = get_link_url();
            if (! TextUtils.isEmpty(link)) {
                text = "<a href=\"" + link + "\">" +  text + "</a>";
            }
            text = "<div>" + text + "</div>";
        }
        text = "<body style='margin:0;padding:0;'>" + text;
        return text;
    }

    public String get_img_url() {
//        if (this.get_type() == TB_None || this.get_type() == TB_TextOnly) {
//            return null;
//        }
        return this.getString("img_url");
//        return "http://218.238.188.74:20004/img/etc/photo_2017-04-21_15-52-321.jpg";
    }

    public String get_link_url() {
        return this.getString("link_url");
//        return "https://web.dominos.co.kr/main";
    }


    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
