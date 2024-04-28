/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.domain.definitions.nodes.jobers;

import static com.huawei.fit.waterflow.common.ErrorCodes.FLOW_EXECUTE_FITABLE_TASK_FAILED;
import static com.huawei.fit.waterflow.spi.WaterflowExceptionNotify.ON_EXCEPTION_GENERICABLE;

import com.huawei.fit.waterflow.common.exceptions.WaterflowException;
import com.huawei.fit.waterflow.domain.common.Constant;
import com.huawei.fit.waterflow.domain.context.FlowData;
import com.huawei.fit.waterflow.domain.enums.FlowJoberProperties;
import com.huawei.fit.waterflow.domain.enums.FlowJoberType;
import com.huawei.fit.waterflow.domain.parsers.FlowGraphData;
import com.huawei.fit.waterflow.spi.WaterflowExceptionNotify;
import com.huawei.fitframework.broker.client.BrokerClient;
import com.huawei.fitframework.broker.client.filter.route.FitableIdFilter;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.util.ObjectUtils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.TypeReference;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 流程定义节点任务关键类
 * 流程实例流转时需要构建该对象
 *
 * @author g00564732
 * @since 1.0
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public abstract class FlowJober {
    private static final Logger LOG = Logger.get(FlowJober.class);

    private static final String EXTRA_JOBER = "extraJober";

    /**
     * 所在节点的metaId
     */
    protected String nodeMetaId;

    /**
     * jober所在的节点位置
     */
    protected String nodeId;

    /**
     * 节点任务名称
     */
    protected String name;

    /**
     * 节点任务类型
     */
    protected FlowJoberType type;

    /**
     * 节点任务属性，所有任务中定义的变量作为该属性的key
     */
    protected Map<String, String> properties;

    /**
     * 节点任务的fitables集合，不同的jober内置的fitables数量不一致
     */
    protected Set<String> fitables;

    /**
     * 节点任务异常处理fitables集合
     */
    protected Set<String> exceptionFitables;

    /**
     * 调用 fitable客户端
     */
    protected BrokerClient brokerClient;

    /**
     * 执行任务核心方法
     *
     * @param inputs 流程实现执行时的入参
     * @return 任务执行结果
     */
    public List<FlowData> execute(List<FlowData> inputs) {
        List<FlowData> flowData;
        Map<String, Object> oldValues = new HashMap<>();
        try {
            oldValues = modifyJoberConfig(inputs.get(0));
            flowData = executeJober(inputs);
        } catch (Throwable ex) {
            for (String fitableId : exceptionFitables) {
                this.brokerClient.getRouter(WaterflowExceptionNotify.class, ON_EXCEPTION_GENERICABLE)
                        .route(new FitableIdFilter(fitableId))
                        .invoke(this.nodeMetaId, filterFlowData(inputs), ex.getMessage());
            }
            String fitableString = Optional.ofNullable(fitables).map(Object::toString).orElse("");
            LOG.error("Catch throwable when remote invoke, fitables is {}. Caused by {}.", fitableString,
                    ex.getMessage());
            LOG.error("Stack: ", ex);
            throw new WaterflowException(FLOW_EXECUTE_FITABLE_TASK_FAILED, this.name, this.type.getCode(),
                    fitableString, ex.getMessage());
        } finally {
            restoreJoberConfig(oldValues);
        }
        return flowData;
    }

    /**
     * 用data中的动态配置替换job的原始配置
     *
     * @param flowData 待替换的数据
     * @return 原始的配置
     */
    protected Map<String, Object> modifyJoberConfig(FlowData flowData) {
        Set<String> oldFitables = new HashSet<>(this.fitables);
        String oldEntity = this.getProperties().get(FlowJoberProperties.ENTITY.getValue());

        Optional<JSONObject> jober = Optional.ofNullable(
                        ObjectUtils.<JSONObject>cast(flowData.getBusinessData().get(nodeMetaId)))
                .map(json -> ObjectUtils.cast(json.get(FlowGraphData.JOBER)));
        if (jober.isPresent()) {
            jober.map(joberObject -> ObjectUtils.<JSONArray>cast(joberObject.get(FlowGraphData.FITABLES)))
                    .map(jsonArray -> JSON.parseObject(jsonArray.toJSONString(), new TypeReference<List<String>>() {}))
                    .filter(stringList -> !stringList.isEmpty())
                    .ifPresent(stringList -> this.fitables = new HashSet<>(stringList));

            jober.map(joberObject -> joberObject.getString(FlowJoberProperties.ENTITY.getValue()))
                    .ifPresent(entity -> this.properties.put(FlowJoberProperties.ENTITY.getValue(), entity));
        }

        Map<String, Object> oldJober = new HashMap<>();
        oldJober.put(FlowGraphData.FITABLES, oldFitables);
        oldJober.put(FlowJoberProperties.ENTITY.getValue(), oldEntity);
        return oldJober;
    }

    /**
     * 执行job任务
     *
     * @param inputs 待处理的数据
     * @return 处理后的数据
     * @throws Throwable 异常
     */
    protected abstract List<FlowData> executeJober(List<FlowData> inputs) throws Throwable;

    /**
     * 将job的配置还原为原始的配置
     *
     * @param originConfigs 原始的配置
     */
    protected void restoreJoberConfig(Map<String, Object> originConfigs) {
        this.setFitables(ObjectUtils.cast(originConfigs.get(FlowGraphData.FITABLES)));
        Optional.ofNullable(originConfigs.get(FlowJoberProperties.ENTITY.getValue()))
                .ifPresent(entity -> this.getProperties()
                        .put(FlowJoberProperties.ENTITY.getValue(), ObjectUtils.cast(entity)));
    }

    /**
     * 将flowDataList转换为序列化的map结构
     *
     * @param flowDataList 待转换的数据
     * @return 转换后的list
     */
    protected List<Map<String, Object>> getInputs(List<FlowData> flowDataList) {
        List<Map<String, Object>> contextData = new ArrayList<>();
        flowDataList.forEach(input -> {
            Optional.ofNullable(this.properties.get(FlowJoberProperties.ENTITY.getValue()))
                    .ifPresent(entity -> input.getBusinessData()
                            .put(FlowJoberProperties.ENTITY.getValue(), JSON.parseObject(entity)));

            Map<String, String> extraJober = new HashMap<>();
            Set<String> knownProperties = Arrays.stream(FlowJoberProperties.values())
                    .map(property -> property.getValue())
                    .collect(Collectors.toSet());
            this.properties.keySet()
                    .stream()
                    .filter(key -> !knownProperties.contains(key))
                    .forEach(key -> extraJober.put(key, this.properties.get(key)));
            input.getContextData().put(EXTRA_JOBER, extraJober);

            Map<String, Object> data = new HashMap<>();
            data.put(Constant.BUSINESS_DATA_KEY, input.getBusinessData());
            data.put(Constant.CONTEXT_DATA, input.getContextData());
            data.put(Constant.PASS_DATA, input.getPassData());
            contextData.add(data);
        });
        return contextData;
    }

    /**
     * 转换为flowData的list
     *
     * @param dataList 待转换的数据
     * @param templateData 构建flowData的模板
     * @return 转换后的数据
     */
    protected List<FlowData> convertToFlowData(List<Map<String, Object>> dataList, FlowData templateData) {
        return dataList.stream()
                .map(output -> FlowData.builder()
                        .operator(templateData.getOperator())
                        .startTime(templateData.getStartTime())
                        .businessData(ObjectUtils.cast(output.get("businessData")))
                        .contextData(new HashMap<>(templateData.getContextData()))
                        .passData(ObjectUtils.cast(output.get("passData")))
                        .build())
                .collect(Collectors.toList());
    }

    private List<Map<String, Object>> filterFlowData(List<FlowData> flowData) {
        return flowData.stream().map(data -> new HashMap<String, Object>() {{
            put(Constant.BUSINESS_DATA_KEY, data.getBusinessData());
            put(Constant.CONTEXT_DATA, data.getContextData());
            put(Constant.PASS_DATA, data.getPassData());
        }}).collect(Collectors.toList());
    }
}
