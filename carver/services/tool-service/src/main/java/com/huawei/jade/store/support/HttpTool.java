/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.support;

import static com.huawei.fitframework.inspection.Validation.notNull;
import static com.huawei.fitframework.util.ObjectUtils.cast;

import com.huawei.fit.http.client.HttpClassicClient;
import com.huawei.fit.http.client.HttpClassicClientFactory;
import com.huawei.fit.http.client.HttpClassicClientRequest;
import com.huawei.fit.http.protocol.HttpRequestMethod;
import com.huawei.fitframework.serialization.ObjectSerializer;
import com.huawei.jade.store.Tool;

import java.io.IOException;
import java.util.Map;

/**
 * 表示 {@link Tool} 的 Http 调用实现。
 *
 * @author 季聿阶
 * @since 2024-05-10
 */
public class HttpTool extends AbstractTool {
    private final HttpClassicClientFactory factory;

    /**
     * 通过工具的基本信息和工具元数据来初始化 {@link AbstractTool} 的新实例。
     *
     * @param factory 表示 Http 客户端的工厂的 {@link HttpClassicClientFactory}。
     * @param serializer 表示 Json 序列化器的 {@link ObjectSerializer}。
     * @param itemInfo 表示工具的基本信息的 {@link Info}。
     * @param metadata 表示工具的元数据的 {@link Metadata}。
     */
    protected HttpTool(HttpClassicClientFactory factory, ObjectSerializer serializer, Info itemInfo,
            Metadata metadata) {
        super(serializer, itemInfo, metadata);
        this.factory = notNull(factory, "The http classic client factory cannot be null.");
    }

    @Override
    public Object call(Object... args) {
        Map<String, Object> runnable = cast(this.info().schema().get("runnable"));
        String method = cast(runnable.get("method"));
        String url = cast(runnable.get("url"));
        HttpClassicClient httpClassicClient = this.factory.create();
        try (HttpClassicClientRequest request = httpClassicClient.createRequest(HttpRequestMethod.from(method), url)) {
            // TODO 待完善的工具逻辑
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
