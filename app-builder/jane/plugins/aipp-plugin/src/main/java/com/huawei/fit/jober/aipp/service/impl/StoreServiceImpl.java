/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.service.impl;

import static com.huawei.fit.jober.aipp.init.AippComponentInitiator.COMPONENT_DATA;

import com.huawei.fit.jober.aipp.common.JsonUtils;
import com.huawei.fit.jober.aipp.constants.AippConst;
import com.huawei.fit.jober.aipp.dto.AppBuilderWaterFlowInfoDto;
import com.huawei.fit.jober.aipp.dto.StoreBasicNodeInfoDto;
import com.huawei.fit.jober.aipp.dto.StoreNodeConfigResDto;
import com.huawei.fit.jober.aipp.dto.StoreWaterFlowDto;
import com.huawei.fit.jober.aipp.enums.AppCategory;
import com.huawei.fit.jober.aipp.mapper.AppBuilderAppMapper;
import com.huawei.fit.jober.aipp.po.AppBuilderAppPO;
import com.huawei.fit.jober.aipp.service.StoreService;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.util.StringUtils;
import com.huawei.jade.carver.tool.model.query.ToolQuery;
import com.huawei.jade.carver.tool.model.transfer.ToolData;
import com.huawei.jade.carver.tool.service.ToolService;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 邬涨财 w00575064
 * @since 2024-05-13
 */
@Component
public class StoreServiceImpl implements StoreService {
    private final ToolService toolService;
    private final AppBuilderAppMapper appBuilderAppMapper;

    public StoreServiceImpl(ToolService toolService, AppBuilderAppMapper appBuilderAppMapper) {
        this.toolService = toolService;
        this.appBuilderAppMapper = appBuilderAppMapper;
    }

    @Override
    public StoreNodeConfigResDto getBasicNodesAndTools(int pageNum, int pageSize) {
        return StoreNodeConfigResDto.builder()
                .toolList(this.buildToolNodesConfig(AppCategory.FIT, pageNum, pageSize))
                .basicList(this.buildBasicNodesConfig())
                .build();
    }

    private List<ToolData> buildToolNodesConfig(AppCategory appCategory, int pageNum, int pageSize) {
        ToolQuery query = new ToolQuery(null,
                Collections.singletonList(appCategory.getTag()),
                Collections.singletonList(StringUtils.EMPTY),
                pageNum,
                pageSize);
        return this.toolService.searchTools(query).getData();
    }

    private List<StoreBasicNodeInfoDto> buildBasicNodesConfig() {
        return JsonUtils.parseObject(COMPONENT_DATA.get(AippConst.BASIC_NODE_COMPONENT_DATA_KEY), List.class);
    }

    @Override
    public List<AppBuilderWaterFlowInfoDto> getWaterFlowInfos(int pageNum, int pageSize) {
        List<ToolData> waterFlows = this.buildToolNodesConfig(AppCategory.WATER_FLOW, pageNum, pageSize);
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
}
