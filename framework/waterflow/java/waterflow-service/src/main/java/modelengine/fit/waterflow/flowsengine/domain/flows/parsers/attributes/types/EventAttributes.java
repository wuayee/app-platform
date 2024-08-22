/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fit.waterflow.flowsengine.domain.flows.parsers.attributes.types;

import modelengine.fit.waterflow.flowsengine.domain.flows.parsers.attributes.objects.Attribute;
import modelengine.fit.waterflow.flowsengine.domain.flows.parsers.attributes.objects.AttributesData;

import java.util.Arrays;
import java.util.Collections;

/**
 * 编排 event.
 *
 * @author 张越
 * @since 2024-08-05
 */
public class EventAttributes extends AbstractAttributes {
    /**
     * 构造函数.
     *
     * @param attributesData 原始数据.
     */
    public EventAttributes(AttributesData attributesData) {
        super(attributesData);
        this.attributeList.add(new Attribute("metaId", Collections.singletonList("id")));
        this.attributeList.add(new Attribute("type", Collections.singletonList("type")));
        this.attributeList.add(new Attribute("runnable", Collections.singletonList("runnable")));
        this.attributeList.add(new Attribute("to", Collections.singletonList("toShape")));
        this.attributeList.add(new Attribute("from", Collections.singletonList("fromShape")));
        this.attributeList.add(new Attribute("fromConnector", Collections.singletonList("definedFromConnector")));
        this.attributeList.add(new Attribute("conditionRule", Arrays.asList("flowMeta", "conditionRule")));
    }
}
