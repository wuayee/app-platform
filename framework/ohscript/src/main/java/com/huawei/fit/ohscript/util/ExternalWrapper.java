/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.ohscript.util;

import com.huawei.fit.ohscript.script.interpreter.ASTEnv;

import java.io.Serializable;

/**
 * 外部对象的包装
 *
 * @since 1.0
 */
public class ExternalWrapper implements Serializable {
    private static final long serialVersionUID = 4727900091448954885L;

    private final String key;

    private Object object;

    public ExternalWrapper(String key, Object object) {
        this.object = object;
        this.key = key;
    }

    /**
     * 被包装的外部对象
     *
     * @param object 外部对象
     * @return this
     */
    public ExternalWrapper setObject(Object object) {
        this.object = object;
        return this;
    }

    /**
     * 获取被包装的对象
     *
     * @param env 尝试从env中获取对象
     * @return env不存在该对象时，返回包装的Object
     */
    public Object object(ASTEnv env) {
        Object value = env.getOh(this.key);
        return value == null ? this.object : value;
    }
}
