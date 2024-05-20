/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.knowledge.params;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * repo查询参数
 *
 * @since 2024/5/18
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RepoQueryParam {
    /** 页数 */
    private Integer offset;

    /** 单页大小 */
    private Integer size;

    /** 知识库名称模糊查询字段 */
    private String name;
}
