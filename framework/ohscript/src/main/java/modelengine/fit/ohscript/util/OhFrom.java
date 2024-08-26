/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.ohscript.util;

/**
 * oh对象的来源枚举
 *
 * @since 1.0
 */
public enum OhFrom {
    EXT("ext::"),
    HTTP("http::"),
    FIT("fit::"),
    OH("");

    private final String name;

    /**
     * 构造函数
     *
     * @param name 对象来源的名称
     */
    OhFrom(String name) {
        this.name = name;
    }

    /**
     * 根据名称获取对象来源的枚举值
     *
     * @param name 对象来源的名称
     * @return 对象来源的枚举值
     */
    public static OhFrom valueFrom(String name) {
        for (OhFrom value : OhFrom.values()) {
            if (value.name.equals(name)) {
                return value;
            }
        }
        return null;
    }

    /**
     * 获取对象来源的名称
     *
     * @return 对象来源的名称
     */
    public String ohName() {
        return this.name;
    }
}
