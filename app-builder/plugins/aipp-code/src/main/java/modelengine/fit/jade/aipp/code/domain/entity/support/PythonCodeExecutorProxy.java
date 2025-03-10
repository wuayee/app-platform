/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.code.domain.entity.support;

import modelengine.fit.jade.aipp.code.domain.entity.CodeExecutor;
import modelengine.fit.jade.aipp.code.domain.entity.ProgrammingLanguage;
import modelengine.fitframework.broker.CommunicationType;
import modelengine.fitframework.broker.client.BrokerClient;
import modelengine.fitframework.broker.client.filter.route.FitableIdFilter;
import modelengine.fitframework.serialization.ObjectSerializer;

import java.util.Map;

/**
 * 表示 python 代码执行器代理。
 *
 * @author 易文渊
 * @author 何嘉斌
 * @since 2024-10-14
 */
public class PythonCodeExecutorProxy implements CodeExecutor {
    private final BrokerClient brokerClient;
    private final ObjectSerializer serializer;

    public PythonCodeExecutorProxy(BrokerClient brokerClient, ObjectSerializer serializer) {
        this.brokerClient = brokerClient;
        this.serializer = serializer;
    }

    @Override
    public Object run(Map<String, Object> args, String code) {
        String ret = this.brokerClient.getRouter("CodeNode.tool")
                .route(new FitableIdFilter("Python_REPL"))
                .communicationType(CommunicationType.ASYNC)
                .invoke(args, code);
        return this.serializer.deserialize(ret, Object.class);
    }

    @Override
    public ProgrammingLanguage language() {
        return ProgrammingLanguage.PYTHON;
    }
}