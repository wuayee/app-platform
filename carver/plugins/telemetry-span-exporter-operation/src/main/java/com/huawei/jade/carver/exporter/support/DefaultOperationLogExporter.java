/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.carver.exporter.support;

import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.util.StringUtils;
import com.huawei.jade.carver.exporter.OperationLogExporter;

import java.util.Map;

/**
 * {@link OperationLogExporter} 的默认实现。
 *
 * @author 刘信宏
 * @since 2024-07-25
 */
@Component
public class DefaultOperationLogExporter implements OperationLogExporter {
    private static final Logger log = Logger.get(DefaultOperationLogExporter.class);

    @Override
    public void succeed(String operation, Map<String, String> params) {
        // 导出国际化错误信息到三方系统
        log.debug("Span {} attribute size: {}", operation, params.size());
    }

    @Override
    public void failed(String operation, Map<String, String> params) {
        // 导出国际化错误信息到三方系统
        String errorMessage = params.getOrDefault(OperationLogExporter.EXCEPTION_DETAIL_KEY, StringUtils.EMPTY);
        log.debug("Span {} error message: {}", operation, errorMessage);
    }
}
