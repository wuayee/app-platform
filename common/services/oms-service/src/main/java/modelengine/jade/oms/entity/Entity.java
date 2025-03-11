/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.jade.oms.entity;

import modelengine.fit.http.protocol.MimeType;
import modelengine.fitframework.inspection.Nonnull;

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
