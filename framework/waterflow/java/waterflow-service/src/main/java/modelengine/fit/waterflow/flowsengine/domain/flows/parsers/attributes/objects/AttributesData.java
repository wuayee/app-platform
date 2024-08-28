/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 */

package modelengine.fit.waterflow.flowsengine.domain.flows.parsers.attributes.objects;

import com.alibaba.fastjson.JSONObject;

import lombok.Getter;

import java.util.Optional;

/**
 * 属性的json数据
 *
 * @author xiafei
 * @since 2024/8/6
 */
@Getter
public class AttributesData {
    private final JSONObject data;

    private final JSONObject flowMeta;

    public AttributesData(JSONObject data) {
        this.data = data;
        this.flowMeta = parseFlowMeta();
    }

    /**
     * 获取flowMeta或者data的json对象
     *
     * @return 如果flowMeta存在，则返回flowMeta，否则返回data
     */
    public JSONObject getFlowMetaOrData() {
        return Optional.ofNullable(this.flowMeta).orElse(this.data);
    }

    private JSONObject parseFlowMeta() {
        return data.getJSONObject("flowMeta");
    }
}
