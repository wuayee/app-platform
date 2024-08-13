/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 操作记录实体类
 *
 * @author 姚江
 * @since 2023-11-17 10:11
 */
@Data
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class OperationRecordEntity {
    private String id;

    private String objectType;

    private String objectId;

    private String operator;

    private String message;

    private LocalDateTime operatedTime;

    private String operate;

    private String instanceTask; // instance类型需要把task标注出来

    private String title;

    private Map<String, Object> content;
}
