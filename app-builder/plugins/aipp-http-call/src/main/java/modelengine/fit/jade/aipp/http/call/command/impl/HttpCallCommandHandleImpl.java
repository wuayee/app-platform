/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.http.call.command.impl;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fit.http.client.HttpClassicClient;
import modelengine.fit.http.client.HttpClassicClientFactory;
import modelengine.fit.http.client.HttpClassicClientRequest;
import modelengine.fit.http.client.HttpClassicClientResponse;
import modelengine.fit.http.protocol.MessageHeaderNames;
import modelengine.fit.jade.aipp.http.call.Authentication;
import modelengine.fit.jade.aipp.http.call.command.HttpCallCommand;
import modelengine.fit.jade.aipp.http.call.command.HttpCallCommandHandler;
import modelengine.fit.jade.aipp.http.call.command.HttpCallResult;
import modelengine.fit.jade.aipp.http.call.enums.AuthenticationType;
import modelengine.fit.jade.aipp.http.call.utils.CookieUtil;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.exception.ClientException;
import modelengine.fitframework.exception.TimeoutException;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.StringUtils;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * {@link HttpCallCommand} 命令执行器实现类。
 *
 * @author 张越
 * @since 2024-11-22
 */
@Component
public class HttpCallCommandHandleImpl implements HttpCallCommandHandler {
    private static final Logger log = Logger.get(HttpCallCommandHandleImpl.class);

    private final HttpClassicClientFactory factory;
    private final Map<HttpClassicClientFactory.Config, HttpClassicClient> clientMap = new ConcurrentHashMap<>();

    public HttpCallCommandHandleImpl(HttpClassicClientFactory factory) {
        notNull(factory, "The factory cannot be null.");
        this.factory = factory;
    }

    @Override
    public HttpCallResult handle(HttpCallCommand command) {
        command.validate();
        String completeUrl = command.getCompleteUrl();
        HttpClassicClient classicClient =
                this.clientMap.computeIfAbsent(command.getConfig(), this::getHttpClassicClient);
        HttpClassicClientRequest request = classicClient.createRequest(command.getMethod().getOriginMethod(),
                completeUrl);
        command.getHeaders().forEach((key, value) -> this.setHeader(request, key, value));
        command.getAuthentication().ifPresent(authentication -> this.setAuthentication(authentication, request));
        if (command.getMethod().isBodyEnable()) {
            command.getEntity(request).ifPresent(request::entity);
        }

        // 请求.
        try (HttpClassicClientResponse<Object> response = classicClient.exchange(request, Object.class)) {
            return new HttpCallResult(response);
        } catch (IOException e) {
            log.error("http call failed.", e);
            return new HttpCallResult(-1, "IOException occurred while executing http call.");
        } catch (TimeoutException e) {
            log.error("http call timeout.", e);
            return new HttpCallResult(TimeoutException.CODE, "Timeout occurred while executing http call.");
        } catch (ClientException e) {
            log.error("http call unknown error.", e);
            return new HttpCallResult(ClientException.CODE, "Client exception occurred while executing http call.");
        }
    }

    private HttpClassicClient getHttpClassicClient(HttpClassicClientFactory.Config config) {
        config.custom().put("client.http.secure.ignore-trust", true);
        config.custom().put("client.http.secure.ignore-hostname", true);
        return this.factory.create(config);
    }

    private void setHeader(HttpClassicClientRequest request, String header, String value) {
        if (StringUtils.isBlank(header)) {
            return;
        }
        if (StringUtils.equalsIgnoreCase(MessageHeaderNames.COOKIE, header)) {
            CookieUtil.parse(value).forEach(c -> request.cookies().add(c));
        } else {
            request.headers().add(header, value);
        }
    }

    private void setAuthentication(Authentication authentication, HttpClassicClientRequest request) {
        if (authentication.getType() == null || StringUtils.isBlank(authentication.getType())) {
            return;
        }
        AuthenticationType authenticationType = AuthenticationType.fromKey(authentication.getType())
                .orElseThrow(() -> new IllegalArgumentException(
                        StringUtils.format("Unsupported authentication type: {0}", authentication.getType())));
        switch (authenticationType) {
            case BASIC:
            case BEARER:
                request.headers().add("Authorization", authentication.getAuthKey());
                break;
            case CUSTOM:
                request.headers().add(authentication.getHeader(), authentication.getAuthKey());
                break;
            case NONE:
            default:
                // ignore
        }
    }
}
