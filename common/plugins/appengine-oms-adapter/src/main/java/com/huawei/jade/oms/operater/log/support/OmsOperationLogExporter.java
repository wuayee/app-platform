/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.oms.operater.log.support;

import static com.huawei.fitframework.util.ObjectUtils.cast;
import static com.huawei.jade.oms.operater.log.util.Constants.DEFAULT_ERROR_MSG;
import static com.huawei.jade.oms.operater.log.util.Constants.FAILED_RESULT;
import static com.huawei.jade.oms.operater.log.util.Constants.FAILED_SUFFIX;
import static com.huawei.jade.oms.operater.log.util.Constants.LOG_TERMINAL;
import static com.huawei.jade.oms.operater.log.util.Constants.MY_SOURCE_NAME;
import static com.huawei.jade.oms.operater.log.util.Constants.OK;
import static com.huawei.jade.oms.operater.log.util.Constants.OPERATION;
import static com.huawei.jade.oms.operater.log.util.Constants.SUCCEED_RESULT;
import static com.huawei.jade.oms.operater.log.util.Constants.SUCCEED_SUFFIX;
import static com.huawei.jade.oms.operater.log.util.Constants.USER_NAME;

import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Initialize;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.parameterization.StringFormatException;
import com.huawei.fitframework.util.IoUtils;
import com.huawei.fitframework.util.MapBuilder;
import com.huawei.fitframework.util.StringUtils;
import com.huawei.jade.oms.operater.log.service.OperateLogClient;
import com.huawei.jade.oms.operater.log.vo.LogI18N;
import com.huawei.jade.oms.operater.log.vo.OperateLog;
import com.huawei.jade.oms.response.ResultVo;
import com.huawei.jade.service.CarverSpanExporter;

import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.sdk.common.CompletableResultCode;
import io.opentelemetry.sdk.trace.data.SpanData;
import io.opentelemetry.sdk.trace.data.StatusData;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 表示 {@link CarverSpanExporter} 的 OMS 操作日志导出器，包含以下功能：
 * <ul>
 *     <li>初始化时，注册国际化信息至 OMS，注册成功则标记导出器状态为可用；</li>
 *     <li>运行过程中，若导出器可用，则将操作 Span 转换为 OMS 操作日志导出至 OMS 后端服务。</li>
 * </ul>
 *
 * @author 易文渊
 * @author 何嘉斌
 * @since 2024-11-19
 */
@Component
public class OmsOperationLogExporter implements CarverSpanExporter {
    private static final Logger log = Logger.get(OmsOperationLogExporter.class);

    private final OmsOperationFlagResolver flagResolver;
    private final OperateLogClient client;
    private final Integer maxRetires = 3;
    private boolean enable = false;

    public OmsOperationLogExporter(OmsOperationFlagResolver flagResolver, OperateLogClient client) {
        this.flagResolver = flagResolver;
        this.client = client;
    }

    @Initialize
    void init() {
        List<LogI18N> i18NList = parseProperties();
        this.enable = false;
        ResultVo<Boolean> result;
        for (int i = 0; i < this.maxRetires; i++) {
            result = this.client.registryInternational(i18NList);
            if (result.getData()) {
                this.enable = true;
                break;
            }
        }
    }

    /**
     * 获取导出器是否有效。
     *
     * @return 表示是否有效的 {@code boolean}。
     */
    public boolean isEnable() {
        return this.enable;
    }

    @Override
    public String name() {
        return "oms.operation.exporter";
    }

    @Override
    public CompletableResultCode export(Collection<SpanData> spans) {
        if (!this.isEnable()) {
            return CompletableResultCode.ofSuccess();
        }
        List<OperateLog> opLogs = spans.stream().map(this::createLog).collect(Collectors.toList());
        if (opLogs.isEmpty()) {
            return CompletableResultCode.ofSuccess();
        }
        ResultVo<Integer> result = this.client.registerLogs(opLogs);
        if (!result.getCode().equals(OK)) {
            log.error("Operation log registration failed.");
        }
        return CompletableResultCode.ofSuccess();
    }

    @Override
    public CompletableResultCode flush() {
        return CompletableResultCode.ofSuccess();
    }

    @Override
    public CompletableResultCode shutdown() {
        return CompletableResultCode.ofSuccess();
    }

    private static List<LogI18N> parseProperties() {
        Map<String, String> propertiesMapping = MapBuilder.<String, String>get()
                .put("zh-cn", "/i18n/messages_zh.properties")
                .put("en-us", "/i18n/messages_en.properties")
                .build();
        return propertiesMapping.entrySet()
                .stream()
                .flatMap(entry -> IoUtils.properties(OmsOperationLogExporter.class,
                                entry.getValue(),
                                StandardCharsets.UTF_8)
                        .entrySet()
                        .stream()
                        .map(property -> new LogI18N(cast(property.getKey()),
                                entry.getKey(),
                                cast(property.getValue()))))
                .collect(Collectors.toList());
    }

    private OperateLog createLog(SpanData span) {
        Attributes attributes = span.getAttributes();
        Map<String, Object> args = span.getAttributes()
                .asMap()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(entry -> entry.getKey().getKey(), Map.Entry::getValue));
        StatusData data = span.getStatus();
        String detail;
        OperateLog logVo = new OperateLog();
        logVo.setOperation(span.getName());
        logVo.setLogType(OPERATION);
        logVo.setSource(MY_SOURCE_NAME);
        logVo.setDatetime(span.getStartEpochNanos() / 1000000);
        logVo.setUsername(attributes.get(USER_NAME));
        logVo.setTerminal(attributes.get(LOG_TERMINAL));
        if (StatusCode.ERROR.equals(data.getStatusCode())) {
            logVo.setResult(FAILED_RESULT);
            args.put("cause", data.getDescription().isEmpty() ? DEFAULT_ERROR_MSG : data.getDescription());
            detail = StringUtils.concat(span.getName(), FAILED_SUFFIX);
        } else {
            logVo.setResult(SUCCEED_RESULT);
            detail = StringUtils.concat(span.getName(), SUCCEED_SUFFIX);
        }
        logVo.setDetail(detail);
        try {
            logVo.setFlag(this.flagResolver.resolve(detail, args));
        } catch (StringFormatException ex) {
            log.error("Flag format failed, operation: [{}]\nSpan args: [{}]\nError message: [{}]",
                    span.getName(),
                    args,
                    ex.getMessage(),
                    ex);
        }
        return logVo;
    }
}