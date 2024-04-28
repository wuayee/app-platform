/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.domain.context;

import com.huawei.fit.waterflow.domain.utils.IdGenerator;
import com.huawei.fitframework.util.MapBuilder;
import com.huawei.fitframework.util.ObjectUtils;

import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * 流程实例运行标识
 * offer数据后该流程生成的context的session id唯一
 *
 * @author y00679285
 * @since 1.0
 */
public class FlowSession extends IdGenerator implements StateContext {
    /**
     * 用户自定义上下文map
     */
    private static final String CUSTOM_STATE = "custom_state";

    /**
     * 流程内置上下文map
     */
    private static final String INNER_STATE = "inner_state";

    /**
     * session上下文状态数据
     */
    private final Map<String, Map<String, Object>> states = MapBuilder.<String, Map<String, Object>>get()
            .put(CUSTOM_STATE, new HashMap<>())
            .put(INNER_STATE, new HashMap<>())
            .build();

    @Setter
    private Object keyBy = null;

    /**
     * FlowTrans
     */
    public FlowSession() {
    }

    public FlowSession(String id) {
        super(id);
    }

    public FlowSession(FlowSession session) {
        super(session.getId());
        this.states.putAll(session.states);
        this.keyBy = session.keyBy;
    }

    /**
     * 两个session是否相同
     *
     * @param session 待判定的session
     * @return 是否相同
     */
    public boolean equals(FlowSession session) {
        return this.id.equals(session.id);
    }

    /**
     * 该session的key
     *
     * @return key
     */
    public Object keyBy() {
        return this.keyBy;
    }

    /**
     * 获取指定key的自定义上下文数据
     *
     * @param key 指定key
     * @return 上下文数据
     */
    @Override
    public <R> R getState(String key) {
        return ObjectUtils.cast(this.states.get(CUSTOM_STATE).get(key));
    }

    /**
     * 设置自定义上下文数据
     *
     * @param key 指定key
     * @param value 待设置的上下文数据
     */
    @Override
    public void setState(String key, Object value) {
        this.states.get(CUSTOM_STATE).put(key, value);
    }

    /**
     * 获取指定key的内置上下文数据
     *
     * @param key 指定key
     * @return 上下文数据
     */
    public <R> R getInnerState(String key) {
        return ObjectUtils.cast(this.states.get(INNER_STATE).get(key));
    }

    /**
     * 设置内置上下文数据
     *
     * @param key 指定key
     * @param value 待设置的上下文数据
     */
    public void setInnerState(String key, Object value) {
        this.states.get(INNER_STATE).put(key, value);
    }
}
