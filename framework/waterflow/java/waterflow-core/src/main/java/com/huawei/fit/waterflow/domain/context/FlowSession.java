/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.domain.context;

import com.huawei.fit.waterflow.domain.utils.IdGenerator;

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
public class FlowSession extends IdGenerator {
    /**
     * 用户自定义session上下文状态数据
     */
    private final Map<String, Object> states = new HashMap<>();

    /**
     * 内置session上下文状态数据
     */
    private final Map<String, Object> interStates = new HashMap<>();

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
     * session的状态数据
     *
     * @return map类型的状态数据
     */
    public Map<String, Object> states() {
        return this.states;
    }

    /**
     * session的内置状态数据
     *
     * @return map类型的状态数据
     */
    public Map<String, Object> interStates() {
        return this.interStates;
    }

    /**
     * 该session的key
     *
     * @return key
     */
    public Object keyBy() {
        return this.keyBy;
    }
}
