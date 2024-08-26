/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fit.waterflow.flowsengine.domain.flows.parsers.extractors;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import modelengine.fit.waterflow.flowsengine.domain.flows.parsers.attributes.extractors.ConditionParamsExtractor;
import modelengine.fit.waterflow.flowsengine.domain.flows.parsers.attributes.objects.AttributesData;
import modelengine.fitframework.util.ObjectUtils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * {@link ConditionParamsExtractor} 对应测试类
 *
 * @author 张越
 * @since 2024/08/05
 */
public class JadeConditionParamsExtractorTest {
    @Test
    @DisplayName("基本测试")
    public void basic() {
        // prepare.
        JSONObject branch = new JSONObject();
        branch.put("runnable", true);

        JSONArray branches = new JSONArray();
        branches.add(branch);

        JSONObject conditionParams = new JSONObject();
        conditionParams.put("branches", branches);

        JSONObject flowMeta = new JSONObject();
        flowMeta.put("conditionParams", conditionParams);

        JSONObject shape = new JSONObject();
        shape.put("flowMeta", flowMeta);

        // when.
        Object result = new ConditionParamsExtractor().extract(new AttributesData(shape));

        // then.
        JSONObject processedConditionParams = ObjectUtils.cast(result);
        JSONArray processedBranches = ObjectUtils.cast(processedConditionParams.get("branches"));
        Assertions.assertEquals(1, processedBranches.size());
    }

    @Test
    @DisplayName("当branches的runnable状态都是false的情况下，提取后branches的个数应该为0")
    public void whenRunnableIsFalseThenBranchesSizeShouldBe0() {
        // prepare.
        JSONObject branch = new JSONObject();
        branch.put("runnable", false);

        JSONArray branches = new JSONArray();
        branches.add(branch);

        JSONObject conditionParams = new JSONObject();
        conditionParams.put("branches", branches);

        JSONObject flowMeta = new JSONObject();
        flowMeta.put("conditionParams", conditionParams);

        JSONObject shape = new JSONObject();
        shape.put("flowMeta", flowMeta);

        // when.
        Object result = new ConditionParamsExtractor().extract(new AttributesData(shape));

        // then.
        JSONObject processedConditionParams = ObjectUtils.cast(result);
        JSONArray processedBranches = ObjectUtils.cast(processedConditionParams.get("branches"));
        Assertions.assertEquals(0, processedBranches.size());
    }

    @Test
    @DisplayName("当branches的runnable状态都是null的情况下，提取后branches的个数应与原来一致")
    public void whenRunnableIsNullThenBranchesSizeShouldBeTheSame() {
        // prepare.
        JSONObject branch = new JSONObject();

        JSONArray branches = new JSONArray();
        branches.add(branch);

        JSONObject conditionParams = new JSONObject();
        conditionParams.put("branches", branches);

        JSONObject flowMeta = new JSONObject();
        flowMeta.put("conditionParams", conditionParams);

        JSONObject shape = new JSONObject();
        shape.put("flowMeta", flowMeta);

        // when.
        Object result = new ConditionParamsExtractor().extract(new AttributesData(shape));

        // then.
        JSONObject processedConditionParams = ObjectUtils.cast(result);
        JSONArray processedBranches = ObjectUtils.cast(processedConditionParams.get("branches"));
        Assertions.assertEquals(branches.size(), processedBranches.size());
    }
}
