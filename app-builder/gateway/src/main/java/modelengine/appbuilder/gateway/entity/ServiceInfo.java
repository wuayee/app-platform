/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.appbuilder.gateway.entity;

import com.alibaba.nacos.api.naming.pojo.Instance;

import lombok.Data;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 表示刷新信息的类。
 *
 * @author 方誉州
 * @since 2025-01-13
 */
@Data
public class ServiceInfo {
    private List<Instance> instances;
    private AtomicInteger curOmsIndex;
    private volatile long lastRefreshTime;

    /**
     * 构造函数。
     *
     * @param instances 表示实例集合的 {@link List}{@code <}{@link Instance}{@code >}。
     */
    public ServiceInfo(List<Instance> instances) {
        this.instances = instances;
        this.curOmsIndex = new AtomicInteger(-1);
        this.lastRefreshTime = -1L;
    }
}
