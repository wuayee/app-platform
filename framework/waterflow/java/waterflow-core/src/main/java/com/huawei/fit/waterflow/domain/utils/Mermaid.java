/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.waterflow.domain.utils;

import com.huawei.fit.waterflow.domain.flow.Flow;
import com.huawei.fit.waterflow.domain.stream.reactive.Processor;
import com.huawei.fit.waterflow.domain.stream.reactive.Publisher;
import com.huawei.fit.waterflow.domain.stream.reactive.Subscriber;
import com.huawei.fit.waterflow.domain.stream.reactive.Subscription;
import com.huawei.fitframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 生成Mermaid绘图
 *
 * @since 1.0
 */
public class Mermaid {
    private static final String TO_ARROW = "-->";

    private static final String LINE_BREAK = "\n";

    private static final String START = "st((Start))";

    private static final String END = "e((End))";

    private static final String START_SHORT = "st";

    private final String meta;

    private final Flow flow;

    private final StringBuilder builder;

    private final HashMap<Publisher, String> named;

    private final HashMap pairs;

    private final AtomicInteger counter;

    /**
     * 构造Mermaid
     *
     * @param flow 要处理的flow
     */
    public Mermaid(Flow flow) {
        this.builder = new StringBuilder();
        this.named = new HashMap<>();
        this.pairs = new HashMap();
        this.flow = flow;
        Publisher start = flow.start();
        builder.append(START + LINE_BREAK);
        named.put(start, START_SHORT);
        this.counter = new AtomicInteger();
        buildMermaid(start);
        meta = builder.toString();
    }

    /**
     * 获取元数据
     *
     * @return 元数据
     */
    public String get() {
        return meta;
    }

    private void buildMermaid(Publisher from) {
        for (Object sp : from.getSubscriptions()) {
            Subscription subscription = ObjectUtils.cast(sp);
            Subscriber subscriber = subscription.getTo();
            if (subscriber instanceof Processor) {
                buildNode(from, (Processor) subscriber);
            } else {
                buildEnd(from);
            }
        }
    }

    private void buildNode(Publisher from, Processor to) {
        // dont build twice
        if (pairs.get(from) == to) {
            return;
        }
        // ignored node, like branch
        if ("".equals(to.display())) {
            named.put(to, named.get(from));
            buildMermaid(to);
            return;
        }
        String name = named.get(to);
        if (name == null) {
            name = "n" + counter.getAndIncrement();
            named.put(to, name);
            if (flow.getNode(to.getId()) == null) {
                builder.append(named.get(from) + TO_ARROW + name + to.display());
            } else {
                builder.append(named.get(from) + TO_ARROW + name + "(" + to.getId() + ")");
            }
        } else {
            builder.append(named.get(from) + TO_ARROW + name);
        }
        pairs.put(from, to);
        builder.append(LINE_BREAK);
        buildMermaid(to);
    }

    private void buildEnd(Publisher from) {
        if (pairs.get(from) == flow.end()) {
            return;
        }
        builder.append(named.get(from) + TO_ARROW + END);
        builder.append(LINE_BREAK);
        pairs.put(from, flow.end());
    }
}
