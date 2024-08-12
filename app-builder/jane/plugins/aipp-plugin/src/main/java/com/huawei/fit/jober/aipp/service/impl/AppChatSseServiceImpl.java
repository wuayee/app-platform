/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.service.impl;

import com.huawei.fit.jober.aipp.common.exception.AippErrCode;
import com.huawei.fit.jober.aipp.common.exception.AippException;
import com.huawei.fit.jober.aipp.service.AippLogService;
import com.huawei.fit.jober.aipp.service.AppChatSseService;
import com.huawei.fit.jober.aipp.util.AippLogUtils;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.flowable.Emitter;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.util.StringUtils;

import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

/**
 * {@link AppChatSseService} 的默认实现
 *
 * @author 邬涨财
 * @since 2024-07-28
 */
@Component
@RequiredArgsConstructor
public class AppChatSseServiceImpl implements AppChatSseService {
    private static final Logger log = Logger.get(AppChatSseServiceImpl.class);

    private final Map<String, Emitter<Object>> emitterMap = new ConcurrentHashMap<>();
    private final Map<String, CountDownLatch> latchMap = new ConcurrentHashMap<>();

    private final AippLogService logService;

    @Override
    public void addEmitter(String instanceId, Emitter<Object> emitter, CountDownLatch latch) {
        this.emitterMap.put(instanceId, emitter);
        this.latchMap.put(instanceId, latch);
    }

    @Override
    public void removeEmitter(String instanceId) {
        this.emitterMap.remove(instanceId);
        Optional.ofNullable(this.latchMap.remove(instanceId)).ifPresent(CountDownLatch::countDown);
    }

    @Override
    public Optional<Emitter<Object>> getEmitter(String instanceId) {
        return Optional.ofNullable(this.emitterMap.get(instanceId));
    }

    @Override
    public void send(String instanceId, Object data) {
        if (data != null) {
            this.getEmitter(instanceId).ifPresent(e -> e.emit(data));
        }
    }

    @Override
    public void sendLastData(String instanceId, Object data) {
        this.send(instanceId, data);
        this.getEmitter(instanceId).ifPresent(Emitter::complete);
        this.removeEmitter(instanceId);
    }

    @Override
    public void sendToAncestor(String instanceId, Object data) {
        String processedInstanceId = this.getProcessedInstanceId(instanceId);
        this.send(processedInstanceId, data);
    }

    @Override
    public void sendToAncestorLastData(String instanceId, Object data) {
        String processedInstanceId = this.getProcessedInstanceId(instanceId);
        this.sendLastData(processedInstanceId, data);
    }

    private String getProcessedInstanceId(String instanceId) {
        String path = this.logService.getParentPath(instanceId);
        if (StringUtils.isNotEmpty(path)) {
            return path.split(AippLogUtils.PATH_DELIMITER)[1];
        }
        return instanceId;
    }

    @Override
    public void latchAwait(String instanceId) {
        Optional.ofNullable(this.latchMap.get(instanceId)).ifPresent(latch -> {
            try {
                latch.await();
            } catch (InterruptedException e) {
                log.error("The instance {0} respond error!", instanceId);
                throw new AippException(AippErrCode.APP_CHAT_WAIT_RESPONSE_ERROR);
            }
        });
    }
}
