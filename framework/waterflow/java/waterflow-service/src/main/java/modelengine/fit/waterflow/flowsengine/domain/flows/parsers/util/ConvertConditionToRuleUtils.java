/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.domain.flows.parsers.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 转换条件为规则的工具类
 *
 * @author 陈镕希
 * @since 2024-05-13
 */
public class ConvertConditionToRuleUtils {
    private static final String CONDITION_KEY = "condition";

    private static final String CONDITIONS_KEY = "conditions";

    private static final String CONDITION_RELATION_KEY = "conditionRelation";

    /**
     * 将条件转换为规则
     *
     * @param jsonData 条件的json数据
     * @return 转换后的规则表达式
     */
    public static String convert(String jsonData) {
        JSONObject jsonObject = JSON.parseObject(jsonData);
        JSONArray conditions = jsonObject.getJSONArray(CONDITIONS_KEY);
        String conditionRelation = OperatorInfo.getByCode(jsonObject.getString(CONDITION_RELATION_KEY)).getOperator();
        StringBuilder expressionBuilder = buildExpression(conditions, conditionRelation);

        return expressionBuilder.toString();
    }

    private static StringBuilder buildExpression(JSONArray conditions, String conditionRelation) {
        StringBuilder expressionBuilder = new StringBuilder();
        for (int i = 0; i < conditions.size(); i++) {
            if (i > 0) {
                expressionBuilder.append(conditionRelation);
            }
            expressionBuilder.append(buildIndividualCondition(conditions.getJSONObject(i)));
        }
        return expressionBuilder;
    }

    private static String buildIndividualCondition(JSONObject condition) {
        OperatorInfo operator = OperatorInfo.getByCode(condition.getString(CONDITION_KEY));
        return operator.buildConditionExpression(condition);
    }
}
