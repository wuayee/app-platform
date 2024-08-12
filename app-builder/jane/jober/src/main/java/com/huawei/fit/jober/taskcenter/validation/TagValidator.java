/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.validation;

/**
 * 为标签提供校验器。
 *
 * @author 梁济时
 * @since 2023-08-16
 */
public interface TagValidator {
    /**
     * 校验标签值。
     *
     * @param tag 表示标签值的 {@link String}。
     * @return 表示符合校验逻辑的标签值的 {@link String}。
     */
    String tag(String tag);

    /**
     * 校验标签的描述信息。
     *
     * @param description 表示标签的描述信息的 {@link String}。
     * @return 表示符合校验逻辑的标签描述信息的 {@link String}。
     */
    String description(String description);

    /**
     * 校验标签使用对象的类型。
     *
     * @param objectType 表示标签使用对象类型的 {@link String}。
     * @return 表示符合校验逻辑的标签使用对象的类型的 {@link String}。
     */
    String objectType(String objectType);

    /**
     * 校验标签使用对象的唯一标识。
     *
     * @param objectId 表示标签使用对象的类型的 {@link String}。
     * @return 表示符合校验逻辑的标签使用对象的唯一标识的 {@link String}。
     */
    String objectId(String objectId);
}
