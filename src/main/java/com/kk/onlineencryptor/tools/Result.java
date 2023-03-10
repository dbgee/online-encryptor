package com.kk.onlineencryptor.tools;

public class Result <T>{
    private final int code;
    private final String msg;
    private T data;

    public Result() {
        this.code=200;
        this.msg="success";
    }

    public Result(T data) {
        this.code=200;
        this.msg="success";
        this.data = data;
    }

    public Result(String msg) {
        this.code=500;
        this.msg = msg;
    }

    public Result(int code,String msg) {
        this.code = code;
        this.msg=msg;
    }

    public static <T> Result<T> success(){
        return new Result<>();
    }

    public static <T> Result<T>  success(T data){
        return new Result<T>(data);
    }

    public static <T> Result<T>  error(String msg){
        return new Result<T>(msg);
    }

    public static <T> Result<T>  error(int code,String msg){
        return new Result<T>(code,msg);
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }

}

