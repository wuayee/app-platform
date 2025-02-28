/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.persist.po;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * fitable usage的PO对象
 *
 * @author 孙怡菲
 * @since 2023-11-16
 */
@Builder
@Getter
@Setter
public class FitableUsagePO {
    private String fitableId;

    private String definitionId;
}
