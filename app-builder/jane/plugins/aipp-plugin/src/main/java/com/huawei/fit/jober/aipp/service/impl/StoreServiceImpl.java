/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.service.impl;

import static com.huawei.fit.jober.aipp.init.AippComponentInitiator.COMPONENT_DATA;

import com.huawei.fit.jober.aipp.common.JsonUtils;
import com.huawei.fit.jober.aipp.constants.AippConst;
import com.huawei.fit.jober.aipp.dto.StoreBasicNodeInfoDto;
import com.huawei.fit.jober.aipp.dto.StoreNodeConfigResDto;
import com.huawei.fit.jober.aipp.enums.AppCategory;
import com.huawei.fit.jober.aipp.service.StoreService;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.util.StringUtils;
import com.huawei.jade.store.service.ItemData;
import com.huawei.jade.store.service.ItemService;

import java.util.Collections;
import java.util.List;

/**
 * @author 邬涨财 w00575064
 * @since 2024-05-13
 */
@Component
public class StoreServiceImpl implements StoreService {
    private final ItemService itemService;

    public StoreServiceImpl(ItemService itemService) {
        this.itemService = itemService;
    }

    @Override
    public StoreNodeConfigResDto getBasicNodesAndTools() {
        return StoreNodeConfigResDto.builder().toolList(this.buildToolNodesConfig()).basicList(this.buildBasicNodesConfig()).build();
    }

    private List<ItemData> buildToolNodesConfig() {
        return this.itemService.getAllItems(AppCategory.FIT.getCategory(),
                Collections.singletonList(AppCategory.FIT.getTag()),
                Collections.singletonList(StringUtils.EMPTY),
                1,
                10);
    }

    private List<StoreBasicNodeInfoDto> buildBasicNodesConfig() {
        return JsonUtils.parseObject(COMPONENT_DATA.get(AippConst.BASIC_NODE_COMPONENT_DATA_KEY), List.class);
    }
}
