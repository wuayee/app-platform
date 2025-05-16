/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.validation;

/**
 * 为索引提供校验器。
 *
 * @author 梁济时
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
