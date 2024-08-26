/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package modelengine.fitframework.annotation;

import modelengine.fitframework.ioc.BeanContainer;
import modelengine.fitframework.ioc.Condition;
import modelengine.fitframework.ioc.annotation.AnnotationMetadata;
import modelengine.fitframework.util.StringUtils;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 为 {@link Condition} 提供用以匹配 {@code Profile} 的实现。
 *
 * @author 梁济时
 * @since 2022-11-15
 */
public final class ProfileCondition implements Condition {
    @Override
    public boolean match(BeanContainer container, AnnotationMetadata annotations) {
        Profile annotation = annotations.getAnnotation(Profile.class);
        if (annotation == null) {
            return true;
        }
        Set<String> profiles = Stream.of(annotation.value())
                .map(StringUtils::trim)
                .filter(StringUtils::isNotEmpty)
                .collect(Collectors.toSet());
        return profiles.isEmpty() || profiles.contains(container.runtime().profile());
    }
}
