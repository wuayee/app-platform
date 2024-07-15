/*
 *  Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.bff.controller.a3000.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 分页查询流程列表入参
 *
 * @author 杨祥宇
 * @since 2024/7/15
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QueryCriteria {
    /**
     * 查询tag
     */
    private String tag;

    /**
     * 创建者
     */
    private String createUser;

    /**
     * 偏移量
     */
    private String offset;

    /**
     * 查询数量
     */
    private String limit;
}
