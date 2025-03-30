/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.common.code;

/**
 * 表示错误码接口类型。
 *
 * @author 易文渊
 * @since 2024-07-18
 */
public interface RetCode {
    /**
     * 获取错误码。
     *
     * @return 表示状态码的 {@code int}。
     */
    int getCode();

    /**
     * 获取错误信息。
     *
     * @return 表示错误信息的 {@link String}。
     */
    String getMsg();
}