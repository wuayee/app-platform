/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.core.document.support;

import static com.huawei.fitframework.util.ObjectUtils.getIfNull;
import static com.huawei.fitframework.util.StringUtils.blankIf;

import com.huawei.fitframework.inspection.Nonnull;
import com.huawei.fitframework.resource.web.Media;
import com.huawei.fitframework.util.StringUtils;
import com.huawei.jade.fel.core.document.Content;

import java.util.Collections;
import java.util.List;

/**
 * 表示消息内容的实体。
 *
 * @author 易文渊
 * @since 2024-06-17
 */
public final class DefaultContent implements Content {
    private final String text;
    private final List<Media> medias;

    /**
     * 使用文本消息和媒体资源创建 {@link Content} 的实例。
     *
     * @param text 表示文本消息的 {@link String}。
     * @param medias 表示媒体资源列表的 {@link List}{@code <}{@link Media}{@code >}。
     */
    public DefaultContent(String text, List<Media> medias) {
        this.text = blankIf(text, StringUtils.EMPTY);
        this.medias = getIfNull(medias, Collections::emptyList);
    }

    @Nonnull
    @Override
    public String text() {
        return this.text;
    }

    @Override
    public List<Media> medias() {
        return this.medias;
    }
}