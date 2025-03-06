/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.oms.entity;

import com.huawei.fit.http.protocol.MimeType;
import com.huawei.fitframework.inspection.Nonnull;

import java.io.Closeable;

/**
 * 表示消息体内的数据。
 *
 * @author 季聿阶
 * @since 2022-07-14
 */
public interface Entity extends Closeable {
    /**
     * 获取实体的媒体文件类型。
     *
     * @return 表示实体的媒体文件类型的 {@link MimeType}。
     */
    @Nonnull
    MimeType resolvedMimeType();
}
