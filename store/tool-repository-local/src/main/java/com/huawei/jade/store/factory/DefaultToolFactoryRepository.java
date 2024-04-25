/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.factory;

import com.huawei.fitframework.annotation.Component;
import com.huawei.jade.store.ToolFactory;
import com.huawei.jade.store.repository.ToolFactoryRepository;

import java.util.HashMap;
import java.util.Map;

/**
 * 表示创建工具的工厂。
 *
 * @author 王攀博
 * @since 2024-04-23
 */
@Component
public class DefaultToolFactoryRepository implements ToolFactoryRepository {
    private static volatile DefaultToolFactoryRepository factoryRepo = null;

    private final Map<String, ToolFactory> factories = new HashMap<>();

    private DefaultToolFactoryRepository() {
    }

    /**
     * 获取默认工具工厂存储库的单例。
     *
     * @return 表示工具工厂存储库的单例的 {@link DefaultToolFactoryRepository}。
     */
    public static DefaultToolFactoryRepository getInstance() {
        if (factoryRepo == null) {
            synchronized (DefaultToolFactoryRepository.class) {
                if (factoryRepo == null) {
                    factoryRepo = new DefaultToolFactoryRepository();
                }
            }
        }
        return factoryRepo;
    }

    @Override
    public void register(ToolFactory factory) {
        if (factory == null) {
            return;
        }
        this.factories.put(factory.type(), factory);
    }

    @Override
    public void unregister(String type) {
        this.factories.remove(type);
    }

    @Override
    public ToolFactory query(String type) {
        return this.factories.get(type);
    }
}
