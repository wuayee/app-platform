/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.dataengine.rest.response;

import static modelengine.fit.jober.common.Constant.OWNER;
import static modelengine.fit.jober.common.Constant.REQUIREMENT_ID;
import static modelengine.fit.jober.common.Constant.STATE;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import modelengine.fit.jober.entity.TaskEntity;
import modelengine.fitframework.util.StringUtils;

/**
 * 第三方平台任务实例信息实体
 *
 * @author 晏钰坤
 * @since 2023/6/16
 */
@Getter
@Builder
@AllArgsConstructor
public class TaskInstanceMetaData {
    /**
     * 任务数据源定义唯一标识
     */
    private String taskSourceId;

    /**
     * 任务定义ID
     */
    private String taskDefinitionId;

    /**
     * 任务类型ID
     */
    private String taskTypeId;

    /**
     * 任务Entity
     */
    private TaskEntity taskEntity;

    /**
     * 获取requirement id
     *
     * @return requirement id
     */
    public String getRequirementId() {
        return taskEntity.getProps()
                .stream()
                .filter(property -> StringUtils.equals(property.getKey(), REQUIREMENT_ID))
                .findAny()
                .map(TaskEntity.TaskProperty::getValue)
                .orElse("");
    }

    /**
     * 获取任务属性prop中的owner信息
     *
     * @return owner
     */
    public String getOwner() {
        return taskEntity.getProps()
                .stream()
                .filter(property -> StringUtils.equals(property.getKey(), OWNER))
                .findAny()
                .map(TaskEntity.TaskProperty::getValue)
                .orElse("");
    }

    /**
     * 获取state
     *
     * @return state
     */
    public String getState() {
        return taskEntity.getProps()
                .stream()
                .filter(property -> StringUtils.equals(property.getKey(), STATE))
                .findAny()
                .map(TaskEntity.TaskProperty::getValue)
                .orElse("");
    }
}
