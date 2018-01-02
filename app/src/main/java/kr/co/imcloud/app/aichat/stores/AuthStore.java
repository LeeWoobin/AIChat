package kr.co.imcloud.app.aichat.stores;

import android.os.Message;
import android.util.Log;

import com.crazyup.utils.AppConfig;
import com.crazyup.utils.LocationUtil;
import com.crazyup.utils.retrofit2.ApiError;
import com.crazyup.utils.retrofit2.ErrorUtils;
import com.crazyup.utils.retrofit2.RetrofitUtil;

import org.json.JSONObject;

import java.util.Map;

import kr.co.imcloud.app.aichat.models.AuthModel;
import kr.co.imcloud.app.aichat.models.ConnectInfoModel;
import kr.co.imcloud.app.aichat.models.LastAccessModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by jeongmin on 17. 3. 17.
 */

public class AuthStore extends MsgBase {

    private static final String TAG = "AuthStore";

    static final int MSG_START = MSG_START_STORE;

    public static final int MSG_AUTH = MSG_START+1;
    public static final int MSG_WaitAddie = MSG_START+3;

    static final int MSG_END = MSG_START+100;

    private AuthModel authModel;
    private LastAccessModel lastAccess;
    public ConnectInfoModel connectInfo = new ConnectInfoModel();

    private static AuthStore _inst = null;

    public static AuthStore inst() {
        if (_inst == null) {
            _inst = new AuthStore();
//            Log.d(TAG, "inst(), _inst="+_inst);
        }
        return _inst;
    }

    private AuthStore() {
    }

    void handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_AUTH: {
                this.handleAuth(msg);
                break;
            }
        }
    }

    @Override
    public void clear() {
    }

    public String getAuthKey() {
        return this.connectInfo.getKey();
    }

    public String getRoomKey() {
        return this.connectInfo.get_room_id();
    }

    public void reqAuth() {
        this.getBackHandler().removeMessages(MSG_AUTH);
        Message msg = this.makeMessage(MSG_AUTH);
        this.sendMessageDelayed(msg, 1000);
    }

    private void handleAuth(final Message msg) {
        final Message msgs = Message.obtain(msg);

        IAuth iAuth = RetrofitUtil.getRetrofit().create(IAuth.class);
        Call<AuthModel> call = iAuth.get(AppConfig.CompanyKey);
        call.enqueue(new Callback<AuthModel>() {
            @Override
            public void onResponse(Call<AuthModel> call, Response<AuthModel> response) {
                if (response.isSuccessful()) {
                    authModel = response.body();
                    Log.d(TAG, "handleAuth(), token=" +  authModel.getToken());
                    connectInfo.setKey(authModel.getToken());
                    handleConnectInfo(msgs);

                    return;
                }
                else {
                    try {
                        ApiError error = ErrorUtils.parseError(response);
                        error.buildMessage(msgs.getData());
                        Log.d(TAG, "handleAuth(), error=" +  error);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    notifyError(msgs);
                    return;
                }
            }

            @Override
            public void onFailure(Call<AuthModel> call, Throwable t) {
                Log.e(TAG, "handleAuth(), t=" +  t.getMessage());
                notifyError(msgs);
            }
        });
    }
    /*
        public AuthModel getModel() {
            return authModel;
        }

        public void setModel(AuthModel authModel) {
            this.authModel = authModel;
        }

        public void reqGetChannels() {
            this.getBackHandler().removeMessages(MSG_AUTH);
            Message msg = this.makeMessage(MSG_AUTH);
            this.sendMessage(msg);
        }

        private void handleGetChannels(Message msg) {
            final Message msgs = Message.obtain(msg);

            IAuth iAuth = RetrofitUtil.getRetrofit().create(IAuth.class);
            Call<ChannelsModel> call = iAuth.getChannels(this.getModel().getToken());
            call.enqueue(new Callback<ChannelsModel>() {
                @Override
                public void onResponse(Call<ChannelsModel> call, Response<ChannelsModel> response) {
                    if (response.isSuccessful()) {
                        channelsModel = response.body();
                        Log.d(TAG, "handleGetChannels(), count=" +  channelsModel.getCount());
                        List<ChannelModel> list = channelsModel.getChannels();
                        chatChannel = channelsModel.getTestChannel();
                        handleOpenChannel(msgs);
    //                    notifyMessage(msgs);
                        return;
                    }
                    else {
                        try {
                            ApiError error = ErrorUtils.parseError(response);
                            error.buildMessage(msgs.getData());
                            Log.d(TAG, "handleGetChannels(), error=" +  error);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        notifyError(msgs);
                        return;
                    }
                }

                @Override
                public void onFailure(Call<ChannelsModel> call, Throwable t) {
                    Log.e(TAG, "handleGetChannels(), t=" +  t.getMessage());
                    notifyError(msgs);
                }
            });
        }

        private void handleOpenChannel(Message msg) {
            final Message msgs = Message.obtain(msg);

            IAuth iAuth = RetrofitUtil.getRetrofit().create(IAuth.class);
            Call<AuthModel> call = iAuth.OpenChannel(this.authModel.getToken(), this.chatChannel.getId());
            call.enqueue(new Callback<AuthModel>() {
                @Override
                public void onResponse(Call<AuthModel> call, Response<AuthModel> response) {
                    if (response.isSuccessful()) {
                        AuthModel model = response.body();
                        Log.d(TAG, "handleOpenChannel(), token=" + model.getToken());
                        connectInfo.setKey(authModel.getToken());
                        connectInfo.set_room_id(model.getToken());
                        handleConnectInfo(msgs);
                        return;
                    }
                    else {
                        try {
                            ApiError error = ErrorUtils.parseError(response);
                            if (error.getResponseCode() == 402) {
                                reqWaitAddie();
                                return;
                            }
                            error.buildMessage(msgs.getData());
                            Log.d(TAG, "handleOpenChannel(), error=" +  error);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        notifyError(msgs);
                        return;
                    }
                }

                @Override
                public void onFailure(Call<AuthModel> call, Throwable t) {
                    Log.e(TAG, "handleOpenChannel(), t=" +  t.getMessage());
                    notifyError(msgs);
                }
            });
        }
*/

        private void handleConnectInfo(Message msg) {
            final Message msgs = Message.obtain(msg);

            IAuth iAuth = RetrofitUtil.getRetrofit().create(IAuth.class);
            JSONObject json = new JSONObject(this.connectInfo);
            String jsonString = json.toString();
            Log.d(TAG, "handleConnectInfo(), jsonString=" + jsonString);
            this.connectInfo.setValue("username", "imcloud");
            this.connectInfo.setValue("y", LocationUtil.inst().getLatitude());
            this.connectInfo.setValue("x", LocationUtil.inst().getLongitude());
            Log.d(TAG, "handleConnectInfo(), x=" + LocationUtil.inst().getLongitude() + ", " + LocationUtil.inst().getLatitude());
            Call<AuthModel> call = iAuth.sendConnectionInfo(this.connectInfo);
            call.enqueue(new Callback<AuthModel>() {
                @Override
                public void onResponse(Call<AuthModel> call, Response<AuthModel> response) {
                    if (response.isSuccessful()) {
                        AuthModel model = response.body();
                        Log.d(TAG, "handleConnectInfo(), token=" + model.getToken());
//                        handleBanner(msgs);
                        notifyMessage(msgs);
                        return;
                    }
                    else {
                        try {
                            ApiError error = ErrorUtils.parseError(response);
                            error.buildMessage(msgs.getData());
                            Log.d(TAG, "handleConnectInfo(), error=" +  error);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        notifyError(msgs);
                        return;
                    }
                }

                @Override
                public void onFailure(Call<AuthModel> call, Throwable t) {
                    Log.e(TAG, "handleConnectInfo(), t=" +  t.getMessage());
                    notifyError(msgs);
                }
            });
        }
/*
        private void handleBanner(Message msg) {
            final Message msgs = Message.obtain(msg);

            IAuth iAuth = RetrofitUtil.getRetrofit().create(IAuth.class);
            Call<BannerModel> call = iAuth.getBannerInfo(this.authModel.getToken());
            call.enqueue(new Callback<BannerModel>() {
                @Override
                public void onResponse(Call<BannerModel> call, Response<BannerModel> response) {
                    if (response.isSuccessful()) {
                        ChatStore.inst().banner = response.body();
    //                    Log.d(TAG, "handleBanner(), token=" + model.getToken());
    //                    notifyMessage(msgs);
                        handleLastAccess(msgs);
                        return;
                    }
                    else {
                        try {
                            ApiError error = ErrorUtils.parseError(response);
                            error.buildMessage(msgs.getData());
                            Log.d(TAG, "handleBanner(), error=" +  error);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        notifyError(msgs);
                        return;
                    }
                }

                @Override
                public void onFailure(Call<BannerModel> call, Throwable t) {
                    Log.e(TAG, "handleBanner(), t=" +  t.getMessage());
                    notifyError(msgs);
                }
            });
        }
    */
    private void handleLastAccess(Message msg) {
        final Message msgs = Message.obtain(msg);

        IAuth iAuth = RetrofitUtil.getRetrofit().create(IAuth.class);
        Call<LastAccessModel> call = iAuth.getLastAccessTime(this.getAuthKey(), this.getRoomKey());
        call.enqueue(new Callback<LastAccessModel>() {
            @Override
            public void onResponse(Call<LastAccessModel> call, Response<LastAccessModel> response) {
                if (response.isSuccessful()) {
                    lastAccess = response.body();
//                    Log.d(TAG, "handleBanner(), token=" + model.getToken());
                    notifyMessage(msgs);
                    return;
                }
                else {
                    try {
                        ApiError error = ErrorUtils.parseError(response);
                        error.buildMessage(msgs.getData());
                        Log.d(TAG, "handleLastAccess(), error=" +  error);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    notifyError(msgs);
                    return;
                }
            }

            @Override
            public void onFailure(Call<LastAccessModel> call, Throwable t) {
                Log.e(TAG, "handleLastAccess(), t=" +  t.getMessage());
                notifyError(msgs);
            }
        });
    }
/*
    private void handleWaitAddie(Message msg) {

        final Message msgs = Message.obtain(msg);

        BaseModel model = new BaseModel();
        model.setString("key", this.authModel.getToken());
        model.setString("channel_id", this.chatChannel.getId());

        IAuth iAuth = RetrofitUtil.getRetrofit().create(IAuth.class);
        Call<BaseModel> call = iAuth.waitAddie(model);
        call.enqueue(new Callback<BaseModel>() {
            @Override
            public void onResponse(Call<BaseModel> call, Response<BaseModel> response) {
                if (response.isSuccessful()) {
                    BaseModel model = response.body();
//                    long wait_time = (long) model.get("wait_time");
                    int wait_time = getWaitTime(model);
                    if (wait_time == 0) {
                        handleOpenChannel(msgs);
                    } else {
                        reqWaitAddie();
                    }
                    return;
                }
                else {
                    try {
                        ApiError error = ErrorUtils.parseError(response);
                        if (error.getResponseCode() == 402) {
                            reqWaitAddie();
                            return;
                        }
                        error.buildMessage(msgs.getData());
                        Log.d(TAG, "handleWaitAddie(), error=" +  error);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    notifyError(msgs);
                    return;
                }
            }

            @Override
            public void onFailure(Call<BaseModel> call, Throwable t) {
                Log.e(TAG, "handleWaitAddie(), t=" +  t.getMessage());
                notifyError(msgs);
            }
        });

    }

    private int getWaitTime(BaseModel model) {
        Object wait_time_object = model.get("wait_time");
        if (wait_time_object != null) {
            if (wait_time_object instanceof Double) {
                return ((Double) wait_time_object).intValue();
            }
            return (int) wait_time_object;
        }
        return 100;
    }
*/
    public LastAccessModel getLastAccess() {
        return lastAccess;
    }

    private void reqWaitAddie() {
        Message msg = this.makeMessage(MSG_WaitAddie);
        this.sendMessageDelayed(msg, 1000);
    }


    public interface IAuth {
        @GET("/api/GetAuthorizationKey")
        Call<AuthModel> get(@Query("key") String key);
        @FormUrlEncoded
        @POST("/api/SendConnectionInfo")
        Call<AuthModel> sendConnectionInfo(@FieldMap(encoded = true) Map<String, Object> fields);
        /*
        @GET("/api/GetAddieList")
        Call<ChannelsModel> getChannels(@Query("key") String key);
        @GET("/api/OpenAddie")
        Call<AuthModel> OpenChannel(@Query("key") String key, @Query("channel_id") String channel_id);

        @FormUrlEncoded
        @POST("/api/WaitAddie")
        Call<BaseModel> waitAddie(@FieldMap(encoded = true) Map<String, Object> fields);
        @GET("/api/GetBannerInfo")
        Call<BannerModel> getBannerInfo(@Query("key") String key);
          */
        @GET("/api/GetLastAccessTime")
        Call<LastAccessModel> getLastAccessTime(@Query("key") String key, @Query("id") String id);

    }


}
