/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.common.localemessage;

/**
 * 异常信息国际化服务。
 *
 * @author 刘信宏
 * @since 2024-10-11
 */
public interface ExceptionLocaleService {
    /**
     * 获取异常对象的国际化提示信息。
     *
     * @param throwable 表示异常对象的 {@link Throwable}。
     * @return 表示国际化提示信息的 {@link String}。
     */
    String localizeMessage(Throwable throwable);
}
