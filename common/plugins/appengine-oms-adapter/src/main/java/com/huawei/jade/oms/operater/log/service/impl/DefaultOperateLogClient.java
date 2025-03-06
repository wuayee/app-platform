/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.oms.operater.log.service.impl;

import static com.huawei.jade.oms.operater.log.util.Constants.REGISTER_INTERNATION_URI;
import static com.huawei.jade.oms.operater.log.util.Constants.REGISTER_LOGS_URI;
import static com.huawei.jade.oms.util.Constants.OMS_FRAMEWORK_NAME;

import com.huawei.fit.http.protocol.HttpRequestMethod;
import com.huawei.fitframework.annotation.Component;
import com.huawei.jade.oms.OmsClient;
import com.huawei.jade.oms.operater.log.service.OperateLogClient;
import com.huawei.jade.oms.operater.log.vo.LogI18N;
import com.huawei.jade.oms.operater.log.vo.OperateLog;
import com.huawei.jade.oms.response.ResultVo;

import java.util.List;

/**
 * 表示 OM 操作日志上报接口的默认实现。
 *
 * @author 何嘉斌
 * @since 2024-11-28
 */
@Component
public class DefaultOperateLogClient implements OperateLogClient {
    private final OmsClient client;

    public DefaultOperateLogClient(OmsClient client) {
        this.client = client;
    }

    @Override
    public ResultVo<Integer> registerLogs(List<OperateLog> logs) {
        return client.executeJson(OMS_FRAMEWORK_NAME, HttpRequestMethod.POST, REGISTER_LOGS_URI, logs, Integer.class);
    }

    @Override
    public ResultVo<Boolean> registryInternational(List<LogI18N> logI18ns) {
        return client.executeJson(OMS_FRAMEWORK_NAME,
                HttpRequestMethod.POST,
                REGISTER_INTERNATION_URI,
                logI18ns,
                Boolean.class);
    }
}