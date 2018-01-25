package com.tapc.platform.model.scancode.dao.response;

/**
 * Created by Administrator on 2018/1/23.
 */

public class QrConfig {
    private String version;
    private String code_site_id;
    private String backimg;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getCode_site_id() {
        return code_site_id;
    }

    public void setCode_site_id(String code_site_id) {
        this.code_site_id = code_site_id;
    }

    public String getBackimg() {
        return backimg;
    }

    public void setBackimg(String backimg) {
        this.backimg = backimg;
    }
}
