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
 * 文件Dto
 *
 * @author s00664640
 * @since 2024-05-10
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class FileDto {
    private String fileName;
    private String fileUrl;
    private String filePic;
    private String picName;
    private Long fileSize;
    private String fileType;
    private List<String> keyWords;
    private Integer fileEnum;
    private List<TextDto> textList;
}
