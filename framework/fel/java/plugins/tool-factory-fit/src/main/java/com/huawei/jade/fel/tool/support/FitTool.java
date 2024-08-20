/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.tool.support;

import static com.huawei.fitframework.inspection.Validation.notBlank;
import static com.huawei.fitframework.inspection.Validation.notNull;
import static com.huawei.fitframework.util.ObjectUtils.cast;

import com.huawei.fitframework.broker.CommunicationType;
import com.huawei.fitframework.broker.client.BrokerClient;
import com.huawei.fitframework.broker.client.Router;
import com.huawei.fitframework.broker.client.filter.route.FitableIdFilter;
import com.huawei.fitframework.serialization.ObjectSerializer;
import com.huawei.fitframework.util.MapUtils;
import com.huawei.fitframework.util.StringUtils;

import java.util.Map;

/**
 * 表示 {@link com.huawei.jade.fel.tool.Tool} 的 FIT 调用实现。
 *
 * @author 王攀博
 * @since 2024-04-24
 */
public class FitTool extends AbstractTool {
    /**
     * 表示 FIT 调用工具的类型。
     */
    public static final String TYPE = "FIT";

    private final BrokerClient brokerClient;

    /**
     * 通过 FIT 调用代理、Json 序列化器和工具元数据信息来初始化 {@link FitTool} 的新实例。
     *
     * @param brokerClient 表示 FIT 调用代理的的 {@link BrokerClient}。
     * @param serializer 表示 Json 序列化器的 {@link ObjectSerializer}。
     * @param info 表示工具的基本信息的 {@link Info}。
     * @param metadata 表示工具元数据信息的 {@link Metadata}。
     * @throws IllegalArgumentException 当 {@code brokerClient}、{@code serializer}、{@code itemInfo} 或
     * {@code metadata} 为 {@code null} 时。
     */
    public FitTool(BrokerClient brokerClient, ObjectSerializer serializer, Info info, Metadata metadata) {
        super(serializer, info, metadata);
        this.brokerClient = notNull(brokerClient, "The broker client cannot be null.");
    }

    @Override
    public Object execute(Object... args) {
        Map<String, Object> runnable = cast(this.info().runnable().get(TYPE));
        if (MapUtils.isEmpty(runnable)) {
            throw new IllegalStateException("No runnable info. [type=FIT]");
        }
        String genericableId = notBlank(cast(runnable.get("genericableId")), "No genericable id in runnable info.");
        Router router = this.brokerClient.getRouter(genericableId);
        String fitableId = cast(runnable.get("fitableId"));
        Router.Filter filter = null;
        if (StringUtils.isNotBlank(fitableId)) {
            filter = new FitableIdFilter(fitableId);
        }
        return router.route(filter).communicationType(CommunicationType.ASYNC).invoke(args);
    }
}
