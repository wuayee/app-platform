/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.schema;

import lombok.Data;

/**
 * 表示评估数据约束生成测试用例参数。
 *
 * @author 兰宇晨
 * @since 2024-08-07
 */
@Data
public class SchemaGeneratorImplTestCaseParam {
    private String json;
    private String schema;
}
