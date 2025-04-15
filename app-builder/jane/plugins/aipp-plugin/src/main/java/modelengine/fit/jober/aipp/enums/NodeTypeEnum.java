/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fit.jober.aipp.enums;

/**
 * 节点类型的枚举类。
 *
 * @author 邱晓霞
 * @since 2024-08-26
 */
public enum NodeTypeEnum {
    /**
     * 基础节点。
     */
    BASIC("basic"),

    /**
     * 评估节点。
     */
    EVALUATION("evaluation");

    private final String type;

    NodeTypeEnum(String type) {
        this.type = type;
    }

    public String type() {
        return this.type;
    }
}
