/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.entity;

import java.time.LocalDateTime;

/**
 * 流程节点上下文信息对象。
 *
 * @author 陈镕希 c00572808
 * @since 2024-05-21
 */
public class FlowContext {
    /**
     * 表示流程实例trace id的 {@link String}。
     */
    private String traceId;

    /**
     * 上下文当前的状态值
     */
    private String status;

    /**
     * 当前context创建时间
     */
    private LocalDateTime createAt;

    /**
     * 当前context更新时间
     */
    private LocalDateTime updateAt;

    /**
     * 当前context处理完成时间
     */
    private LocalDateTime archivedAt;

    /**
     * 流程节点上下文信息对象的无参构造方法。
     */
    public FlowContext() {
    }

    /**
     * 流程节点上下文信息对象的全参构造方法。
     *
     * @param status 流程节点状态信息的 {@link String}。
     * @param createAt 流程节点创建时间信息的 {@link LocalDateTime}。
     * @param updateAt 流程节点最后更新时间信息的 {@link LocalDateTime}。
     * @param archivedAt 流程节点结束时间信息的 {@link LocalDateTime}。
     */
    public FlowContext(String status, LocalDateTime createAt, LocalDateTime updateAt, LocalDateTime archivedAt) {
        this.status = status;
        this.createAt = createAt;
        this.updateAt = updateAt;
        this.archivedAt = archivedAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    public LocalDateTime getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(LocalDateTime updateAt) {
        this.updateAt = updateAt;
    }

    public LocalDateTime getArchivedAt() {
        return archivedAt;
    }

    public void setArchivedAt(LocalDateTime archivedAt) {
        this.archivedAt = archivedAt;
    }
}
