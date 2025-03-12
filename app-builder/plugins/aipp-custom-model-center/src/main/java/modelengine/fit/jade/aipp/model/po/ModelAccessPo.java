/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.model.po;

import lombok.Data;

/**
 * 模型访问信息 ORM 对象。
 *
 * @author lixin
 * @since 2025/3/11
 */
@Data
public class ModelAccessPo {
    ModelPo modelPO;
    String apiKey;
}
