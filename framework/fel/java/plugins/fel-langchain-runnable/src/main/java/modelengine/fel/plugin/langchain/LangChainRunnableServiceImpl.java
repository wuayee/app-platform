/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fel.plugin.langchain;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fitable;
import modelengine.fitframework.broker.client.BrokerClient;
import modelengine.fitframework.broker.client.filter.route.FitableIdFilter;
import modelengine.fitframework.conf.runtime.SerializationFormat;
import modelengine.fitframework.inspection.Validation;
import modelengine.fel.service.langchain.LangChainRunnableService;

import java.util.concurrent.TimeUnit;

/**
 * LangChain Runnable 算子服务的实现。
 *
 * @author 刘信宏
 * @since 2024-06-12
 */
@Component
public class LangChainRunnableServiceImpl implements LangChainRunnableService {
    private static final int INVOKE_TIMEOUT = 30000;

    private final BrokerClient brokerClient;

    public LangChainRunnableServiceImpl(BrokerClient brokerClient) {
        this.brokerClient = Validation.notNull(brokerClient, "The broker client cannot be null.");
    }

    @Override
    @Fitable("modelengine.fel.plugin.langchain.runnable.invoke")
    public Object invoke(String taskId, String fitableId, Object input) {
        return this.brokerClient.getRouter(Validation.notBlank(taskId, "The task id cannot be blank."))
                .route(new FitableIdFilter(Validation.notBlank(fitableId, "The fitable id cannot be blank.")))
                .format(SerializationFormat.CBOR)
                .timeout(INVOKE_TIMEOUT, TimeUnit.MILLISECONDS)
                .invoke(Validation.notNull(input, "The input data cannot be null."));
    }
}
