/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.knowledge.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 知识库。
 *
 * @since 2024-05-18
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KRepoDto {
    private Long id;
    private String name;
    private Long ownerId;
    private String ownerName;
    private String description;
    private Date createdAt;
    private Date updatedAt;
}