/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.fit.http.util;

import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fit.http.header.HeaderValue;
import com.huawei.fit.http.header.ParameterCollection;
import com.huawei.fit.http.header.support.DefaultHeaderValue;
import com.huawei.fit.http.header.support.DefaultParameterCollection;
import com.huawei.fitframework.model.MultiValueMap;
import com.huawei.fitframework.util.StringUtils;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Http 协议相关的工具类。
 *
 * @author 季聿阶 j00559309
 * @author 邬涨财 w00575064
 * @since 2022-07-22
 */
public class HttpUtils {
    private static final char KEY_VALUE_PAIR_SEPARATOR = '&';
    private static final char KEY_VALUE_SEPARATOR = '=';
    private static final char STRING_VALUE_SURROUNDED = '\"';

    /**
     * 从消息头的字符串值中解析消息头的值。
     *
     * @param rawValue 表示待解析的消息头的字符串值的 {@link String}。
     * @return 表示解析后的消息头值的 {@link HeaderValue}。
     */
    public static HeaderValue parseHeaderValue(String rawValue) {
        if (StringUtils.isBlank(rawValue)) {
            return HeaderValue.create(StringUtils.EMPTY);
        }
        List<String> splits =
                StringUtils.split(rawValue, DefaultHeaderValue.SEPARATOR, ArrayList::new, StringUtils::isNotBlank);
        if (!isValueOfHeader(splits.get(0))) {
            return HeaderValue.create(StringUtils.EMPTY, parseParameters(splits));
        }
        String value = StringUtils.trim(splits.get(0), STRING_VALUE_SURROUNDED);
        return HeaderValue.create(value, parseParameters(splits.subList(1, splits.size())));
    }

    private static boolean isValueOfHeader(String splitValue) {
        return isValueSurrounded(splitValue) || !splitValue.contains(DefaultParameterCollection.SEPARATOR);
    }

    private static boolean isValueSurrounded(String splitValue) {
        return splitValue.length() > 1 && splitValue.charAt(0) == STRING_VALUE_SURROUNDED
                && splitValue.charAt(splitValue.length() - 1) == STRING_VALUE_SURROUNDED;
    }

    private static ParameterCollection parseParameters(List<String> parameterStrings) {
        ParameterCollection parameterCollection = ParameterCollection.create();
        for (String parameterString : parameterStrings) {
            int index = parameterString.indexOf(DefaultParameterCollection.SEPARATOR);
            if (index < 0) {
                continue;
            }
            String key = parameterString.substring(0, index).trim();
            String value = parameterString.substring(index + 1).trim();
            parameterCollection.set(key, StringUtils.trim(value, STRING_VALUE_SURROUNDED));
        }
        return parameterCollection;
    }

    /**
     * 将 Http 查询参数或表单参数的内容解析成为一个键和多值的映射。
     * <p>该映射的实现默认为 {@link LinkedHashMap}，即键是有序的。</p>
     * <p>查询参数和表单参数的格式是一个的，都为 {@code k1=v1&k2=v2} 的样式。</p>
     *
     * @param keyValues 表示待解析的查询参数或表单参数的 {@link String}。
     * @return 表示解析后的键与多值的映射的 {@link MultiValueMap}{@code <}{@link String}{@code ,
     * }{@link String}{@code >}。
     */
    public static MultiValueMap<String, String> parseQueryOrForm(String keyValues) {
        MultiValueMap<String, String> map = MultiValueMap.create(LinkedHashMap::new);
        if (StringUtils.isBlank(keyValues)) {
            return map;
        }
        List<String> keyValuePairs =
                StringUtils.split(keyValues, KEY_VALUE_PAIR_SEPARATOR, ArrayList::new, StringUtils::isNotBlank);
        for (String keyValuePair : keyValuePairs) {
            int index = keyValuePair.indexOf(KEY_VALUE_SEPARATOR);
            if (index <= 0) {
                continue;
            }
            String key = keyValuePair.substring(0, index);
            String value = keyValuePair.substring(index + 1);
            map.add(key, value);
        }
        return map;
    }

    /**
     * 将指定的 URL 信息转为 {@link URL} 对象。
     *
     * @param url 表示指定的 URL 信息的 {@link String}。
     * @return 表示 URL 对象的 {@link URL}。
     * @throws IllegalStateException 当 URL 对象不合法时。
     */
    public static URL toUrl(String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            throw new IllegalStateException("The request URL is incorrect.", e);
        }
    }

    /**
     * 将指定的 URL 信息转为 {@link URI} 对象。
     *
     * @param url 表示指定的 URL 信息的 {@link URL}。
     * @return 表示 URI 对象 {@link URI}。
     * @throws IllegalArgumentException 当 {@code url} 为 {@code null} 时。
     * @throws IllegalStateException 当 URL 对象不合法时。
     */
    public static URI toUri(URL url) {
        notNull(url, "The url cannot be null.");
        try {
            return url.toURI();
        } catch (URISyntaxException e) {
            throw new IllegalStateException("The request URL is incorrect.", e);
        }
    }
}
