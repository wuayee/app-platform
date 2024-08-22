/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fit.waterflow.domain.flow;

import modelengine.fit.waterflow.domain.context.FlowSession;
import modelengine.fit.waterflow.domain.emitters.Emitter;
import modelengine.fit.waterflow.domain.states.Activity;
import modelengine.fit.waterflow.domain.states.State;
import modelengine.fit.waterflow.domain.stream.reactive.Processor;
import modelengine.fit.waterflow.domain.stream.reactive.Publisher;
import modelengine.fit.waterflow.domain.stream.reactive.Subscriber;
import modelengine.fit.waterflow.domain.utils.IdGenerator;
import modelengine.fitframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * FitStream外的一层flow wrapper
 * 通过flow将fit stream的所有流操作按照functional++的方式串联起来
 * 通过泛型推演让编程效率提高，并且代码更为整洁
 *
 * @param <D> stream其实数据类型，用于offer数据时限定，该类型会被第一处理函数推导出来
 * @since 1.0
 */
public abstract class Flow<D> extends IdGenerator {
    /**
     * 每条流除了起始点和终结点，还有若干中间节点：processor
     */
    protected final List<Processor<?, ?>> nodes = new ArrayList<>();

    /**
     * 每一条流都有一个起始点：publisher
     */
    protected Publisher<D> start;

    /**
     * 每一条流都有一个终结点：subscriber
     */
    protected Subscriber end;

    private final Map<String, Activity> tagNodes = new HashMap<>();

    /**
     * 获取结束节点
     *
     * @return 结束节点
     */
    public Subscriber end() {
        return this.end;
    }

    /**
     * 获取启动节点
     *
     * @return 启动节点
     */
    public Publisher<D> start() {
        return this.start;
    }

    /**
     * 设置结束节点
     *
     * @param end 待设置的结束节点
     */
    public void setEnd(Subscriber end) {
        this.end = end;
    }

    /**
     * 为给定的节点设置id，使之成为一个named的具名节点
     *
     * @param id 待设置的id
     * @param activity 待设置的节点
     */
    public void tagNode(String id, Activity activity) {
        this.tagNodes.put(id, activity);
    }

    /**
     * 从一个named节点侦听一个外部数据源，侦听后将得到外部数据源的emit数据
     *
     * @param id 节点id
     * @param publisher 外部数据源
     */
    public void offer(String id, Emitter publisher) {
        ObjectUtils.<State>cast(this.tagNodes.get(id)).offer(publisher);
    }

    /**
     * 传入单条数据进入stream处理
     *
     * @param data 待处理的数据
     * @return 流程实例事务ID
     */
    public String offer(D data) {
        return this.start.offer(data);
    }

    /**
     * 传入单条数据进入stream处理，并指明该数据所属的session
     *
     * @param data 待处理的数据
     * @param session 数据所属的session
     * @return 提交后该数据对应的trace
     */
    public String offer(D data, FlowSession session) {
        D[] array = ObjectUtils.cast(new Object[1]);
        array[0] = data;
        return this.offer(array, session);
    }

    /**
     * 传入多条数据处理【数组参数】
     *
     * @param data 待处理数据
     * @return 流程实例事务ID
     */
    public String offer(D[] data) {
        return this.start.offer(data);
    }

    /**
     * 传入多条数据处理【数组参数】
     *
     * @param data 待处理数据
     * @param session 数据所属的session
     * @return 流程实例事务ID
     */
    public String offer(D[] data, FlowSession session) {
        FlowSession flowSession = copyFlowSession(session);
        return this.start.offer(data, flowSession);
    }

    /**
     * 从一个named具名节点注入一个数据
     *
     * @param id 节点id
     * @param data 需要注入的数据
     * @return 返回注入后的traceid
     */
    public String offer(String id, Object data) {
        return ObjectUtils.<State>cast(this.tagNodes.get(id)).publisher().offer(data);
    }

    /**
     * 从一个named具名节点注入一个数据
     *
     * @param id 节点id
     * @param data 需要注入的数据
     * @param token 用于表示数据归属的session
     * @return 返回注入后的traceid
     */
    public String offer(String id, Object data, String token) {
        return ObjectUtils.<State>cast(this.tagNodes.get(id)).publisher().offer(data, new FlowSession(token));
    }

    /**
     * 从一个named具名节点注入一个数据
     *
     * @param id 节点id
     * @param data 需要注入的数据数组
     * @param session 用于表示数据归属的session
     * @return 返回注入后的traceid
     */
    public String offer(String id, Object[] data, FlowSession session) {
        FlowSession flowSession = copyFlowSession(session);
        return ObjectUtils.<State>cast(this.tagNodes.get(id)).publisher().offer(data, flowSession);
    }

    /**
     * 通过id获取获取flow中的named的具名节点
     *
     * @param id node的id
     * @return 获取到的节点
     */
    public Activity getNode(String id) {
        return this.tagNodes.get(id);
    }

    /**
     * 获取flow的所有nodes
     *
     * @return node列表
     */
    public List<Processor<?, ?>> nodes() {
        return this.nodes;
    }

    private static FlowSession copyFlowSession(FlowSession session) {
        return (session == null) ? new FlowSession() : new FlowSession(session);
    }
}
