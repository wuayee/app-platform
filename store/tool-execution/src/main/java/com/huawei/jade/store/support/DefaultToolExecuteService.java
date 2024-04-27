/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.support;

import static com.huawei.fitframework.inspection.Validation.notBlank;
import static com.huawei.fitframework.inspection.Validation.notNull;
import static java.nio.charset.StandardCharsets.UTF_8;

import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fit;
import com.huawei.fitframework.annotation.Fitable;
import com.huawei.fitframework.broker.client.BrokerClient;
import com.huawei.fitframework.serialization.ObjectSerializer;
import com.huawei.fitframework.util.MapBuilder;
import com.huawei.jade.store.ItemInfo;
import com.huawei.jade.store.Tool;
import com.huawei.jade.store.ToolFactory;
import com.huawei.jade.store.repository.ToolFactoryRepository;
import com.huawei.jade.store.service.ItemDto;
import com.huawei.jade.store.service.ItemService;
import com.huawei.jade.store.service.ToolExecuteService;

import java.util.Map;
import java.util.Optional;

/**
 * 表示 {@link ToolExecuteService} 的默认实现。
 *
 * @author 季聿阶 j00559309
 * @since 2024-04-08
 */
@Component
public class DefaultToolExecuteService implements ToolExecuteService {
    private static final String ERROR_MESSAGE_KEY = "errorMessage";

    private final ItemService service;
    private final ObjectSerializer serializer;
    private final ToolFactoryRepository toolFactoryRepository;

    /**
     * 通过元素服务、客户端和序列化实例创建 {@link DefaultToolExecuteService} 的新实例。
     *
     * @param service 表示元素管理服务的 {@link ItemService}。
     * @param brokerClient 表示运行客户端的 {@link BrokerClient}。
     * @param serializer 表示 Json 序列化实例的 {@link ObjectSerializer}。
     * @param toolFactoryRepository 表示创建工具工厂的存储库的实例的 {@link ToolFactoryRepository}。
     */
    public DefaultToolExecuteService(ItemService service, BrokerClient brokerClient,
            @Fit(alias = "json") ObjectSerializer serializer, ToolFactoryRepository toolFactoryRepository) {
        this.service = service;
        this.serializer = serializer;
        this.toolFactoryRepository = notNull(toolFactoryRepository, "The tool factory repo cannot be null.");
        this.toolFactoryRepository.register(ToolFactory.fit(brokerClient, this.serializer));
    }

    @Override
    @Fitable(id = "standard")
    public String executeTool(String uniqueName, String jsonArgs) {
        notBlank(uniqueName, "The tool name cannot be blank.");
        ItemDto itemDto = this.service.getItem(uniqueName);
        Optional<ToolFactory> factory = this.toolFactoryRepository.query(itemDto.getTags());
        if (!factory.isPresent()) {
            return this.makeErrorMessage();
        }
        ItemInfo itemInfo = ItemDto.convertToItemInfo(itemDto);
        Tool tool = factory.get().create(itemInfo, Tool.Metadata.fromSchema(itemDto.getSchema()));
        return tool.callByJson(jsonArgs);
    }

    private String makeErrorMessage() {
        Map<Object, Object> error = MapBuilder.get().put(ERROR_MESSAGE_KEY, "Tags in invalid.").build();
        return new String(this.serializer.serialize(error, UTF_8));
    }
}
