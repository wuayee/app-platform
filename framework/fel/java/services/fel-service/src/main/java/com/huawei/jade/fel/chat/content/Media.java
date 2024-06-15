/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.chat.content;

import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.util.StringUtils;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * 表示媒体数据的实体。
 *
 * @author 易文渊
 * @since 2024-04-16
 */
@Data
@NoArgsConstructor
public class Media {
    private String data = StringUtils.EMPTY;
    private String mimeType;

    /**
     * 媒体数据url或者base64编码创建 {@link MediaContent} 的实例。
     *
     * @param data 表示媒体数据url或者base64编码的 {@link String}。
     * @param mimeType 描述消息内容类型，通用结构为 {@code type/subtype} 的 {@link String}。
     */
    public Media(String data, String mimeType) {
        this.data = Validation.notBlank(data, "The data cannot be null.");
        this.mimeType = mimeType;
    }

    /**
     * 判断是否是一个url。
     *
     * @return 表示是否是一个url的 {@code boolean}。
     */
    public boolean isUrl() {
        try {
            new URL(this.data);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }
}