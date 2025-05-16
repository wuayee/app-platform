/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2025. All rights reserved.
 */

package modelengine.jade.authentication.impl;

import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.serialization.ObjectSerializer;
import modelengine.fitframework.util.StringUtils;
import modelengine.framework.crypt.grpc.client.CryptClient;
import modelengine.framework.crypt.grpc.client.exception.CryptoInvokeException;
import modelengine.framework.crypt.grpc.client.model.CipherTextWithoutDomain;
import modelengine.jade.authentication.AuthenticationService;
import modelengine.jade.authentication.dto.JsonTokenPayload;
import modelengine.jade.crypt.client.CryptClientServer;

import java.util.Optional;

/**
 * 表示用户认证服务接口实现。
 *
 * @author 杭潇
 * @since 2024-12-30
 */
@Component
public class DefaultAuthenticationService implements AuthenticationService {
    private static final String DEFAULT_USERNAME = "admin";
    private static final String BEARER = "Bearer ";
    private static final String AUTHORIZATION = "Authorization";

    private final ObjectSerializer objectSerializer;

    /**
     * 创建默认用户认证服务实现。
     *
     * @param objectSerializer 表示对象序列化器的 {@link ObjectSerializer}。
     */
    public DefaultAuthenticationService(@Fit(alias = "json") ObjectSerializer objectSerializer) {
        this.objectSerializer = objectSerializer;
    }

    @Override
    public String getUserName(HttpClassicServerRequest request) {
        Optional<String> optionalAuthorization = request.headers().first(AUTHORIZATION);
        String authorization = optionalAuthorization.orElseThrow(
                () -> new IllegalArgumentException("Can not get header Authorization!"));
        String userName = this.jsonToken(authorization.substring(BEARER.length()));
        return userName == null ? DEFAULT_USERNAME : userName;
    }

    private String jsonToken(String jwtString) {
        if (StringUtils.isBlank(jwtString)) {
            throw new IllegalArgumentException("The jwt info doesn't exist.");
        }
        String[] split = jwtString.split("\\.");
        if (split.length != 3) {
            throw new IllegalArgumentException(StringUtils.format("The jwt info is invalid. [jwt={0}]", jwtString));
        }
        String encryJwtStr = split[2];
        try {
            CryptClient cryptClient = CryptClientServer.getCryptClient();
            String headerAndPayload =
                    cryptClient.getCryptoService().decrypt(new CipherTextWithoutDomain(encryJwtStr)).getData();
            String[] headerAndPayloadSplit = headerAndPayload.split("\\.");
            if (headerAndPayloadSplit.length < 2) {
                throw new IllegalArgumentException(
                        StringUtils.format("Header and payload info invalid. [info={0}]", headerAndPayload));
            }
            JsonTokenPayload jsonTokenPayload =
                    JsonTokenPayload.decoderBase64Url(headerAndPayloadSplit[1], this.objectSerializer);
            return jsonTokenPayload.getUser();
        } catch (CryptoInvokeException e) {
            throw new IllegalArgumentException("The jwt is invalid.", e);
        }
    }
}
