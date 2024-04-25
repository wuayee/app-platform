/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.local;

import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fit;
import com.huawei.fitframework.broker.Fitable;
import com.huawei.fitframework.broker.Genericable;
import com.huawei.fitframework.broker.LocalGenericableRepository;
import com.huawei.fitframework.broker.client.BrokerClient;
import com.huawei.fitframework.serialization.ObjectSerializer;
import com.huawei.jade.store.ItemInfo;
import com.huawei.jade.store.Tool;
import com.huawei.jade.store.factory.DefaultToolFactoryRepository;
import com.huawei.jade.store.repository.ToolRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 表示 {@link ToolRepository} 的本地实现。
 *
 * @author 季聿阶 j00559309
 * @since 2024-04-08
 */
@Component
public class LocalToolRepository implements ToolRepository {
    private static final String TOOL_TYPE_FIT = "FIT";

    private final LocalGenericableRepository localGenericableRepository;
    private final BrokerClient client;
    private final ObjectSerializer serializer;

    public LocalToolRepository(LocalGenericableRepository localGenericableRepository, BrokerClient client,
            @Fit(alias = "json") ObjectSerializer serializer) {
        this.localGenericableRepository = localGenericableRepository;
        this.client = client;
        this.serializer = serializer;
    }

    @Override
    public int getToolCount() {
        return this.localGenericableRepository.getAll().size();
    }

    @Override
    public List<String> getToolGroups(String type, int offset, int limit) {
        // TODO: 基于类型做过滤，当前本地实现，只有 FIT 工具，其他类型返回错误
        if (!this.TOOL_TYPE_FIT.equals(type)) {
            return null;
        }
        List<Genericable> genericables = new ArrayList<>(this.localGenericableRepository.getAll().values());
        if (offset > genericables.size()) {
            return null;
        }
        int toIndex = Math.min(offset + limit, genericables.size());
        return genericables.subList(offset, toIndex)
                .stream()
                .map(genericable -> genericable.id())
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getToolNames(String type, String group, int offset, int limit) {
        // TODO: 基于类型做过滤，当前本地实现，只有 FIT 工具，其他类型返回错误
        if (!this.TOOL_TYPE_FIT.equals(type)) {
            return null;
        }
        Optional<List<Fitable>> fitablesOp =
                this.localGenericableRepository.get(group, "1.0.0").map(genericable -> genericable.fitables());
        if (fitablesOp.isPresent()) {
            List<Fitable> fitables = fitablesOp.get();
            if (offset > fitables.size()) {
                return null;
            }
            int toIndex = Math.min(offset + limit, fitables.size());
            return fitables.subList(offset, toIndex).stream().map(fitable -> fitable.id()).collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public Optional<Tool> getTool(String group, String name) {
        Optional<Genericable> genericableOp = this.localGenericableRepository.get(group, "1.0.0");
        if (genericableOp.isPresent()) {
            return Optional.empty();
        }
        Genericable genericable = genericableOp.get();
        List<Tool> tools = genericable.fitables()
                .stream()
                .filter(fitable -> fitable.id().equals(name))
                .collect(Collectors.toList())
                .stream()
                .map(fitable -> DefaultToolFactoryRepository.getInstance()
                        .query("FIT")
                        .create(ItemInfo.custom().build(), Tool.Metadata.fromMethod(genericable.method().method())))
                .collect(Collectors.toList());
        return tools.isEmpty() ? Optional.empty() : Optional.ofNullable(tools.get(0));
    }
}
