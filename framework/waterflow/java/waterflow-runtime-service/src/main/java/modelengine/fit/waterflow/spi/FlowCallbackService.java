/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.spi;

import modelengine.fitframework.annotation.Genericable;

import java.util.List;
import java.util.Map;

/**
 * 流程实例回调函数 Genericable。
 *
 * @author 李哲峰
 * @since 2023-12-11
 */
public interface FlowCallbackService {
    /**
     * 回调函数实现
     *
     * @param contexts 流程上下文信息
     */
    @Genericable(id = "w8onlgq9xsw13jce4wvbcz3kbmjv3tuw")
    void callback(List<Map<String, Object>> contexts);
}
