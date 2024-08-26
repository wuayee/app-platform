/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fit.waterflow.flowsengine.domain.flows.parsers.attributes.types;

import com.alibaba.fastjson.JSONObject;

import modelengine.fit.waterflow.flowsengine.domain.flows.parsers.attributes.extractors.ConditionParamsExtractor;
import modelengine.fit.waterflow.flowsengine.domain.flows.parsers.attributes.extractors.JoberFilterExtractor;
import modelengine.fit.waterflow.flowsengine.domain.flows.parsers.attributes.extractors.NameExtractor;
import modelengine.fit.waterflow.flowsengine.domain.flows.parsers.attributes.extractors.TaskFilterExtractor;
import modelengine.fit.waterflow.flowsengine.domain.flows.parsers.attributes.objects.Attribute;
import modelengine.fit.waterflow.flowsengine.domain.flows.parsers.attributes.objects.AttributesData;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 编排 node.
 *
 * @author 张越
 * @since 2024-08-05
 */
public class NodeAttributes extends AbstractAttributes {
    private static final Map<String, Supplier<Attribute>> BUILT_IN_ATTRIBUTES = new HashMap<>();

    static {
        BUILT_IN_ATTRIBUTES.put("conditionParams",
                () -> new Attribute("conditionParams", new ConditionParamsExtractor()));
    }

    /**
     * 构造函数.
     *
     * @param attributesData 原始数据.
     */
    public NodeAttributes(AttributesData attributesData) {
        super(attributesData);
        this.attributeList.add(new Attribute("metaId", Collections.singletonList("id")));
        this.attributeList.add(new Attribute("type", Collections.singletonList("type")));
        this.attributeList.add(new Attribute("runnable", Collections.singletonList("runnable")));
        this.attributeList.add(new Attribute("name", new NameExtractor()));
        this.attributeList.add(new Attribute("triggerMode", Arrays.asList("flowMeta", "triggerMode")));
        this.attributeList.add(new Attribute("task", Arrays.asList("flowMeta", "task")));
        this.attributeList.add(new Attribute("taskFilter", new TaskFilterExtractor()));
        this.attributeList.add(new Attribute("jober", Arrays.asList("flowMeta", "jober")));
        this.attributeList.add(new Attribute("joberFilter", new JoberFilterExtractor()));
        this.attributeList.add(new Attribute("callback", Arrays.asList("flowMeta", "callback")));

        Set<String> exists = this.attributeList.stream().map(Attribute::getKey).collect(Collectors.toSet());
        exists.remove("metaId");
        exists.add("id");
        JSONObject flowMeta = attributesData.getFlowMeta();
        if (flowMeta != null) {
            flowMeta.entrySet()
                    .stream()
                    .filter(item -> !exists.contains(item.getKey()))
                    .forEach(item -> {
                        Attribute attribute = Optional.ofNullable(BUILT_IN_ATTRIBUTES.get(item.getKey()))
                                .map(Supplier::get)
                                .orElseGet(() -> new Attribute(item.getKey(), item.getValue()));
                        this.attributeList.add(attribute);
                    });
        }
    }
}
