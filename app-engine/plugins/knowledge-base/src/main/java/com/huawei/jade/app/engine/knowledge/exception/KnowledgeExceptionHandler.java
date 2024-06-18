package com.huawei.jade.app.engine.knowledge.exception;

import com.huawei.fit.http.annotation.ExceptionHandler;
import com.huawei.fit.http.annotation.ResponseStatus;
import com.huawei.fit.http.protocol.HttpResponseStatus;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.plugin.Plugin;
import com.huawei.fitframework.util.StringUtils;

import com.huawei.jade.app.engine.knowledge.service.exception.ServiceException;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;


@Component
@RequiredArgsConstructor
public class KnowledgeExceptionHandler {
    private static final Logger log = Logger.get(KnowledgeExceptionHandler.class);

    private final Plugin plugin;

//    /**
//     * handleThrowable
//     *
//     * @param exception exception
//     * @return Rsp<String>
//     */
//    @ExceptionHandler(Throwable.class)
//    @ResponseStatus(HttpResponseStatus.OK)
//    public Rsp<String> handleThrowable(Throwable exception) {
//        log.error(exception.getClass().getName(), exception);
//        return Rsp.err(AippErrCode.UNKNOWN.getErrorCode(), AippErrCode.UNKNOWN.getMessage());
//    }
//
//    /**
//     * ServiceException 处理器。
//     *
//     * @param exception 异常的 {@link ServiceException}。
//     * @return 包含异常信息的通用返回体的 {@link Rsp}{@code <}{@link String}{@code >}
//     */
//    @ExceptionHandler(ServiceException.class)
//    @ResponseStatus(HttpResponseStatus.OK)
//    public Rsp<String> handleAippException(ServiceException exception) {
//        log.error(exception.getClass().getName(), exception);
//        return Rsp.err(400,
//                getLocaleMessage(String.valueOf(exception.getCode()),
//                        exception.getMessage(),
//                        exception.getArgs(),
//                        exception.getContext()));
//    }

}
