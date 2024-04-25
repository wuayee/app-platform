/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.support;

import static com.huawei.fitframework.inspection.Validation.notNull;
import static java.nio.charset.StandardCharsets.UTF_8;

import com.huawei.fitframework.broker.client.BrokerClient;
import com.huawei.fitframework.broker.client.Router;
import com.huawei.fitframework.broker.client.filter.route.FitableIdFilter;
import com.huawei.fitframework.serialization.ObjectSerializer;
import com.huawei.fitframework.util.MapBuilder;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.fitframework.util.TypeUtils;
import com.huawei.jade.store.ItemInfo;
import com.huawei.jade.store.Tool;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 表示 {@link Tool} 的 FIT 调用实现。
 *
 * @author 王攀博
 * @since 2024-04-24
 */
public class FitTool extends AbstractTool {
    /**
     * 表示 FIT 调用工具的类型。
     */
    public static final String TYPE = "FIT";

    private static final String ERROR_MESSAGE = "errorMessage";

    private final BrokerClient brokerClient;
    private final ObjectSerializer serializer;

    /**
     * 通过 FIT 调用代理、Json 序列化器和工具元数据信息来初始化 {@link FitTool} 的新实例。
     *
     * @param brokerClient 表示 FIT 调用代理的的 {@link BrokerClient}。
     * @param serializer 表示 Json 序列化器的 {@link ObjectSerializer}。
     * @param itemInfo 表示工具的基本信息的 {@link ItemInfo}。
     * @param metadata 表示工具元数据信息的 {@link Metadata}。
     * @throws IllegalArgumentException 当 {@code brokerClient}、{@code serializer}、{@code itemInfo} 或
     * {@code metadata} 为 {@code null} 时。
     */
    public FitTool(BrokerClient brokerClient, ObjectSerializer serializer, ItemInfo itemInfo, Metadata metadata) {
        super(itemInfo, metadata);
        this.brokerClient = notNull(brokerClient, "The broker client cannot be null");
        this.serializer = notNull(serializer, "The serializer cannot be null");
    }

    @Override
    public Object call(Object... args) {
        Router router;
        if (this.metadata().getMethod().isPresent()) {
            router = this.brokerClient.getRouter(this.itemInfo().group(), this.metadata().getMethod().get());
        } else {
            router = this.brokerClient.getRouter(this.itemInfo().group());
        }
        return router.route(new FitableIdFilter(Collections.singletonList(this.itemInfo().name()))).invoke(args);
    }

    @Override
    public String callByJson(String jsonArgs) {
        try {
            return this.call0(jsonArgs);
        } catch (Throwable cause) {
            return this.makeErrorMessage(cause);
        }
    }

    private String call0(String jsonArgs) {
        Map<String, Object> mapArgs = this.serializer.deserialize(jsonArgs.getBytes(UTF_8),
                UTF_8,
                TypeUtils.parameterized(Map.class, new Type[] {String.class, Object.class}));
        List<String> params = this.metadata().parameterNames();
        List<Type> types = this.metadata().parameters();
        Object[] args = new Object[params.size()];
        for (int i = 0; i < args.length; ++i) {
            Object value = mapArgs.get(params.get(i));
            if (value == null) {
                value = this.metadata().parameterDefaultValue(params.get(i));
            }
            args[i] = ObjectUtils.toCustomObject(value, types.get(i));
        }
        Object result = call(args);
        return new String(this.serializer.serialize(result, UTF_8), UTF_8);
    }

    private String makeErrorMessage(Throwable cause) {
        Map<Object, Object> error = MapBuilder.get().put(ERROR_MESSAGE, cause.getMessage()).build();
        return new String(this.serializer.serialize(error, UTF_8));
    }
}
