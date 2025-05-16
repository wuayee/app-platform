/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.domain;

import modelengine.fit.jober.taskcenter.domain.support.DefaultInstanceEvent;

/**
 * 表示任务实例的事件。
 *
 * @author 梁济时
 * @since 2023-09-04
 */
public interface InstanceEvent {
    /**
     * 获取事件的类型。
     *
     * @return 表示事件类型的 {@link InstanceEvent}。
     */
    InstanceEventType type();

    /**
     * 获取事件处理服务实现的唯一标识。
     *
     * @return 表示用以处理该事件的服务实现的唯一标识的 {@link String}。
     */
    String fitableId();

    /**
     * 为实例事件提供构建器。
     *
     * @author 梁济时
     * @since 2023-09-04
     */
    interface Builder {
        /**
         * 设置事件的类型。
         *
         * @param type 表示事件类型的 {@link InstanceEventType}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder type(InstanceEventType type);

        /**
         * 设置事件处理服务实现的唯一标识。
         *
         * @param fitableId 表示事件处理服务实现唯一标识的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder fitableId(String fitableId);

        /**
         * 构建实例事件。
         *
         * @return 表示新构建的实例事件的 {@link InstanceEvent}。
         */
        InstanceEvent build();
    }

    /**
     * 返回一个构建器，用以构建实例事件的新实例。
     *
     * @return 表示用以构建实例事件的构建器的 {@link Builder}。
     */
    static Builder custom() {
        return new DefaultInstanceEvent.Builder();
    }
}
