/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.support;

import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.util.CollectionUtils;
import com.huawei.jade.store.ToolFactory;
import com.huawei.jade.store.repository.ToolFactoryRepository;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 表示工厂的存储库。
 *
 * @author 王攀博
 * @since 2024-04-26
 */

@Component
public class DefaultToolFactoryRepo implements ToolFactoryRepository {
    private Map<String, ToolFactory> toolFactoryMap = new ConcurrentHashMap<>();

    @Override
    public Optional<ToolFactory> query(Set<String> tags) {
        if (CollectionUtils.isEmpty(tags)) {
            return Optional.empty();
        }
        for (String tag : tags) {
            if (toolFactoryMap.containsKey(tag)) {
                return Optional.ofNullable(this.toolFactoryMap.get(tag));
            }
        }
        return Optional.empty();
    }

    @Override
    public void register(ToolFactory toolFactory) {
        if (toolFactory == null) {
            return;
        }
        this.toolFactoryMap.put(toolFactory.type(), toolFactory);
    }

    @Override
    public void unregister(ToolFactory toolFactory) {
        if (toolFactory == null) {
            return;
        }
        this.toolFactoryMap.remove(toolFactory.type());
    }
}
