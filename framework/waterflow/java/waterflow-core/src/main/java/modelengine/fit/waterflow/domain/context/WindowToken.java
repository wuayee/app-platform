/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fit.waterflow.domain.context;

import modelengine.fit.waterflow.domain.stream.operators.Operators;
import modelengine.fit.waterflow.domain.stream.reactive.Publisher;
import modelengine.fit.waterflow.domain.utils.Tuple;

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

    private Map<Object, Tuple<FlowSession, Object>> accs = new HashMap<>();

    private List todo = new ArrayList();

    private List<I> origins = new ArrayList<>();

    @Getter
    private final Operators.Window window;

    public WindowToken(Operators.Window<I> window) {
        this.window = window;
        this.id = UUID.randomUUID().toString();
    }

    /**
     * 判定window是否条件满足，满足则执行window的聚合
     */
    public synchronized void fire() {
        // 如果还有未处理的数据，不能触发累积节点数据流转
        if (this.todo.size() > 0 || !this.fulfilled()) {
            return;
        }

        accs.values().forEach(acc -> processor.offer(acc.second(), acc.first()));
        accs.clear();
    }

    /**
     * 获取id
     *
     * @return 得到id
     */
    public String id() {
        return this.id;
    }

    public <T> void setProcessor(Publisher<T> processor) {
        this.processor = processor;
    }

    /**
     * 获取本window对应的收集器
     *
     * @return accs
     */
    public Map<Object, Tuple<FlowSession, Object>> accs() {
        return accs;
    }

    /**
     * 是否window内所有满足要求的数据已经到达
     *
     * @return 是否到达
     */
    public boolean fulfilled() {
        return this.window.fulfilled(this.origins);
    }

    /**
     * 添加原始来源对象
     *
     * @param data 原始来源对象
     */
    public synchronized void addOrigin(I data) {
        this.origins.add(data);
    }

    /**
     * 添加一个待执行任务
     *
     * @param data 任务对象
     */
    public synchronized void addToDo(Object data) {
        this.todo.add(data);
    }

    /**
     * 删除一个待执行任务
     *
     * @param data 任务对象
     */
    public synchronized void removeToDo(Object data) {
        this.todo.remove(data);
    }
}

