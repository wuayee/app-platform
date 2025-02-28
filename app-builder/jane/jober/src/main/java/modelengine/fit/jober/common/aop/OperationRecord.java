/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.common.aop;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 用于标注需要记录操作日志的方法
 *
 * @author 姚江
 * @since 2023-11-16 15:12
 */
@Target(METHOD)
@Retention(RUNTIME)
public @interface OperationRecord {
    /**
     * objectId所在的位置
     * <p>
     * 如果在result中，则返回-1
     *
     * @return objectId的获取位置 {@link Integer}
     */
    int objectId() default 0;

    /**
     * 当objectId==-1时，需要将result中获取id的方法名填写
     *
     * @return 从result中获取id的方法名 {@link String}
     */
    String objectIdGetMethodName() default "";

    /**
     * 被修改的对象类型
     *
     * @return 被修改的对象类型 {@link ObjectTypeEnum}
     */
    ObjectTypeEnum objectType();

    /**
     * 操作类型
     *
     * @return 操作类型 {@link OperateEnum}
     */
    OperateEnum operate();

    /**
     * 修改的Declaration的位置
     *
     * @return 修改的Declaration的位置 {@link Integer}
     */
    int declaration() default -1;

    /**
     * 上下文在参数中的位置
     * 默认-1，最后一个
     *
     * @return 上下文在参数列表的位置 {@link Integer}
     */
    int context() default -1;
}
