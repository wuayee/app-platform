/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fit.waterflow.domain.stream.operators;

import modelengine.fit.waterflow.domain.context.FlowContext;

import java.util.List;

/**
 * 基于个人session的window
 *
 * @param <T> 数据类型
 * @since 1.0
 */
public class SessionWindow<T> implements Operators.Window<T> {
    private final String key;

    private final Operators.Window<T> window;

    private SessionWindow(String key, Operators.Window<T> window) {
        this.window = window;
        this.key = key;
    }

    /**
     * 指定一个window条件，构造一个sessionWindow
     *
     * @param key 用于构建window的key
     * @param window 给定的window条件
     * @param <T> 数据类型
     * @return 构造后的sessionWindow
     */
    public static <T> SessionWindow<T> from(String key, Operators.Window<T> window) {
        return new SessionWindow<>(key, window);
    }

    /**
     * 指定一个window条件，构造一个sessionWindow
     *
     * @param window 给定的window条件
     * @param <T> 数据类型
     * @return 构造后的sessionWindow
     */
    public static <T> SessionWindow<T> from(Operators.Window<T> window) {
        return from(null, window);
    }

    @Override
    public Object getSessionKey(FlowContext<T> input) {
        if (this.key == null) {
            return input.keyBy();
        } else {
            return input.getState(this.key);
        }
    }

    @Override
    public boolean fulfilled(List<T> inputs) {
        return this.window.fulfilled(inputs);
    }
}
