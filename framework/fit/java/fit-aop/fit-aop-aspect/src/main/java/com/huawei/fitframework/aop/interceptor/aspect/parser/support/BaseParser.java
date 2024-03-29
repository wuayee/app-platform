/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2024. All rights reserved.
 */

package com.huawei.fitframework.aop.interceptor.aspect.parser.support;

import static com.huawei.fitframework.aop.interceptor.aspect.util.ExpressionUtils.getContentClass;

import com.huawei.fitframework.aop.interceptor.aspect.parser.ExpressionParser;
import com.huawei.fitframework.aop.interceptor.aspect.parser.model.PointcutSupportedType;
import com.huawei.fitframework.inspection.Nullable;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * 切点表达式解析器的通用抽象实现。
 *
 * @author 白鹏坤 bWX1068551
 * @since 2023-04-06
 */
public abstract class BaseParser implements ExpressionParser {
    /**
     * 逻辑运算符类型集合。
     */
    public static final Set<String> logicReferenceTypes = new HashSet<>();

    static {
        logicReferenceTypes.add(PointcutSupportedType.AND.getValue());
        logicReferenceTypes.add(PointcutSupportedType.OR.getValue());
        logicReferenceTypes.add(PointcutSupportedType.NOT.getValue());
        logicReferenceTypes.add(PointcutSupportedType.LEFT_BRACKET.getValue());
        logicReferenceTypes.add(PointcutSupportedType.RIGHT_BRACKET.getValue());
    }

    /**
     * 获取解析器类型。
     *
     * @return 返回解析器类型的 {@link PointcutSupportedType}。
     */
    protected abstract PointcutSupportedType parserType();

    /**
     * 根据解析内容，创建具体的解析器。
     *
     * @param content 解析器待解析内容的 {@link String}。
     * @return 返回解析结果的 {@link Result}。
     */
    protected abstract Result createConcreteParser(String content);

    @Override
    public boolean couldParse(String expression) {
        return expression.startsWith(this.parserType().getValue());
    }

    @Override
    public Result parse(String expression) {
        String replacedContent = expression;
        if (!logicReferenceTypes.contains(expression)) {
            replacedContent =
                    ExpressionParser.getParseContent(expression, this.parserType().getValue()).replaceAll(" ", "");
        }
        return this.createConcreteParser(replacedContent);
    }

    abstract class BaseResult implements Result {
        /**
         * 解析器待解析的内容。
         */
        protected final String content;
        private final ClassLoader classLoader;

        public BaseResult(String content, ClassLoader classLoader) {
            this.content = content;
            this.classLoader = classLoader;
        }

        @Override
        public boolean couldMatch(Class<?> bean) {
            return false;
        }

        @Override
        public boolean match(Method method) {
            return false;
        }

        @Override
        public boolean isBinding() {
            return getClassIgnoreException(this.content, this.classLoader) == null;
        }

        @Override
        public PointcutSupportedType type() {
            return BaseParser.this.parserType();
        }

        @Override
        public Object content() {
            return this.content;
        }
    }

    /**
     * 忽略异常，将字符串转换为 class 类。
     *
     * @param content 待转换为类的 {@link String}。
     * @param classLoader 类加载器。
     * @return 如果可以可以转换 class，返回 {@link Class}，否则，返回 {@code null}。
     */
    @Nullable
    public static Class<?> getClassIgnoreException(String content, ClassLoader classLoader) {
        Class<?> contentClass = null;
        try {
            contentClass = getContentClass(content, classLoader);
        } catch (IllegalArgumentException e) {
            // do nothing
        }
        return contentClass;
    }
}
