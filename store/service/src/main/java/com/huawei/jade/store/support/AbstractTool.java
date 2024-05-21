/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.support;

import static com.huawei.fitframework.inspection.Validation.notNull;
import static java.nio.charset.StandardCharsets.UTF_8;

import com.huawei.fitframework.serialization.ObjectSerializer;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.fitframework.util.TypeUtils;
import com.huawei.jade.store.Tool;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 表示 {@link Tool} 的抽象实现类。
 *
 * @author 季聿阶
 * @since 2024-04-25
 */
public abstract class AbstractTool implements Tool {
    private final Info info;
    private final Metadata metadata;
    private final ObjectSerializer serializer;

    /**
     * 通过工具的基本信息和工具元数据来初始化 {@link AbstractTool} 的新实例。
     *
     * @param serializer 表示 Json 序列化器的 {@link ObjectSerializer}。
     * @param itemInfo 表示工具的基本信息的 {@link Info}。
     * @param metadata 表示工具的元数据的 {@link Metadata}。
     */
    protected AbstractTool(ObjectSerializer serializer, Info itemInfo, Metadata metadata) {
        this.info = notNull(itemInfo, "The item info cannot be null.");
        this.metadata = notNull(metadata, "The tool metadata cannot be null.");
        this.serializer = notNull(serializer, "The serializer cannot be null");
    }

    @Override
    public Info info() {
        return this.info;
    }

    @Override
    public Metadata metadata() {
        return this.metadata;
    }

    @Override
    public String callByJson(String jsonArgs) {
        Map<String, Object> mapArgs = this.serializer.deserialize(jsonArgs.getBytes(UTF_8),
                UTF_8,
                TypeUtils.parameterized(Map.class, new Type[] {String.class, Object.class}));
        List<String> params = this.metadata().parameterNames();
        List<Type> types = this.metadata().parameters();
        ArrayList<Object> args = new ArrayList<>();
        for (int i = 0; i < params.size(); ++i) {
            Object value = mapArgs.get(params.get(i));
            if (value == null) {
                value = this.metadata().parameterDefaultValue(params.get(i));
            }
            args.add(ObjectUtils.toCustomObject(value, types.get(i)));
        }
        Object result = this.call(args);
        return new String(this.serializer.serialize(result, UTF_8), UTF_8);
    }
}
