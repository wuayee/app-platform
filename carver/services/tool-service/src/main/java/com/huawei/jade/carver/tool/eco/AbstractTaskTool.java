/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.carver.tool.eco;

import static com.huawei.fitframework.inspection.Validation.isInstanceOf;
import static com.huawei.fitframework.inspection.Validation.isTrue;
import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fitframework.serialization.ObjectSerializer;
import com.huawei.jade.carver.tool.support.AbstractTool;

/**
 * 表示 {@link TaskTool} 的抽象实现父类。
 *
 * @author 季聿阶
 * @since 2024-06-04
 */
public abstract class AbstractTaskTool extends AbstractTool implements TaskTool {
    /**
     * 通过 Json 序列化器、工具的基本信息和工具元数据来初始化 {@link AbstractTaskTool} 的新实例。
     *
     * @param serializer 表示 Json 序列化器的 {@link ObjectSerializer}。
     * @param itemInfo 表示工具的基本信息的 {@link Info}。
     * @param metadata 表示工具的元数据的 {@link Metadata}。
     */
    protected AbstractTaskTool(ObjectSerializer serializer, Info itemInfo, Metadata metadata) {
        super(serializer, itemInfo, metadata);
    }

    @Override
    public Object execute(Object... args) {
        notNull(args, "The call args cannot be null.");
        isTrue(args.length >= 1, "The call args must have 1 arg at least.");
        String taskId = isInstanceOf(args[0], String.class, "The first arg must be String.class.");
        Object[] actual = new Object[args.length - 1];
        System.arraycopy(args, 1, actual, 0, actual.length);
        return this.executeWithTask(taskId, actual);
    }
}
