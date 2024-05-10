/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2024. All rights reserved.
 */

package com.huawei.fit.jane.dlock.jdbc.utils;

/**
 * 分布式锁状态
 *
 * @author y00679285
 * @since 2024/3/7
 */
public enum DistributedLockStatus {
    NOT_EXIST(true, false),
    LOCK_BY_OTHER(false, false),
    LOCK_BY_OTHER_EXPIRED(false, true),
    LOCK_BY_ME(false, true),
    LOCK_BY_ME_EXPIRED(false, true);

    private boolean isAllowUpdate;

    private boolean isAllowCreate;

    DistributedLockStatus(boolean isAllowCreate, boolean isAllowUpdateLock) {
        this.isAllowCreate = isAllowCreate;
        this.isAllowUpdate = isAllowUpdateLock;
    }

    /**
     * 是否允许创建
     *
     * @return 是否允许
     */
    public boolean isAllowCreate() {
        return isAllowCreate;
    }

    /**
     * 是否允许更新
     *
     * @return 是否允许更新
     */
    public boolean isAllowUpdate() {
        return isAllowUpdate;
    }

    /**
     * 是否被占用
     *
     * @return 占用结果
     */
    public boolean isOccupied() {
        return !isAllowCreate() && !isAllowUpdate();
    }
}
