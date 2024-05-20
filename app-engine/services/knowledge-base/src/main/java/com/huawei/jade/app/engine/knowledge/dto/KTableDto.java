/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.knowledge.dto;

import com.huawei.jade.app.engine.knowledge.dto.enums.KStorageType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 知识表。
 *
 * @since 2024-05-18
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KTableDto {
    private Long id;
    private String name;
    private Long repositoryId;
    private KStorageType serviceType;
    private Long serviceId;
    private Date createAt;
    private Date updateAt;
}
