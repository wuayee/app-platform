/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.fitable;

import com.huawei.fit.jane.meta.multiversion.MetaService;
import com.huawei.fit.jane.meta.multiversion.definition.Meta;
import com.huawei.fit.jober.FlowPublishService;
import com.huawei.fit.jober.aipp.common.JsonUtils;
import com.huawei.fit.jober.aipp.common.MetaUtils;
import com.huawei.fit.jober.aipp.common.Utils;
import com.huawei.fit.jober.aipp.constants.AippConst;
import com.huawei.fit.jober.aipp.domain.AppBuilderApp;
import com.huawei.fit.jober.aipp.enums.MetaInstStatusEnum;
import com.huawei.fit.jober.aipp.factory.AppBuilderAppFactory;
import com.huawei.fit.jober.aipp.service.AippFlowRuntimeInfoService;
import com.huawei.fit.jober.entity.FlowNodePublishInfo;
import com.huawei.fit.jober.entity.FlowPublishContext;
import com.huawei.fit.jober.entity.consts.NodeTypes;
import com.huawei.fit.runtime.NodeRuntimeDataPublisher;
import com.huawei.fit.runtime.entity.NodeInfo;
import com.huawei.fit.runtime.entity.Parameter;
import com.huawei.fit.runtime.entity.RuntimeData;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fitable;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.fitframework.util.StringUtils;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javafx.util.Pair;

/**
 * 订阅节点运行时信息.
 *
 * @author z00559346 张越
 * @since 2024-05-23
 */
@Component
public class FlowPublishSubscriber implements FlowPublishService {
    private final NodeRuntimeDataPublisher nodeRuntimeDataPublisher;
    private final MetaService metaService;
    private final AppBuilderAppFactory appFactory;
    private final AippFlowRuntimeInfoService aippFlowRuntimeInfoService;

    private final Map<String, CacheInfo> cache = new ConcurrentHashMap<>();

    /**
     * 构造函数.
     *
     * @param nodeRuntimeDataPublisher {@link NodeRuntimeDataPublisher} 对象.
     * @param metaService {@link MetaService} 对象.
     * @param appFactory {@link AppBuilderAppFactory} app工厂对象.
     * @param aippFlowRuntimeInfoService {@link AippFlowRuntimeInfoService} 对象.
     */
    public FlowPublishSubscriber(NodeRuntimeDataPublisher nodeRuntimeDataPublisher, MetaService metaService,
            AppBuilderAppFactory appFactory, AippFlowRuntimeInfoService aippFlowRuntimeInfoService) {
        this.nodeRuntimeDataPublisher = nodeRuntimeDataPublisher;
        this.metaService = metaService;
        this.appFactory = appFactory;
        this.aippFlowRuntimeInfoService = aippFlowRuntimeInfoService;
    }


    @Fitable("com.huawei.fit.jober.aipp.fitable.FlowPublishSubscriber")
    @Override
    public void publishNodeInfo(FlowNodePublishInfo flowNodePublishInfo) {
        Map<String, Object> businessData = flowNodePublishInfo.getBusinessData();
        FlowPublishContext context = flowNodePublishInfo.getFlowContext();
        String traceId = context.getTraceId();
        String nodeType = flowNodePublishInfo.getNodeType();

        CacheInfo cacheInfo = this.getResult(context, traceId, nodeType);
        RuntimeData runtimeData = new RuntimeData();
        runtimeData.setStartTime(cacheInfo.startTime);
        runtimeData.setEndTime(this.getEndTime(context));
        runtimeData.setFlowDefinitionId(flowNodePublishInfo.getFlowDefinitionId());
        runtimeData.setExtraParams(this.getExtraParams(cacheInfo.keys, businessData));
        runtimeData.setPublished(this.isPublished(businessData));
        runtimeData.setTraceId(traceId);
        runtimeData.setAippInstanceId(ObjectUtils.cast(businessData.get(AippConst.BS_AIPP_INST_ID_KEY)));
        runtimeData.setNodeInfos(Collections.singletonList(this.convert(flowNodePublishInfo, context)));
        this.nodeRuntimeDataPublisher.onPublish(runtimeData);
        this.aippFlowRuntimeInfoService.cache(runtimeData);

        // 遇到结束节点或异常时删除缓存.
        if (NodeTypes.END.name().equals(nodeType) || MetaInstStatusEnum.ERROR.name().equals(context.getStatus())) {
            this.cache.remove(traceId);
        }
    }

    private long getEndTime(FlowPublishContext context) {
        LocalDateTime time = Optional.ofNullable(context.getArchivedAt()).orElseGet(LocalDateTime::now);
        return this.toLong(time);
    }

    private CacheInfo getResult(FlowPublishContext context, String traceId, String nodeType) {
        if (NodeTypes.START.name().equals(nodeType)) {
            long startTime = this.toLong(context.getCreateAt());
            List<String> keys = this.nodeRuntimeDataPublisher.getExtraParamKeys();
            CacheInfo cacheInfo = new CacheInfo(keys, startTime);
            this.cache.put(traceId, cacheInfo);
            return cacheInfo;
        }
        return Optional.ofNullable(this.cache.get(traceId))
                .orElseThrow(() -> new IllegalStateException(
                        StringUtils.format("node sequence if trace[{0}] disorder", traceId)));
    }

    private boolean isPublished(Map<String, Object> businessData) {
        String aippId = ObjectUtils.cast(businessData.get(AippConst.BS_AIPP_ID_KEY));
        String version = ObjectUtils.cast(businessData.get(AippConst.BS_AIPP_VERSION_KEY));
        Meta meta = MetaUtils.getAnyMeta(this.metaService, aippId, version, Utils.getOpContext(businessData));
        String appId = ObjectUtils.cast(meta.getAttributes().get(AippConst.ATTR_APP_ID_KEY));
        AppBuilderApp app = this.appFactory.create(appId);
        return app.isPublished();
    }

    private NodeInfo convert(FlowNodePublishInfo publishInfo, FlowPublishContext context) {
        Map<String, Object> businessData = publishInfo.getBusinessData();
        NodeInfo info = new NodeInfo();
        info.setNodeId(publishInfo.getNodeId());
        info.setNodeType(publishInfo.getNodeType());
        info.setStartTime(this.toLong(context.getCreateAt()));
        info.setRunCost(this.getEndTime(context) - info.getStartTime());
        info.setParameters(this.buildParameters(businessData, publishInfo.getNodeId()));
        info.setStatus(context.getStatus());
        return info;
    }

    @SuppressWarnings("unchecked")
    private List<Parameter> buildParameters(Map<String, Object> businessData, String nodeId) {
        // 如果根据nodeId找不到，则说明节点没有出入参.
        List<Map<String, Object>> executeInfos = MapUtils.getValueByKeys(businessData,
                Arrays.asList("_internal", "executeInfo", nodeId), List.class).orElseGet(Collections::emptyList);
        if (executeInfos.isEmpty()) {
            return Collections.emptyList();
        }
        return executeInfos.stream().map(info -> {
            Parameter parameter = new Parameter();
            Optional.ofNullable(info.get("input")).ifPresent(in -> parameter.setInput(JsonUtils.toJsonString(in)));
            Optional.ofNullable(info.get("output")).ifPresent(out -> parameter.setOutput(JsonUtils.toJsonString(out)));
            return parameter;
        }).collect(Collectors.toList());
    }

    private Map<String, Object> getExtraParams(List<String> keys, Map<String, Object> businessData) {
        return keys.stream()
                .map(k -> new Pair<>(k, businessData.get(k)))
                .collect(Collectors.toMap(Pair::getKey, Pair::getValue));
    }

    private long toLong(LocalDateTime time) {
        return time.atZone(ZoneOffset.UTC).toInstant().toEpochMilli();
    }

    /**
     * 缓存信息.
     */
    @Data
    @AllArgsConstructor
    private static class CacheInfo {
        private List<String> keys;
        private long startTime;
    }

    private static class MapUtils {
        /**
         * 通过keys获取结果.
         *
         * @param map map对象.
         * @param keys 需要查询的key的列表.
         * @param clz 结果对应的类型.
         * @param <T> 代表结果的类型.
         * @return 结果对象.
         */
        @SuppressWarnings("unchecked")
        public static <T> Optional<T> getValueByKeys(Map<String, Object> map, List<String> keys, Class<T> clz) {
            Map<String, Object> tmp = map;
            for (int i = 0; i < keys.size() - 1; i++) {
                tmp = ObjectUtils.as(tmp.get(keys.get(i)), Map.class);
                if (Objects.isNull(tmp)) {
                    throw new IllegalArgumentException(
                            StringUtils.format("No keys in businessData.keys: []", String.join(",", keys)));
                }
            }
            return Optional.ofNullable(ObjectUtils.as(tmp.get(keys.get(keys.size() - 1)), clz));
        }
    }
}
