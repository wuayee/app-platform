/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.support;

import com.huawei.fitframework.log.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

/**
 * 常见方法的工具类。
 *
 * @author 王攀博
 * @since 2024-04-27
 */
public class ItemRepositoryUtil {
    private static final Logger log = Logger.get(ItemRepositoryUtil.class);

    /**
     * 反序列化。
     *
     * @param jsonString 表示待反序列化的 {@link String}。
     * @return 商品的格式的 {@link Map}{@code <}{@link String}{@code ,}{@link Object}{@code >}。
     */
    public static Map<String, Object> json2Obj(String jsonString) {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> mp = null;
        try {
            mp = objectMapper.readValue(jsonString, new TypeReference<Map<String, Object>>() {});
        } catch (JsonProcessingException e) {
            log.error("Json serialization failed.");
        }
        return mp;
    }

    /**
     * 根据页数和一页的长度获取起始索引。
     *
     * @param pageNum 表示开始的页数的 {@link int}。
     * @param limit 表示每页的长度的 {@link int}。
     * @return 表示起始索引 {@link int}。
     */
    public static int getOffset(int pageNum, int limit) {
        return (pageNum > 0) ? ((pageNum - 1) * limit) : 0;
    }

    /**
     * 将一个对象转化为 Json 格式。
     *
     * @param obj 表示要转化为 Json 的对象的 {@link T}。
     * @param <T> 表示对象的类型的 {@link T}。
     * @return 表示对象转化为 Json 格式字符串的 {@link T}。
     */
    public static <T> String convertToJson(T obj) {
        String jsonString = null;
        try {
            jsonString = new ObjectMapper().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("Json serialization failed.");
        }
        return jsonString;
    }
}
