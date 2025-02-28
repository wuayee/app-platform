/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.carver.operation.support;

import lombok.Getter;

import java.util.Map;

/**
 * 组合参数入参，表示操作日志中系统和用户的属性。
 *
 * @author 方誉州
 * @since 2024-08-02
 */
@Getter
public class CompositParam {
    /**
     * 用户添加的操作日志span的属性，来源于注解的属性
     */
    private final Map<String, String> userAttribute;

    /**
     * 内置的操作日志span的属性，来源于threadLocal
     */
    private final Map<String, String> systemAttribute;

    /**
     * 构造函数
     *
     * @param userAttribute 表示用户添加的操作日志属性的{@link Map}{@code <}{@link String}{@code , }{@link String}{@code >}。
     * @param systemAttribute 表示系统内置的操作日志属性的{@link Map}{@code <}{@link String}{@code , }{@link String}{@code >}。
     */
    public CompositParam(Map<String, String> userAttribute, Map<String, String> systemAttribute) {
        this.userAttribute = userAttribute;
        this.systemAttribute = systemAttribute;
    }
}
