/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

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
