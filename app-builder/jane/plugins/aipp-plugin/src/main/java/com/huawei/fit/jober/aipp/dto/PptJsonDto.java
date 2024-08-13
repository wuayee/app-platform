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

/**
 * PPT文件的数据传输对象
 *
 * @author 孙怡菲
 * @since 2023-05-10
 */
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
