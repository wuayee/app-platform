/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.waterflow.domain.utils;

import lombok.Getter;
import lombok.Setter;

/**
 * ID生成器抽象类
 *
 * @author 高诗意
 * @since 1.0
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
