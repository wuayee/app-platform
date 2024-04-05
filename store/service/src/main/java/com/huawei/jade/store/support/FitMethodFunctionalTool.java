/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.support;

import static com.huawei.fitframework.inspection.Validation.notBlank;
import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fitframework.broker.client.BrokerClient;
import com.huawei.fitframework.util.GenericableUtils;

import java.lang.reflect.Method;

/**
 * 表示函数工具的带本地方法的 FIT 服务实现。
 *
 * @author 季聿阶
 * @since 2024-04-05
 */
public class FitMethodFunctionalTool extends AbstractMethodFunctionalTool {
    private final BrokerClient brokerClient;
    private final String genericableId;

    /**
     * 通过 FIT 调用客户端和本地方法来初始化 {@link AbstractTool} 的新实例。
     *
     * @param brokerClient 表示 FIT 调用客户端的 {@link BrokerClient}。
     * @param method 表示本地方法的 {@link Method}。
     */
    public FitMethodFunctionalTool(BrokerClient brokerClient, Method method) {
        super(GenericableUtils.getGenericableId(method), method);
        this.brokerClient = notNull(brokerClient, "The broker client cannot be null.");
        this.genericableId = notBlank(GenericableUtils.getGenericableId(method), "The genericable id cannot be blank.");
    }

    @Override
    public Object call(Object... args) {
        return this.brokerClient.getRouter(this.genericableId, this.getMethod()).route().invoke(args);
    }
}
