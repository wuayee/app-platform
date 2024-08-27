/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.domain.stream.objects;

import lombok.Builder;
import lombok.Data;

/**
 * 流程的配置信息
 *
 * @author xiafei
 * @since 1.0
 */
@Data
@Builder
public class FlowConfig {
    private ThreadMode threadMode;
}
