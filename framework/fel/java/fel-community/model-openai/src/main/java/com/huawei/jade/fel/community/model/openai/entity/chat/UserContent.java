/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.community.model.openai.entity.chat;

import com.huawei.fitframework.annotation.Property;
import com.huawei.fitframework.resource.UrlUtils;
import com.huawei.fitframework.resource.web.Media;
import com.huawei.fitframework.serialization.annotation.SerializeStrategy;

/**
 * 用户请求内容，分为两种类型：
 * <ol>
 *     <li>文本类型；</li>
 *     <li>图片类型；</li>
 * </ol>
 *
 * @author 易文渊
 * @since 2024-08-17
 */
@SerializeStrategy(include = SerializeStrategy.Include.NON_NULL)
public class UserContent {
    private final String type;
    private final String text;
    @Property(name = "image_url")
    private final ImageUrl imageUrl;

    private UserContent(String type, String text, ImageUrl imageUrl) {
        this.type = type;
        this.text = text;
        this.imageUrl = imageUrl;
    }

    /**
     * 使用文本构造一个新的 {@link UserContent}。
     *
     * @param data 表示文本内容的 {@link String}。
     * @return 表示类型为文本的 {@link UserContent} 。
     */
    public static UserContent from(String data) {
        return new UserContent("text", data, null);
    }

    /**
     * 使用图片地址构造一个新的 {@link UserContent} 。
     *
     * @param media 表示图片的 {@link Media}。
     * @return 表示类型为图片的 {@link UserContent} 。
     */
    public static UserContent from(Media media) {
        String image = UrlUtils.isUrl(media.getData())
                ? media.getData()
                : String.format("data:%s;base64,%s", media.getMime(), media.getData());
        return new UserContent("image_url", null, new ImageUrl(image));
    }

    private static class ImageUrl {
        private final String url;

        private ImageUrl(String url) {
            this.url = url;
        }
    }
}