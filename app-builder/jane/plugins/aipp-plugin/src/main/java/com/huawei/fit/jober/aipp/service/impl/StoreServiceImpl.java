/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.service.impl;

import static com.huawei.fit.jober.aipp.enums.ToolCategoryEnum.HUGGINGFACE;
import static com.huawei.fit.jober.aipp.init.AippComponentInitiator.COMPONENT_DATA;
import static com.huawei.jade.common.Result.calculateOffset;

import com.huawei.fit.jane.common.entity.OperationContext;
import com.huawei.fit.jober.aipp.constants.AippConst;
import com.huawei.fit.jober.aipp.dto.AppBuilderWaterFlowInfoDto;
import com.huawei.fit.jober.aipp.dto.ModelDto;
import com.huawei.fit.jober.aipp.dto.StoreBasicNodeInfoDto;
import com.huawei.fit.jober.aipp.dto.StoreNodeConfigResDto;
import com.huawei.fit.jober.aipp.dto.StoreWaterFlowDto;
import com.huawei.fit.jober.aipp.dto.ToolDto;
import com.huawei.fit.jober.aipp.dto.ToolModelDto;
import com.huawei.fit.jober.aipp.enums.AppCategory;
import com.huawei.fit.jober.aipp.mapper.AppBuilderAppMapper;
import com.huawei.fit.jober.aipp.po.AppBuilderAppPo;
import com.huawei.fit.jober.aipp.service.StoreService;
import com.huawei.fit.jober.aipp.util.JsonUtils;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.MapUtils;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;
import com.huawei.jade.carver.ListResult;
import com.huawei.jade.carver.tool.model.query.ToolQuery;
import com.huawei.jade.carver.tool.model.transfer.ToolData;
import com.huawei.jade.carver.tool.service.ToolService;
import com.huawei.jade.common.ui.globalization.LocaleUiWord;
import com.huawei.jade.store.entity.query.ModelQuery;
import com.huawei.jade.store.entity.transfer.TaskData;
import com.huawei.jade.store.service.EcoTaskService;
import com.huawei.jade.store.service.HuggingFaceModelService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 市场相关接口实现
 *
 * @author 孙怡菲
 * @since 2024-05-10
 */
@Component
public class StoreServiceImpl implements StoreService {
    private static final Logger log = Logger.get(StoreServiceImpl.class);
    private static final String CODENODESTATE = "CODENODESTATE";

    private final ToolService toolService;
    private final EcoTaskService ecoTaskService;
    private final HuggingFaceModelService huggingFaceModelService;
    private final AppBuilderAppMapper appBuilderAppMapper;

    public StoreServiceImpl(ToolService toolService, EcoTaskService ecoTaskService,
            HuggingFaceModelService huggingFaceModelService, AppBuilderAppMapper appBuilderAppMapper) {
        this.toolService = toolService;
        this.ecoTaskService = ecoTaskService;
        this.huggingFaceModelService = huggingFaceModelService;
        this.appBuilderAppMapper = appBuilderAppMapper;
    }

    @Override
    public StoreNodeConfigResDto getBasicNodesAndTools(String tag, String mode, int pageNum, int pageSize,
            String version) {
        return StoreNodeConfigResDto.builder()
                .toolList(this.getToolModelList(tag, mode, pageNum, pageSize, version))
                .basicList(this.buildBasicNodesConfig())
                .build();
    }

    @Override
    public ToolDto getPlugins(ToolQuery toolQuery, OperationContext operationContext) {
        ListResult<ToolData> toolDataListResult = this.toolService.searchTools(toolQuery);
        return ToolDto.builder().toolData(toolDataListResult.getData()).total(toolDataListResult.getCount()).build();
    }

    @Override
    public List<StoreBasicNodeInfoDto> getBasic() {
        return this.buildBasicNodesConfig();
    }

    private List<ToolModelDto> getToolModelList(String tag, String mode, int pageNum, int pageSize,
            String version) {
        return this.buildToolNodesConfig(tag, mode, pageNum, pageSize, version)
                .getData()
                .stream()
                .map(toolData -> ToolModelDto.combine2ToolModelDto(toolData,
                        tag.equalsIgnoreCase(HUGGINGFACE.getName())
                                ? getDefaultModel(toolData, tag)
                                : StringUtils.EMPTY))
                .collect(Collectors.toList());
    }

    private ListResult<ToolData> buildToolNodesConfig(String tag, String mode, int pageNum, int pageSize,
            String version) {
        List<String> includeTag = new ArrayList<>();
        if (StringUtils.isNotBlank(tag)) {
            includeTag.add(tag);
        } else {
            includeTag.add("WATERFLOW");
            includeTag.add("FIT");
        }
        ToolQuery query = new ToolQuery.Builder()
                .toolName(null)
                .includeTags(new HashSet<>(includeTag))
                .excludeTags(new HashSet<>(Collections.singleton(StringUtils.EMPTY)))
                .mode(StringUtils.isNotBlank(tag) ? mode : "OR")
                .offset(calculateOffset(pageNum, pageSize))
                .limit(pageSize)
                .version(version)
                .build();
        return this.toolService.searchTools(query);
    }

    private String getDefaultModel(ToolData toolData, String tag) {
        Map<String, Object> map = ObjectUtils.cast(toolData.getRunnables().get(tag.toUpperCase(Locale.ROOT)));
        if (MapUtils.isEmpty(map) || !map.containsKey("taskName")) {
            return StringUtils.EMPTY;
        }
        String taskName = map.get("taskName") instanceof String ? (String) map.get("taskName") : StringUtils.EMPTY;
        TaskData task = ecoTaskService.getTask(taskName);
        if (task != null) {
            Map<String, Object> context = task.getContext();
            return context.get("defaultModel") instanceof String
                    ? (String) context.get("defaultModel")
                    : StringUtils.EMPTY;
        }
        return StringUtils.EMPTY;
    }

    private List<StoreBasicNodeInfoDto> buildBasicNodesConfig() {
        List<StoreBasicNodeInfoDto> basicNodeList =
                LocaleUiWord.getLocale().getLanguage().equals(Locale.ENGLISH.getLanguage())
                        ? JsonUtils.parseArray(COMPONENT_DATA.get(AippConst.BASIC_NODE_COMPONENT_DATA_EN_KEY),
                        StoreBasicNodeInfoDto[].class)
                        : JsonUtils.parseArray(COMPONENT_DATA.get(AippConst.BASIC_NODE_COMPONENT_DATA_ZH_KEY),
                                StoreBasicNodeInfoDto[].class);
        setUniqueName(basicNodeList);
        return basicNodeList;
    }

    private void setUniqueName(List<StoreBasicNodeInfoDto> basicNodeList) {
        ToolQuery query = new ToolQuery.Builder()
                .toolName(null)
                .includeTags(new HashSet<>(Collections.singletonList(CODENODESTATE)))
                .excludeTags(new HashSet<>())
                .mode("AND")
                .offset(null)
                .limit(null)
                .version(null)
                .build();
        ListResult<ToolData> tools = toolService.getTools(query);
        final String uniqueName;
        if (tools.getCount() != 1) {
            log.warn("Get {} tools with tag CODENODESTATE", tools.getCount());
            uniqueName = StringUtils.EMPTY;
        } else {
            uniqueName = tools.getData().get(0).getUniqueName();
        }
        basicNodeList.stream()
                .filter(nodeInfoDto -> "codeNodeState".equals(nodeInfoDto.getType()))
                .forEach(nodeInfoDto -> {
                    nodeInfoDto.setUniqueName(uniqueName);
                });
    }

    @Override
    public List<AppBuilderWaterFlowInfoDto> getWaterFlowInfos(String mode, int pageNum, int pageSize,
            String version) {
        List<ToolData> waterFlows =
                this.buildToolNodesConfig(AppCategory.WATER_FLOW.getTag(), mode, pageNum, pageSize, version)
                        .getData();
        List<String> storeIds = waterFlows.stream().map(ToolData::getUniqueName).collect(Collectors.toList());
        if (storeIds.isEmpty()) {
            return Collections.emptyList();
        }
        List<AppBuilderAppPo> appInfos = appBuilderAppMapper.selectWithStoreId(storeIds);
        Map<String, StoreWaterFlowDto> appInfoMap = appInfos.stream()
                .collect(Collectors.toMap(info -> JsonUtils.parseObject(info.getAttributes())
                                .get("store_id")
                                .toString(),
                        info -> StoreWaterFlowDto.builder()
                                .version(info.getVersion())
                                .id(info.getId())
                                .tenantId(info.getTenantId())
                                .build()));
        return waterFlows.stream()
                .map(waterFlow -> buildWaterFlowInfo(waterFlow, appInfoMap))
                .collect(Collectors.toList());
    }

    private AppBuilderWaterFlowInfoDto buildWaterFlowInfo(ToolData waterFlow,
            Map<String, StoreWaterFlowDto> appInfoMap) {
        String uniqueName = waterFlow.getUniqueName();
        StoreWaterFlowDto appInfo = appInfoMap.get(uniqueName);
        return AppBuilderWaterFlowInfoDto.builder()
                .itemData(waterFlow)
                .appId(appInfo.getId())
                .version(appInfo.getVersion())
                .tenantId(appInfo.getTenantId())
                .build();
    }

    @Override
    public ModelDto getModels(String taskName, int pageNum, int pageSize) {
        return new ModelDto(this.huggingFaceModelService.getModels(new ModelQuery(taskName, pageNum, pageSize)),
                this.huggingFaceModelService.getCount(taskName));
    }
}
