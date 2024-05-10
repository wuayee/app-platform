/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Text与StringValue的结构体。
 *
 * @author 陈镕希 c00572808
 * @since 2023-06-26
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TextStringValue {
    private String text;

    private String value;
}
