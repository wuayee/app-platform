/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.support;

import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.jade.store.ItemInfo;
import com.huawei.jade.store.Tool;

/**
 * 表示 {@link Tool} 的抽象实现类。
 *
 * @author 季聿阶
 * @since 2024-04-25
 */
public abstract class AbstractTool extends AbstractItem implements Tool {
    private final Metadata metadata;

    /**
     * 通过工具的基本信息和工具元数据来初始化 {@link AbstractTool} 的新实例。
     *
     * @param itemInfo 表示工具的基本信息的 {@link ItemInfo}。
     * @param metadata 表示工具的元数据的 {@link Metadata}。
     */
    protected AbstractTool(ItemInfo itemInfo, Metadata metadata) {
        super(itemInfo);
        this.metadata = notNull(metadata, "The tool metadata cannot be null.");
    }

    @Override
    public Metadata metadata() {
        return this.metadata;
    }
}
