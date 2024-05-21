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
 * 存储服务。
 *
 * @since 2024-05-18
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KStorageDto {
    private Long id;
    private String name;
    private KStorageType type;
    private String url;
    private Date createdAt;
    private Date updatedAt;
}
