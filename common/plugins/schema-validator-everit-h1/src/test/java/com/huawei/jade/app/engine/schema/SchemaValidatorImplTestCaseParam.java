/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.schema;

import lombok.Data;

import java.util.List;

/**
 * 表示校验数据校验测试用例参数。
 *
 * @author 兰宇晨
 * @since 2024-07-29
 */
@Data
public class SchemaValidatorImplTestCaseParam {
    private String schema;
    private List<String> content;
}