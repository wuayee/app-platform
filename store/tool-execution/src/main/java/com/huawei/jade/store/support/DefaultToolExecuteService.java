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
import com.huawei.fitframework.serialization.ObjectSerializer;
import com.huawei.fitframework.util.MapBuilder;
import com.huawei.fitframework.util.StringUtils;
import com.huawei.jade.store.Tool;
import com.huawei.jade.store.repository.ItemRepository;
import com.huawei.jade.store.service.ToolExecuteService;

import java.util.Map;

/**
 * 表示 {@link ToolExecuteService} 的默认实现。
 *
 * @author 季聿阶 j00559309
 * @since 2024-04-08
 */
@Component
public class DefaultToolExecuteService implements ToolExecuteService {
    private static final String ERROR_MESSAGE_KEY = "errorMessage";

    private final ObjectSerializer serializer;
    private final ItemRepository itemRepository;

    /**
     * 通过元素服务、客户端和序列化实例创建 {@link DefaultToolExecuteService} 的新实例。
     *
     * @param serializer 表示 Json 序列化实例的 {@link ObjectSerializer}。
     * @param itemRepository 表示商品的存储库的 {@link ItemRepository}。
     */
    public DefaultToolExecuteService(@Fit(alias = "json") ObjectSerializer serializer, ItemRepository itemRepository) {
        this.serializer = notNull(serializer, "The serializer cannot be null.");
        this.itemRepository = notNull(itemRepository, "The item repo cannot be null.");
    }

    @Override
    @Fitable(id = "standard")
    public String executeTool(String uniqueName, String jsonArgs) {
        notBlank(uniqueName, "The tool name cannot be blank.");
        return this.itemRepository.getItem(uniqueName)
                .filter(item -> item instanceof Tool)
                .map(Tool.class::cast)
                .map(tool -> tool.callByJson(jsonArgs))
                .orElseGet(() -> this.makeErrorMessage(uniqueName));
    }

    private String makeErrorMessage(String uniqueName) {
        Map<Object, Object> error = MapBuilder.get()
                .put(ERROR_MESSAGE_KEY, StringUtils.format("No tool. [toolUniqueName={0}]", uniqueName))
                .build();
        return new String(this.serializer.serialize(error, UTF_8));
    }
}
