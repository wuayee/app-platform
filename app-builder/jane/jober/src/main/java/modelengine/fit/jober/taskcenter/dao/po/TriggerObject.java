/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.dao.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 任务属性触发ORM数据对象。
 *
 * @author 王伟
 * @since 2023-08-10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TriggerObject {
    private String id;

    private String taskSourceId;

    private String taskPropertyId;

    private String fitableId;
}
