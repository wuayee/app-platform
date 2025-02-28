/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.validation;

/**
 * 为实例事件提供校验器。
 *
 * @author 梁济时
 * @since 2023-09-04
 */
public interface InstanceEventValidator {
    /**
     * 校验数据来源的唯一标识。
     *
     * @param sourceId 表示数据来源的唯一标识的 {@link String}。
     * @return 表示符合条件的数据来源唯一标识的 {@link String}。
     */
    String sourceId(String sourceId);

    /**
     * 校验事件的类型。
     *
     * @param type 表示事件类型的 {@link String}。
     * @return 表示符合条件的事件类型的 {@link String}。
     */
    String type(String type);

    /**
     * 校验事件处理服务实现的唯一标识。
     *
     * @param fitableId 表示事件处理服务实现的唯一标识的 {@link String}。
     * @return 表示符合条件的事件处理服务实现的唯一标识的 {@link String}。
     */
    String fitableId(String fitableId);
}
