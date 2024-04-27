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
import com.huawei.jade.store.Item;
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
    public DefaultToolExecuteService(@Fit(alias = "json") ObjectSerializer serializer,
            ItemRepository itemRepository) {
        this.serializer = notNull(serializer, "The serializer cannot be null.");
        this.itemRepository = notNull(itemRepository, "The item repo cannot be null.");
    }

    @Override
    @Fitable(id = "standard")
    public String executeTool(String uniqueName, String jsonArgs) {
        notBlank(uniqueName, "The tool name cannot be blank.");
        Item item = itemRepository.getItem(uniqueName);
        Tool tool = null;
        if (item instanceof Tool) {
            tool = (Tool) item;
        } else {
            return this.makeErrorMessage();
        }

        if (tool == null) {
            return this.makeErrorMessage();
        }
        return tool.callByJson(jsonArgs);
    }

    private String makeErrorMessage() {
        Map<Object, Object> error = MapBuilder.get().put(ERROR_MESSAGE_KEY, "Tags in invalid.").build();
        return new String(this.serializer.serialize(error, UTF_8));
    }
}
