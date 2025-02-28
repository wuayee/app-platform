/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.service.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 将方法参数自动添加为 span 的属性。
 *
 * @author 马朝阳
 * @since 2024-09-02
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface SpanAttr {
    /**
     * 表示指定方法参数作为 span 属性添加到创建的 span 中时的属性名称。
     *
     * @return 表示添加到 span 中的属性名的数组。
     */
    String[] value() default {};
}
