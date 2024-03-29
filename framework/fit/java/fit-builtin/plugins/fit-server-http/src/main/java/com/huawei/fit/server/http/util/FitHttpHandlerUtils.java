/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.server.http.util;

import static com.huawei.fit.http.header.HttpHeaderKey.FIT_ASYNC_TASK_ID;
import static com.huawei.fit.http.header.HttpHeaderKey.FIT_CODE;
import static com.huawei.fit.http.header.HttpHeaderKey.FIT_DATA_FORMAT;
import static com.huawei.fit.http.header.HttpHeaderKey.FIT_GENERICABLE_VERSION;
import static com.huawei.fit.http.header.HttpHeaderKey.FIT_MESSAGE;
import static com.huawei.fit.http.header.HttpHeaderKey.FIT_METADATA;
import static com.huawei.fit.http.header.HttpHeaderKey.FIT_TLV;
import static com.huawei.fit.http.header.HttpHeaderKey.FIT_VERSION;

import com.huawei.fit.http.header.ContentType;
import com.huawei.fit.http.protocol.HttpResponseStatus;
import com.huawei.fit.http.protocol.MimeType;
import com.huawei.fit.http.server.DoHttpHandlerException;
import com.huawei.fit.http.server.HttpClassicServerRequest;
import com.huawei.fit.http.server.HttpClassicServerResponse;
import com.huawei.fitframework.broker.GenericableMetadata;
import com.huawei.fitframework.broker.server.Response;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.util.StringUtils;

import java.util.Base64;
import java.util.Objects;

/**
 * 表示 {@link com.huawei.fit.http.server.HttpHandler} 相关的工具类。
 *
 * @author 王成 w00863339
 * @since 2023-11-17
 */
public class FitHttpHandlerUtils {
    /**
     * 表示 Json 序列化。
     */
    public static final int FIT_DATA_FORMAT_JSON = 1;

    private static final Logger log = Logger.get(FitHttpHandlerUtils.class);

    /**
     * 获取请求中的序列化方式。
     *
     * @param request 表示请求的 {@link HttpClassicServerRequest}。
     * @return 表示请求中序列化方式的 {@code int}。
     */
    public static int getDataFormat(HttpClassicServerRequest request) {
        return request.headers()
                .first(FIT_DATA_FORMAT.value())
                .map(Integer::parseInt)
                .orElseGet(() -> request.contentType()
                        .map(ContentType::mediaType)
                        .filter(mediaType -> Objects.equals(mediaType, MimeType.APPLICATION_JSON.value()))
                        .map(mediaType -> FIT_DATA_FORMAT_JSON)
                        .orElseThrow(() -> new DoHttpHandlerException("No specified FIT-Data-Format.")));
    }

    /**
     * 向响应中设置消息头。
     *
     * @param response 表示响应的 {@link HttpClassicServerResponse}。
     * @param result 表示结果的 {@link Response}。
     */
    public static void setResponseHeaders(HttpClassicServerResponse response, Response result) {
        // 旧版本通信协议返回值的消息头
        response.headers().set(FIT_METADATA.value(), encode(result.metadata().serialize()));
        // 新版本通信协议返回值的消息头
        response.headers().set(FIT_VERSION.value(), String.valueOf(result.metadata().version()));
        response.headers().set(FIT_DATA_FORMAT.value(), String.valueOf(result.metadata().dataFormat()));
        response.headers().set(FIT_CODE.value(), String.valueOf(result.metadata().code()));
        response.headers().set(FIT_MESSAGE.value(), result.metadata().message());
        response.headers().set(FIT_TLV.value(), encode(result.metadata().tagValues().serialize()));
    }

    /**
     * 获取请求中的服务版本号。
     *
     * @param request 表示请求的 {@link HttpClassicServerRequest}。
     * @return 表示请求中的服务版本号的 {@link String}。
     */
    public static String getGenericableVersion(HttpClassicServerRequest request) {
        return request.headers().first(FIT_GENERICABLE_VERSION.value()).orElseGet(() -> {
            log.warn("No specified FIT-Genericable-Version in headers, use default value instead. "
                    + "[defaultGenericableVersion={}]", GenericableMetadata.DEFAULT_VERSION);
            return GenericableMetadata.DEFAULT_VERSION;
        });
    }

    /**
     * 向响应中设置返回状态码。
     *
     * @param response 表示响应的 {@link HttpClassicServerResponse}。
     * @param status 表示返回状态码的 {@link HttpResponseStatus}。
     */
    public static void setResponseCode(HttpClassicServerResponse response, HttpResponseStatus status) {
        response.statusCode(status.statusCode());
    }

    /**
     * 获取请求中的异步任务的唯一标识。
     *
     * @param request 表示请求的 {@link HttpClassicServerRequest}。
     * @return 表示请求中的异步任务的唯一标识的 {@link String}。
     */
    public static String getAsyncTaskId(HttpClassicServerRequest request) {
        return request.headers().first(FIT_ASYNC_TASK_ID.value()).orElse(StringUtils.EMPTY);
    }

    private static String encode(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

    /**
     * 将数据进行解码。
     *
     * @param data 表示待解码的数据的 {@link String}。
     * @return 表示解码后的数据内容的 {@code byte[]}。
     */
    public static byte[] decode(String data) {
        return Base64.getDecoder().decode(data);
    }
}
