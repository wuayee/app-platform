/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.core.template.support;

import static com.huawei.fitframework.inspection.Validation.notBlank;

import com.huawei.fitframework.resource.web.Media;
import com.huawei.jade.fel.core.chat.ChatMessage;
import com.huawei.jade.fel.core.chat.support.ToolMessage;
import com.huawei.jade.fel.core.template.StringTemplate;

import java.util.List;

/**
 * 工具消息模板实现。
 *
 * @author 易文渊
 * @since 2024-04-25
 */
public class ToolMessageTemplate extends AbstractMessageTemplate {
    private final String id;

    /**
     * 使用 mustache 模板语法创建 {@link ToolMessageTemplate} 的实例。
     *
     * @param template 表示使用 mustache 模板语法的 {@link String}。
     * @see <a href="https://mustache.github.io/">mustache</a>。
     */
    public ToolMessageTemplate(String template, String id) {
        this(StringTemplate.create(template), id);
    }

    /**
     * 使用字符串模板创建 {@link ToolMessageTemplate} 的实例。
     *
     * @param template 表示字符串模板的 {@link StringTemplate}。
     */
    public ToolMessageTemplate(StringTemplate template, String id) {
        super(template);
        this.id = notBlank(id, "The id cannot be blank.");
    }

    @Override
    protected ChatMessage collect(String text, List<Media> ignore) {
        return new ToolMessage(this.id, text);
    }
}