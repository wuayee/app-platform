/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.carver.tool.repository.pgsql.support;

import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.util.CollectionUtils;
import com.huawei.fitframework.util.StringUtils;
import com.huawei.jade.carver.tool.ToolFactory;
import com.huawei.jade.carver.tool.repository.ToolFactoryRepository;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 表示工厂的存储库。
 *
 * @author 李金绪 l00878072
 * @since 2024/5/16
 */
@Component
public class DefaultToolFactoryRepository implements ToolFactoryRepository {
    private final Map<String, ToolFactory> tagFactoryMapping = new ConcurrentHashMap<>();

    @Override
    public Optional<ToolFactory> query(Set<String> tags) {
        if (CollectionUtils.isEmpty(tags)) {
            return Optional.empty();
        }
        for (String tag : tags) {
            String actual = StringUtils.toUpperCase(tag);
            if (this.tagFactoryMapping.containsKey(actual)) {
                return Optional.ofNullable(this.tagFactoryMapping.get(actual));
            }
        }
        return Optional.empty();
    }

    @Override
    public void register(ToolFactory toolFactory) {
        if (toolFactory == null) {
            return;
        }
        this.tagFactoryMapping.put(StringUtils.toUpperCase(toolFactory.type()), toolFactory);
    }

    @Override
    public void unregister(ToolFactory toolFactory) {
        if (toolFactory == null) {
            return;
        }
        this.tagFactoryMapping.remove(StringUtils.toUpperCase(toolFactory.type()));
    }
}
