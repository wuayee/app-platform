/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.waterflow.edatamate.enums;

import lombok.Getter;

/**
 * 扫盘状态
 *
 * @author 宋永坦
 * @since 2024/02/18
 */
@Getter
public enum ScanStatus {
    RUNNING("running"),
    END("end");

    private final String code;

    ScanStatus(String code) {
        this.code = code;
    }
}
