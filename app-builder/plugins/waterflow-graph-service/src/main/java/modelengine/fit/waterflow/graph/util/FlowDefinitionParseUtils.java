/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.waterflow.graph.util;

import modelengine.fit.waterflow.flowsengine.domain.flows.parsers.attributes.FlowAttributes;

import com.alibaba.fastjson.JSONObject;

/**
 * 图graph解析成流程引擎所需格式
 *
 * @author 杨祥宇
 * @since 2023/12/18
 */
public class FlowDefinitionParseUtils {
    /**
     * getParsedGraphData
     *
     * @param parsedData parsedData
     * @param version version
     * @return String
     */
    public static String getParsedGraphData(JSONObject parsedData, String version) {
        return new FlowAttributes(parsedData, version).toString();
    }
}
