/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.engine.operators.patterns;

import com.huawei.fit.waterflow.domain.context.FlowSession;
import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.fitframework.util.StringUtils;
import com.huawei.jade.fel.core.Pattern;
import com.huawei.jade.fel.core.memory.Memory;
import com.huawei.jade.fel.core.util.Tip;
import com.huawei.jade.fel.engine.util.StateKey;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

/**
 * 历史记录键值对生成器。
 *
 * @param <I> 表示输入数据类型。
 * @author 刘信宏
 * @since 2024-04-22
 */
public class HistoryTipper<I> implements SyncTipper<I> {
    private final Map<String, Object> args;
    private final String historyKey;

    public HistoryTipper(String historyKey) {
        this(historyKey, Collections.emptyMap());
    }

    private HistoryTipper(String historyKey, Map<String, Object> args) {
        this.historyKey = Validation.notBlank(historyKey, "The history key cannot be blank.");
        this.args = Validation.notNull(args, "The args cannot be null.");
    }

    @Override
    public Tip invoke(I input) {
        FlowSession session = ObjectUtils.cast(this.args.get(StateKey.FLOW_SESSION));
        Validation.notNull(session, "The session cannot be null.");
        Memory memory = session.getInnerState(StateKey.HISTORY_OBJ);
        String memoryStr = Optional.ofNullable(memory).map(Memory::text).orElse(StringUtils.EMPTY);
        return Tip.from(historyKey, memoryStr);
    }

    @Override
    public Pattern<I, Tip> bind(Map<String, Object> args) {
        return new HistoryTipper<>(this.historyKey, args);
    }
}
