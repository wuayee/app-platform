/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.waterflow.flowsengine.persist.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * FlowContext更新信息
 *
 * @since 2024-03-13
 */
@Getter
@Setter
public class FlowContextUpdateInfo {
    private final String toBatch;

    private final String status;

    private final String position;

    private final LocalDateTime updateAt;

    private final LocalDateTime archivedAt;

    public FlowContextUpdateInfo(String toBatch, String status, String position, LocalDateTime updateAt,
            LocalDateTime archivedAt) {
        this.toBatch = toBatch;
        this.status = status;
        this.position = position;
        this.updateAt = updateAt;
        this.archivedAt = archivedAt;
    }

    /**
     * 不构造toBatch信息
     *
     * @param status 状态
     * @param position 位置
     * @param updateAt 更新事件
     * @param archivedAt 完成时间
     */
    public FlowContextUpdateInfo(String status, String position, LocalDateTime updateAt,
            LocalDateTime archivedAt) {
        this("", status, position, updateAt, archivedAt);
    }
}
