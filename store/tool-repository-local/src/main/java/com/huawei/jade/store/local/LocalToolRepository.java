/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.local;

import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.broker.Genericable;
import com.huawei.fitframework.broker.LocalGenericableRepository;
import com.huawei.fitframework.broker.client.BrokerClient;
import com.huawei.jade.store.Tool;
import com.huawei.jade.store.ToolRepository;
import com.huawei.jade.store.support.FitMethodFunctionalTool;

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
    private final LocalGenericableRepository localGenericableRepository;
    private final BrokerClient client;

    public LocalToolRepository(LocalGenericableRepository localGenericableRepository, BrokerClient client) {
        this.localGenericableRepository = localGenericableRepository;
        this.client = client;
    }

    @Override
    public int getToolCount() {
        return this.localGenericableRepository.getAll().size();
    }

    @Override
    public List<Tool> getTools(int offset, int limit) {
        List<Genericable> genericables = new ArrayList<>(this.localGenericableRepository.getAll().values());
        List<Genericable> subGenericables = genericables.subList(offset, offset + limit);
        return subGenericables.stream().map(this::convert).collect(Collectors.toList());
    }

    @Override
    public Optional<Tool> getTool(String name) {
        return this.localGenericableRepository.get(name, "1.0.0").map(this::convert);
    }

    private Tool convert(Genericable genericable) {
        return new FitMethodFunctionalTool(this.client, genericable.method().method());
    }
}
