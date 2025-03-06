/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.jade.authentication.dto;

import modelengine.fitframework.serialization.ObjectSerializer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * JsonToken 的 payload。
 *
 * @author 杭潇
 * @since 2024-12-30
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JsonTokenPayload {
    /**
     * 签发人。
     */
    private String iss;

    /**
     * 主题。
     */
    private String sub;

    /**
     * 受众：在 OM 中 jwt 定义中的 aud 应该为具体的 uri 资源描述，格式为 method uri。
     * 例：GET /xxx/xxx
     */
    private String aud;

    /**
     * 编号：这里用来记录 tokenId。
     */
    private String jti;

    /**
     * 账户。
     */
    private String user;

    /**
     * 账户 Id。
     */
    private String userId;

    /**
     * 账户对应的角色。
     */
    private String role;

    /**
     * 将 jwt 中 payload 部分字符串转回 JsonTokenPayload 对象实例。
     *
     * @param base64 表示 jwt 中 payload 部分字符串的 {@link String}。
     * @param objectSerializer 表示对象序列化器的 {@link ObjectSerializer}。
     * @return 表示 JsonTokenPayload 对象实例的 {@link JsonTokenPayload}。
     */
    public static JsonTokenPayload decoderBase64Url(String base64, ObjectSerializer objectSerializer) {
        return objectSerializer.deserialize(new String(Base64.getUrlDecoder().decode(base64), StandardCharsets.UTF_8),
                JsonTokenPayload.class);
    }
}
