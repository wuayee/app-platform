/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.validation;

/**
 * 为索引提供校验器。
 *
 * @author 梁济时 l00815032
 * @since 2024-01-05
 */
public interface IndexValidator {
    /**
     * 校验索引的名称。
     *
     * @param name 表示索引的名称的 {@link String}。
     * @return 表示符合校验逻辑的索引名称的 {@link String}。
     */
    String name(String name);
}
