/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2025-2025. All rights reserved.
 */

package modelengine.jade.app.engine.eval.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * 表示 Json 的数据对象。
 *
 * @author 兰宇晨
 * @since 2024-08-10
 */
@Data
@AllArgsConstructor
public class JsonEntity {
    /**
     * Json 文件的具体内容。
     */
    private List<String> contents;

    /**
     * Json 文件的数据约束。
     */
    private String schema;
}
