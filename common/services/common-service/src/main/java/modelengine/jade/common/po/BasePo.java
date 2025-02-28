/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.common.po;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 持久层对象公共基类。
 *
 * @author 易文渊
 * @since 2024-07-20
 */
@Data
public class BasePo {
    /**
     * 表示主键。
     */
    private Long id;

    /**
     * 表示创建时间。
     */
    private LocalDateTime createdAt;

    /**
     * 表示更新时间。
     */
    private LocalDateTime updatedAt;

    /**
     * 表示创建者。
     */
    private String createdBy;

    /**
     * 表示更新者。
     */
    private String updatedBy;
}