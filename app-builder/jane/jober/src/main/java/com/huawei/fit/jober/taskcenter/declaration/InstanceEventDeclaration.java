/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.declaration;

import com.huawei.fit.jane.task.util.UndefinableValue;

/**
 * 为实例事件提供声明。
 *
 * @author 梁济时 l00815032
 * @since 2023-09-04
 */
public interface InstanceEventDeclaration {
    /**
     * 获取事件的类型。
     *
     * @return 表示事件类型的 {@link UndefinableValue}{@code <}{@link String}{@code >}。
     */
    UndefinableValue<String> type();

    /**
     * 获取事件处理服务实现的唯一标识。
     *
     * @return 表示服务事件唯一标识的 {@link UndefinableValue}{@code <}{@link String}{@code >}。
     */
    UndefinableValue<String> fitableId();

    /**
     * 为实例事件的声明提供构建器。
     *
     * @author 梁济时 l00815032
     * @since 2023-09-04
     */
    interface Builder {
        /**
         * 设置事件的类型。
         *
         * @param type 表示事件类型的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder type(String type);

        /**
         * 设置事件处理服务实现的唯一标识。
         *
         * @param fitableId 表示事件处理服务的唯一标识的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder fitableId(String fitableId);

        /**
         * 构建实例事件声明。
         *
         * @return 表示新构建的实例事件声明的 {@link InstanceEventDeclaration}。
         */
        InstanceEventDeclaration build();
    }

    /**
     * 返回一个构建器，用以构建实例事件声明的新实例。
     *
     * @return 表示用以构建实例事件的构建器的 {@link Builder}。
     */
    static Builder custom() {
        return new DefaultInstanceEventDeclarationBuilder();
    }
}