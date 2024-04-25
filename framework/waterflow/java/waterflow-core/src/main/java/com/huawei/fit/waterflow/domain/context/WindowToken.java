/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.waterflow.domain.context;

import com.huawei.fit.waterflow.domain.stream.operators.Operators;
import com.huawei.fit.waterflow.domain.stream.reactive.Publisher;
import com.huawei.fit.waterflow.domain.utils.Tuple;

import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * window的处理器
 *
 * @param <I> 处理的数据类型
 * @since 1.0
 */
public class WindowToken<I> {
    private final String id;

    private Publisher processor;

    Map<Object, Tuple<FlowSession, Object>> accs = new HashMap<>();

    // private boolean consumed = false;
    private List todo = new ArrayList();

    private List<I> origins = new ArrayList<>();

    @Getter
    private final Operators.Window window;

    public WindowToken(Operators.Window<I> window) {
        this.window = window;
        this.id = UUID.randomUUID().toString();
    }

    public synchronized void fire() {
        // 如果还有未处理的数据，不能触发累积节点数据流转
        if (this.todo.size() > 0 || !this.fulfilled()) {
            return;
        }

        accs.values().forEach(acc -> processor.offer(acc.second(), acc.first()));
        accs.clear();
    }

    public String id() {
        return this.id;
    }

    public <T> void setProcessor(Publisher<T> processor) {
        this.processor = processor;
    }

    public Map<Object, Tuple<FlowSession, Object>> accs() {
        return accs;
    }

    public boolean fulfilled() {
        return this.window.fulfilled(this.origins);
    }

    public synchronized void addOrigin(I data) {
        this.origins.add(data);
    }

    public synchronized void addToDo(Object data) {
        this.todo.add(data);
    }

    public synchronized void removeToDo(Object data) {
        this.todo.remove(data);
    }
}

