/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.common.config;

import com.huawei.fitframework.util.StringUtils;

import com.dtflys.forest.logging.DefaultLogHandler;
import com.dtflys.forest.logging.RequestLogMessage;

/**
 * 默认Forest日志处理器。
 *
 * @author 陈镕希 c00572808
 * @since 2023-09-01
 */
public class DefaultForestLogHandler extends DefaultLogHandler {
    @Override
    protected String requestLoggingHeaders(RequestLogMessage requestLogMessage) {
        return StringUtils.EMPTY;
    }

    @Override
    protected String requestLoggingBody(RequestLogMessage requestLogMessage) {
        return StringUtils.EMPTY;
    }
}
