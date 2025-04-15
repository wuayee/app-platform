/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fit.jober.aipp.enums;

import lombok.Getter;

/**
 * 表示重新对话的模式。
 *
 * @author 黄夏露
 * @since 2024-07-12
 */
@Getter
public enum RestartModeEnum {
    /**
     * 覆盖式。
     */
    OVERWRITE("overwrite"),

    /**
     * 增长式。
     */
    INCREMENT("increment");

    private final String mode;

    RestartModeEnum(String mode) {
        this.mode = mode;
    }
}
