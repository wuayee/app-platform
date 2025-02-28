/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.common.filter.config;

import modelengine.fitframework.annotation.AcceptConfigValues;
import modelengine.fitframework.annotation.Component;

import java.util.List;

/**
 * 默认 http 响应包装器的配置类。
 *
 * @author 易文渊
 * @since 2024-07-18
 */
@Component
@AcceptConfigValues("fit.application.http.wrapper")
public class DefaultHttpResponseWrapperConfig {
    private List<String> support;
    private List<String> nonsupport;

    /**
     * 获取包装器支持的路径样式列表。
     *
     * @return 表示包装器路径支持样式列表的 {@link List}{@code <}{@link String}{@code >}。
     */
    public List<String> getSupport() {
        return this.support;
    }

    /**
     * 设置包装器支持的路径样式列表。
     *
     * @param support 表示包装器路径支持样式列表的 {@link List}{@code <}{@link String}{@code >}。
     */
    public void setSupport(List<String> support) {
        this.support = support;
    }

    /**
     * 获取包装器不支持的路径样式列表。
     *
     * @return 表示包装器路径不支持样式列表的 {@link List}{@code <}{@link String}{@code >}。
     */
    public List<String> getNonsupport() {
        return this.nonsupport;
    }

    /**
     * 设置包装器不支持的路径样式列表。
     *
     * @param nonsupport 表示包装器路径不支持样式列表的 {@link List}{@code <}{@link String}{@code >}。
     */
    public void setNonsupport(List<String> nonsupport) {
        this.nonsupport = nonsupport;
    }
}