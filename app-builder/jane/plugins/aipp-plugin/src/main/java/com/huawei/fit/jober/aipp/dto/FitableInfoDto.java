/*
 *  Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * fitable信息
 *
 * @author 孙怡菲 s00664640
 * @since 2024-04-24
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FitableInfoDto {
    String name;

    String fitableId;

    String hash;
}
