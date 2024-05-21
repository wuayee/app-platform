/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.flowsengine.domain.flows.definitions.nodes.jobers;

import static com.alibaba.fastjson.serializer.SerializerFeature.WriteMapNullValue;
import static com.huawei.fit.jober.common.Constant.BUSINESS_DATA_KEY;
import static com.huawei.fitframework.util.ObjectUtils.cast;

import com.huawei.fit.jober.flowsengine.domain.flows.context.FlowData;
import com.huawei.fitframework.broker.CommunicationType;
import com.huawei.fitframework.broker.client.filter.route.DefaultFilter;
import com.huawei.fitframework.log.Logger;

import com.alibaba.fastjson.JSON;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 调用store服务的jober
 *
 * @author s00558940
 * @since 2024/5/7
 */
public class FlowStoreJober extends FlowJober {

    private static final Logger LOG = Logger.get(FlowStoreJober.class);

    public static final String TOOL_EXECUTE_GENERICABLE_ID = "com.huawei.jade.store.tool.execute";

    /**
     * 工具服务的元数据信息
     *
     * @author s00558940
     * @since 2024/5/8
     */
    @Builder
    @Getter
    public static class ServiceMeta {
        /**
         * 唯一名字
         */
        private String uniqueName;

        /**
         * 调用需要的入参
         */
        private List<String> params;

        /**
         * 从业务数据中获取入参
         *
         * @param businessData 业务数据
         * @return 调用参数列表
         */
        public List<String> getArgs(Map<String, Object> businessData) {
            Map<String, Object> args = new HashMap<>();
            this.params.stream().forEach(item -> args.put(item, businessData.get(item)));
            return Arrays.asList(this.uniqueName, JSON.toJSONString(args, WriteMapNullValue));
        }
    }

    @Setter
    @Getter
    private ServiceMeta serviceMeta;

    @Override
    protected List<FlowData> executeJober(List<FlowData> inputs) {
        List<Map<String, Object>> inputData = getInputs(inputs);
        FlowData contextInfo = inputs.get(0);
        List<FlowData> result = new ArrayList<>(inputs.size());
        inputData.forEach(input -> {
            String invokeResult = this.brokerClient.getRouter(TOOL_EXECUTE_GENERICABLE_ID)
                    .route(DefaultFilter.INSTANCE)
                    .communicationType(CommunicationType.ASYNC)
                    .invoke(this.serviceMeta.getArgs(cast(input.get(BUSINESS_DATA_KEY))).toArray());
            LOG.info("Call store tool successfully, nodeId={}, tool={}.", this.nodeMetaId, serviceMeta.uniqueName);
            result.add(addResultToFlowData(JSON.parse(invokeResult), cast(input.get(BUSINESS_DATA_KEY)), contextInfo));
        });

        return result;
    }
}
