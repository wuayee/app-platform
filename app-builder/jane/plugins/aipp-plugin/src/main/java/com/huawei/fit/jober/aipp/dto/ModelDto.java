/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.dto;

import com.huawei.jade.store.entity.transfer.ModelData;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 模型的传输类。
 *
 * @author 李金绪
 * @since 2024/6/13
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModelDto {
    private List<ModelData> modelDatas;
    private int total;
}
