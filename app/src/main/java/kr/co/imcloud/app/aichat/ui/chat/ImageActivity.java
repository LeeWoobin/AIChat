package kr.co.imcloud.app.aichat.ui.chat;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.crashlytics.android.Crashlytics;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import io.fabric.sdk.android.Fabric;
import kr.co.imcloud.app.aichat.R;

/**
 * Created by WB on 2018-05-10.
 */

public class ImageActivity extends AppCompatActivity {

    public ImageLoader imageLoader;
    public DisplayImageOptions options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppThemeNoTitle);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        String imagePath = getIntent().getStringExtra("imagePath");

        ImageView imageView = (ImageView)findViewById(R.id.image_item);

        /*
        Bitmap bitmap = null;
        BitmapFactory.Options bmOptions;
        bmOptions = new BitmapFactory.Options();
        InputStream in = null;
        try {
            in = OpenHttpConnection(imagePath);
            bitmap = BitmapFactory.decodeStream(in, null, bmOptions);
            in.close();
        } catch (IOException e1) {
        }

        BitmapDrawable bitmapDrawable = new BitmapDrawable(bitmap);
        imageView.setBackground(bitmapDrawable);
*/

        imageLoader = ImageLoader.getInstance();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(ImageActivity.this)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .writeDebugLogs() // Remove for release app
                .build();

        imageLoader.init(config);

        options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.no_img)
                .showImageOnFail(R.drawable.no_img)
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .considerExifParams(true)
                .postProcessor(new BitmapProcessor() {
                    @Override
                    public Bitmap process(Bitmap bmp) {
                        return Bitmap.createScaledBitmap(bmp, 500, 500, false);
                    }
                })
                .build();

        imageLoader.displayImage(imagePath, imageView, options);

        Button closeButton = (Button)findViewById(R.id.close_btn);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private InputStream OpenHttpConnection(String strURL) throws IOException {
        InputStream inputStream = null;
        URL url = new URL(strURL);
        URLConnection conn = url.openConnection();

        try{
            HttpURLConnection httpConn = (HttpURLConnection)conn;
            httpConn.setRequestMethod("GET");
            httpConn.connect();

            if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = httpConn.getInputStream();
            }
        }
        catch (Exception ex)
        {
        }
        return inputStream;
    }
}

