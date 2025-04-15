/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2025-2025. All rights reserved.
 */

package modelengine.fit.jober.aipp.domains.task;

/**
 * 普通数据类，当Task作为领域对象时使用的entity类.
 *
 * @author 张越
 * @since 2025-01-14
 */
public class TaskDomainEntity extends TaskEntity<TaskDomainEntity> {
    TaskDomainEntity() {}

    @Override
    public TaskDomainEntity self() {
        return this;
    }
}
