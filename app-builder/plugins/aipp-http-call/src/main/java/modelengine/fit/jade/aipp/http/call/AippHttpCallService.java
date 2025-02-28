/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.http.call;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fit.http.client.HttpClientException;
import modelengine.fit.jade.aipp.http.call.command.HttpCallCommand;
import modelengine.fit.jade.aipp.http.call.command.HttpCallCommandHandler;
import modelengine.fit.jade.aipp.http.call.command.HttpCallResult;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fitable;

/**
 * 表示 {@link HttpCallService} 的 aipp 实现。
 *
 * @author 张越
 * @since 2024-11-21
 */
@Component
public class AippHttpCallService implements HttpCallService {
    private final HttpCallCommandHandler handler;

    public AippHttpCallService(HttpCallCommandHandler handler) {
        this.handler = handler;
    }

    @Override
    @Fitable("aipp")
    public HttpResult httpCall(HttpRequest request) throws HttpClientException {
        notNull(request, "Http request cannot be null.");
        HttpCallCommand command = new HttpCallCommand();
        command.setMethod(request.getHttpMethod());
        command.setUrl(request.getUrl());
        command.setParams(request.getParams());
        command.setHeaders(request.getHeaders());
        command.setAuthentication(request.getAuthentication());
        command.setTimeout(request.getTimeout());
        command.setHttpBody(request.getRequestBody());
        command.setArgs(request.getArgs());
        HttpCallResult httpCallResult = this.handler.handle(command);
        HttpResult result = new HttpResult();
        result.setStatus(httpCallResult.getStatus());
        result.setErrorMsg(httpCallResult.getErrorMsg());
        result.setData(httpCallResult.getData());
        return result;
    }
}
