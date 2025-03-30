/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fel.core.model.http;

import modelengine.fel.core.model.AbstractModelExtractParam;

/**
 * 表示 http 请求体中的额外参数。
 *
 * @author 易文渊
 * @since 2024-12-23
 */
public class ModelExtraHttpBody extends AbstractModelExtractParam<Object> {
    public ModelExtraHttpBody(Object data) {
        super(data);
    }
}