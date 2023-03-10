package com.kk.onlineencryptor.controller;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson2.JSON;
import com.kk.onlineencryptor.tools.RSAUtils;
import com.kk.onlineencryptor.tools.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class EncryptController {
    @RequestMapping("/decrypt")
    public Result decrypt(@RequestBody String data){
        JSONObject jsonObject= JSONObject.parseObject(data);
        Result result;
        String output;
        String msg = jsonObject.getString("msg");
        String key = jsonObject.getString("key");
        log.info("msg:{}",msg);
        log.info("key:{}",key);
        try {
            output = RSAUtils.decrypt(msg,key);
        } catch (Exception e) {
            log.error("解密异常，请确认密钥是否正确->"+e.getMessage());
            result=Result.error("解密异常，请确认密钥是否正确->"+e.getMessage());
            return result;
        }
        result= Result.success(output);
        return result;
    }

    @RequestMapping("/encrypt")
    public Result encrypt(@RequestBody String data){
        JSONObject jsonObject= JSONObject.parseObject(data);
        Result result;
        String output;
        String msg = jsonObject.getString("msg");
        String key = jsonObject.getString("key");
        log.info("msg:{}",msg);
        log.info("key:{}",key);
        try {
            output = RSAUtils.encrypt(msg,key);
        } catch (Exception e) {
            log.error("解密异常，请确认密钥是否正确->"+e.getMessage());
            result=Result.error("加密异常，请确认密钥是否正确->"+e.getMessage());
            return result;
        }
        result= Result.success(output);
        return result;
    }
}
