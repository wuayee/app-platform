/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.domain.emitters;

import modelengine.fit.waterflow.domain.context.FlowSession;
import modelengine.fit.waterflow.domain.utils.FlowDebug;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 流程数据发布器
 *
 * @param <D> 数据类型
 * @since 1.0
 */
public class FlowEmitter<D> implements Emitter<D, FlowSession> {
    /**
     * Emitter的监听器
     */
    protected List<EmitterListener<D, FlowSession>> listeners = new ArrayList<>();

    private final List<D> data = new ArrayList<>();

    /**
     * 构造单个数据的Emitter
     *
     * @param data 单个数据
     */
    protected FlowEmitter(D data) {
        this.data.add(data);
    }

    /**
     * 构造一组数据的Emitter
     *
     * @param data 一组数据
     */
    protected FlowEmitter(D... data) {
        this.data.addAll(Arrays.asList(data));
    }

    /**
     * 构造一个mono类型的发布器
     *
     * @param data 待发布的批量数据
     * @param <I> 待发布的数据类型
     * @return 构造一个数据发布器
     */
    public static <I> FlowEmitter<I> mono(I data) {
        return new FlowEmitter<>(data);
    }

    /**
     * 构造一个flux类型的发布器
     *
     * @param data 待发布的批量数据
     * @param <I> 待发布的数据类型
     * @return 构造一个批量数据发布器
     */
    public static <I> FlowEmitter<I> flux(I... data) {
        return new FlowEmitter<>(data);
    }

    @Override
    public void register(EmitterListener<D, FlowSession> listener) {
        this.listeners.add(listener);
    }

    @Override
    public void emit(D data, FlowSession trans) {
        FlowDebug.log(trans, "start listeners size: " + listeners.size() + " data:" + data);
        this.listeners.forEach(listener -> listener.handle(data, trans));
    }

    @Override
    public void start(FlowSession trans) {
        FlowDebug.log(trans, "start data size: " + data.size());
        data.forEach(obj -> this.emit(obj, trans));
    }
}
