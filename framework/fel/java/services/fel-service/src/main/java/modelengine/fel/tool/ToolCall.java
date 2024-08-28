/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fel.tool;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 表示工具调用请求的实体类。
 *
 * @author 易文渊
 * @since 2024-04-16
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ToolCall {
    /**
     * 表示请求编号。
     */
    private String id;

    /**
     * 表示工具名。
     */
    private String name;

    /**
     * 表示调用参数。
     */
    private String parameters;
}