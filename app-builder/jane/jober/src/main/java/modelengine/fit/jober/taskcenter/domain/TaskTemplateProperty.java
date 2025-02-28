/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.domain;

import modelengine.fit.jane.task.domain.DomainObject;
import modelengine.fit.jane.task.domain.PropertyDataType;
import modelengine.fit.jane.task.util.OperationContext;
import modelengine.fit.jane.task.util.UndefinableValue;
import modelengine.fit.jober.taskcenter.domain.support.DefaultTaskTemplateProperty;

import java.util.List;
import java.util.Map;

/**
 * 任务模板属性
 *
 * @author 姚江
 * @since 2023-12-04
 */
public interface TaskTemplateProperty extends DomainObject {
    /**
     * 获取任务模板属性名称
     *
     * @return 任务模板配置名称 {@link String}
     */
    String name();

    /**
     * 获取任务模板属性数据类型
     *
     * @return 任务模板属性数据类型 {@link PropertyDataType}
     */
    PropertyDataType dataType();

    /**
     * 获取任务模板属性序号
     *
     * @return 任务模板属性的序号 32位整数
     */
    int sequence();

    /**
     * 获取数据存储在宽表中的列的名称。
     *
     * @return 表示宽表列的名称的 {@link String}。
     */
    String column();

    /**
     * 获取任务模板Id
     *
     * @return 任务模板Id {@link String}
     */
    String taskTemplateId();

    /**
     * 为任务模板属性提供构建器
     *
     * @author 姚江
     * @since 2023-12-04
     */
    interface Builder extends DomainObject.Builder<TaskTemplateProperty, TaskTemplateProperty.Builder> {
        /**
         * 设置任务模板属性名称
         *
         * @param name 任务模板属性名称 {@link String}
         * @return 构建器 {@link Builder}
         */
        Builder name(String name);

        /**
         * 设置任务模板属性数据类型
         *
         * @param dataType 任务模板属性名称 {@link PropertyDataType}
         * @return 构建器 {@link Builder}
         */
        Builder dataType(PropertyDataType dataType);

        /**
         * 设置任务模板属性序号
         *
         * @param sequence 任务模板属性序号 32位整数
         * @return 构建器 {@link Builder}
         */
        Builder sequence(int sequence);

        /**
         * 设置任务模板Id
         *
         * @param taskTemplateId 任务模板Id {@link String}
         * @return 构建器 {@link Builder}
         */
        Builder taskTemplateId(String taskTemplateId);
    }

    /**
     * 返回默认的任务模板属性构建器
     *
     * @return 任务模板属性构建器 {@link Builder}
     */
    static Builder custom() {
        return new DefaultTaskTemplateProperty.Builder();
    }

    /**
     * 为任务模板属性提供声明
     *
     * @author 姚江
     * @since 2023-12-04
     */
    interface Declaration {
        /**
         * 获取任务模板属性声明id，用于修改
         *
         * @return 任务模板属性声明id {@link UndefinableValue}{@code <}{@link String}{@code >}
         */
        UndefinableValue<String> id();

        /**
         * 获取任务模板属性声明名称
         *
         * @return 任务模板属性声明名称 {@link UndefinableValue}{@code <}{@link String}{@code >}
         */
        UndefinableValue<String> name();

        /**
         * 获取任务模板属性声明数据类型
         *
         * @return 任务模板属性声明数据类型 {@link UndefinableValue}{@code <}{@link String}{@code >}
         */
        UndefinableValue<String> dataType();

        /**
         * 为任务模板属性声明提供构建器
         *
         * @author 姚江
         * @since 2023-12-04
         */
        interface Builder {
            /**
             * 设置任务模板属性声明id
             *
             * @param id 任务模板属性声明id
             * @return 构建器 {@link Builder}
             */
            Builder id(String id);

            /**
             * 设置任务模板属性声明名称
             *
             * @param name 任务模板属性声明名称
             * @return 构建器 {@link Builder}
             */
            Builder name(String name);

            /**
             * 设置任务模板属性声明数据类型
             *
             * @param dataType 任务模板属性声明名称
             * @return 构建器 {@link Builder}
             */
            Builder dataType(String dataType);

            /**
             * 构建任务模板属性声明
             *
             * @return 任务模板属性声明 {@link Declaration}
             */
            Declaration build();
        }

        /**
         * 获取默认的任务模板属性声明的构建器
         *
         * @return 任务模板属性声明的构建器 {@link Builder}
         */
        static Builder custom() {
            return new DefaultTaskTemplateProperty.Declaration.Builder();
        }
    }

    /**
     * 为任务模板属性提供数据库存储功能
     *
     * @author 姚江
     * @since 2023-12-04
     */
    interface Repo {
        /**
         * 创建任务模板属性
         *
         * @param taskTemplateId 任务模板id {@link String}
         * @param declarations 任务模板属性声明列表 {@link List}{@code <}{@link Declaration}{@code >}
         * @param context 操作上下文 {@link OperationContext}
         * @return 任务模板属性列表 {@link List}{@code <}{@link TaskTemplateProperty}{@code >}
         */
        List<TaskTemplateProperty> create(String taskTemplateId, List<Declaration> declarations,
                OperationContext context);

        /**
         * 更新任务模板属性
         *
         * @param taskTemplateId 任务模板id {@link String}
         * @param id 任务模板属性Id {@link String}
         * @param declaration 任务模板属性声明 {@link Declaration}
         * @param context 操作上下文 {@link OperationContext}
         */
        void patch(String taskTemplateId, String id, Declaration declaration, OperationContext context);

        /**
         * 删除某个任务模板属性
         *
         * @param taskTemplateId 任务模板id {@link String}
         * @param id 任务模板属性id {@link String}
         * @param context 操作上下文 {@link OperationContext}
         */
        void delete(String taskTemplateId, String id, OperationContext context);

        /**
         * 批量删除任务模板属性
         *
         * @param ids 任务模板属性id列表 {@link List}{@code <}{@link String}{@code >}
         * @param context 操作上下文 {@link OperationContext}
         */
        void delete(List<String> ids, OperationContext context);

        /**
         * 删除任务模板下的所有属性
         *
         * @param taskTemplateId 任务模板id {@link String}
         * @param context 操作上下文 {@link OperationContext}
         */
        void deleteByTaskTemplateId(String taskTemplateId, OperationContext context);

        /**
         * 检索任务模板属性
         *
         * @param taskTemplateId 任务模板id {@link String}
         * @param id 任务模板属性id {@link String}
         * @param context 操作上下文 {@link OperationContext}
         * @return 任务模板属性 {@link TaskTemplateProperty}
         */
        TaskTemplateProperty retrieve(String taskTemplateId, String id, OperationContext context);

        /**
         * 查询任务模板属性
         *
         * @param taskTemplateId 任务模板id {@link String}
         * @param context 操作上下文 {@link OperationContext}
         * @return 任务模板属性列表 {@link List}{@code <}{@link TaskTemplateProperty}{@code >}
         */
        List<TaskTemplateProperty> list(String taskTemplateId, OperationContext context);

        /**
         * 查询任务模板属性
         *
         * @param taskTemplateIds 任务模板id {@link List}{@code <}{@link String}{@code >}
         * @param context 操作上下文 {@link OperationContext}
         * @return 任务模板属性列表
         * {@link Map}{@code <}{@link String}{@code ,}
         * {@link List}{@code <}{@link TaskTemplateProperty}{@code >}{@code >}
         */
        Map<String, List<TaskTemplateProperty>> list(List<String> taskTemplateIds, OperationContext context);
    }
}
