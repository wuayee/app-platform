/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.tool.loop.entities;

import lombok.Data;

import java.util.List;

/**
 * 循环配置
 *
 * @author 夏斐
 * @since 2025/3/10
 */
@Data
public class Config {
    private List<String> loopKeys;
}
