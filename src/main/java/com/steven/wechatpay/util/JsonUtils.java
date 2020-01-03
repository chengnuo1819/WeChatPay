package com.steven.wechatpay.util;

import com.alibaba.fastjson.JSONArray;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;

/**
 * json工具类，jackson实现
 *
 * @author wangshijie
 */
@Slf4j
public class JsonUtils {

    private static ObjectMapper objectMapper;

    private JsonUtils() {}

    public static ObjectMapper getInstance() {
        if(objectMapper == null){
            synchronized (JsonUtils.class){
                if(objectMapper == null){
                    objectMapper = new ObjectMapper();
                }
            }
        }
        return objectMapper;

    }

    /**
     * json字符串转换为对象
     */
    public static <T> T toObject(String json, Class<T> clazz) {
        try {
            return getInstance().readValue(json, clazz);
        }  catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 对象转换为json字符串
     */
    public static String toJson(Object object) {
        try {
            return getInstance().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *  转换为List
     *
     * @param json
     * @param clazz
     * @param <T>
     * @return
     */
    public static  <T> List<T> toList(String json,Class<T> clazz){
        try{
            return JSONArray.parseArray(json).toJavaList(clazz);
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }
}
