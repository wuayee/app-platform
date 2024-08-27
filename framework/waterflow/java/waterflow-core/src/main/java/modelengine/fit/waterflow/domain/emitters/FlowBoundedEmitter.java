/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.domain.emitters;

import modelengine.fit.waterflow.domain.context.FlowSession;
import modelengine.fit.waterflow.domain.stream.reactive.Publisher;
import modelengine.fit.waterflow.domain.utils.FlowDebug;
import modelengine.fit.waterflow.domain.utils.UUIDUtil;
import modelengine.fitframework.model.Tuple;
import modelengine.fitframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 优先流的发射器
 *
 * @author xiafei
 * @since 1.0
 */
public class FlowBoundedEmitter<D> extends FlowEmitter<D> {
    /**
     * 是否是有界数据流的KEY
     */
    public static final String IS_BOUNDED = "_isBounded";

    /**
     * 是否是有界数据流完成的KEY
     */
    public static final String IS_BOUNDED_COMPLETE = "_isBoundedComplete";

    /**
     * 是否是有界数据流错误的KEY
     */
    public static final String IS_BOUNDED_ERROR = "_isBoundedError";

    /**
     * 有界数据流错误信息的KEY
     */
    public static final String BOUNDED_ERROR = "_boundedError";

    /**
     * 有界数据流的会话ID
     */
    public static final String BOUNDED_SESSION_ID = "_boundedSessionId";

    private List<Tuple> dataSessions = new ArrayList<>();

    protected FlowBoundedEmitter(D data) {
        super(data);
    }

    protected FlowBoundedEmitter(D... data) {
        super(data);
    }

    /**
     * 构造一个mono类型的发布器
     *
     * @param data 待发布的批量数据
     * @param <I> 待发布的数据类型
     * @return 构造一个数据发布器
     */
    public static <I> FlowBoundedEmitter<I> mono(I data) {
        return new FlowBoundedEmitter<>(data);
    }

    /**
     * 构造一个flux类型的发布器
     *
     * @param data 待发布的批量数据
     * @param <I> 待发布的数据类型
     * @return 构造一个批量数据发布器
     */
    public static <I> FlowBoundedEmitter<I> flux(I... data) {
        return new FlowBoundedEmitter<>(data);
    }

    /**
     * 从已有的发射器创建一个新的发射器
     *
     * @param emitter 已有的发射器
     * @param <I> 待发布的数据类型
     * @return 新的发射器
     */
    public static <I> FlowBoundedEmitter<I> from(Emitter<I, FlowSession> emitter) {
        FlowBoundedEmitter<I> cachedEmitter = new FlowBoundedEmitter<>();
        EmitterListener<I, FlowSession> emitterListener = (data, token) -> {
            FlowSession flowSession = new FlowSession(token);
            cachedEmitter.emit(data, flowSession);
        };
        emitter.register(emitterListener);
        return cachedEmitter;
    }

    @Override
    public void register(EmitterListener<D, FlowSession> listener) {
        super.register(listener);
        startOnRegister();
    }

    private synchronized void startOnRegister() {
        if (this.dataSessions.isEmpty()) {
            return;
        }
        FlowDebug.log("startOnRegister");
        this.dataSessions.forEach(tuple -> {
            this.emit(ObjectUtils.cast(tuple.get(0).orElse(null)), ObjectUtils.cast(tuple.get(1).orElse(null)));
        });
        this.dataSessions.clear();
    }

    @Override
    public synchronized void start(FlowSession session) {
        FlowDebug.log(session, "start");
        this.setBounded(session);
        super.start(session);
        FlowSession newSession = new FlowSession(session);
        setSystem(newSession);
        setBoundedComplete(newSession);
        this.emit(null, newSession);
    }

    @Override
    public void emit(D data, FlowSession session) {
        this.setBounded(session);
        if (super.listeners.isEmpty()) {
            dataSessions.add(Tuple.duet(data, session));
            return;
        }
        super.emit(data, session);
    }

    private void setBounded(FlowSession session) {
        Optional.ofNullable(session).ifPresent(flowSession -> flowSession.setInnerState(IS_BOUNDED, true));
    }

    private void setSystem(FlowSession session) {
        Optional.ofNullable(session).ifPresent(flowSession -> {
            flowSession.setInnerState(Publisher.SESSION_TRACE_ID, UUIDUtil.uuid());
            flowSession.setInnerState(Publisher.IS_SYSTEM, true);
        });
    }

    private void setBoundedComplete(FlowSession session) {
        Optional.ofNullable(session).ifPresent(flowSession -> flowSession.setInnerState(IS_BOUNDED_COMPLETE, true));
    }
}
