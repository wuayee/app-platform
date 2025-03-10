/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.carver.util;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.serialization.ObjectSerializer;
import modelengine.fitframework.util.TypeUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * 表示序列化的工具类。
 *
 * @author 李金绪
 * @since 2024-06-14
 */
public class SerializeUtils {
    private static final ParameterizedType TYPE =
            TypeUtils.parameterized(Map.class, new Type[] {String.class, Object.class});

    /**
     * 将 Json 格式化数据反序列化为一个键值对。
     *
     * @param json 表示待序列化的字符串 {@link String}。
     * @param serializer 表示 Json 序列化对象的 {@link ObjectSerializer}。
     * @return 序列化的结果的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     */
    public static Map<String, Object> json2obj(String json, ObjectSerializer serializer) {
        notNull(serializer, "The serializer cannot be null.");
        return json != null ? serializer.deserialize(json, TYPE) : null;
    }
}
