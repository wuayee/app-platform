/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.aipp.dto.xiaohai;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * 文件列表DTO
 *
 * @author 孙怡菲
 * @since 2024-05-10
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SearchW3RespDto {
    private int code;
    private String msg;
    private List<SearchW3Item> data;
}
