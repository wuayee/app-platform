/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.oms.license.handler;

import static com.huawei.fit.jober.aipp.common.exception.AippErrCode.INVALID_LICENSE;
import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fit.jober.aipp.common.exception.AippException;
import com.huawei.fit.jober.aipp.event.AppCreatingEvent;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.event.EventHandler;
import com.huawei.jade.oms.license.manager.LicenseClient;

/**
 * 许可证事件的处理器。
 *
 * @author 李金绪
 * @since 2024-12-05
 */
@Component
public class AppCreateEventHandler implements EventHandler<AppCreatingEvent> {
    private final LicenseClient licenseClient;

    /**
     * 构造函数。
     *
     * @param licenseClient 表示许可证客户端的 {@link LicenseClient}。
     */
    public AppCreateEventHandler(LicenseClient licenseClient) {
        this.licenseClient = notNull(licenseClient, "The license client cannot be null.");
    }

    @Override
    public void handleEvent(AppCreatingEvent event) {
        if (!this.licenseClient.isLicenseValid()) {
            throw new AippException(INVALID_LICENSE);
        }
    }
}
