/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.tool.support;

import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.log.Logger;
import com.huawei.jade.fel.tool.ToolFactory;
import com.huawei.jade.fel.tool.ToolFactoryRepository;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 表示 {@link ToolFactoryRepository} 的默认实现。
 *
 * @author 易文渊
 * @since 2024-08-15
 */
@Component
public class DefaultToolFactoryRepository implements ToolFactoryRepository {
    private static final Logger log = Logger.get(DefaultToolFactoryRepository.class);

    private final Map<String, ToolFactory> factoryCache = new ConcurrentHashMap<>();

    @Override
    public void register(ToolFactory factory) {
        if (factory == null) {
            return;
        }
        this.factoryCache.put(factory.type(), factory);
        log.info("Register factory[type={}] success.", factory.type());
    }

    @Override
    public void unregister(ToolFactory factory) {
        if (factory == null) {
            return;
        }
        this.factoryCache.remove(factory.type());
        log.info("Unregister factory[type={}] success.", factory.type());
    }

    @Override
    public Optional<ToolFactory> match(Set<String> runnable) {
        return runnable.stream().filter(this.factoryCache::containsKey).map(this.factoryCache::get).findFirst();
    }
}