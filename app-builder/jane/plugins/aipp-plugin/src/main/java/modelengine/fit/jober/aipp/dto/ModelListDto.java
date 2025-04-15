/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fit.jober.aipp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 服务上模型列表的传输类。
 *
 * @author 方誉州
 * @since 2024-09-13
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModelListDto {
    private List<ModelAccessInfo> models;
    private int total;
}
