/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.fitable;

import com.huawei.fit.jane.meta.multiversion.MetaService;
import com.huawei.fit.jane.meta.multiversion.definition.Meta;
import com.huawei.fit.jober.FlowPublishService;
import com.huawei.fit.jober.aipp.aop.AippLogInsertAspect;
import com.huawei.fit.jober.aipp.constants.AippConst;
import com.huawei.fit.jober.aipp.domain.AppBuilderApp;
import com.huawei.fit.jober.aipp.domain.AppBuilderRuntimeInfo;
import com.huawei.fit.jober.aipp.factory.AppBuilderAppFactory;
import com.huawei.fit.jober.aipp.repository.AppBuilderRuntimeInfoRepository;
import com.huawei.fit.jober.aipp.util.DataUtils;
import com.huawei.fit.jober.aipp.util.JsonUtils;
import com.huawei.fit.jober.aipp.util.MetaUtils;
import com.huawei.fit.jober.entity.FlowNodePublishInfo;
import com.huawei.fit.jober.entity.FlowPublishContext;
import com.huawei.fit.runtime.entity.Parameter;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fitable;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.fitframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 订阅节点运行时信息.
 *
 * @author z00559346 张越
 * @since 2024-05-23
 */
@Component
public class FlowPublishSubscriber implements FlowPublishService {
    private static final Logger log = Logger.get(AippLogInsertAspect.class);

    private final MetaService metaService;
    private final AppBuilderAppFactory appFactory;
    private final AppBuilderRuntimeInfoRepository runtimeInfoRepository;

    /**
     * 构造函数.
     *
     * @param metaService {@link MetaService} 对象.
     * @param appFactory {@link AppBuilderAppFactory} app工厂对象.
     * @param runtimeInfoRepository {@link AppBuilderRuntimeInfoRepository} 对象.
     */
    public FlowPublishSubscriber(MetaService metaService,
            AppBuilderAppFactory appFactory,
            AppBuilderRuntimeInfoRepository runtimeInfoRepository) {
        this.metaService = metaService;
        this.appFactory = appFactory;
        this.runtimeInfoRepository = runtimeInfoRepository;
    }


    @Fitable("com.huawei.fit.jober.aipp.fitable.FlowPublishSubscriber")
    @Override
    public void publishNodeInfo(FlowNodePublishInfo flowNodePublishInfo) {
        log.info("Receive node publish info. info={}", JsonUtils.toJsonString(flowNodePublishInfo));
        Map<String, Object> businessData = flowNodePublishInfo.getBusinessData();
        FlowPublishContext context = flowNodePublishInfo.getFlowContext();
        String traceId = context.getTraceId();
        String nodeId = flowNodePublishInfo.getNodeId();
        String nodeType = flowNodePublishInfo.getNodeType();

        AppBuilderRuntimeInfo runtimeInfo = AppBuilderRuntimeInfo.builder()
                .traceId(traceId)
                .flowDefinitionId(flowNodePublishInfo.getFlowDefinitionId())
                .instanceId(ObjectUtils.cast(businessData.get(AippConst.BS_AIPP_INST_ID_KEY)))
                .nodeId(nodeId)
                .nodeType(nodeType)
                .startTime(this.toLong(context.getCreateAt()))
                .endTime(this.getEndTime(context))
                .published(this.isPublished(businessData))
                .parameters(this.buildParameters(businessData, nodeId))
                .errorMsg(flowNodePublishInfo.getErrorMsg())
                .status(context.getStatus())
                .createBy("system")
                .updateBy("system")
                .createAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .build();

        this.runtimeInfoRepository.insertOne(runtimeInfo);
    }

    private long getEndTime(FlowPublishContext context) {
        LocalDateTime time = Optional.ofNullable(context.getArchivedAt()).orElseGet(LocalDateTime::now);
        return this.toLong(time);
    }

    private boolean isPublished(Map<String, Object> businessData) {
        String aippId = ObjectUtils.cast(businessData.get(AippConst.BS_AIPP_ID_KEY));
        String version = ObjectUtils.cast(businessData.get(AippConst.BS_AIPP_VERSION_KEY));
        Meta meta = MetaUtils.getAnyMeta(this.metaService, aippId, version, DataUtils.getOpContext(businessData));
        String appId = ObjectUtils.cast(meta.getAttributes().get(AippConst.ATTR_APP_ID_KEY));
        AppBuilderApp app = this.appFactory.create(appId);
        return app.isPublished();
    }

    @SuppressWarnings("unchecked")
    private List<Parameter> buildParameters(Map<String, Object> businessData, String nodeId) {
        // 如果根据nodeId找不到，则说明节点没有出入参.
        List<Map<String, Object>> executeInfos = this.getValueByKeys(businessData,
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

    private <T> Optional<T> getValueByKeys(Map<String, Object> map, List<String> keys, Class<T> clz) {
        Map<String, Object> tmp = map;
        for (int i = 0; i < keys.size() - 1; i++) {
            if (tmp.get(keys.get(i)) instanceof Map) {
                tmp = ObjectUtils.cast(tmp.get(keys.get(i)));
            } else {
                tmp = null;
            }
            if (Objects.isNull(tmp)) {
                throw new IllegalArgumentException(
                        StringUtils.format("No keys in businessData.keys: []", String.join(",", keys)));
            }
        }
        return Optional.ofNullable(ObjectUtils.as(tmp.get(keys.get(keys.size() - 1)), clz));
    }

    private long toLong(LocalDateTime time) {
        return time.atZone(ZoneOffset.UTC).toInstant().toEpochMilli();
    }
}
