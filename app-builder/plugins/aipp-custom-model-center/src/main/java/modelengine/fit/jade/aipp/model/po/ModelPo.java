/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.model.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import modelengine.jade.common.po.BasePo;

/**
 * 模型信息 ORM 对象。
 *
 * @author lixin
 * @since 2025/3/11
 */
@Data
@AllArgsConstructor
public class ModelPo extends BasePo {
    private String modelId;
    private String name;
    private String tag;
    private String baseUrl;
    private String type;
}
