/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fit.waterflow.edatamate.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * 任务完成回调信息
 *
 * @author yangxiangyu
 * @since 2024/5/27
 */
@Getter
@Setter
@Builder
public class InstanceFinishedTaskInfo {
    /**
     * 任务实例执行状态
     */
    private String status;

    /**
     * 任务执行所需业务参数
     */
    private Map<String, Object> params;
}
