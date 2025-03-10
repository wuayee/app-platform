/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.service.impl;

import static modelengine.fit.jober.aipp.constants.AippConst.BS_NODE_ID_KEY;
import static modelengine.fit.jober.aipp.constants.AippConst.NODE_START_TIME_KEY;
import static modelengine.fitframework.util.ObjectUtils.cast;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jane.meta.multiversion.MetaInstanceService;
import modelengine.fit.jane.meta.multiversion.MetaService;
import modelengine.fit.jane.meta.multiversion.definition.Meta;
import modelengine.fit.jane.meta.multiversion.instance.Instance;
import modelengine.fit.jober.aipp.constants.AippConst;
import modelengine.fit.jober.aipp.domain.AppBuilderApp;
import modelengine.fit.jober.aipp.domain.AppBuilderRuntimeInfo;
import modelengine.fit.jober.aipp.enums.MetaInstStatusEnum;
import modelengine.fit.jober.aipp.factory.AppBuilderAppFactory;
import modelengine.fit.jober.aipp.repository.AppBuilderRuntimeInfoRepository;
import modelengine.fit.jober.aipp.service.RuntimeInfoService;
import modelengine.fit.jober.aipp.util.ConvertUtils;
import modelengine.fit.jober.aipp.util.DataUtils;
import modelengine.fit.jober.aipp.util.JsonUtils;
import modelengine.fit.jober.aipp.util.MetaInstanceUtils;
import modelengine.fit.jober.aipp.util.MetaUtils;
import modelengine.fit.jober.entity.consts.NodeTypes;
import modelengine.fit.runtime.entity.Parameter;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 流程运行时信息实现类
 *
 * @author 邬涨财
 * @since 2024-12-17
 */
@Component
public class RuntimeInfoServiceImpl implements RuntimeInfoService {
    private final MetaService metaService;

    private final AppBuilderAppFactory appFactory;

    private final MetaInstanceService metaInstanceService;

    private final AppBuilderRuntimeInfoRepository runtimeInfoRepository;

    /**
     * 构造函数.
     *
     * @param metaService {@link MetaService} 对象.
     * @param appFactory {@link AppBuilderAppFactory} app工厂对象.
     * @param metaInstanceService {@link MetaInstanceService} 对象。
     * @param runtimeInfoRepository {@link AppBuilderRuntimeInfoRepository} 对象。
     */
    public RuntimeInfoServiceImpl(MetaService metaService, AppBuilderAppFactory appFactory,
            MetaInstanceService metaInstanceService, AppBuilderRuntimeInfoRepository runtimeInfoRepository) {
        this.metaService = metaService;
        this.appFactory = appFactory;
        this.metaInstanceService = metaInstanceService;
        this.runtimeInfoRepository = runtimeInfoRepository;
    }

    /**
     * 根据业务数据判断应用是否已发布。
     *
     * @param businessData 业务数据。
     * @return 是否已发布。
     */
    public boolean isPublished(Map<String, Object> businessData) {
        String aippId = cast(businessData.get(AippConst.BS_AIPP_ID_KEY));
        String version = cast(businessData.get(AippConst.BS_AIPP_VERSION_KEY));
        Meta meta = MetaUtils.getAnyMeta(this.metaService, aippId, version, DataUtils.getOpContext(businessData));
        String appId = cast(meta.getAttributes().get(AippConst.ATTR_APP_ID_KEY));
        AppBuilderApp app = this.appFactory.create(appId);
        return app.isPublished();
    }

    /**
     * 构建参数集合
     *
     * @param map 业务数据。
     * @param nodeId 节点id。
     * @return 构建的参数集合
     */
    public List<Parameter> buildParameters(Map<String, Object> map, String nodeId) {
        // 如果根据nodeId找不到，则说明节点没有出入参.
        List<Map<String, Object>> executeInfos = cast(
                this.getValueByKeys(map, Arrays.asList("_internal", "executeInfo", nodeId), List.class)
                        .orElseGet(Collections::emptyList));
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

    @Override
    public void insertRuntimeInfo(String instanceId, Map<String, Object> map, MetaInstStatusEnum status,
            String errorMsg, OperationContext context) {
        String versionId = this.metaInstanceService.getMetaVersionId(instanceId);
        Instance instDetail = MetaInstanceUtils.getInstanceDetail(versionId, instanceId, context,
                this.metaInstanceService);
        String flowTraceId = instDetail.getInfo().get(AippConst.INST_FLOW_INST_ID_KEY);
        Meta meta = this.metaService.retrieve(versionId, context);
        String appId = cast(meta.getAttributes().get(AippConst.ATTR_APP_ID_KEY));
        AppBuilderApp app = this.appFactory.create(appId);
        String nodeId = cast(map.getOrDefault(BS_NODE_ID_KEY, StringUtils.EMPTY));
        AppBuilderRuntimeInfo runtimeInfo = AppBuilderRuntimeInfo.builder()
                .traceId(flowTraceId)
                .flowDefinitionId(
                        cast(meta.getAttributes().getOrDefault(AippConst.ATTR_FLOW_DEF_ID_KEY, StringUtils.EMPTY)))
                .instanceId(instanceId)
                .nodeId(nodeId)
                .nodeType(NodeTypes.STATE.getType())
                .startTime(cast(map.getOrDefault(NODE_START_TIME_KEY, ConvertUtils.toLong(LocalDateTime.now()))))
                .endTime(ConvertUtils.toLong(LocalDateTime.now()))
                .published(app.isPublished())
                .parameters(this.buildParameters(map, nodeId))
                .errorMsg(errorMsg)
                .status(status.name())
                .createBy("system")
                .updateBy("system")
                .createAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .build();
        this.runtimeInfoRepository.insertOne(runtimeInfo);
    }

    private <T> Optional<T> getValueByKeys(Map<String, Object> map, List<String> keys, Class<T> clz) {
        Map<String, Object> tmp = map;
        for (int i = 0; i < keys.size() - 1; i++) {
            if (tmp.get(keys.get(i)) instanceof Map) {
                tmp = cast(tmp.get(keys.get(i)));
            } else {
                tmp = null;
            }
            if (Objects.isNull(tmp)) {
                return Optional.empty();
            }
        }
        return Optional.ofNullable(ObjectUtils.as(tmp.get(keys.get(keys.size() - 1)), clz));
    }
}
