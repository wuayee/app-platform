/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.flowsengine.domain.flows.definitions.nodes.jobers;

import static com.huawei.fit.jober.FlowExceptionService.HANDLE_EXCEPTION_GENERICABLE;
import static com.huawei.fit.jober.common.Constant.BUSINESS_DATA_KEY;
import static com.huawei.fit.jober.common.Constant.CONTEXT_DATA;
import static com.huawei.fit.jober.common.Constant.PASS_DATA;
import static com.huawei.fit.jober.common.ErrorCodes.FLOW_EXECUTE_FITABLE_TASK_FAILED;
import static com.huawei.fit.jober.common.ErrorCodes.INPUT_PARAM_IS_INVALID;
import static com.huawei.fit.jober.flowsengine.domain.flows.enums.FlowJoberProperties.ENTITY;

import com.huawei.fit.jober.FlowExceptionService;
import com.huawei.fit.jober.FlowableService;
import com.huawei.fit.jober.common.OhscriptExecuteException;
import com.huawei.fit.jober.common.TypeNotSupportException;
import com.huawei.fit.jober.common.exceptions.JobberException;
import com.huawei.fit.jober.common.exceptions.JobberParamException;
import com.huawei.fit.jober.flowsengine.domain.flows.context.FlowData;
import com.huawei.fit.jober.flowsengine.domain.flows.definitions.nodes.FlowNode;
import com.huawei.fit.jober.flowsengine.domain.flows.definitions.nodes.converter.FlowDataConverter;
import com.huawei.fit.jober.flowsengine.domain.flows.enums.FlowJoberProperties;
import com.huawei.fit.jober.flowsengine.domain.flows.enums.FlowJoberType;
import com.huawei.fit.jober.flowsengine.domain.flows.parsers.FlowGraphData;
import com.huawei.fit.jober.flowsengine.utils.FlowUtil;
import com.huawei.fitframework.broker.CommunicationType;
import com.huawei.fitframework.broker.client.BrokerClient;
import com.huawei.fitframework.broker.client.filter.route.FitableIdFilter;
import com.huawei.fitframework.exception.FitException;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.util.ObjectUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 流程定义节点任务关键类
 * 流程实例流转时需要构建该对象
 *
 * @author g00564732
 * @since 2023/08/14
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public abstract class FlowJober {
    private static final Logger log = Logger.get(FlowJober.class);

    private static final String EXTRA_JOBER = "extraJober";

    private static final String FLOWABLE_HANDLE_TASK_GENERICABLE = "b735c87f5e7e408d852d8440d0b2ecdf";

    private static final String TIMEOUT = "timeout";

    private static final String IS_ASYNC = "isAsync";

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
     * fitable对应的系统配置和业务参数配置
     */
    protected Map<String, Object> fitablesConfig;

    /**
     * 任务数据转换器
     */
    protected FlowDataConverter converter;

    /**
     * jober归属的流程节点
     */
    protected FlowNode parentNode;

    /**
     * 是否异步job
     *
     * @return true:异步
     */
    public Boolean isAsync() {
        return Optional.ofNullable(this.properties.get(IS_ASYNC)).map(Boolean::parseBoolean).orElse(false);
    }

    /**
     * 执行任务核心方法
     *
     * @param inputs 流程实现执行时的入参
     * @return 任务执行结果
     */
    public List<FlowData> execute(List<FlowData> inputs) {
        List<FlowData> flowData;
        Map<String, Object> oldValues = new HashMap<>();
        List<FlowData> convertedInputs = convertFlowData(inputs);
        try {
            oldValues = modifyJoberConfig(convertedInputs.get(0));
            flowData = executeJober(convertedInputs);
            if (this.parentNode.getParentFlow().isEnableOutputScope()) {
                flowData.forEach(data -> FlowUtil.cacheResultToNode(data.getBusinessData(), this.nodeMetaId));
            }
        } catch (OhscriptExecuteException | TypeNotSupportException ex) {
            throw ex;
        } catch (FitException ex) {
            notifyException(ex, convertedInputs);
            String fitableString = Optional.ofNullable(fitables).map(Object::toString).orElse("");
            log.error("Catch throwable when remote invoke, fitables is {}. Caused by {}.", fitableString,
                    ex.getMessage());
            log.error("Stack: ", ex);
            throw new JobberException(ex, FLOW_EXECUTE_FITABLE_TASK_FAILED, this.name, this.type.getCode(),
                    fitableString, ex.getMessage());
        } finally {
            restoreJoberConfig(oldValues);
        }
        return flowData;
    }

    public void notifyException(Throwable ex, List<FlowData> convertedInputs) {
        for (String fitableId : exceptionFitables) {
            this.brokerClient.getRouter(FlowExceptionService.class, HANDLE_EXCEPTION_GENERICABLE)
                    .route(new FitableIdFilter(fitableId))
                    .invoke(this.nodeMetaId, filterFlowData(convertedInputs), ex.getMessage());
        }
    }

    /**
     * modifyJoberConfig
     *
     * @param flowData flowData
     * @return Map<String, Object>
     */
    protected Map<String, Object> modifyJoberConfig(FlowData flowData) {
        Set<String> oldFitables = new LinkedHashSet<>(this.fitables);
        String oldEntity = this.getProperties().get(ENTITY.getValue());

        Optional<JSONObject> jober = Optional.ofNullable(
                        ObjectUtils.<JSONObject>cast(flowData.getBusinessData().get(nodeMetaId)))
                .map(json -> ObjectUtils.cast(json.get(FlowGraphData.JOBER)));
        if (jober.isPresent()) {
            jober.map(joberObject -> ObjectUtils.<JSONArray>cast(joberObject.get(FlowGraphData.FITABLES)))
                    .map(jsonArray -> JSON.parseObject(jsonArray.toJSONString(), new TypeReference<List<String>>() {}))
                    .filter(stringList -> !stringList.isEmpty())
                    .ifPresent(stringList -> this.fitables = new LinkedHashSet<>(stringList));

            jober.map(joberObject -> joberObject.getString(ENTITY.getValue()))
                    .ifPresent(s -> this.properties.put(ENTITY.getValue(), s));
        }

        Map<String, Object> oldJober = new HashMap<>();
        oldJober.put(FlowGraphData.FITABLES, oldFitables);
        oldJober.put(ENTITY.getValue(), oldEntity);
        return oldJober;
    }

    /**
     * executeJober
     *
     * @param inputs inputs
     * @return List<FlowData>
     */
    protected abstract List<FlowData> executeJober(List<FlowData> inputs);

    /**
     * restoreJoberConfig
     *
     * @param oldValues oldValues
     */
    protected void restoreJoberConfig(Map<String, Object> oldValues) {
        this.setFitables(ObjectUtils.cast(oldValues.get(FlowGraphData.FITABLES)));
        Optional.ofNullable(oldValues.get(ENTITY.getValue()))
                .ifPresent(value -> this.getProperties().put(ENTITY.getValue(), ObjectUtils.cast(value)));
    }

    /**
     * getInputs
     *
     * @param inputs inputs
     * @return List<Map < String, Object>>
     */
    protected List<Map<String, Object>> getInputs(List<FlowData> inputs) {
        List<Map<String, Object>> contextData = new ArrayList<>();
        inputs.forEach(input -> {
            Optional.ofNullable(this.properties.get(ENTITY.getValue()))
                    .ifPresent(e -> input.getBusinessData().put(ENTITY.getValue(), JSON.parseObject(e)));

            Map<String, String> extraJober = new HashMap<>();
            Set<String> knownProperties = Arrays.stream(FlowJoberProperties.values())
                    .map(property -> property.getValue())
                    .collect(Collectors.toSet());
            this.properties.keySet()
                    .stream()
                    .filter(k -> !knownProperties.contains(k))
                    .forEach(k -> extraJober.put(k, this.properties.get(k)));

            Map<String, Object> data = new HashMap<>();
            data.put(BUSINESS_DATA_KEY, input.getBusinessData());
            data.put(CONTEXT_DATA, input.getContextData());
            data.put(PASS_DATA, input.getPassData());
            contextData.add(data);
        });
        return contextData;
    }

    /**
     * convertToFlowData
     *
     * @param outputEntities outputEntities
     * @param input input
     * @return List<FlowData>
     */
    protected List<FlowData> convertToFlowData(List<Map<String, Object>> outputEntities, FlowData input) {
        return outputEntities.stream()
                .map(output -> FlowData.builder()
                        .operator(input.getOperator())
                        .startTime(input.getStartTime())
                        .businessData(ObjectUtils.cast(output.get("businessData")))
                        .contextData(new HashMap<>(input.getContextData()))
                        .passData(ObjectUtils.cast(output.get("passData")))
                        .build())
                .collect(Collectors.toList());
    }

    private List<Map<String, Object>> filterFlowData(List<FlowData> flowData) {
        return flowData.stream().map(data -> new HashMap<String, Object>() {
            {
                put(BUSINESS_DATA_KEY, data.getBusinessData());
                put(CONTEXT_DATA, data.getContextData());
                put(PASS_DATA, data.getPassData());
            }
        }).collect(Collectors.toList());
    }

    /**
     * fitableInvoke
     *
     * @param contextData contextData
     * @param fitableId fitableId
     * @return List<Map < String, Object>>
     */
    protected List<Map<String, Object>> fitableInvoke(List<Map<String, Object>> contextData, String fitableId) {
        List<Map<String, Object>> outputEntities;
        Map<String, Object> fitableConf = ObjectUtils.cast(this.fitablesConfig.get(fitableId));
        if (fitableConf != null && fitableConf.containsKey(TIMEOUT)) {
            outputEntities = this.brokerClient.getRouter(FlowableService.class, FLOWABLE_HANDLE_TASK_GENERICABLE)
                    .route(new FitableIdFilter(fitableId))
                    .timeout(ObjectUtils.cast(fitableConf.get(TIMEOUT)), TimeUnit.MILLISECONDS)
                    .invoke(contextData);
        } else {
            outputEntities = this.brokerClient.getRouter(FlowableService.class, FLOWABLE_HANDLE_TASK_GENERICABLE)
                    .route(new FitableIdFilter(fitableId))
                    .communicationType(CommunicationType.ASYNC)
                    .invoke(contextData);
        }
        return outputEntities;
    }

    private List<FlowData> convertFlowData(List<FlowData> inputs) {
        if (Objects.isNull(converter)) {
            return inputs;
        }
        return inputs.stream().map(converter::convertInput).collect(Collectors.toList());
    }

    protected FlowData addResultToFlowData(Object result, Map<String, Object> businessData,
            FlowData contextInfo) {
        if (Objects.isNull(converter)) {
            log.error("There is no converter for adding result, nodeMetaId={}.", this.nodeMetaId);
            throw new JobberParamException(INPUT_PARAM_IS_INVALID, "flowConverter");
        }
        FlowData flowData = FlowData.builder()
                .operator(contextInfo.getOperator())
                .startTime(contextInfo.getStartTime())
                .businessData(businessData)
                .contextData(new HashMap<>(contextInfo.getContextData()))
                .build();
        return converter.convertOutput(result, flowData);
    }
}
