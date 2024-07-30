/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.dao.po;

import lombok.Data;

/**
 * (TaskProperty)实体类
 *
 * @author d30022216
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
