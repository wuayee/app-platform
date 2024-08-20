/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.service.support;

import com.huawei.fitframework.util.EnumUtils;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.fitframework.util.StringUtils;

import java.util.function.Predicate;

/**
 * 部署状态枚举类。
 *
 * @author 鲁为
 * @since 2024-08-13
 */
public enum DeployStatus {
    /** 表示插件已部署。 */
    DEPLOYED,

    /** 表示插件部署中。 */
    DEPLOYING,

    /** 表示插件未部署。 */
    UNDEPLOYED,

    /** 表示部署失败。 */
    DEPLOYMENT_FAILED;

    /**
     * 将字符串转换成枚举类。
     *
     * @param value 表示部署状态的 {@link String}。
     * @return 表示部署状态的枚举类的 {@link DeployStatus}。
     */
    public static DeployStatus from(String value) {
        Predicate<Enum> predicate = enumConstant -> StringUtils.equalsIgnoreCase(enumConstant.toString(), value);
        return ObjectUtils.<DeployStatus>cast(
            EnumUtils.firstOrDefault(ObjectUtils.cast(DeployStatus.class), predicate));
    }
}
