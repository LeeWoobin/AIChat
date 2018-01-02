package com.crazyup.utils.retrofit2;

import com.crazyup.utils.AppConfig;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by jeongmin on 17. 3. 20.
 */

public class RetrofitUtil {
    private static RetrofitUtil _inst = null;
    private Retrofit retrofit;

    public static RetrofitUtil inst() {
        if (_inst == null) {
            _inst = new RetrofitUtil();
//            Log.d(TAG, "inst(), _inst="+_inst);
        }
        return _inst;
    }

    private RetrofitUtil() {
    }

    public static Retrofit getRetrofit() {
            inst().retrofit = new Retrofit.Builder()
                    .baseUrl(AppConfig.DEFAULT_APIERVER_URL)
                    .addConverterFactory(GsonConverterFactory.create())
//                    .addConverterFactory(JsonConverterFactory.create())
                    .client(createOkHttpClient())
                    .build();

        return inst().retrofit;
    }

    private static OkHttpClient createOkHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.addInterceptor(interceptor);
//        builder.addNetworkInterceptor(new Interceptor() {
//            @Override
//            public Response intercept(Chain chain) throws IOException {
//                Request.Builder requestBuilder = chain.request().newBuilder();
//                requestBuilder.header("Content-Type", "application/json");
//                return chain.proceed(requestBuilder.build());
//            }
//        });
        return builder.build();
    }

}
