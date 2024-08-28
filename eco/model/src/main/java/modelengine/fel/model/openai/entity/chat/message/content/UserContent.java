/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fel.model.openai.entity.chat.message.content;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * 用户请求内容，分为两种类型：1.文本，2.图片。
 *
 * @author 易文渊
 * @author 张庭怿
 * @since 2024-4-30
 */
@Data
public class UserContent {
    private String type;

    private String text;

    @JsonProperty("image_url")
    private ImageUrl imageUrl;

    public UserContent(String type, String text, ImageUrl imageUrl) {
        this.type = type;
        this.text = text;
        this.imageUrl = imageUrl;
    }

    /**
     * 使用文本构造一个新的 {@link UserContent}。
     *
     * @param data 文本内容。
     * @return 类型为文本的 {@link UserContent} 。
     */
    public static UserContent text(String data) {
        return new UserContent("text", data, null);
    }

    /**
     * 使用图片地址构造一个新的 {@link UserContent} 。
     *
     * @param data 图片地址。
     * @return 类型为图片的 {@link UserContent} 。
     */
    public static UserContent image(String data) {
        ImageUrl url = new ImageUrl();
        url.setUrl(data);
        return new UserContent("image_url", null, url);
    }

    /**
     * 图片地址对象，此嵌套类用于序列化为 JSON 中的 image_url 字段。
     */
    @Data
    public static class ImageUrl {
        private String url;
    }
}
