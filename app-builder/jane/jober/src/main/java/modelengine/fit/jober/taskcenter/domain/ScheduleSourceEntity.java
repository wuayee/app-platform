/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

/**
 * 表示通过定时任务同步数据的数据源。
 *
 * @author 陈镕希
 * @since 2023-08-07
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ScheduleSourceEntity extends SourceEntity {
    private String fitableId;

    private int interval;

    private Map<String, Object> filter;
}

