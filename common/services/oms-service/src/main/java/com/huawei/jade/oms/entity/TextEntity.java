/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2024. All rights reserved.
 */

package com.huawei.jade.oms.entity;

import static com.huawei.fitframework.util.ObjectUtils.nullIf;

import com.huawei.fit.http.protocol.MimeType;
import com.huawei.fitframework.inspection.Nonnull;
import com.huawei.fitframework.util.StringUtils;

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
