/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fit.jober.aipp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 接入的模型服务的信息
 *
 * @author 方誉州
 * @since 2024-09-13
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModelAccessInfo {
    private String serviceName;
    private String tag;
}
