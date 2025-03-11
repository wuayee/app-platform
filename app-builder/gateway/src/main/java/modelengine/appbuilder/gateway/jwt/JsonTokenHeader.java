/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.appbuilder.gateway.jwt;

import com.alibaba.fastjson.JSON;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * JsonToken的Header
 *
 * @author 李智超
 * @since 2025-01-02
 */
public class JsonTokenHeader {
    private final String type = "JWT";

    /**
     * 将jwt中的header部分字符串转回header对象
     *
     * @param base64 jwt的header部分字符串
     * @return JsonTokenHeader对象实例
     */
    public static JsonTokenHeader decoderBase64Url(String base64) {
        return JSON.parseObject(new String(Base64.getUrlDecoder().decode(base64)), JsonTokenHeader.class);
    }

    /**
     * 通过Base64URL编码处理
     *
     * @return base64字符串
     */
    public String base64url() {
        return new String(Base64.getUrlEncoder().encode(toJson().getBytes(StandardCharsets.UTF_8)),
                StandardCharsets.UTF_8);
    }

    /**
     * 获取jwt类型
     *
     * @return 类型
     */
    public String getType() {
        return type;
    }

    private String toJson() {
        return JSON.toJSONString(this);
    }
}

