/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.knowledge.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 查询
 *
 * @since 2024/5/20
 */
@Getter
@Setter
@AllArgsConstructor
public class PageResultVo<T> {
    /** 总数 */
    private int count;

    /** 查询结果列表 */
    private List<T> result;
}
