/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.aipp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@ToString
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PptJsonDto {
    private String title;

    private String author;

    private String date;

    private List<PptPageDto> pages = new ArrayList<>();
}
