/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fitframework.aop.interceptor.aspect.parser.support;

import static com.huawei.fitframework.util.ObjectUtils.nullIf;

import com.huawei.fitframework.aop.interceptor.aspect.interceptor.inject.AspectParameterInjectionHelper;
import com.huawei.fitframework.aop.interceptor.aspect.parser.PointcutParameter;
import com.huawei.fitframework.aop.interceptor.aspect.parser.model.PointcutSupportedType;
import com.huawei.fitframework.aop.interceptor.aspect.util.ExpressionUtils;
import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.util.ObjectUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 解析切点表达式中关键字 @params 的解析器。
 * <P>用于匹配方法参数指定的注解，不支持通配符，不支持单独使用，支持嵌套注解查找，有以下 2 种用法：</p>
 * <ul>
 *     <li>参数过滤：匹配的是参数类型和个数，个数在 @param 括号中以逗号分隔，类型是在 @params 括号中声明。</li>
 *     <li>参数绑定：匹配的是参数类型和个数，个数在 @param 括号中以逗号分隔，类型是在增强方法中定义，并且必须在 {@code argsName} 中声明。</li>
 * </ul>
 *
 * @author 白鹏坤
 * @since 2023-04-27
 */
public class AtParamsParser extends BaseParser {
    private final PointcutParameter[] parameters;
    private final ClassLoader classLoader;

    public AtParamsParser(PointcutParameter[] parameters, ClassLoader classLoader) {
        this.parameters = nullIf(parameters, new PointcutParameter[0]);
        this.classLoader = classLoader;
    }

    @Override
    protected PointcutSupportedType parserType() {
        return PointcutSupportedType.AT_PARAMS;
    }

    @Override
    protected Result createConcreteParser(String content) {
        return new AtParamsResult(content);
    }

    class AtParamsResult extends BaseResult {
        public AtParamsResult(String content) {
            super(content, AtParamsParser.this.classLoader);
        }

        @Override
        public boolean couldMatch(Class<?> beanClass) {
            return true;
        }

        @Override
        public boolean match(Method method) {
            List<Annotation> annotations =
                    Arrays.stream(method.getParameterAnnotations()).flatMap(Stream::of).collect(Collectors.toList());
            if (annotations.isEmpty()) {
                return false;
            }
            Class<?> clazz;
            if (this.isBinding()) {
                Optional<PointcutParameter> parameter = Arrays.stream(AtParamsParser.this.parameters)
                        .filter(param -> Objects.equals(param.getName(), this.content()))
                        .findFirst();
                Validation.isTrue(parameter.isPresent(),
                        "Pointcut params name can not be found.[name={0}]",
                        this.content);
                clazz = parameter.get().getType();
            } else {
                clazz = ExpressionUtils.getContentClass(this.content, AtParamsParser.this.classLoader);
            }
            return annotations.stream().parallel().anyMatch(annotation -> {
                Class<? extends Annotation> type = annotation.annotationType();
                if (type == clazz) {
                    return true;
                }
                return AspectParameterInjectionHelper.getAnnotationMetadata(type)
                        .isAnnotationPresent(ObjectUtils.cast(clazz));
            });
        }
    }
}
