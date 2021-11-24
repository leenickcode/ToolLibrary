package com.aratek.retrofit2demo.http;

/**
 * @ClassName HttpResult
 * @Description 响应结果封装
 * @Author nick
 * @Date 2021/11/11 17:01
 * @Version 1.0
 */
public class HttpResult<T> {
    int errorCode;
    String errorMsg;
    T data;

    public int getCode() {
        return errorCode;
    }

    public void setCode(int code) {
        this.errorCode = code;
    }

    public String getMessage() {
        return errorMsg;
    }

    public void setMessage(String message) {
        this.errorMsg = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
