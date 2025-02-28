/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.service;

import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.LockUtils;

import io.opentelemetry.api.OpenTelemetry;

/**
 * {@link OpenTelemetry} 全局对象。
 *
 * @author 刘信宏
 * @since 2024-07-22
 */
public final class CarverGlobalOpenTelemetry {
    private static final Logger log = Logger.get(CarverGlobalOpenTelemetry.class);
    private static final Object LOCK = LockUtils.newSynchronizedLock();

    private static volatile OpenTelemetry instance;

    private CarverGlobalOpenTelemetry() {}

    /**
     * 获取遥测全局管理对象。
     *
     * @return 表示遥测全局管理对象的 {@link OpenTelemetry}。
     */
    public static OpenTelemetry get() {
        OpenTelemetry openTelemetry = instance;
        if (openTelemetry != null) {
            return openTelemetry;
        }

        synchronized (LOCK) {
            openTelemetry = instance;
            if (openTelemetry == null) {
                set(OpenTelemetry.noop());
            }
        }
        return instance;
    }

    /**
     * 设置遥测全局管理对象。
     *
     * @param openTelemetry 表示遥测全局管理对象的 {@link OpenTelemetry}。
     */
    public static void set(OpenTelemetry openTelemetry) {
        Validation.notNull(openTelemetry, "The telemetry cannot be null.");
        synchronized (LOCK) {
            if (instance != null) {
                log.error("Global telemetry instance exist.");
                throw new IllegalStateException("Not support to reset global telemetry instance.");
            }
            instance = openTelemetry;
        }
    }
}
