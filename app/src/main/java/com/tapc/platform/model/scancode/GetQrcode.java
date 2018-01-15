package com.tapc.platform.model.scancode;

import com.android.module.retrofit.RetrofitClient;
import com.tapc.platform.model.scancode.api.ScanQrcodeService;
import com.tapc.platform.model.scancode.url.ScanCodeUrl;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.functions.Consumer;
import okhttp3.ResponseBody;

/**
 * Created by Administrator on 2018/1/9.
 */

public class GetQrcode {
    private class GetLoginType {
        private static final String QRCODE = "qrcode";
        private static final String RANDOMCODE = "randomcode";
        private static final String ADVERTISMENT = "advertisement";
    }

    private String deviceId;
    private boolean mNeedChangeQrcode = true;

    public GetQrcode(String deviceId) {
        this.deviceId = deviceId;
    }

    public void setNeedChangeQrcode(boolean isNeed) {
        mNeedChangeQrcode = isNeed;
    }

    public String getQrcode() {
        final Object o = new Object();
        Map<String, Object> map = getMap(GetLoginType.QRCODE);
        RetrofitClient.getInstance().getService(ScanQrcodeService.class).getCode(ScanCodeUrl.GET_CODE, map)
                .subscribe(new Consumer<ResponseBody>() {
                    @Override
                    public void accept(ResponseBody responseBody) throws Exception {

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        synchronized (o) {
                            notify();
                        }
                    }
                });
        synchronized (o) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public String getRandomcode() {
        Map<String, Object> map = getMap(GetLoginType.RANDOMCODE);
        RetrofitClient.getInstance().getService(ScanQrcodeService.class).getCode(ScanCodeUrl.GET_CODE, map)
                .subscribe(new Consumer<ResponseBody>() {
                    @Override
                    public void accept(ResponseBody responseBody) throws Exception {

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
    }

    private Map<String, Object> getMap(String type) {
        Map<String, Object> map = new HashMap<>();
        map.put("device_id", deviceId);
        map.put("type", type);
        return map;
    }
}
