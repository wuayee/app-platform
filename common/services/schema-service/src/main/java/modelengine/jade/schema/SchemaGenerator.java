/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.schema;

import modelengine.fitframework.annotation.Genericable;
import modelengine.jade.schema.exception.JsonSchemaInvalidException;

/**
 * 生成数据约束接口。
 *
 * @author 兰宇晨
 * @since 2024-08-07
 */
public interface SchemaGenerator {
    /**
     * 根据 json 生成数据约束。
     *
     * @param json 表示用于生成评估数据约束的 {@link String}。
     * @return 表示 json 对应数据约束的 {@link String}。
     * @throws JsonSchemaInvalidException 当 {@code json} 为无效数据时。
     */
    @Genericable(id = "modelengine.jade.app.eval.schema.generate")
    String generateSchema(String json);
}