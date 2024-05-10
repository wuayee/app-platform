/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.flowsengine.domain.flows.streams;

import com.huawei.fit.jober.common.utils.UUIDUtil;

import lombok.Getter;
import lombok.Setter;

/**
 * ID生成器抽象类
 *
 * @author g00564732
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
