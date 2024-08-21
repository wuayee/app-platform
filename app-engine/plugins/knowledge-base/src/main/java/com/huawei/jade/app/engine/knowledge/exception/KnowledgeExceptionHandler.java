/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.knowledge.exception;

import modelengine.fit.http.annotation.ExceptionHandler;
import modelengine.fit.http.annotation.ResponseStatus;
import modelengine.fit.http.protocol.HttpResponseStatus;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.log.Logger;
import com.huawei.jade.app.engine.knowledge.common.KnowledgeRsp;
import com.huawei.jade.app.engine.knowledge.service.exception.ServiceException;

import lombok.RequiredArgsConstructor;

/**
 * 处理知识库Controller相关异常。
 *
 * @since 2024-06-18
 */
@Component
@RequiredArgsConstructor
public class KnowledgeExceptionHandler {
    private static final Logger log = Logger.get(KnowledgeExceptionHandler.class);

    /**
     * ServiceException 处理器。
     *
     * @param exception 异常的 {@link ServiceException}。
     * @return 包含异常信息的通用返回体的 {@link KnowledgeRsp}{@code <}{@link String}{@code >}
     */
    @ExceptionHandler(ServiceException.class)
    @ResponseStatus(HttpResponseStatus.BAD_REQUEST)
    public KnowledgeRsp handleKnowledgeException(ServiceException exception) {
        log.error(exception.getClass().getName(), exception);
        return KnowledgeRsp.err(400, exception.getMessage());
    }
}
