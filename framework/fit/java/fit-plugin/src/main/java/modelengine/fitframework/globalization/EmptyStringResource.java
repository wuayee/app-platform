/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.globalization;

import java.util.Locale;

/**
 * 为 {@link StringResource} 提供空实现。
 *
 * @author 梁济时
 * @since 2022-11-22
 */
final class EmptyStringResource implements StringResource {
    /**
     * 表示当前类型的唯一实例。
     */
    static final EmptyStringResource INSTANCE = new EmptyStringResource();

    /**
     * 隐藏默认构造方法，避免单例类型被外部实例化。
     */
    private EmptyStringResource() {}

    @Override
    public String getMessage(Locale locale, String key, String defaultMessage, Object... args) {
        return null;
    }
}
