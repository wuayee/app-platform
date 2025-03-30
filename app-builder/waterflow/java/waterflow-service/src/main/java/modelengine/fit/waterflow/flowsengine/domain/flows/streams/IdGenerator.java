/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.domain.flows.streams;

import lombok.Getter;
import lombok.Setter;
import modelengine.fit.ohscript.util.UUIDUtil;

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
