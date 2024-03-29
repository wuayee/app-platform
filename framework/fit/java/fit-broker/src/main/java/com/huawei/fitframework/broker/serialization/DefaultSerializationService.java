/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2024. All rights reserved.
 */

package com.huawei.fitframework.broker.serialization;

import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fit.serialization.MessageSerializer;
import com.huawei.fitframework.broker.SerializationService;
import com.huawei.fitframework.ioc.BeanContainer;
import com.huawei.fitframework.ioc.BeanFactory;
import com.huawei.fitframework.util.CollectionUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 为 {@link SerializationService} 提供默认实现。
 *
 * @author 梁济时 l00815032
 * @author 季聿阶 j00559309
 * @since 2020-11-12
 */
public class DefaultSerializationService implements SerializationService {
    private final BeanContainer container;
    private final Map<Method, List<Integer>> cachedSupportedFormatsMapping = new HashMap<>();

    public DefaultSerializationService(BeanContainer container) {
        this.container = notNull(container, "The bean container cannot be null.");
    }

    @Override
    public Optional<MessageSerializer> get(int format) {
        return this.getMessageSerializers().stream().filter(serializer -> serializer.getFormat() == format).findFirst();
    }

    @Override
    public List<Integer> getSupportedFormats(Method genericableMethod) {
        List<Integer> cachedSupportedFormats = this.cachedSupportedFormatsMapping.get(genericableMethod);
        if (CollectionUtils.isNotEmpty(cachedSupportedFormats)) {
            return cachedSupportedFormats;
        }
        List<Integer> supportedFormats = this.resolveSupportedSerialization(genericableMethod);
        if (CollectionUtils.isNotEmpty(supportedFormats)) {
            this.cachedSupportedFormatsMapping.put(genericableMethod, supportedFormats);
        }
        return supportedFormats;
    }

    private List<Integer> resolveSupportedSerialization(Method method) {
        return this.getMessageSerializers()
                .stream()
                .filter(serializer -> serializer.isSupported(method))
                .map(MessageSerializer::getFormat)
                .collect(Collectors.toList());
    }

    private List<MessageSerializer> getMessageSerializers() {
        return this.container.all(MessageSerializer.class)
                .stream()
                .map(BeanFactory::<MessageSerializer>get)
                .collect(Collectors.toList());
    }
}
