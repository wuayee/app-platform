/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.domain.flows.parsers.attributes.extractors;

import modelengine.fit.waterflow.flowsengine.domain.flows.parsers.attributes.objects.AttributesData;

/**
 * 属性提取器接口.
 *
 * @author 张越
 * @since 2024-08-05
 */
public interface ValueExtractor {
    /**
     * json 格式数据.
     *
     * @param data 数据.
     * @return 属性对象值.
     */
    Object extract(AttributesData data);
}
