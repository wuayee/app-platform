/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.serialization.util;

import com.huawei.fit.serialization.MessageSerializer;
import com.huawei.fitframework.ioc.BeanContainer;
import com.huawei.fitframework.ioc.BeanFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 表示消息序列化器的工具类。
 *
 * @author 季聿阶
 * @since 2023-09-21
 */
public class MessageSerializerUtils {
    /**
     * 通过容器，从全局获取支持指定格式的消息序列化器。
     *
     * @param container 表示 Bean 容器的 {@link BeanContainer}。
     * @param format 表示指定格式的 {@code int}。
     * @return 表示指定格式的消息序列化器的 {@link Optional}{@code <}{@link MessageSerializer}{@code >}。
     */
    public static Optional<MessageSerializer> getMessageSerializer(BeanContainer container, int format) {
        return getMessageSerializers(container).stream()
                .filter(serializer -> serializer.getFormat() == format)
                .findFirst();
    }

    private static List<MessageSerializer> getMessageSerializers(BeanContainer container) {
        return container.all(MessageSerializer.class)
                .stream()
                .map(BeanFactory::<MessageSerializer>get)
                .collect(Collectors.toList());
    }
}
