/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 */

package com.huawei.fit.dynamicform.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 表单中的一条元数据信息
 *
 * @author 夏斐
 * @since 2023/12/13
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class FormMetaItem {
    /**
     * 字段的key
     */
    private String key;

    /**
     * 字段的名称
     */
    private String name;

    /**
     * 字段的类型
     * 如string, int等
     */
    private String type;

    /**
     * 字段长度，对于string类型指定
     * 其他类型通常为null
     */
    private Integer length;

    /**
     * 默认值
     */
    private Object defaultValue;
}
