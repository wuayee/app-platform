/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.service.impl;

import static com.huawei.fit.jober.aipp.enums.ToolCategoryEnum.HUGGINGFACE;
import static com.huawei.fit.jober.aipp.init.AippComponentInitiator.COMPONENT_DATA;

import com.huawei.fit.jane.common.entity.OperationContext;
import com.huawei.fit.jober.aipp.common.JsonUtils;
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
import com.huawei.fit.jober.aipp.po.AppBuilderAppPO;
import com.huawei.fit.jober.aipp.service.StoreService;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.util.MapUtils;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.fitframework.util.StringUtils;
import com.huawei.jade.carver.ListResult;
import com.huawei.jade.carver.tool.model.query.ToolQuery;
import com.huawei.jade.carver.tool.model.transfer.ToolData;
import com.huawei.jade.carver.tool.service.ToolService;
import com.huawei.jade.store.entity.query.ModelQuery;
import com.huawei.jade.store.entity.transfer.TaskData;
import com.huawei.jade.store.service.EcoTaskService;
import com.huawei.jade.store.service.HuggingFaceModelService;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 邬涨财 w00575064
 * @since 2024-05-13
 */
@Component
public class StoreServiceImpl implements StoreService {
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
    public StoreNodeConfigResDto getBasicNodesAndTools(String tag, boolean orTags, int pageNum, int pageSize) {
        return StoreNodeConfigResDto.builder()
                .toolList(this.getToolModelList(tag, orTags, pageNum, pageSize))
                .basicList(this.buildBasicNodesConfig())
                .build();
    }

    @Override
    public ToolDto getPlugins(String tag, boolean orTags, int pageNum, int pageSize,
            OperationContext operationContext) {
        ListResult<ToolData> toolDataListResult = this.buildToolNodesConfig(tag, orTags, pageNum, pageSize);
        return ToolDto.builder().toolData(toolDataListResult.getData()).total(toolDataListResult.getCount()).build();
    }

    @Override
    public List<StoreBasicNodeInfoDto> getBasic() {
        return this.buildBasicNodesConfig();
    }

    private List<ToolModelDto> getToolModelList(String tag, boolean orTags, int pageNum, int pageSize) {
        return this.buildToolNodesConfig(tag, orTags, pageNum, pageSize).getData()
                .stream()
                .map(toolData -> ToolModelDto.combine2ToolModelDto(toolData,
                        tag.equalsIgnoreCase(HUGGINGFACE.getName())
                                ? getDefaultModel(toolData, tag)
                                : StringUtils.EMPTY))
                .collect(Collectors.toList());
    }

    private ListResult<ToolData> buildToolNodesConfig(String tag, boolean orTags, int pageNum, int pageSize) {
        ToolQuery query = new ToolQuery(null,
                Collections.singletonList(tag),
                Collections.singletonList(StringUtils.EMPTY),
                orTags,
                pageNum,
                pageSize);
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
        return JsonUtils.parseObject(COMPONENT_DATA.get(AippConst.BASIC_NODE_COMPONENT_DATA_KEY), List.class);
    }

    @Override
    public List<AppBuilderWaterFlowInfoDto> getWaterFlowInfos(boolean orTags, int pageNum, int pageSize) {
        List<ToolData> waterFlows =
                this.buildToolNodesConfig(AppCategory.WATER_FLOW.getTag(), orTags, pageNum, pageSize).getData();
        List<String> storeIds = waterFlows.stream().map(ToolData::getUniqueName).collect(Collectors.toList());
        if (storeIds.isEmpty()) {
            return Collections.emptyList();
        }
        List<AppBuilderAppPO> appInfos = appBuilderAppMapper.selectWithStoreId(storeIds);
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
