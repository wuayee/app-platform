/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.ohscript.script.interpreter;

import static com.huawei.fit.ohscript.util.Constants.ARRAY_INSERT;
import static com.huawei.fit.ohscript.util.Constants.ARRAY_REMOVE;
import static com.huawei.fit.ohscript.util.Constants.ARRAY_SIZE;

import com.huawei.fit.ohscript.script.errors.OhPanic;
import com.huawei.fit.ohscript.script.parser.nodes.SyntaxNode;
import com.huawei.fit.ohscript.script.semanticanalyzer.type.expressions.TypeExprFactory;
import com.huawei.fit.ohscript.util.TriFunction;
import com.huawei.fitframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统方法解释器
 *
 * @since 1.0
 */
public class SystemMethodInterpreters {
    /**
     * 系统方法的映射表
     * <p>
     * 这是一个映射表，用于存储系统方法的名称和对应的解释器
     */
    private static final Map<String, TriFunction<List<ReturnValue>, ASTEnv, ActivationContext, ReturnValue>> METHODS
            = new HashMap<>();

    static {
        METHODS.put(ARRAY_SIZE, (args, env, current) -> new ReturnValue(current, TypeExprFactory.createNumber(null),
                (ObjectUtils.<List>cast(current.getThis().value())).size()));
        METHODS.put(ARRAY_REMOVE, (args, env, current) -> new ReturnValue(current, TypeExprFactory.createGeneric(null),
                (ObjectUtils.<List>cast(current.getThis().value())).remove((int) args.get(0).value())));
        METHODS.put(ARRAY_INSERT, (args, env, current) -> {
            (ObjectUtils.<List>cast(current.getThis().value())).add(ObjectUtils.cast(args.get(0).value()), args.get(1));
            return ReturnValue.UNIT;
        });
    }

    /**
     * 解释执行系统方法
     * 这个方法用于解释系统方法，根据方法的名称和参数，调用对应的解释器，并返回解释结果
     *
     * @param method 系统方法的语法节点
     * @param env 抽象语法树环境
     * @param current 激活上下文
     * @return 返回解释结果
     * @throws OhPanic 当解释过程中出现错误时抛出
     */
    public static ReturnValue interpret(SyntaxNode method, ASTEnv env, ActivationContext current) throws OhPanic {
        String name = method.child(1).lexeme();
        List<ReturnValue> args = new ArrayList<>();
        for (int i = 2; i < method.childCount() - 1; i++) {
            args.add(method.child(i).interpret(env, current));
        }
        return METHODS.get(name).apply(args, env, current);
    }
}
