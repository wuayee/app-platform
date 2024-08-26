/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fit.waterflow.flowsengine.domain.flows.parsers.handler;

import com.alibaba.fastjson.JSONObject;

import modelengine.fit.waterflow.flowsengine.domain.flows.parsers.attributes.objects.AttributesData;
import modelengine.fit.waterflow.flowsengine.domain.flows.parsers.attributes.types.EventAttributes;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

/**
 * {@link EventAttributes} 对应测试类
 *
 * @author 张越
 * @since 2024/08/05
 */
public class EventAttributesTest {
    @Test
    @DisplayName("基本测试")
    public void basic() {
        // prepare.
        JSONObject data = new JSONObject();
        data.put("toShape", "toShape1");
        data.put("fromShape", "fromShape1");
        data.put("definedFromConnector", "definedFromConnector1");

        JSONObject flowMeta = new JSONObject();
        flowMeta.put("conditionRule", "conditionRule1");
        data.put("flowMeta", flowMeta);

        // when.
        Map<String, Object> result = new EventAttributes(new AttributesData(data)).getData();

        // then.
        Assertions.assertEquals("toShape1", result.get("to"));
        Assertions.assertEquals("fromShape1", result.get("from"));
        Assertions.assertEquals("definedFromConnector1", result.get("fromConnector"));
        Assertions.assertEquals("conditionRule1", result.get("conditionRule"));
    }
}
