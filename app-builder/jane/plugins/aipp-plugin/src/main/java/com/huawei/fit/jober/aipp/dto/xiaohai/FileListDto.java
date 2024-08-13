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
 * 文件列表Dto
 *
 * @author 孙怡菲
 * @since 2024-05-10
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class FileListDto {
    private List<FileDto> res;
}
