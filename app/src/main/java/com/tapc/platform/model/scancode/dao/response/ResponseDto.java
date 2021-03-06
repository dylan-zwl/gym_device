package com.tapc.platform.model.scancode.dao.response;

import java.io.Serializable;

@SuppressWarnings("serial")
public class ResponseDto<T> implements Serializable {

    private int status; // 状态码
    private String message; // 请求结果信息
    private T response; // 请求返回的数据

    public ResponseDto() {
        super();
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getResponse() {
        return response;
    }

    public void setResponse(T response) {
        this.response = response;
    }


    @Override
    public String toString() {
        return "Response [status=" + status + ", message=" + message
                + ", response=" + response + "]";
    }

}
