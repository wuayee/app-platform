/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.extract.command;

import lombok.Data;
import modelengine.fit.jade.aipp.memory.AippChatRound;
import modelengine.fit.jade.aipp.memory.AippMemoryConfig;

import java.util.List;

/**
 * 表示信息提取的命令。
 *
 * @author 易文渊
 * @since 2024-10-24
 */
@Data
public class ContentExtractCommand {
    /**
     * 需要提取的文本。
     */
    private String text;

    /**
     * 提取要求描述。
     */
    private String desc;

    /**
     * 输出结构描述，必须是一个合法的 json Schema。
     */
    private String outputSchema;

    /**
     * 模型名。
     */
    private String model;

    /**
     * 模型标签。
     */
    private String modelTag;

    /**
     * 温度。
     */
    private Double temperature;

    /**
     * 历史记录。
     */
    private List<AippChatRound> histories;

    /**
     * 历史记录配置。
     */
    private AippMemoryConfig memoryConfig;
}