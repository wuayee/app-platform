/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.formatter.util;

import modelengine.fitframework.serialization.ObjectSerializer;
import modelengine.fitframework.util.ObjectUtils;

/**
 * 序列化工具类。
 *
 * @author 刘信宏
 * @since 2024-11-27
 */
public class SerializerUtils {
    /**
     * 将指定对象序列化为字符串。
     *
     * @param serializer 表示对象序列化器的 {@link ObjectSerializer}。
     * @param input 表示指定对象的 {@link Object}。
     * @return 表示序列化后的字符串 {@link String}。
     */
    public static String serialize(ObjectSerializer serializer, Object input) {
        if (input instanceof String) {
            return ObjectUtils.cast(input);
        }
        return serializer.serialize(input);
    }
}
