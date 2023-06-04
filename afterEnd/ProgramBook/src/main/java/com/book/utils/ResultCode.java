package com.book.utils;

public enum ResultCode {

    //成功
    SUCCESS(200, "成功"),
    PARAM_IS_INVALID(1001, "参数无效"),
    PARAM_IS_BLANK(1002, "参数为空"),
    PARAM_IS_ERROR(1004, "参数错误"),
    PARAM_IS_COMPLETE(1003, "参数缺失"),
    USER_LONGIN_ERROR(2001, "账号或密码错误"),
    USER_LONGIN_EXIST(2002, "用户不存在"),
    USER_LONGIN_EXISTD(2003, "用户已存在"),
    KHXX_EXISTD(2010, "客户信息已存在！"),
    USER_LONGIN_EXAMINE(2004, "用户待审核"),
    UPDATE_EXAMINE(2006, "修改申请已提交，等待管理员审核"),
    USER_LONGIN_STOP(2005, "用户已停用"),
    ERROR(500, "系统异常"),
    CUSTOMER_EXISTD(400, "客户信息已存在"),
    PHONE_ERROR(402, "验证码或者手机号错误"),
    PHONE_EXIST(405, "手机号为空"),
    PHONE_RETRY(405, "请2分钟后重试"),
    PHONE_FPRMATEXIST(406, "手机号错误"),
    SINE_ERROR(401, "用户信息已过期，请重新登录"),
    LONG_OVERTIME(406, "登录超时，请重新登录"),
    KHXX_HTJH_EXISTD(1005, "该类型的计划已存在"),
    USER_INSUFFICIENT_AUTHORITY(403, "权限不足"),
    FILE_NOTBLANK(2007, "上传文件不能为空"),
    FILE_MAXSIZE(2008, "上传文件不能超过2M"),
    FILE_ERRORSUFFIX(2009, "上传文件错误，只能上传文档或表格以及PDF格式");

    private Integer code;

    private String msg;

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

    ResultCode(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

}

