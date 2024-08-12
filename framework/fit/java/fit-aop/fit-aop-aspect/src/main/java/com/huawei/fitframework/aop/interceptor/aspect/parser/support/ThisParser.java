/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fitframework.aop.interceptor.aspect.parser.support;

import static com.huawei.fitframework.util.ObjectUtils.nullIf;

import com.huawei.fitframework.aop.interceptor.aspect.parser.PointcutParameter;
import com.huawei.fitframework.aop.interceptor.aspect.parser.model.PointcutSupportedType;
import com.huawei.fitframework.aop.interceptor.aspect.util.ExpressionUtils;

import java.lang.reflect.Method;

/**
 * 解析切点表达式中关键字 this 的解析器。
 * <P>用于匹配方法所属代理类，不支持通配符，有以下 2 种用法：</p>
 * <ul>
 *     <li>参数过滤：匹配的是参数类型和个数，个数在 this 括号中以逗号分隔，类型是在 this 括号中声明。</li>
 *     <li>参数绑定：匹配的是参数类型和个数，个数在 this 括号中以逗号分隔，类型是在增强方法中定义，并且必须在 {@code argsName} 中声明。</li>
 * </ul>
 *
 * @author 郭龙飞 gwx900499
 * @since 2023-03-14
 */
public class ThisParser extends BaseParser {
    private final PointcutParameter[] parameters;
    private final ClassLoader classLoader;

    public ThisParser(PointcutParameter[] parameters, ClassLoader classLoader) {
        this.parameters = nullIf(parameters, new PointcutParameter[0]);
        this.classLoader = classLoader;
    }

    @Override
    protected PointcutSupportedType parserType() {
        return PointcutSupportedType.THIS;
    }

    @Override
    protected Result createConcreteParser(String content) {
        return new ThisResult(content);
    }

    class ThisResult extends BaseParser.BaseResult {
        public ThisResult(String content) {
            super(content, ThisParser.this.classLoader);
        }

        @Override
        public boolean couldMatch(Class<?> beanClass) {
            if (this.isBinding()) {
                return this.isClassMatch(this.content, beanClass, ThisParser.this.parameters);
            }
            Class<?> contentClass =
                    ExpressionUtils.getContentClass(this.content().toString(), ThisParser.this.classLoader);
            if (contentClass == null) {
                return false;
            }
            return contentClass.isAssignableFrom(beanClass);
        }

        @Override
        public boolean match(Method method) {
            return true;
        }
    }
}
