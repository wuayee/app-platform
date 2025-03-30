/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.rewrite.domain.entity.support;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fel.core.chat.ChatMessage;
import modelengine.fel.core.chat.ChatModel;
import modelengine.fel.core.template.support.DefaultStringTemplate;
import modelengine.fit.jade.aipp.rewrite.domain.entity.RewriteStrategy;
import modelengine.fit.jade.aipp.rewrite.domain.entity.Rewriter;
import modelengine.fit.jade.aipp.rewrite.domain.vo.RewriteParam;
import modelengine.fit.jade.aipp.rewrite.util.Constant;
import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.serialization.ObjectSerializer;
import modelengine.fitframework.serialization.SerializationException;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.IoUtils;
import modelengine.fitframework.util.LineSeparator;
import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.StringUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 表示 {@link Rewriter} 的内置实现。
 *
 * @author 易文渊
 * @since 2024-09-23
 */
public class BuiltinRewriter extends AbstractRewriter {
    private static final Logger LOG = Logger.get(BuiltinRewriter.class);

    // 匹配所有的标点符号与空格
    private static final Pattern REPLACE_PATTERN = Pattern.compile("[\\p{Punct}\\s]");

    private final String builtinPrompt;
    private final ObjectSerializer serializer;

    /**
     * 创建 {@link BuiltinRewriter} 的实例。
     *
     * @param modelService 表示模型服务的 {@link ChatModel}。
     * @param serializer 表示序列化器的 {@link ObjectSerializer}。
     * @throws IOException 当无法读取内置提示模板文件时。
     * @throws IllegalArgumentException 当 {@code modelService}, {@code serializer} 为 {@code null} 时。
     */
    public BuiltinRewriter(ChatModel modelService, ObjectSerializer serializer) throws IOException {
        super(modelService);
        String content = IoUtils.content(BuiltinRewriter.class, Constant.BUILTIN_PROMPT);
        this.builtinPrompt = content.replace(LineSeparator.CRLF.value(), LineSeparator.LF.value());
        this.serializer = notNull(serializer, "The serializer cannot be null.");
    }

    @Override
    public RewriteStrategy strategy() {
        return RewriteStrategy.BUILTIN;
    }

    @Override
    protected String preparePrompt(@Nonnull RewriteParam param) {
        Map<String, String> variables = param.getVariables();
        String rawQuery = notNull(variables.get(Constant.QUERY_KEY), "The raw query cannot be null.");
        String histories = variables.getOrDefault(Constant.HISTORY_KEY, StringUtils.EMPTY);
        String chatBg = new DefaultStringTemplate(param.getTemplate()).render(variables);
        String systemPrompt =
                StringUtils.isEmpty(chatBg) ? StringUtils.EMPTY : StringUtils.format("Q: 对话背景\nA: {0}\n", chatBg);
        String concatHistory = (systemPrompt + histories).trim();
        Map<String, String> values = MapBuilder.<String, String>get()
                .put(Constant.QUERY_KEY, rawQuery)
                .put(Constant.HISTORY_KEY, concatHistory)
                .build();
        return new DefaultStringTemplate(this.builtinPrompt).render(values);
    }

    @Override
    protected List<String> parseOutput(@Nonnull RewriteParam param, @Nonnull ChatMessage answer) {
        Map<String, String> variables = param.getVariables();
        String rawQuery = variables.get(Constant.QUERY_KEY);
        try {
            List<String> queries = CollectionUtils.connect(Collections.singletonList(rawQuery),
                    this.serializer.deserialize(answer.text(), Constant.TYPE_LIST_STRING));
            return filterSameQuery(queries);
        } catch (SerializationException ex) {
            LOG.warn("Failed to rewrite: query [{}], answer [{}].", rawQuery, answer.text());
            return Collections.singletonList(rawQuery);
        }
    }

    private static List<String> filterSameQuery(List<String> queries) {
        if (queries.size() <= 1) {
            return queries;
        }
        Set<String> set = new HashSet<>(queries.size());
        return queries.stream().filter(query -> {
            String str = REPLACE_PATTERN.matcher(query).replaceAll(StringUtils.EMPTY);
            if (set.contains(str)) {
                return false;
            }
            set.add(str);
            return true;
        }).collect(Collectors.toList());
    }
}