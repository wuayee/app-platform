/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2025-2025. All rights reserved.
 */

package modelengine.fit.jober.aipp.domains.taskinstance;

/**
 * 应用实例的数据类，当实例作为领域对象使用时的数据类.
 *
 * @author 张越
 * @since 2025-01-08
 */
public class TaskInstanceDomainEntity extends TaskInstanceEntity<TaskInstanceDomainEntity> {
    TaskInstanceDomainEntity() {
        super();
    }

    @Override
    public TaskInstanceDomainEntity self() {
        return this;
    }
}
