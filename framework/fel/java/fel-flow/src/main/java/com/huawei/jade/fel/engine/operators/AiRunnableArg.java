/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.engine.operators;

import com.huawei.fit.waterflow.domain.context.FlowSession;
import com.huawei.fitframework.inspection.Validation;
import com.huawei.jade.fel.core.memory.Memory;
import com.huawei.jade.fel.engine.util.StateKey;

import lombok.Getter;

/**
 * 表示对 {@link AiRunnable} 的业务数据的封装。
 *
 * @param <T> 表示 {@link AiRunnable} 处理的业务数据的类型。
 * @author 刘信宏
 * @since 2024-4-28
 */
public class AiRunnableArg<T> implements CustomState<T> {
    private final T data;
    @Getter
    private final FlowSession session;

    /**
     * 初始化 {@link AiRunnableArg}{@code <}{@link T}{@code >}。
     *
     * @param data 表示业务数据的 {@link T}。
     * @param session 表示流程会话实例信息的 {@link FlowSession}。
     * @throws IllegalArgumentException 当 {@code data} 或 {@code session} 为 {@code null} 时。
     */
    public AiRunnableArg(T data, FlowSession session) {
        Validation.notNull(data, "Data cannot be null.");
        Validation.notNull(session, "FlowSession cannot be null.");
        this.data = data;
        this.session = session;
    }

    @Override
    public T data() {
        return this.data;
    }

    @Override
    public Memory memory() {
        return this.session.getInnerState(StateKey.HISTORY_OBJ);
    }

    @Override
    public <R> R getState(String key) {
        Validation.notBlank(key, "invalid key.");
        return this.session.getState(key);
    }

    @Override
    public void setState(String key, Object value) {
        Validation.notBlank(key, "invalid key.");
        this.session.setState(key, value);
    }

    /**
     * 获取指定键的流程内置状态数据。
     *
     * @param key 表示指定键的 {@link String}。
     * @return 表示流程内置状态数据的 {@link R}。
     * @throws IllegalArgumentException 当 {@code key} 为 {@code null} 、空字符串或只有空白字符的字符串时。
     */
    public <R> R getInnerState(String key) {
        Validation.notBlank(key, "invalid key.");
        return this.session.getInnerState(key);
    }

    /**
     * 设置流程内置状态数据。
     *
     * @param key 表示指定键的 {@link String}。
     * @param value 表示状态数据的 {@link Object}。
     * @throws IllegalArgumentException 当 {@code key} 为 {@code null} 、空字符串或只有空白字符的字符串时。
     */
    public void setInnerState(String key, Object value) {
        Validation.notBlank(key, "invalid key.");
        this.session.setInnerState(key, value);
    }
}
