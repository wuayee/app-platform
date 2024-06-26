/*
 *  Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.flowsengine.persist.po;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * fitable usage的PO对象
 *
 * @author 孙怡菲 s00664640
 * @since 2023-11-16
 */
@Builder
@Getter
@Setter
public class FitableUsagePO {
    private String fitableId;

    private String definitionId;
}
