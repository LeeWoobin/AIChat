package kr.co.imcloud.app.aichat.stores;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Message;
import android.util.Log;

import com.crazyup.app.CrazyApplication;
import com.crazyup.utils.AppConfig;
import com.crazyup.utils.NetworkUtils;
import com.crazyup.utils.retrofit2.ApiError;
import com.crazyup.utils.retrofit2.ErrorUtils;
import com.crazyup.utils.retrofit2.RetrofitUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import kr.co.imcloud.app.aichat.models.BannerModel;
import kr.co.imcloud.app.aichat.models.BaseModel;
import kr.co.imcloud.app.aichat.models.ChatModel;
import kr.co.imcloud.app.aichat.models.OrderItemModel;
import kr.co.imcloud.app.aichat.models.OrderResultModel;
import kr.co.imcloud.app.aichat.models.ThreadItem;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

/**
 * Created by jeongmin on 17. 3. 17.
 */

public class ChatStore extends MsgBase {

    private static final String TAG = "ChatStore";

    static final int MSG_START = AuthStore.MSG_END;

    public static final int MSG_CHAT_SEND = MSG_START + 3;
    public static final int MSG_CHAT_RECV = MSG_START + 5;

    public static final int MSG_BannerImage = MSG_START + 7;
    public static final int MSG_ChatTime = MSG_START + 9;

    public static final int MSG_OrderEnd = MSG_START + 11;

    public static final int MSG_OrderResult = MSG_START + 13;

    public static final int MSG_LoadImage = MSG_START + 15;

    private static final int MSG_LoadItemsImage = MSG_START + 17;

    public static final int MSG_SendGrade = MSG_START + 19;

    public static final int MSG_CHAT_EMPTY_SEND = MSG_START + 21;

    public static final int MSG_HIDE_KEYBOARD = MSG_START + 23;

    public static final int MSG_OrderCancel = MSG_START + 25;


    static final int MSG_END = MSG_START + 100;

    private static final String ORDER_ENDED_STR = "{DOMINO_ORDER_ENDED}";
    private static final String ORDER_CANCELED_STR = "{DOMINO_ORDER_CANCELED}";

    private ChatModel chatModel;
    private String userId;
    BannerModel banner;
    private OrderResultModel orderResultModel;
    private int netRetryCnt = 0;

    //    public static final int ChatTimePoolingInterval = 60*1000;
    public static final int ChatTimePoolingInterval = 1000;
    private int chatTime = 0; // minute

    private static ChatStore _inst = null;

    public static ChatStore inst() {
        if (_inst == null) {
            _inst = new ChatStore();
//            Log.d(TAG, "inst(), _inst="+_inst);
        }
        return _inst;
    }

    private ChatStore() {
    }

    void handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_CHAT_SEND: {
                this.handleChatSend(msg);
                break;
            }
//            case MSG_BannerImage: {
//                this.handleLoadBannerImage(msg);
//                break;
//            }
            case MSG_ChatTime: {
                this.handleChatTimer(msg);
                break;
            }
//            case MSG_OrderResult: {
//                this.handleGetCompletedOrderInfo(msg);
//                break;
//            }
//            case MSG_LoadItemsImage: {
//                this.handleLoadItemsImage(msg);
//                break;
//            }
//            case MSG_LoadImage: {
//                this.handleLoadImage(msg);
//                break;
//            }
            case MSG_SendGrade: {
                this.handleSendGrade(msg);
                break;
            }
//            case MSG_OrderEnd: {
//                this.handleOrderEnd(msg);
//                break;
//            }
//            case MSG_OrderCancel: {
//                this.notifyMessage(Message.obtain(msg));
//                break;
//            }
            case MSG_CHAT_EMPTY_SEND: {
                this.handleSendEmptyChat(msg);
                break;
            }


        }
    }

    private void handleOrderEnd(Message msg) {
        this.notifyMessage(Message.obtain(msg));
    }

    @Override
    public void clear() {

    }

    public BannerModel getBanner() {
//        return null;
        return this.banner;
    }

    public void startChatTimer() {
        chatTime = 0;
        this.getBackHandler().removeMessages(MSG_ChatTime);
        this.sendMessageDelayed(this.makeMessage(MSG_ChatTime), ChatTimePoolingInterval + 1000);
    }

    public void handleChatTimer(Message msg) {
        chatTime++;
        this.notifyMessage(Message.obtain(msg));
        this.getBackHandler().removeMessages(MSG_ChatTime);
        this.sendMessageDelayed(this.makeMessage(MSG_ChatTime), ChatTimePoolingInterval);
    }

    public void reqHideKeyboard() {
        this.notifyMessage(MSG_HIDE_KEYBOARD);
    }

    public void reqSendEmptyChat() {
        Message msg = this.makeMessage(MSG_CHAT_EMPTY_SEND);
//        msg.getData().putString("message", "");
        this.sendMessageDelayed(msg, 500);
    }

    private void handleSendEmptyChat(Message msg) {
        final Message msgs = Message.obtain(msg);
        IChat iChat = RetrofitUtil.getRetrofit().create(IChat.class);
        Call<BaseModel> call;
//        if(AppConfig.DEFAULT_APIERVER_URL.equals("http://211.58.205.50:12000")) {
            call = iChat.sendMessage(createSendModel("HELLO:", "2"));
//        }else{
//            call = iChat.sendMessage(createSendModel("HELLO:", "1"));
//        }
        call.enqueue(new Callback<BaseModel>() {
            @Override
            public void onResponse(Call<BaseModel> call, Response<BaseModel> response) {
                Message msgr = Message.obtain(msgs);
                msgr.what = MSG_CHAT_RECV;
                if (response.isSuccessful()) {
                    JSONObject ResponseObject = new JSONObject(response.body());
                    try {
                        JSONArray msg_list = ResponseObject.getJSONArray("msg_list");

                        if (msg_list != null) {

                            for (int i = 0; i < msg_list.length(); i++) {


                                JSONObject jsonObject = msg_list.getJSONObject(i);
                                ThreadItem item = null;
                                String msg = null;
                                String msgType = null;
                                String contentType = null;

                                if (jsonObject.has("msg")) {
                                    msg = jsonObject.getString("msg");
                                }
                                if (jsonObject.has("msg_type")) {
                                    msgType = jsonObject.getString("msg_type");
                                }
                                item = new ThreadItem(false, "bot", msgType, msg);

                                if(item != null) {
                                    chatModel.addItem(item);
                                    notifyMessage(msgr);
                                }
                            }
                        }
                    } catch (Exception e) {

                    }
                    return;

                } else {
                    try {
                        ApiError error = ErrorUtils.parseError(response);
                        error.buildMessage(msgr.getData());
                        Log.d(TAG, "handleChatSend(), error=" + error);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    notifyError(msgr);
                    return;
                }
            }

            @Override
            public void onFailure(Call<BaseModel> call, Throwable t) {
                Log.e(TAG, "handleChatSend(), t=" + t.getMessage());
                notifyError(msgs);
            }
        });
    }

    private void handleCloseAddie(Message msg) {
        final Message msgs = Message.obtain(msg);

        BaseModel model = new BaseModel();
        model.setValue("key", AuthStore.inst().getAuthKey());

        IChat iChat = RetrofitUtil.getRetrofit().create(IChat.class);

        Call<BaseModel> call = iChat.closeAddie(model);
        call.enqueue(new Callback<BaseModel>() {
            @Override
            public void onResponse(Call<BaseModel> call, Response<BaseModel> response) {
                if (response.isSuccessful()) {
                    BaseModel model = response.body();
                    notifyMessage(msgs);
                    return;
                } else {

                    return;
                }
            }

            @Override
            public void onFailure(Call<BaseModel> call, Throwable t) {
                Log.e(TAG, "handleCloseAddie(), t=" + t.getMessage());
//                notifyError(msgs);
            }
        });

    }


    public int reqSendChat(String message) {
        if (!this.isEnableNet()) {
            netRetryCnt++;
            return netRetryCnt;
        }
        netRetryCnt = 0;
        Message msg = this.makeMessage(MSG_CHAT_SEND);
        msg.getData().putString("message", message);
        this.sendMessage(msg);
        return netRetryCnt;
    }

    private boolean isEnableNet() {
        int net = NetworkUtils.getConnectivityStatus(CrazyApplication.getContext());
        if (net == NetworkUtils.TYPE_WIFI || net == NetworkUtils.TYPE_MOBILE) {
            return true;
        }
        return false;
    }

    private BaseModel createSendModel(String message, String msgType) {
        BaseModel model = new BaseModel();
        model.setValue("key", AuthStore.inst().getAuthKey());
        model.setString("msg_type",msgType);
        model.setString("message", message);
        model.setString("msg_sub","");

//        if(msgType.equals("2")) {
//            model.setString("message", "윤선생");
//            model.setString("msg_sub", "{\"기능\":\"\",\"번호\":\"111\",\"이름\":\"방그네\",\"말하길\":\"" + message + "\"}");
//        }else{
//            model.setString("message", message);
//            model.setString("msg_sub","");
//        }
        return model;
    }

    private void handleChatSend(Message msg) {
        final Message msgs = Message.obtain(msg);
        String message = msg.getData().getString("message");
        int id = this.getThreadCount();


        IChat iChat = RetrofitUtil.getRetrofit().create(IChat.class);

        String msgType;

//        if(AppConfig.DEFAULT_APIERVER_URL.equals("http://211.58.205.50:12000")) {
//            msgType = "2";
//        }else{
            msgType = "2";
//        }


        Call<BaseModel> call= iChat.sendMessage(createSendModel(message, msgType));
        call.enqueue(new Callback<BaseModel>() {
            @Override
            public void onResponse(Call<BaseModel> call, Response<BaseModel> response) {
                Message msgr = Message.obtain(msgs);
                msgr.what = MSG_CHAT_RECV;
                if (response.isSuccessful()) {
                    JSONObject ResponseObject = new JSONObject(response.body());
                    try {
                        JSONArray msg_list = ResponseObject.getJSONArray("msg_list");

                        if (msg_list != null) {

                            for (int i = 0; i < msg_list.length(); i++) {

                                JSONObject jsonObject = msg_list.getJSONObject(i);
                                ThreadItem item = null;
                                String msg = null;
                                String msgType = null;
                                String contentType = null;


                                if (jsonObject.has("msg")) {
                                    msg = jsonObject.getString("msg");
                                }
                                if (jsonObject.has("msg_type")) {
                                    msgType = jsonObject.getString("msg_type");
                                }

                                if (jsonObject.has("content_type")) {
                                    contentType = jsonObject.getString("content_type");
                                }

                                if (jsonObject.has("content")) {
                                    if (jsonObject.isNull("content")) {
                                        item = new ThreadItem(false, "bot", msgType, msg);
                                    } else {
                                        JSONObject dataObj = jsonObject.getJSONObject("content");

                                        if (dataObj != null) {

                                            item = new ThreadItem(false, "bot", msgType, msg, contentType, dataObj);
                                        }
                                    }
                                } else {
                                    item = new ThreadItem(false, "bot", msgType, msg);
                                }

                                if (item != null) {
                                    chatModel.addItem(item);
                                    notifyMessage(msgr);
                                }
                            }
                        }
                    } catch (Exception e) {

                    }
                    return;
                } else {
                    try {
                        ApiError error = ErrorUtils.parseError(response);
                        error.buildMessage(msgr.getData());
                        Log.d(TAG, "handleChatSend(), error=" + error);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    notifyError(msgr);
                    return;
                }

            }

            @Override
            public void onFailure(Call<BaseModel> call, Throwable t) {
                Log.e(TAG, "handleChatSend(), t=" + t.getMessage());
                notifyError(msgs);
            }
        });
        ThreadItem item = new ThreadItem(true, "bot", msgType,message);
                chatModel.addItem(item);
                this.notifyMessage(msgs);

//        if(msgType != null) {
//            if (!msgType.equals("2")) {
//
//            }
//        }
    }

    public String trimCr(String str) {
        int len = str.length();
        int st = 0;

        while ((st < len) && (str.charAt(st) <= '\n')) {
            st++;
        }
        while ((st < len) && (str.charAt(len - 1) <= '\n')) {
            len--;
        }
        return ((st > 0) || (len < str.length())) ? str.substring(st, len) : str;
    }


    public ChatModel getChatModel() {
        return chatModel;
    }

    public void setChatModel(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getThreadCount() {
        return this.chatModel.getItems().size();
    }

    public void reqLoadBannerImage() {
        Message msg = this.makeMessage(MSG_BannerImage);
        this.sendMessage(msg);
    }

    private void handleLoadBannerImage(Message msg) {
        String imgUrl = this.banner.get_img_url();
        if (imgUrl == null) {
            return;
        }

        URL url = null;
        try {
//            url = new URL(AppConfig.DEFAUT_APIERVER_URL + imgUrl);
            url = new URL(imgUrl);
            Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            this.banner.setBitmap(bmp);
            this.notifyMessage(msg);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reqGetCompletedOrderInfo() {
        Message msg = this.makeMessage(MSG_OrderResult);
        this.sendMessage(msg);
    }

    private void handleGetCompletedOrderInfo(Message msg) {
        IChat iChat = RetrofitUtil.getRetrofit().create(IChat.class);
        final Message msgs = Message.obtain(msg);
        this.orderResultModel = null;

        Call<OrderResultModel> call = iChat.getCompletedOrderInfo(AuthStore.inst().getAuthKey());
        call.enqueue(new Callback<OrderResultModel>() {
            @Override
            public void onResponse(Call<OrderResultModel> call, Response<OrderResultModel> response) {
                if (response.isSuccessful()) {
                    orderResultModel = response.body();
                    orderResultModel.buildItems();
//                    notifyMessage(msgs);
                    reqLoadItemsImage();
                    return;
                } else {
                    try {
                        ApiError error = ErrorUtils.parseError(response);
                        error.buildMessage(msgs.getData());
                        Log.d(TAG, "handleGetCompletedOrderInfo(), error=" + error);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    notifyError(msgs);
                    return;
                }

            }

            @Override
            public void onFailure(Call<OrderResultModel> call, Throwable t) {
                Log.e(TAG, "handleGetCompletedOrderInfo(), t=" + t.getMessage());
                notifyError(msgs);
            }
        });
    }

    public int getChatTime() {
        return chatTime;
    }

    public OrderResultModel getOrderResultModel() {
        return orderResultModel;
    }

    public void reqLoadImage(OrderItemModel mItem) {
        Message msg = this.makeMessage(MSG_LoadImage);
        msg.getData().putString("itemId", mItem.getId());
        this.sendMessage(msg);
    }

    private void handleLoadImage(Message msg) {
        boolean result = this.orderResultModel.handleLoadImage(msg);
        this.notifyMessage(Message.obtain(msg));
    }

    public void reqLoadItemsImage() {
        this.sendMessage(MSG_LoadItemsImage);
    }

    private void handleLoadItemsImage(Message msg) {
        this.orderResultModel.handleLoadItemsImage(msg);
        this.notifyMessage(MSG_OrderResult);
    }


    public void setTestOrderItems(List<OrderItemModel> items) {
        if (orderResultModel == null) {
            orderResultModel = new OrderResultModel();
        }
        orderResultModel.setTestOrderItems(items);
    }

    public void reqSendGrade(int value) {
        Message msg = this.makeMessage(MSG_SendGrade);
        msg.getData().putInt("grade", value);
        this.sendMessage(msg);
    }

    private void handleSendGrade(Message msg) {
        int grade = msg.getData().getInt("grade");
        Log.d(TAG, "handleSendGrade(), grade=" + grade);

        IChat iChat = RetrofitUtil.getRetrofit().create(IChat.class);

        BaseModel model = new BaseModel();
        model.setValue("key", AuthStore.inst().getAuthKey());
        model.setValue("grade", grade);

        final Message msgr = Message.obtain(msg);

        Call<BaseModel> call = iChat.sendGrade(model);
        call.enqueue(new Callback<BaseModel>() {
            @Override
            public void onResponse(Call<BaseModel> call, Response<BaseModel> response) {
                handleCloseAddie(msgr);
//                notifyMessage(msgr);
            }

            @Override
            public void onFailure(Call<BaseModel> call, Throwable t) {
                Log.e(TAG, "handleSendGrade(), t=" + t.getMessage());
                notifyMessage(msgr);
            }
        });
    }


    public interface IChat {
        @FormUrlEncoded
        @POST("/api/SendMessage")
        Call<BaseModel> sendMessage(@FieldMap(encoded = true) Map<String, Object> fields);

        @GET("/api/GetCompletedOrderInfo")
        Call<OrderResultModel> getCompletedOrderInfo(@Query("key") String key);

        @FormUrlEncoded
        @PUT("/api/SendGrade")
        Call<BaseModel> sendGrade(@FieldMap(encoded = true) Map<String, Object> fields);

        @FormUrlEncoded
        @POST("/api/CloseAddie")
        Call<BaseModel> closeAddie(@FieldMap(encoded = true) Map<String, Object> fields);

    }


}
