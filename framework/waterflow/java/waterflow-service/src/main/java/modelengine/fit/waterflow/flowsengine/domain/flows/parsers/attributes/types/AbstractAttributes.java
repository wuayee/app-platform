/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fit.waterflow.flowsengine.domain.flows.parsers.attributes.types;

import modelengine.fit.waterflow.flowsengine.domain.flows.parsers.attributes.objects.Attribute;
import modelengine.fit.waterflow.flowsengine.domain.flows.parsers.attributes.objects.AttributesData;
import modelengine.fitframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 图形抽象类.
 *
 * @author 张越
 * @since 2024-08-05
 */
public class AbstractAttributes implements Attributes {
    /**
     * 属性列表.
     */
    protected final List<Attribute> attributeList;

    /**
     * 原始数据.
     */
    protected final AttributesData attributesData;
    private Map<String, Object> result;

    public AbstractAttributes(AttributesData attributesData) {
        this.attributeList = new ArrayList<>();
        this.attributesData = attributesData;
    }

    /**
     * 获取数据.
     *
     * @return {@link Map}{@code <}{@link String}{@code ,}{@link Object}{@code >} 键值对.
     */
    public Map<String, Object> getData() {
        return Optional.ofNullable(this.result).orElseGet(() -> {
            this.result = this.buildData();
            return this.result;
        });
    }

    private Map<String, Object> buildData() {
        return this.attributeList.stream()
                .peek(a -> a.extract(this.attributesData))
                .filter(a -> !Objects.isNull(a.getValue()))
                .collect(Collectors.toMap(Attribute::getKey, Attribute::getValue));
    }

    @Override
    public boolean isRunnable() {
        Boolean runnable = ObjectUtils.cast(this.getData().get("runnable"));
        return runnable == null || runnable == Boolean.TRUE;
    }
}
