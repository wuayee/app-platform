/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.http.call.enums;

import lombok.Getter;
import modelengine.fitframework.util.StringUtils;

import java.util.Arrays;
import java.util.Optional;

/**
 * 鉴权类型。
 *
 * @author 张越
 * @since 2024-11-22
 */
@Getter
public enum AuthenticationType {
    NONE("none"),
    BASIC("basic"),
    BEARER("Bearer"),
    CUSTOM("custom");

    private final String key;

    AuthenticationType(String key) {
        this.key = key;
    }

    /**
     * 将key转换为 {@link AuthenticationType} .
     *
     * @param key 键值.
     * @return {@link Optional}{@code <}{@link AuthenticationType}{@code >} 对象.
     */
    public static Optional<AuthenticationType> fromKey(String key) {
        return Arrays.stream(AuthenticationType.values()).filter(type -> StringUtils.equals(type.key, key)).findFirst();
    }
}
