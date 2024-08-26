/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.waterflow.flowsengine.domain.flows.streams;

import lombok.Getter;
import lombok.Setter;
import modelengine.fit.waterflow.common.utils.UUIDUtil;

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
