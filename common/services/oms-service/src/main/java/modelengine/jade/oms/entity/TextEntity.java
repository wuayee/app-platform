/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.oms.entity;

import static modelengine.fitframework.util.ObjectUtils.nullIf;

import modelengine.fit.http.protocol.MimeType;
import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.util.StringUtils;

import java.io.IOException;

/**
 * 表示文本格式的消息体数据。
 *
 * @author 季聿阶
 * @since 2022-07-14
 */
public class TextEntity implements Entity {
    private final String content;

    /**
     * 创建文本类型的消息体数据对象。
     *
     * @param content 表示文本数据的 {@link String}。
     */
    public TextEntity(String content) {
        this.content = nullIf(content, StringUtils.EMPTY);
    }

    /**
     * 获取实体内容。
     *
     * @return 返回实体内容的 {@link String}。
     */
    public String content() {
        return this.content;
    }

    @Override
    @Nonnull
    public MimeType resolvedMimeType() {
        return MimeType.TEXT_PLAIN;
    }

    @Override
    public void close() throws IOException {
    }
}
