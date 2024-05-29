/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.eval.algorithm;

import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fitable;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

import me.codeleep.jsondiff.common.model.JsonCompareResult;
import me.codeleep.jsondiff.common.model.JsonComparedOption;
import me.codeleep.jsondiff.core.DefaultJsonDifference;

/**
 * json 匹配算法。
 *
 * @author 董春寅
 * @since 2024-05-28
 */
@Component
public class JsonMatch implements EvalAlgorithm {
    /**
     * 评估方法。
     *
     * @param gt 表示标准答案的 {@link String}。
     * @param gm 表示应用生成答案的 {@link String}。
     * @return 表示评估得分的 {@link Double}。
     */
    @Override
    @Fitable(id = "JsonMatch")
    public double eval(String gt, String gm) {
        if (gt == null || gm == null) {
            return 0;
        }

        String gtClean = gt.trim();
        String gmClean = gm.trim();

        JsonComparedOption jsonComparedOption = new JsonComparedOption().setIgnoreOrder(true);
        DefaultJsonDifference differ = new DefaultJsonDifference().option(jsonComparedOption);
        JsonCompareResult jsonCompareResult;
        if (!gtClean.isEmpty() && (gtClean.charAt(0) == '[')) {
            jsonCompareResult = differ.detectDiff(JSONArray.parseArray(gtClean), JSONArray.parseArray(gmClean));
        } else if (!gtClean.isEmpty() && (gtClean.charAt(0) == '{')) {
            jsonCompareResult = differ.detectDiff(JSONObject.parseObject(gtClean), JSONObject.parseObject(gmClean));
        } else {
            return 0;
        }

        return jsonCompareResult.isMatch() ? 1 : 0;
    }
}
