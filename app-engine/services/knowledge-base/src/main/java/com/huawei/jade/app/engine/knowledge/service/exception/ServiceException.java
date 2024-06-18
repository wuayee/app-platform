/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.jade.app.engine.knowledge.service.exception;

/**
 * ServiceException 服务自定义异常
 *
 * @author YangPeng
 * @since 2024-05-20
 */
public class ServiceException extends RuntimeException {
    /**
     * 包含错误原因的构造方法
     *
     * @param cause 错误原因
     */
    public ServiceException(Throwable cause) {
        super(cause);
    }

    /**
     * 包含错误原因的构造方法
     *
     * @param cause 错误原因
     */
    public ServiceException(String cause) {
        super(cause);
    }
}
