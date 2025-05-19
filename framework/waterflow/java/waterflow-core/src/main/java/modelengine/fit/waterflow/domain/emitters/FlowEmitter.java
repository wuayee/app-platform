/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.domain.emitters;

import modelengine.fit.waterflow.domain.context.FlowSession;

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
    private EmitterListener<D, FlowSession> listener;

    private List<D> data = new ArrayList<>();

    private FlowEmitter(D data) {
        this.data.add(data);
    }

    private FlowEmitter(D... data) {
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
        this.listener = listener;
    }

    @Override
    public void emit(D data, FlowSession trans) {
        this.listener.handle(data, trans);
    }

    @Override
    public void start(FlowSession trans) {
        FlowSession actualSession = trans == null ? new FlowSession() : trans;
        for (D d : this.data) {
            this.emit(d, actualSession);
        }
    }
}
