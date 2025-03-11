/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.appbuilder.gateway.jwt;

import com.alibaba.fastjson.JSON;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * JsonToken的payload
 *
 * @author 李智超
 * @since 2025-01-02
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JsonTokenPayload {
    /**
     * 签发人
     */
    private String iss;

    /**
     * 主题
     */
    private String sub;

    /**
     * 受众：在OM中jwt定义中的aud应该为具体的uri资源描述，格式为method uri，例：GET /xxx/xxx
     */
    private String aud;

    /**
     * 编号：这里用来记录tokenId
     */
    private String jti;

    /**
     * 账户
     */
    private String user;

    /**
     * 账户Id
     */
    private String userId;

    /**
     * 账户对应的角色
     */
    private String role;

    /**
     * 将jwt中payload部分字符串转回JsonTokenPayload对象实例
     *
     * @param base64 jwt中payload部分字符串
     * @return JsonTokenPayload对象实例
     */
    public static JsonTokenPayload decoderBase64Url(String base64) {
        return JSON.parseObject(new String(Base64.getUrlDecoder().decode(base64), StandardCharsets.UTF_8),
                JsonTokenPayload.class);
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
     * 判断当前受众是否正确
     *
     * @param aud 需要校验的aud
     * @return 是否合法 true-合法
     */
    public boolean auth(String aud) {
        return StringUtils.equals(aud, this.aud);
    }

    private String toJson() {
        return JSON.toJSONString(this);
    }
}

