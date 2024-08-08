/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.core.retriever.filter;

import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.fitframework.util.StringUtils;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * 表示 {@link ExpressionParser} 的抽象实现。
 *
 * @author 易文渊
 * @since 2024-08-10
 */
public abstract class AbstractExpressionParser implements ExpressionParser {
    @Override
    public String parse(Operand.Expression expression) {
        StringBuilder buf = new StringBuilder();
        this.parseExpression(expression, buf);
        return buf.toString();
    }

    /**
     * 解析操作数。
     *
     * @param operand 表示操作数的 {@link Operand}。
     * @param buf 表示输出缓冲区的 {@link StringBuilder}。
     */
    protected void parseOperand(Operand operand, StringBuilder buf) {
        if (operand instanceof Operand.Key) {
            this.parseKey(ObjectUtils.cast(operand), buf);
        } else if (operand instanceof Operand.Value) {
            this.unzipValue(ObjectUtils.cast(operand), buf);
        } else {
            this.parseExpression(ObjectUtils.cast(operand), buf);
        }
    }

    /**
     * 解析值对象，有以下两种情况：
     * <p>
     * <ol>
     *     <li>如果是集合，则将集合中的每一个元素分别解析后组装</li>
     *     <li>如果不是集合，则直接进行解析</li>
     * </ol>
     * </p>
     *
     * @param value 表示值对象的 {@link Operand.Value}。
     * @param buf 表示输出缓冲区的 {@link StringBuilder}。
     */
    protected void unzipValue(Operand.Value value, StringBuilder buf) {
        Object payload = value.payload();
        if (payload instanceof Collection) {
            this.enterCollection(buf);
            Collection<?> collection = ObjectUtils.cast(payload);
            buf.append(collection.stream()
                    .map(this::parseValue)
                    .collect(Collectors.joining(this.getCollectionDelimiter())));
            this.leaveCollection(buf);
            return;
        }
        buf.append(parseValue(payload));
    }

    /**
     * 解析键对象。
     *
     * @param key 表示键对象的 {@link Operand.Key}。
     * @return 表示解析后字符串的 {@link String}。
     */
    protected abstract String parseKey(String key);

    /**
     * 解析非集合类型。
     *
     * @param object 表示非集合类型值的 {@link Object}。
     * @return 表示解析后字符串的 {@link String}。
     */
    protected String parseValue(Object object) {
        return object instanceof String ? StringUtils.format("\"{0}\"", object) : object.toString();
    }

    /**
     * 解析表达式。
     *
     * @param expression 表示表达式的 {@link Operand.Expression}。
     * @param buf 表示输出缓冲区的 {@link StringBuilder}。
     */
    protected abstract void parseExpression(Operand.Expression expression, StringBuilder buf);

    /**
     * 在解析集合类型值时，首先需要在输出缓冲区中添加一个开始标记。
     *
     * @param buf 表示输出缓冲区的 {@link StringBuilder}。
     */
    protected void enterCollection(StringBuilder buf) {
        buf.append("[");
    }

    /**
     * 在解析集合类型值时，最后需要在输出缓冲区中添加一个结束标记。
     *
     * @param buf 表示输出缓冲区的 {@link StringBuilder}。
     */
    protected void leaveCollection(StringBuilder buf) {
        buf.append("]");
    }

    /**
     * 获取集合元素之间的分隔符。
     *
     * @return 表示集合元素之间的分隔符的 {@link String}。
     */
    protected String getCollectionDelimiter() {
        return ",";
    }

    /**
     * 移除字符串两端的外层引号。
     * <p>具体步骤如下：
     * <ul>
     *     <li>首先，对字符串进行去空格处理；</li>
     *     <li>然后，检查字符串是否含有外层引号，如果有，则去除字符串两端的引号，并再次进行去空格处理；</li>
     *     <li>重复上述步骤，直到字符串两端无外层引号。</li>
     * </ul>
     * </p>
     *
     * @param source 表示指定字符串的 {@link String}。
     * @return 表示去除两端外层引号后的字符串的 {@link String}。
     */
    private static String removeOuterQuotes(String source) {
        if (StringUtils.isBlank(source)) {
            return source;
        }
        String dst = source.trim();
        while (hasOuterQuotes(dst)) {
            dst = dst.substring(1, dst.length() - 1).trim();
        }
        return dst;
    }

    /**
     * 检查指定字符串是否含有外层引号。
     *
     * @param source 表示待检查的字符串的 {@link String}。
     * @return 如果指定字符串含有外层引号，则返回 {@code true}，否则，返回 {@code false}。
     */
    private static boolean hasOuterQuotes(String source) {
        return (source.startsWith("\"") && source.endsWith("\"")) || (source.startsWith("'") && source.endsWith("'"));
    }

    private void parseKey(Operand.Key key, StringBuilder buf) {
        String standard = removeOuterQuotes(key.key());
        buf.append(this.parseKey(standard));
    }
}