/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.factory.store;

import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fitable;
import com.huawei.jade.fel.spi.chat.message.FlatChatMessage;
import com.huawei.jade.fel.spi.chat.message.ToolMessage;
import com.huawei.jade.fel.spi.tool.Tool;
import com.huawei.jade.fel.spi.tool.ToolCall;
import com.huawei.jade.fel.spi.tool.ToolProvider;
import com.huawei.jade.store.service.ItemService;
import com.huawei.jade.store.service.ToolExecuteService;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 表示 {@link ToolProvider} 的默认实现。
 *
 * @author 刘信宏
 * @since 2024-4-17
 */
@Component
public class WaterFlowToolProvider implements ToolProvider {
    private final ToolExecuteService executeService;
    private final ItemService itemService;

    /**
     * 创建默认工具提供者。
     *
     * @param executeService 表示执行服务的 {@link ToolExecuteService}。
     * @param itemService 表示查询服务的 {@link ItemService}。
     */
    public WaterFlowToolProvider(ToolExecuteService executeService, ItemService itemService) {
        this.executeService = executeService;
        this.itemService = itemService;
    }

    @Override
    @Fitable(id = "app-factory")
    public FlatChatMessage call(ToolCall toolCall) {
        return new FlatChatMessage(new ToolMessage(toolCall.getId(),
                executeService.executeTool(toolCall.getName(), toolCall.getParameters())));
    }

    @Override
    @Fitable(id = "app-factory")
    public List<Tool> getTool(List<String> name) {
        return name.stream().map(itemService::getItem).filter(Objects::nonNull).map(item -> {
            Set<String> tags = item.getTags();
            return new Tool(tags.contains("WaterFlow"), item.getSchema());
        }).collect(Collectors.toList());
    }
}
