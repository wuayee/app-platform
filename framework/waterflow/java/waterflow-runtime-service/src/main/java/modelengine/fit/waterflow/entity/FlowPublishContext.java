/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.entity;

import java.time.LocalDateTime;

/**
 * 流程节点上下文信息对象。
 *
 * @author 陈镕希
 * @since 2024-05-21
 */
public class FlowPublishContext {
    /**
     * 表示流程实例trace id的 {@link String}。
     */
    private String traceId;

    /**
     * 上下文当前的状态值
     */
    private String status;

    /**
     * 阶段信息：before/after，分别代表节点执行前、执行后
     */
    private String stage;

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
    public FlowPublishContext() {
    }

    /**
     * 流程节点上下文信息对象的全参构造方法。
     *
     * @param traceId 流程节点所属实例唯一标识的 {@link String}。
     * @param status 流程节点状态信息的 {@link String}。
     * @param stage 流程阶段信息 {@link String}。
     * @param createAt 流程节点创建时间信息的 {@link LocalDateTime}。
     * @param updateAt 流程节点最后更新时间信息的 {@link LocalDateTime}。
     * @param archivedAt 流程节点结束时间信息的 {@link LocalDateTime}。
     */
    public FlowPublishContext(String traceId, String status, String stage, LocalDateTime createAt,
            LocalDateTime updateAt, LocalDateTime archivedAt) {
        this.traceId = traceId;
        this.status = status;
        this.stage = stage;
        this.createAt = createAt;
        this.updateAt = updateAt;
        this.archivedAt = archivedAt;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
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

    /**
     * 获取阶段信息。
     *
     * @return 返回阶段信息
     */
    public String getStage() {
        return stage;
    }

    /**
     * 设置阶段信息。
     *
     * @param stage 阶段信息
     */
    public void setStage(String stage) {
        this.stage = stage;
    }
}
