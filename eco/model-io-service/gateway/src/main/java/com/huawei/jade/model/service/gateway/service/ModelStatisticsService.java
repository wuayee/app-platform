/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.model.service.gateway.service;

import com.huawei.jade.model.service.gateway.entity.ModelInfo;
import com.huawei.jade.model.service.gateway.entity.ModelStatistics;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 处理模型统计信息相关的业务逻辑。
 *
 * @author 张庭怿
 * @since 2024-05-20
 */
@Service
@Slf4j
public class ModelStatisticsService {
    @Getter
    private Map<String, ModelInfo> models = new ConcurrentHashMap<>();

    @Getter
    private Map<String, Integer> modelLinkControl = new ConcurrentHashMap<>();

    private double totalLatency;

    private long totalTokens;

    /**
     * 读取请求体用于更新模型统计信息。
     *
     * @param modelStats 请求体中的模型统计信息。
     */
    public void updateModelWithRequestBody(ModelStatistics modelStats) {
        if (modelStats == null || modelStats.getModel() == null || modelStats.getModel().isEmpty()) {
            return;
        }
        if (this.modelLinkControl.containsKey(modelStats.getModel())
                && this.modelLinkControl.get(modelStats.getModel()) == 0) {
            // modelLinkControl 中最大连接数为0，则无法符合新的请求
            log.error("load cannot afford, error");
            throw new IllegalArgumentException("load cannot afford, error");
        }

        log.info("Update stats with request for model: " + modelStats.getModel());
        ModelInfo modelInfo = this.models.getOrDefault(modelStats.getModel(), new ModelInfo());
        modelInfo.setRequests(modelInfo.getRequests() + 1);
        modelInfo.setModel(modelStats.getModel());
        this.models.put(modelStats.getModel(), modelInfo);
        if (this.modelLinkControl.get(modelStats.getModel()) != null) {
            // modellinkControl中无对应模型信息但可以路由，则允许访问大模型
            // modellinkControl中有对应模型信息，则更新最大链接值减一
            Integer linkNum = this.modelLinkControl.get(modelStats.getModel()) - 1;
            log.info("Update modelLinkControl for " + modelStats.getModel()
                    + " in request process, link num to: " + linkNum);
            this.modelLinkControl.put(modelStats.getModel(), linkNum);
        }
    }

    /**
     * 读取响应体更新模型统计信息。
     *
     * @param modelStats 响应体中的模型统计信息。
     */
    public void updateModelWithResponseBody(ModelStatistics modelStats) {
        if (modelStats == null || modelStats.getModel() == null || modelStats.getModel().isEmpty()) {
            return;
        }

        String modelName = modelStats.getModel();
        log.info("Update response for model=" + modelStats);
        if (!this.models.containsKey(modelName)) {
            // 走到这个分支说明存在一些问题，现在暂时先不处理。
            log.error("Update stats failed, something's wrong with this model: " + modelName);
            return;
        }

        ModelInfo modelInfo = this.models.get(modelName);
        modelInfo.setResponses(modelInfo.getResponses() + 1);

        // 释放currentRoutes的请求连接数
        Integer oldValue = modelLinkControl.replace(modelName, modelLinkControl.getOrDefault(modelName, 0) + 1);
        if (oldValue != null) {
            log.info("Update modelLinkControl for " + modelName
                    + " in response process, link num to: " + (oldValue + 1));
        }

        ModelStatistics.Usage usage = modelStats.getUsage();
        if (usage == null) {
            log.error("Update stats failed, usage is null for model: " + modelName);
            return;
        }
        modelInfo.setTotalInputTokens(modelInfo.getTotalInputTokens() + usage.getPromptTokens());
        modelInfo.setTotalOutputTokens(modelInfo.getTotalOutputTokens() + usage.getCompletionTokens());
    }

    /**
     * 更新模型性能统计信息，此接口需要在更新完响应体中的统计信息后再调用。
     *
     * @param model 模型名称。
     * @param latency 模型响应时延（单位：秒）。
     */
    public void updateModelPerformanceStatistics(String model, double latency) {
        ModelInfo modelInfo = this.models.get(model);
        if (modelInfo == null) {
            log.error("Failed to update latency for model=" + model);
            return;
        }

        if (modelInfo.getRequests() == 0) {
            log.error("Failed to update latency: there is no request sent to model: " + model);
            return;
        }
        totalLatency += latency;
        modelInfo.setLatency(totalLatency / modelInfo.getRequests());

        totalTokens += modelInfo.getTotalOutputTokens();
        if (totalTokens != 0) {
            modelInfo.setSpeed((double) totalTokens / totalLatency);
        } else {
            // 防止由于浮点数精度问题导致结果非0
            modelInfo.setSpeed(0D);
        }
    }
}
