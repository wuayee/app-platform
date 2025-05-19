/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.dataengine.biz.converter;

import modelengine.fit.jober.dataengine.domain.aggregate.timescheduler.TimeScheduler;
import modelengine.fit.jober.dataengine.rest.request.StaticMetaDataTaskDto;

/**
 * 定时任务Converter
 *
 * @author 晏钰坤
 * @since 2023/6/27
 */
public interface TimeSchedulerConverter {
    /**
     * {@link TimeScheduler} 转换类
     *
     * @param staticMetaDataTaskDTO {@link StaticMetaDataTaskDto}
     * @return {@link TimeScheduler}
     */
    TimeScheduler converter(StaticMetaDataTaskDto staticMetaDataTaskDTO);
}
