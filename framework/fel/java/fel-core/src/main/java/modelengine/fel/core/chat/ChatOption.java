/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fel.core.chat;

import modelengine.fel.core.tool.ToolInfo;
import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.pattern.builder.BuilderFactory;

import java.util.List;

/**
 * 表示聊天大模型参数的实体。
 *
 * @author 刘信宏
 * @author 易文渊
 * @since 2024-04-12
 */
public interface ChatOption {
    /**
     * 获取调用模型的名字。
     *
     * @return 表示模型名字的 {@link String}。
     */
    @Nonnull
    String model();

    /**
     * 获取是否使用流式接口。
     * <p>
     * 如果设置为 {@code true}，则使用流式接口；否则，使用普通接口。
     * </p>
     *
     * @return 表示是否使用流式接口的 {@code boolean}。
     */
    @Nonnull
    Boolean stream();

    /**
     * 获取模型接口秘钥。
     *
     * @return 表示模型接口秘钥的 {@link String}。
     */
    String apiKey();

    /**
     * 获取生成文本的最大长度。
     *
     * @return 表示生成文本最大长度的 {@link Integer}。
     */
    Integer maxTokens();

    /**
     * 获取频率惩罚系数。
     * <p>
     * 取值范围为 {@code -2.0 - 2.0} 之间的数字，影响模型如何根据已存在文本的频率惩罚新文本。<br/>
     * 正值将通过惩罚已经频繁使用的词来降低模型一行中重复用词的可能性。<br/>
     * 为了稍微减少输出中的重复词语，惩罚系数的合理值通常约为0.1至1。<br/>
     * 如果目标是显著抑制重复，系数可以增加到2，但这可能会对输出的质量产生负面影响。<br/>
     * 相反，使用负值可以增加重复的可能性。<br/>
     * </p>
     *
     * @return 表示频率惩罚系数的 {@link Double}。
     */
    Double frequencyPenalty();

    /**
     * 获取文本出现惩罚系数。
     * <p>
     * 取值范围为 {@code -2.0 - 2.0} 之间的数字，影响模型如何根据到目前为止是否出现在文本中来惩罚新词汇。<br/>
     * 正值将通过惩罚已经使用的词，增加模型谈论新主题的可能性。<br/>
     * 通常 presencePenalty 的默认值为0，当希望使用输入中的单词生成与输入提示一致的文本时，使用该值。<br/>
     * 另一方面，如果希望模型不受输入限制，那么可以使用更大的存在惩罚，这将鼓励模型生成输入中不存在的新词，
     * 从而允许更多样化和创造性的输出。<br/>
     * frequency_penalty 和 presence_penalty 的不同点在于 frequency_penalty 的惩罚会随着 token 出现的次数增加而不断加强，
     * 而 presence_penalty 则只会区分是否出现。
     * </p>
     *
     * @return 表示文本出现惩罚系数的 {@link Double}。
     */
    Double presencePenalty();

    /**
     * 获取停止字符串列表。
     * <p>
     * 当模型输出中存在定义的字符串后，将会停止生成。
     * </p>
     *
     * @return 表示停止字符串列表的 {@link List}{@code <}{@link String}{@code >}。
     */
    List<String> stop();

    /**
     * 获取采样温度。
     * <p>
     * 取值范围为 {@code 0.0 - 2.0} 之间的数字，控制生成的文本的多样性和随机性。<br/>
     * 如果 temperature 值较高，则更倾向于生成随机的文本；如果值较低，则更倾向于生成相对可预测的文本。
     * </p>
     *
     * @return 表示采样温度的 {@link Double}。
     */
    Double temperature();

    /**
     * 获取采样率。
     * <p>
     * 取值范围为 {@code 0.0 - 1.0} 之间的数字，表示模型考虑具有 top_p 概率质量的令牌的结果，与 temperature 不建议同时设置。
     * 例如： 0.1 意味着只考虑包含前 10% 概率质量的标记。
     * </p>
     *
     * @return 表示采样率的 {@link Double}。
     */
    Double topP();

    /**
     * 获取模型能使用的工具列表。
     *
     * @return 表示模型能使用的工具列表的 {@link List}{@code <}{@link ToolInfo}{@code >}。
     */
    List<ToolInfo> tools();

    /**
     * {@link ChatOption} 的构建器。
     */
    interface Builder {
        /**
         * 设置调用模型的名字。
         *
         * @param model 表示模型名字的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder model(String model);

        /**
         * 设置是否使用流式接口。
         *
         * @param stream 表示是否使用流式接口的 {@code boolean}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder stream(boolean stream);

        /**
         * 设置模型接口秘钥。
         *
         * @param apiKey 表示模型接口秘钥的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder apiKey(String apiKey);

        /**
         * 设置生成文本的最大长度。
         *
         * @param maxTokens 表示生成文本最大长度的 {@code int}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder maxTokens(int maxTokens);

        /**
         * 设置频率惩罚系数。
         *
         * @param frequencyPenalty 表示频率惩罚系数的 {@code double}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder frequencyPenalty(double frequencyPenalty);

        /**
         * 设置文本出现惩罚系数。
         *
         * @param presencePenalty 表示文本出现惩罚系数的 {@code double}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder presencePenalty(double presencePenalty);

        /**
         * 设置停止字符串列表。
         *
         * @param stop 表示停止字符串列表的 {@link List}{@code <}{@link String}{@code >}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder stop(List<String> stop);

        /**
         * 设置采样温度。
         *
         * @param temperature 表示采样温度的 {@code double}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder temperature(double temperature);

        /**
         * 设置采样率。
         *
         * @param topP 表示采样率的 {@code double}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder topP(double topP);

        /**
         * 设置模型能使用的工具列表。
         *
         * @param tools 表示模型能使用的工具列表的 {@link List}{@code <}{@link ToolInfo}{@code >}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder tools(List<ToolInfo> tools);

        /**
         * 构建对象。
         *
         * @return 表示构建出来的对象的 {@link ChatOption}。
         */
        ChatOption build();
    }

    /**
     * 获取 {@link ChatOption} 的构建器。
     *
     * @return 表示 {@link ChatOption} 的构建器的 {@link Builder}。
     */
    static Builder custom() {
        return custom(null);
    }

    /**
     * 获取 {@link ChatOption} 的构建器，同时将指定对象的值进行填充。
     *
     * @param value 表示指定对象的 {@link ChatOption}。
     * @return 表示 {@link ChatOption} 的构建器的 {@link Builder}。
     */
    static Builder custom(ChatOption value) {
        return BuilderFactory.get(ChatOption.class, Builder.class).create(value);
    }
}