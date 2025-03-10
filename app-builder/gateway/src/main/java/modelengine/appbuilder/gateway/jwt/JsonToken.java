/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.appbuilder.gateway.jwt;

import com.huawei.framework.crypt.grpc.client.CryptClient;
import com.huawei.framework.crypt.grpc.client.exception.CryptoInvokeException;
import com.huawei.framework.crypt.grpc.client.model.CipherTextWithoutDomain;
import com.huawei.framework.crypt.grpc.client.model.PlainTextWithoutDomain;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import modelengine.appbuilder.gateway.service.CryptClientServer;

import org.apache.commons.lang3.StringUtils;

/**
 * JWT要用到的JsonToken
 *
 * @author 李智超
 * @since 2025-01-02
 */
@Slf4j
public class JsonToken {
    private final JsonTokenHeader jsonTokenHeader;

    /**
     * 获取Json token的数据
     *
     * @return token数据
     */
    @Getter
    private final JsonTokenPayload jsonTokenPayload;

    /**
     * 构造器
     *
     * @param jsonTokenPayload jwt的payload内容
     */
    public JsonToken(JsonTokenPayload jsonTokenPayload) {
        this.jsonTokenHeader = new JsonTokenHeader();
        this.jsonTokenPayload = jsonTokenPayload;
    }

    /**
     * 通过jwt字符串构造JsonToken对象
     *
     * @param jwtString jwt字符串
     * @throws CryptoInvokeException jwt解析异常。
     */
    public JsonToken(String jwtString) throws CryptoInvokeException {
        if (StringUtils.isBlank(jwtString)) {
            log.error("jwt info don't exist.");
            throw new IllegalArgumentException("jwt info don't exist.");
        }
        String[] split = jwtString.split("\\.");
        if (split.length != 3) {
            log.error("jwt info invalid. jwt = {}", jwtString);
            throw new IllegalArgumentException("jwt info invalid.");
        }
        String encryJwtStr = split[2];
        try {
            CryptClient cryptClient = CryptClientServer.getCryptClient();
            String headerAndPayload =
                    cryptClient.getCryptoService().decrypt(new CipherTextWithoutDomain(encryJwtStr)).getData();
            String[] headerAndPayloadSplit = headerAndPayload.split("\\.");
            this.jsonTokenHeader = JsonTokenHeader.decoderBase64Url(headerAndPayloadSplit[0]);
            this.jsonTokenPayload = JsonTokenPayload.decoderBase64Url(headerAndPayloadSplit[1]);
        } catch (CryptoInvokeException e) {
            log.error("jwt is invalid.");
            throw e;
        }
    }

    /**
     * 将对象转为jwt字符串
     *
     * @return jwt
     * @throws CryptoInvokeException 创建jwt发生异常。
     */
    public String toJwt() throws CryptoInvokeException {
        try {
            CryptClient cryptClient = CryptClientServer.getCryptClient();
            String signature =
                    cryptClient.getCryptoService().encrypt(new PlainTextWithoutDomain(base64Splice())).getData();
            return base64Splice() + "." + signature;
        } catch (CryptoInvokeException e) {
            log.error("cryptClient exec error, CryptoService may be exception.", e);
            throw e;
        }
    }

    /**
     * 判断当前受众是否正确
     *
     * @param aud 需要校验的aud
     * @return 是否合法 true-合法
     */
    public boolean isValid(String aud) {
        return jsonTokenPayload.auth(aud);
    }

    /**
     * 获取角色
     *
     * @return 角色
     */
    public String getRole() {
        return jsonTokenPayload.getRole();
    }

    private String base64Splice() {
        return jsonTokenHeader.base64url() + "." + jsonTokenPayload.base64url();
    }
}
