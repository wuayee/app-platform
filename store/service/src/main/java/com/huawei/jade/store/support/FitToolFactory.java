/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.support;

import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fitframework.broker.client.BrokerClient;
import com.huawei.fitframework.serialization.ObjectSerializer;
import com.huawei.jade.store.ItemInfo;
import com.huawei.jade.store.Tool;
import com.huawei.jade.store.ToolFactory;

/**
 * 表示创建参数工具的工厂。
 *
 * @author 王攀博
 * @since 2024-04-23
 */
public class FitToolFactory implements ToolFactory {
    private final BrokerClient brokerClient;
    private final ObjectSerializer serializer;

    public FitToolFactory(BrokerClient brokerClient, ObjectSerializer serializer) {
        this.brokerClient = notNull(brokerClient, "The broker client cannot be null.");
        this.serializer = notNull(serializer, "The serializer cannot be null.");
    }

    @Override
    public String type() {
        return FitTool.TYPE;
    }

    @Override
    public Tool create(ItemInfo itemInfo, Tool.Metadata metadata) {
        return new FitTool(this.brokerClient, this.serializer, itemInfo, metadata);
    }
}
