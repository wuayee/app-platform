/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.domain.context;

import lombok.Setter;
import modelengine.fit.waterflow.domain.utils.IdGenerator;
import modelengine.fit.waterflow.domain.utils.UUIDUtil;
import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.ObjectUtils;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 流程实例运行标识。
 * offer 数据后该流程生成的 context 的会话标识唯一。
 *
 * @author 杨祥宇
 * @since 1.0
 */
public class FlowSession extends IdGenerator implements StateContext {
    /**
     * 用户自定义上下文 map。
     */
    private static final String CUSTOM_STATE = "custom_state";

    /**
     * 流程内置上下文 map。
     */
    private static final String INNER_STATE = "inner_state";

    /**
     * 会话上下文状态数据。
     */
    private final Map<String, Map<String, Object>> states = MapBuilder.<String, Map<String, Object>>get()
            .put(CUSTOM_STATE, new ConcurrentHashMap<>())
            .put(INNER_STATE, new ConcurrentHashMap<>())
            .build();

    @Setter
    private Object keyBy = null;

    /**
     * 构造一个 {@link FlowSession} 实例。
     */
    public FlowSession() {
    }

    /**
     * 构造一个 {@link FlowSession} 实例，拷贝运行实例的状态数据。
     *
     * @param id 表示被拷贝的运行实例信息唯一标识的 {@link String}。
     */
    public FlowSession(String id) {
        super(id);
    }

    /**
     * 拷贝运行实例的状态数据。
     *
     * @param session 表示被拷贝的运行实例信息的 {@link FlowSession}。
     */
    public FlowSession(FlowSession session) {
        super(Optional.ofNullable(session).map(IdGenerator::getId).orElseGet(UUIDUtil::uuid));
        this.copyState(session);
        this.keyBy = Optional.ofNullable(session).map(FlowSession::keyBy).orElse(null);
    }

    /**
     * 拷贝运行实例的状态数据。
     *
     * @param session 表示被拷贝的运行实例信息的 {@link FlowSession}。
     */
    public void copySessionState(FlowSession session) {
        this.copyState(session);
    }

    /**
     * 判断两个会话是否相同。
     *
     * @param session 表示待判定的 {@link FlowSession}。
     * @return 表示会话是否相同的 {@code boolean}。
     */
    public boolean equals(FlowSession session) {
        return this.id.equals(session.id);
    }

    /**
     * 获取会话的关键标识。
     *
     * @return 表示关键标识的 {@link Object}。
     */
    public Object keyBy() {
        return this.keyBy;
    }

    /**
     * 获取指定键的自定义上下文数据。
     *
     * @param key 表示键的 {@link String}。
     * @return 表示上下文数据的 {@link R}。
     */
    @Override
    public <R> R getState(String key) {
        return ObjectUtils.cast(this.states.get(CUSTOM_STATE).get(key));
    }

    /**
     * 设置自定义上下文数据。
     *
     * @param key 表示键的 {@link String}。
     * @param value 表示待设置的上下文数据的 {@link Object}。
     */
    @Override
    public void setState(String key, Object value) {
        this.states.get(CUSTOM_STATE).put(key, value);
    }

    /**
     * 获取指定键的内置上下文数据。
     *
     * @param key 表示键的 {@link String}。
     * @return 上下文数据 {@link R}。
     */
    public <R> R getInnerState(String key) {
        return ObjectUtils.cast(this.states.get(INNER_STATE).get(key));
    }

    /**
     * 设置内置上下文数据。
     *
     * @param key 表示键的 {@link String}。
     * @param value 表示待设置的上下文数据的 {@link Object}。
     */
    public void setInnerState(String key, Object value) {
        this.states.get(INNER_STATE).put(key, value);
    }

    private void copyState(FlowSession session) {
        if (session != null) {
            this.states.get(CUSTOM_STATE).putAll(session.states.get(CUSTOM_STATE));
            this.states.get(INNER_STATE).putAll(session.states.get(INNER_STATE));
        }
    }
}
