package com.example.jiuzhou.common.utils;

import com.example.jiuzhou.common.Enum.ResultEnum;
import org.apache.commons.lang.StringUtils;

public class Result<T> {
    private Integer code;
    private String msg;
    private T data;

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

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Result() {
    }

    public Result(T data) {
        this.data = data;
    }

    public static Result success() {
        Result result = new Result<>();
        result.setCode(200);
        result.setMsg("成功");
        return result;
    }

    public static <T> Result<T> success(T data) {
        Result<T> result = data!=null? new Result<>(data): new Result<>();
        result.setCode(200);
        result.setMsg("成功");
        return result;
    }

    public static Result error(ResultEnum resultEnum, String msg) {
        Result result = new Result();
        result.setCode(resultEnum.getCode());
        result.setMsg(StringUtils.isEmpty(msg)? resultEnum.getMsg():msg);
        return result;
    }
    public static Result error(ResultEnum resultEnum) {
        Result result = new Result();
        result.setCode(resultEnum.getCode());
        result.setMsg( resultEnum.getMsg());
        return result;
    }

}