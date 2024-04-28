/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.factory.store;

import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fit;
import com.huawei.fitframework.broker.client.BrokerClient;
import com.huawei.fitframework.serialization.ObjectSerializer;
import com.huawei.jade.store.ItemInfo;
import com.huawei.jade.store.Tool;
import com.huawei.jade.store.ToolFactory;

/**
 * 表示自定义流式工具的工厂实现。
 *
 * @author 王攀博
 * @since 2024-04-23
 */
@Component
public class WaterFlowToolFactory implements ToolFactory {
    private final ToolFactory fitToolFactory;

    WaterFlowToolFactory(BrokerClient brokerClient, @Fit(alias = "json") ObjectSerializer serializer) {
        this.fitToolFactory = ToolFactory.fit(brokerClient, serializer);
    }

    @Override
    public String type() {
        return "WaterFlow";
    }

    @Override
    public Tool create(ItemInfo itemInfo, Tool.Metadata metadata) {
        Tool tool = this.fitToolFactory.create(itemInfo, metadata);
        return new WaterFlowTool(tool, itemInfo, metadata);
    }
}
