/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.aipp.dto.xiaohai;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 文本Dto
 *
 * @author 00664640
 * @since 2024-05-10
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TextDto {
    private String text;
    private Long ckNum;
}
