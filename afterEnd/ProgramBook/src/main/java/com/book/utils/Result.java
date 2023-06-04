package com.book.utils;

import java.io.Serializable;

/**
 * 返回体
 *
 * @author
 */
public class Result implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    //状态码
    private Integer code;

    //响应消息
    private String msg;

    //响应数据
    private Object data;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Result(ResultCode resultCode, Object data) {
        this.code = resultCode.getCode();
        this.msg = resultCode.getMsg();
        this.data = data;
    }

    public Result(Integer code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

}
