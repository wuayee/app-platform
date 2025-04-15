/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2025-2025. All rights reserved.
 */

package modelengine.fit.jober.aipp.domains.taskinstance;

/**
 * 应用实例的修改数据类.
 *
 * @author 张越
 * @since 2025-01-08
 */
public class TaskInstanceUpdateEntity extends TaskInstanceEntity<TaskInstanceUpdateEntity> {
    public TaskInstanceUpdateEntity(String taskId, String instanceId) {
        super();
        this.setTaskId(taskId).setInstanceId(instanceId);
    }

    @Override
    public TaskInstanceUpdateEntity self() {
        return this;
    }
}
