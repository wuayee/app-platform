/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.carver.tool.support;

/**
 * 表示工具元数据中的键。
 *
 * @author 刘信宏
 * @since 2024-06-26
 */
public interface SchemaKey {
    /**
     * 表示工具参数。
     */
    String PARAMETERS = "parameters";

    /**
     * 表示工具参数的属性列表。
     */
    String PARAMETERS_PROPERTIES = "properties";

    /**
     * 表示必填参数的列表。
     */
    String PARAMETERS_REQUIRED = "required";

    /**
     * 表示参数顺序。
     */
    String PARAMETERS_ORDER = "order";

    /**
     * 表示参数的默认值。
     */
    String DEFAULT_PARAMETER = "default";

    /**
     * 表示参数的拓展属性。
     */
    String PARAMETERS_EXTENSIONS = "parameterExtensions";

    /**
     * 表示配置输入的参数列表。
     */
    String CONFIG_PARAMETERS = "config";

    /**
     * 表示工具的返回值格式。
     */
    String RETURN_SCHEMA = "return";

    /**
     * 表示工参数类型的描述。
     */
    String PROPERTIES_TYPE = "type";
}
