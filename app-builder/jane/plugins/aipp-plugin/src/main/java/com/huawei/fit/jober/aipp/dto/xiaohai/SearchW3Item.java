/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.aipp.dto.xiaohai;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 搜索W3数据项
 *
 * @author 00664640
 * @since 2024-05-10
 */
@Data
@NoArgsConstructor
@ToString
@AllArgsConstructor
public class SearchW3Item {
    private String docTitle;

    private String docUrl;

    private String docText;
}
