/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.flowsengine.domain.flows.streams;

import com.huawei.fit.waterflow.common.utils.UUIDUtil;

import lombok.Getter;
import lombok.Setter;

/**
 * ID生成器抽象类
 *
 * @author 高诗意
 * @since 2023/08/14
 */
public abstract class IdGenerator implements Identity {
    /**
     * id
     */
    @Getter
    @Setter
    protected String id;

    public IdGenerator() {
        this(UUIDUtil.uuid());
    }

    public IdGenerator(String id) {
        this.id = id;
    }
}
