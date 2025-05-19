/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.dao.po;

import lombok.Data;

/**
 * (TaskProperty)实体类
 *
 * @author 董建华
 * @since 2023-08-09
 */
@Data
public class TaskPropertyObject {
    private String id;

    private String taskId;

    private String name;

    private Boolean isRequired;

    private String templateId;

    private Boolean isIdentifiable;

    private String description;

    private String scope;

    private String dataType;

    private Integer sequence;

    private String appearance;
}
