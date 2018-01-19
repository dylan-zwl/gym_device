package com.tapc.platform.model.scancode;

import com.android.module.retrofit.RetrofitClient;
import com.google.gson.Gson;
import com.tapc.platform.model.common.NoActionModel;
import com.tapc.platform.model.scancode.api.ScanCodeUrl;
import com.tapc.platform.model.scancode.api.ScanQrcodeService;
import com.tapc.platform.model.scancode.dao.request.UploadDeviceInfo;
import com.tapc.platform.model.scancode.dao.request.UserSportsData;
import com.tapc.platform.model.scancode.dao.response.ResponseDTO;
import com.tapc.platform.utils.GsonUtils;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import io.reactivex.functions.Consumer;
import okhttp3.ResponseBody;

/**
 * Created by Administrator on 2018/1/9.
 */

public class HttpRequest {
    public class GetLoginType {
        public static final String QRCODE = "qrcode";
        public static final String RANDOMCODE = "randomcode";
        public static final String ADVERTISMENT = "advertisement";
    }

    private static HttpRequest sHttpRequest;
    private ScanQrcodeService mService;
    private String deviceId;

    private HttpRequest() {
    }

    public static HttpRequest getInstance() {
        if (sHttpRequest == null) {
            synchronized (NoActionModel.class) {
                if (sHttpRequest == null) {
                    sHttpRequest = new HttpRequest();
                }
            }
        }
        return sHttpRequest;
    }

    public void init(String deviceId) {
        this.deviceId = deviceId;
        mService = RetrofitClient.getInstance().getService(ScanQrcodeService.class);
    }

    /**
     * 功能描述 : 获取二维码， 阻塞等待数据返回
     *
     * @return : 返回空值获取二维码失败
     */
    private Object mObject;

    public synchronized <T> T getCode(String type, final Type typeOfT) {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        mService.getCode(ScanCodeUrl.GET_CODE, deviceId, type).subscribe(new Consumer<ResponseBody>() {
            @Override
            public void accept(ResponseBody responseBody) throws Exception {
                ResponseDTO<T> responseDTO = new Gson().fromJson(responseBody.string(), typeOfT);
                if (responseDTO != null && responseDTO.getStatus() == 0) {
                    mObject = responseDTO.getResponse();
                    countDownLatch.countDown();
                }
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                countDownLatch.countDown();
            }
        });
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return (T) mObject;
    }

    /**
     * 功能描述 : 上传运动信息
     *
     * @param : type  机器类型
     * @param : version  机型
     * @param : parameter  参数
     */
    private Object mObject2;

    public synchronized <T> T uploadDeviceInformation(UploadDeviceInfo info, final Class<T> classOfT) {
        Map<String, Object> map = new HashMap<>();
        map.put("device_id", deviceId);
        map.put("type", info.getType());
        map.put("version", info.getModel());
        map.put("parameter", info.getParameter());
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        mService.commonRequest(ScanCodeUrl.UPLOAD_DEVICE_INFORMATION, map).subscribe(new Consumer<ResponseBody>() {
            @Override
            public void accept(ResponseBody responseBody) throws Exception {
                mObject2 = GsonUtils.fromJson(responseBody.string(), classOfT);
                countDownLatch.countDown();
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                countDownLatch.countDown();
            }
        });
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return (T) mObject2;
    }

    public void uploadSportData(UserSportsData sportsData) {
//        Map<String, Object> map = new HashMap<>();
//        map.put("device_id", deviceId);
//        map.put("user_id", info.getType());
//        map.put("plan_id", info.getModel());
//        map.put("time", info.getParameter());
//
//        map.put("calorie", info.getType());
//        map.put("distance", info.getModel());
//        map.put("customer_id", info.getParameter());
//
//        map.put("sport_data", info.getType());
//        map.put("weight", info.getModel());
//        map.put("sport_type", info.getParameter());
//        map.put("date", info.getParameter());

        mService.commonRequest(ScanCodeUrl.UPLOAD_SPORTS_DATA, );
    }
}
