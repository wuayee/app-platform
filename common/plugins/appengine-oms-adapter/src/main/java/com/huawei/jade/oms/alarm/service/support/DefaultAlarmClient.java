/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.oms.alarm.service.support;

import static com.huawei.jade.oms.alarm.util.Constants.ADD_EVENT_URI;
import static com.huawei.jade.oms.alarm.util.Constants.OK;
import static com.huawei.jade.oms.alarm.util.Constants.REGISTER_EVENT_DEFINE_URI;
import static com.huawei.jade.oms.util.Constants.OMS_MONITOR_NAME;

import com.huawei.fit.http.protocol.HttpRequestMethod;
import com.huawei.fitframework.annotation.Component;
import com.huawei.jade.oms.OmsClient;
import com.huawei.jade.oms.alarm.service.AlarmClient;
import com.huawei.jade.oms.alarm.vo.EventInfo;
import com.huawei.jade.oms.alarm.vo.RegisterEventDefinitionReq;
import com.huawei.jade.oms.response.ResultVo;

import java.util.List;

/**
 * 告警信息上报接口的默认实现。
 *
 * @author 何嘉斌
 * @since 2024-12-05
 */
@Component
public class DefaultAlarmClient implements AlarmClient {
    private final OmsClient client;

    public DefaultAlarmClient(OmsClient client) {
        this.client = client;
    }

    @Override
    public boolean registerEventDefinition(RegisterEventDefinitionReq req) {
        ResultVo<Void> result =
                client.executeJson(OMS_MONITOR_NAME, HttpRequestMethod.POST, REGISTER_EVENT_DEFINE_URI, req, Void.class);
        return result != null && result.getCode().equals(OK);
    }

    @Override
    public boolean sendEvents(List<EventInfo> alarmInfos) {
        ResultVo<Boolean> result =
                client.executeJson(OMS_MONITOR_NAME, HttpRequestMethod.POST, ADD_EVENT_URI, alarmInfos, Boolean.class);
        return result != null && result.getData();
    }
}