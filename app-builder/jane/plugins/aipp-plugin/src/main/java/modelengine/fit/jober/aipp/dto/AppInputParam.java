/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 表示应用开始节点配置参数
 *
 * @author 孙怡菲
 * @since 2024-11-25
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppInputParam {
    private String name;

    private String type;

    private String description;

    private boolean isRequired;

    private boolean isVisible;
}
