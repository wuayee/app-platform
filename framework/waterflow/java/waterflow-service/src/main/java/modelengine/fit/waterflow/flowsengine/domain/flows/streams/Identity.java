/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.domain.flows.streams;

/**
 * ID生成器接口
 *
 * @author 高诗意
 * @since 2023/08/14
 */
@FunctionalInterface
public interface Identity {
    /**
     * getId
     *
     * @return String
     */
    String getId();
}
