/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2024. All rights reserved.
 */

package com.huawei.fitframework.aop.interceptor.aspect.parser.support;

import static com.huawei.fitframework.util.ObjectUtils.nullIf;

import com.huawei.fitframework.aop.interceptor.aspect.interceptor.inject.AspectParameterInjectionHelper;
import com.huawei.fitframework.aop.interceptor.aspect.parser.PointcutParameter;
import com.huawei.fitframework.aop.interceptor.aspect.parser.model.PointcutSupportedType;
import com.huawei.fitframework.aop.interceptor.aspect.util.ExpressionUtils;
import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.ioc.annotation.AnnotationMetadata;
import com.huawei.fitframework.util.ObjectUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

/**
 * 解析切点表达式中关键字 @target 的解析器。
 * <P>用于匹配方法所属类上指定的注解，不支持通配符，不支持单独使用，支持嵌套注解查找，有以下 2 种用法：</p>
 * <ul>
 *     <li>参数过滤：匹配的是参数类型和个数，个数在 @target 括号中以逗号分隔，类型是在 @target 括号中声明。</li>
 *     <li>参数绑定：匹配的是参数类型和个数，个数在 @target 括号中以逗号分隔，类型是在增强方法中定义，并且必须在 {@code argsName} 中声明。</li>
 * </ul>
 *
 * @author 郭龙飞 gwx900499
 * @since 2023-03-14
 */
public class AtTargetParser extends BaseParser {
    private final PointcutParameter[] parameters;
    private final ClassLoader classLoader;

    public AtTargetParser(PointcutParameter[] parameters, ClassLoader classLoader) {
        this.parameters = nullIf(parameters, new PointcutParameter[0]);
        this.classLoader = classLoader;
    }

    @Override
    protected PointcutSupportedType parserType() {
        return PointcutSupportedType.AT_TARGET;
    }

    @Override
    protected Result createConcreteParser(String content) {
        return new AtTargetResult(content);
    }

    class AtTargetResult extends BaseParser.BaseResult {
        public AtTargetResult(String content) {
            super(content, AtTargetParser.this.classLoader);
        }

        @Override
        public boolean couldMatch(Class<?> bean) {
            AnnotationMetadata annotationMetadata = AspectParameterInjectionHelper.getAnnotationMetadata(bean);
            if (this.isBinding()) {
                Optional<PointcutParameter> parameter = Arrays.stream(AtTargetParser.this.parameters)
                        .filter(param -> param.getName().equals(this.content()))
                        .findFirst();
                Validation.isTrue(parameter.isPresent(),
                        "pointcut params name can not be found.[name={0}]",
                        this.content);
                return parameter.filter(pointcutParameter -> annotationMetadata.isAnnotationPresent(ObjectUtils.cast(
                        pointcutParameter.getType()))).isPresent();
            }
            Class<?> contentClass =
                    ExpressionUtils.getContentClass(this.content().toString(), AtTargetParser.this.classLoader);
            return annotationMetadata.isAnnotationPresent(ObjectUtils.cast(contentClass));
        }

        @Override
        public boolean match(Method method) {
            return true;
        }
    }
}
