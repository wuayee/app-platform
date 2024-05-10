/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.serializer.impl;

import com.huawei.fit.jober.aipp.domain.AppBuilderFlowGraph;
import com.huawei.fit.jober.aipp.po.AppBuilderFlowGraphPO;
import com.huawei.fit.jober.aipp.serializer.BaseSerializer;

/**
 * @author 邬涨财 w00575064
 * @since 2024-04-17
 */
public class AppBuilderFlowGraphSerializer implements BaseSerializer<AppBuilderFlowGraph, AppBuilderFlowGraphPO> {
    @Override
    public AppBuilderFlowGraphPO serialize(AppBuilderFlowGraph appBuilderFlowGraph) {
        if (appBuilderFlowGraph == null) {
            return null;
        }
        return AppBuilderFlowGraphPO.builder()
                .id(appBuilderFlowGraph.getId())
                .name(appBuilderFlowGraph.getName())
                .appearance(appBuilderFlowGraph.getAppearance())
                .createAt(appBuilderFlowGraph.getCreateAt())
                .updateAt(appBuilderFlowGraph.getUpdateAt())
                .createBy(appBuilderFlowGraph.getCreateBy())
                .updateBy(appBuilderFlowGraph.getUpdateBy())
                .build();
    }

    @Override
    public AppBuilderFlowGraph deserialize(AppBuilderFlowGraphPO appBuilderFlowGraphPO) {
        if (appBuilderFlowGraphPO == null) {
            return null;
        }
        return AppBuilderFlowGraph.builder()
                .id(appBuilderFlowGraphPO.getId())
                .name(appBuilderFlowGraphPO.getName())
                .appearance(appBuilderFlowGraphPO.getAppearance())
                .createAt(appBuilderFlowGraphPO.getCreateAt())
                .updateAt(appBuilderFlowGraphPO.getUpdateAt())
                .createBy(appBuilderFlowGraphPO.getCreateBy())
                .updateBy(appBuilderFlowGraphPO.getUpdateBy())
                .build();
    }
}
