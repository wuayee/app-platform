/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fel.chat;

import modelengine.fel.tool.Tool;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 表示聊天大模型参数的实体类
 *
 * @author 易文渊
 * @since 2024-04-12
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatOptions {
    /**
     * 模型名。
     */
    private String model;

    /**
     * 模型接口秘钥。
     */
    private String apiKey;

    /**
     * 生成文本的最大长度。
     */
    private Integer maxTokens;

    /**
     * 取值范围为{@code -2.0 - 2.0}之间的数字，影响模型如何根据文本中token的现有频率惩罚新token。<br/>
     * 正值将通过惩罚已经频繁使用的词来降低模型一行中重复用词的可能性。<br/>
     * 为了稍微减少输出中的重复词语，惩罚系数的合理值通常约为0.1至1。<br/>
     * 如果目标是显著抑制重复，系数可以增加到2，但这可能会对输出的质量产生负面影响。<br/>
     * 相反，使用负值可以增加重复的可能性。<br/>
     */
    private Double frequencyPenalty;

    /**
     * 取值范围为{@code -2.0 - 2.0}之间的数字，影响模型如何根据到目前为止是否出现在文本中来惩罚新词汇。<br/>
     * 正值将通过惩罚已经使用的词，增加模型谈论新主题的可能性。<br/>
     * 通常{@code presencePenalty}的默认值为0，当希望使用输入中的单词生成与输入提示一致的文本时，使用该值。<br/>
     * 另一方面，如果希望模型不受输入限制，那么可以使用更大的存在惩罚，这将鼓励模型生成输入中不存在的新词，从而允许更多样化和创造性的输出。
     */
    private Double presencePenalty;

    /**
     * 当模型生成列表中存在的字符时停止。
     */
    private List<String> stop;

    /**
     * 取值范围为{@code 0.0 - 1.0}之间的数字，控制生成的文本的多样性和随机性。<br/>
     * 如果{@code temperature}值较高，则更倾向于生成随机的文本；如果值较低，则更倾向于生成相对可预测的文本。
     */
    private Double temperature;

    /**
     * 模型能使用的工具列表。
     */
    private List<Tool> tools;
}