package com.tapc.platform.model.scancode;

import com.android.module.retrofit.RetrofitClient;
import com.google.gson.Gson;
import com.tapc.platform.model.scancode.api.ScanCodeUrl;
import com.tapc.platform.model.scancode.api.ScanQrcodeService;
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

    private String deviceId;

    public HttpRequest(String deviceId) {
        this.deviceId = deviceId;
    }

    /**
     * 功能描述 : 获取二维码， 阻塞等待数据返回
     *
     * @return : 返回空值获取二维码失败
     */
    private Object mObject;

    public synchronized <T> T getCode(String type, final Type typeOfT) {
        Map<String, Object> map = new HashMap<>();
        map.put("device_id", deviceId);
        map.put("type", type);
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        RetrofitClient.getInstance().getService(ScanQrcodeService.class).getCode(ScanCodeUrl.GET_CODE, map)
                .subscribe(new Consumer<ResponseBody>() {
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
     * @param : version  版本号
     * @param : parameter  参数
     */
    private Object mObject2;

    public synchronized <T> T uploadDeviceInformation(String type, String version, String parameter, final Class<T>
            classOfT) {
        Map<String, Object> map = new HashMap<>();
        map.put("device_id", deviceId);
        map.put("type", type);
        map.put("version", version);
        map.put("parameter", parameter);
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        RetrofitClient.getInstance().getService(ScanQrcodeService.class).uploadDeviceInformation(ScanCodeUrl
                .UPLOAD_DEVICE_INFORMATION, map)
                .subscribe(new Consumer<ResponseBody>() {
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
}
