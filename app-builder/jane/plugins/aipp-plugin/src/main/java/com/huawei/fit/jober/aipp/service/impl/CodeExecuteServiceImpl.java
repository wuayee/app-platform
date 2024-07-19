/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.service.impl;

import com.huawei.fit.jober.aipp.dto.CodeExecuteResDto;
import com.huawei.fit.jober.aipp.service.CodeExecuteService;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fit;
import com.huawei.fitframework.broker.client.BrokerClient;
import com.huawei.fitframework.broker.client.filter.route.FitableIdFilter;
import com.huawei.fitframework.serialization.ObjectSerializer;
import com.huawei.fitframework.util.MapBuilder;
import com.huawei.fitframework.util.StringUtils;

import java.util.Map;

/**
 * CodeExecuteService接口的实现类
 *
 * @author f00881613
 * @since 2024-07-11
 */
@Component
public class CodeExecuteServiceImpl implements CodeExecuteService {
    private static final Map<String, FitInfo> EXECUTORMAPPER;

    static {
        MapBuilder<String, FitInfo> mapBuilder = MapBuilder.get();
        mapBuilder.put("python", new FitInfo("CodeNode.tool", "Python_REPL"));
        EXECUTORMAPPER = mapBuilder.build();
    }

    private final BrokerClient brokerClient;
    private final ObjectSerializer serializer;

    /**
     * CodeExecuteServiceImpl构造函数
     *
     * @param brokerClient 表示fitable调用的的接口的{@link BrokerClient}
     * @param serializer 表示对象序列化的序列化器的{@link ObjectSerializer}
     */
    public CodeExecuteServiceImpl(BrokerClient brokerClient, @Fit(alias = "json") ObjectSerializer serializer) {
        this.brokerClient = brokerClient;
        this.serializer = serializer;
    }

    @Override
    public CodeExecuteResDto run(Map<String, Object> args, String code, String language) {
        FitInfo executor = EXECUTORMAPPER.get(StringUtils.toLowerCase(language));
        if (executor == null) {
            return CodeExecuteResDto.builder()
                    .isOk(false)
                    .msg("Not supported language: " + language)
                    .build();
        }

        String ret = this.brokerClient.getRouter(executor.genericableId)
                .route(new FitableIdFilter(executor.fitableId))
                .invoke(args, code);

        return serializer.deserialize(ret, CodeExecuteResDto.class);
    }

    private static class FitInfo {
        private final String genericableId;
        private final String fitableId;

        public FitInfo(String genericableId, String fitableId) {
            this.genericableId = genericableId;
            this.fitableId = fitableId;
        }
    }
}
